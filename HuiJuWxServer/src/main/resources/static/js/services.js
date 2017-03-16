angular.module('myApp.services', [])

.factory('UtilFactory', function() {

	return {
		getQueryString: function(str) {
			
			var reg = new RegExp("(^|&)"+ str +"=([^&]*)(&|$)");
			var r = window.location.search.substr(1).match(reg);
			if(r!=null) {
				return  unescape(r[2]); 
			}
			return null;
		},
		setBodyTitle: function(data) {
			var $body = $('body');
  			document.title = data;
  			var $iframe = $("<iframe style='display:none;' src='/favicon.ico'></iframe>");
  			$iframe.on('load',function() {
    			setTimeout(function() {
      				$iframe.off('load').remove();
    			}, 0);
  			}).appendTo($body);
		},
		bytesToString: function(data) {
			var str = '';
			for (var i = 0; i < data.length; i++) {
				var temp = data.charCodeAt(i).toString(16).toUpperCase();
				if (temp.length == 1) {
					temp = '0' + temp;
				}
				str += temp;
			}
			return str;
		},
		stringToBytes: function(data) {
			var pos = 0;
			var len = data.length;
			if (len % 2 != 0) {return null;}
			len /= 2;
			var hexArray = new Array();
			for (var i = 0; i < len; i++) {
				var s = data.substr(pos, 2);
				var v = parseInt(s, 16);
				hexArray.push(v);
				pos += 2;
			}
			return hexArray;
		}
	}
})

.factory('ModelFactory', function(){
	var self = this;
	var openid = '';
	var deviceList = {};
	var selectedDevice = {};
	var selectedKey = '';
	var lockId = "";
	var deviceListFromWX = {};
	var curKey =null;//当前连接的管理钥匙
	var deviceId = "";//成功发送消息给设备后缓存的设备id
	return {
		setOpenId: function(data) {
			openid = data;
		},
		getOpenId: function() {
			return openid;
		},
		setDeviceListFromWX: function(data) {
			deviceListFromWX = data;
        },
        setDeviceListFromServer: function(data) {
        	 deviceList = data;
        },
        removeDevice: function(data) {
        	/*delete deviceList[data];*/
        	 for (var i = 0; i < deviceList.length; i++) {
				var device = deviceList[i];
				if(device.lockId==data){
					deviceList.splice(i,1);
				} 
			}
        },
        updateKeyState:function(data){
        	for (var i = 0; i < deviceList.length; i++) {
				var device = deviceList[i];
				var keyList = device.keyList;
				for (var j = 0; j < keyList.length; j++) {
					if(keyList[j].keyId == data.keyId){
						keyList[j].state = data.state;
					}
				}
			}
        },
        removeKey:function(data){
        	for (var i = 0; i < deviceList.length; i++) {
				var device = deviceList[i];
				var keyList = device.keyList;
				for (var j = 0; j < keyList.length; j++) {
					if(keyList[j].keyId == data.keyId){
						keyList.splice(j,1);
					}
				}
			}
        },
        insertKey:function(key,lockId){
        	for (var i = 0; i < deviceList.length; i++) {
				var device = deviceList[i];
				var keyList = device.keyList;
				if(lockId==device.lockId){
					keyList.push(key);
				} 
			}
        },
		getDeviceList: function() {
			if(deviceList.length>0){
				for (var i = 0; i < deviceList.length; i++) {
					var device = deviceList[i];
					var connectionCount = 0;
					var keyList = device.keyList;
					device.deviceId = "";
					for (var j = 0; j < keyList.length; j++) {
						if(keyList[j].state=="2"){
							keyList[j].stateStr = "已停用";
						}else if(keyList[j].state=="1"){
							keyList[j].stateStr = "已启用";
						}else{
							keyList[j].stateStr = "已停用";
						}
						if(keyList[j].type==1){
							for (var k = 0; k < deviceListFromWX.length; k++) {
								if(keyList[j].deviceId==deviceListFromWX[k].deviceId){
									connectionCount = deviceListFromWX[k].state=="connected"?++connectionCount:connectionCount;
								    device.deviceId+=deviceListFromWX[k].state=="connected"?deviceListFromWX[k].deviceId+",":"";
								    keyList[j].connectStatus = deviceListFromWX[k].state=="connected"?"已连接":"未连接";
								}
							}
							device.state = "已连接"+connectionCount+"个蓝牙设备"; 
							//toaster.info("----"+device.deviceId);
						}
					}
				}
			}
			return deviceList;
		},
		compareDeviceId:function(deviceIds,deviceId){
			//dtoaster.info("indexOf="+deviceIds.indexOf(deviceId));
			if(deviceId==""||deviceId==null||deviceId==undefined||deviceId=="undefined"){
				return false;
			}
			return new RegExp(deviceId).test(deviceIds);
		},
		getCurKeyByDeviceId:function(data){
			if(deviceList.length>0){
				for (var i = 0; i < deviceList.length; i++) {
					var device = deviceList[i];
					var keyList = device.keyList;
					for (var j = 0; j < keyList.length; j++) {
						if(keyList[j].deviceId==data){
							curKey = keyList[j];
						} 
					}
				}
			}
			return curKey;
		},
		setSelectedDevice: function(data) {
			selectedDevice = data;
		},
		getSelectedDevice: function() {
			return selectedDevice;
		},
		setSelectedKey: function(data) {
			selectedKey = data;
		},
		getSelectedKey: function() {
			return selectedKey;
		},
		setSelectedLockId:function(data){
			lockId = data;
		},
		getSelectedLockId:function(){
			return lockId;
		},
		getDefaultDeviceId:function(){
			return deviceId;
		},
		setDefaultDeviceId:function(data){
			deviceId = data;
		},
		sendDataToWxDevice:function(deviceId,base64Data){
	    	 
			wx.invoke('sendDataToWXDevice', {"deviceId": deviceId,"base64Data":base64Data}, function(res) {
			    if (res.err_msg =="sendDataToWXDevice:ok") {  
			    	 toaster.info('消息发送成功');
			    } else {  
			        toaster.info('消息发送失败'+JSON.stringify(res));
			    }       
			});
	    }
	}
})
.service('UtilService',function(UtilFactory,ModelFactory,$http){
   var self = this;
   this.alertMsg = function(msg){
	   try{
			var alertMsg = UtilFactory.bytesToString(msg);
			toaster.info(alertMsg);
		}catch(e){
			toaster.info("-------异常"+e.message);
		}
   }
   this.myDialog = function(options){
	  
   }
   /**
    * 远程请求方法
    * options 是传递的参数 {method:请求方式 GET /POST, url:请求地址 ，params ：请求参数}
    */
   this.requestHttp = function(options,successFunc,errorFunc){
		$http({
			method: options.method,
			url: options.url,
			params: options.params
		}).then(successFunc,errorFunc);
   }
}); 
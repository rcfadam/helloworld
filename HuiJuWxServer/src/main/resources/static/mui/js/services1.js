angular.module('MyApp.services',[])
.service('UtilsService',function(UtilFactory,$http,$rootScope,$timeout,ModelFactory){
	var self = this;
	self.alertMsg = function(msg){
		   try{
				var alertMsg = UtilFactory.bytesToString(msg);
				mui.toast(alertMsg);
			}catch(e){
				mui.toast("-------异常"+e.message);
			}
	}
	  /**
	    * 远程请求方法
	    * options 是传递的参数 {method:请求方式 GET /POST, url:请求地址 ，params ：请求参数}
	    */
	   self.requestHttp = function(options,successFunc,errorFunc){
			$http({
				method: options.method,
				url: options.url,
				params: options.params
			}).then(successFunc,errorFunc);
	   }
	 self.pullDownRefresh = function(){
		  mui('#devicelist').pullRefresh().endPulldownToRefresh();
	 }
	 self.pullUpRefresh = function(){
		 mui('#devicelist').pullRefresh().endPullupToRefresh(true);
	 }
	 
	self.init = function(){
		var currentUrl = window.location.href;
		self.reqJsApiParam(currentUrl);
		self.wxReady();
	}
	self.reqJsApiParam = function(currentUrl){
		$http({
			method: 'GET',
			url: '/getSysInfoByUrl',
			params: {
				url:currentUrl
			}
		}).then(function(resp) {
			//mui.toast(JSON.stringify(resp));
			UtilFactory.setSignature(resp.data.signature);
			UtilFactory.setNonceStr(resp.data.noncestr);
			UtilFactory.setTimestamp(resp.data.timestamp);
			self.wxInit(resp.data.timestamp,resp.data.noncestr,resp.data.signature);
		}, function(resp) {
			mui.toast("获取jsapi参数失败"+JSON.stringify(resp));
		});
	}
	self.wxInit = function(timestamp,nonceStr,signature){
		wx.config({
			beta: true,
			debug: false,
			//appId:'wxb4ba14562eb9a765',   //测试   huijuwx.com
			appId: 'wxf410780b57e7f0ad',//正式 live-smart.com.cn
			timestamp: timestamp,
			nonceStr: nonceStr,
			signature:signature,
			jsApiList: ['openWXDeviceLib',
						'closeWXDeviceLib',
						'getWXDeviceInfos', 
						'getWXDeviceTicket', 
						'onReceiveDataFromWXDevice',
						'onScanWXDeviceResult',
						'connectWXDevice',
						'startScanWXDevice',
						'stopScanWXDevice',
						'sendDataToWXDevice',
						'hideOptionMenu',
						'scanQRCode', 
						'closeWindow']
		});
	}
	
	self.getOpenId = function(){
		var codeStr = UtilFactory.getQueryString("code");
		$http({
			method: 'GET',
			url: '/getOpenId',
			params: {
				code: codeStr
			}
		}).then(function(resp) {
			var user = resp.data.user;
			ModelFactory.setOpenId(user.openId);
			ModelFactory.setUser(user);
			//mui.toast(JSON.stringify(ModelFactory.getUser()));
		}, function(resp) {
			mui.toast("openid 获取失败");	
		});
	}
	
	self.wxReady = function(){
		wx.ready(function() {
			wx.invoke('openWXDeviceLib', {'connType':'blue'}, function(res){
				$rootScope.$broadcast('transfer.initOK', '');
			});
			wx.hideOptionMenu();
			wx.on('onReceiveDataFromWXDevice', function(res) {
				var recvMsg = window.atob(res.base64Data);  
				recvMsg = UtilFactory.bytesToString(recvMsg);
				var cmdId = recvMsg.substr(0, 4); //取CMD ID
				//alert("recvmsg="+recvMsg);
				//第二步，接收蓝牙钥匙发送来的消息 来决定钥匙的状态。  
				if (cmdId == '0001') {
					var keyState = '';
					var resultCode = recvMsg.substr(4,2);
					if(resultCode=="01"){
						keyState = recvMsg.substr(6,29);//插入钥匙
					}else{
						keyState = "";
					}
					$rootScope.$broadcast('transfer.keyState', keyState); 
				} else if (cmdId == '0002') {
					var position = recvMsg.substr(6,2);//取钥匙在锁芯的position位置号 
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if (resultCode == '01') {
						$rootScope.$broadcast('transfer.addkeyOK',position);
					} else if (resultCode == '02') {
						$rootScope.$broadcast('transfer.addedKey', position);
					} else if (resultCode == '03') {
						$rootScope.$broadcast('transfer.addkeyFailed', '位号已满');
					}
				} else if (cmdId == '0003') {
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if (resultCode == '01') {
						$rootScope.$broadcast('transfer.deletekeyOK');
					} else if (resultCode == '02') {
						$rootScope.$broadcast('transfer.deletekeyFailed', '删除钥匙失败');
					}
				} else if (cmdId == '0004') {
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if (resultCode == '01') {
						$rootScope.$broadcast('transfer.enablekeyOK');
					} else if (resultCode == '02') {
						$rootScope.$broadcast('transfer.enablekeyFailed', '启用钥匙失败');
					}
				} else if (cmdId == '0005') {
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if (resultCode == '01') {
						$rootScope.$broadcast('transfer.disablekeyOK');
					} else if (resultCode == '02') {
						$rootScope.$broadcast('transfer.disablekeyFailed', '禁用钥匙失败');
					}
				}else if(cmdId=='0006'){
					$rootScope.$broadcast('transfer.batteryOk',recvMsg);
				}else if(cmdId=='0000'){
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if(resultCode=="01"){
						ModelFactory.addSyncKey(recvMsg.substring(6,recvMsg.length));
					}
					$rootScope.$broadcast('transfer.syncKeyOk',ModelFactory.addSyncKey());
				}else if(cmdId=="0007"){
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if(resultCode=="01"){
						$rootScope.$broadcast('transfer.initLockOK');
					}else{
						$rootScope.$broadcast('transfer.initLockFailed',"初始化锁芯失败");
					}
				}else if(cmdId=="0008"){
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if (resultCode == '01') {
						$rootScope.$broadcast('transfer.changeSelfKeyOK');
					} else if (resultCode == '02') {
						$rootScope.$broadcast('transfer.changeSelfKeyFailed', '钥匙状态改变失败');
					}
				} 
			});
		});

		wx.error(function(res){
	    	mui.toast("wx.error:" + JSON.stringify(res));
		});
		//获取openid
		self.getOpenId();
	}
	 
})
.factory('UtilFactory',function(){
	var signature = '';//微信签名
	var noncestr = ''; //微信随机数
	var timestamp = ''; //微信时间戳
	return {
		setSignature:function(data){
			signature = data;
		},
		getSignature:function(){
			return signature;
		},
		setNonceStr:function(data){
			noncestr = data;
		},
		getNonceStr:function(){
			return noncestr;
		},
		setTimestamp:function(data){
			timestamp = data;
		},
		getTimestamp:function(){
			return timestamp;
		},
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
	var syncKeyList = new Array();//同步钥匙列表
	var user = {};//微信用户信息
	return {
		setOpenId: function(data) {
			openid = data;
		},
		getOpenId: function() {
			return openid;
		},
		setUser: function(data) {
			user = data;
		},
		getUser: function() {
			return user;
		},
		setDeviceListFromWX: function(data) {
			deviceListFromWX = data;
        },
        getDeviceListFromWX: function() {
			return deviceListFromWX;
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
        updateKeyState:function(data,lockId){
        	try{
        		for (var i = 0; i < deviceList.length; i++) {
        			var device = deviceList[i];
        			if(lockId==device.lockId){
        				var keyList = device.keyList;
        				for (var j = 0; j < keyList.length; j++) {
        					if(keyList[j].keyTb.keyId == data.keyTb.keyId){
        						if(keyList[j].keyTb.type==1){
        							keyList[j].state = data.keyTb.state;
        							keyList[j].keyTb.state = data.keyTb.state;
        						}else{
        							keyList[j].state = data.keyTb.state;
        						}
        					}
        				}
        			}
        		}
        	}catch(e){
        		mui.toast("修改状态"+JSON.stringify(e));
        	}
        },
        removeKey:function(data,lockId){
        	try{
        		mui.toast("lockid="+lockId);
        		for (var i = 0; i < deviceList.length; i++) {
        			var device = deviceList[i];
        			if(device.lockId==lockId){
        				var keyList = device.keyList;
        				for (var j = 0; j < keyList.length; j++) {
        					//mui.toast("移除:"+keyList[j].keyTb.keyId+"-----"+data.keyTb.keyId);
        					if(keyList[j].keyTb.keyId == data.keyTb.keyId){
        						keyList.splice(j,1);
        					}
        				}
        				
        			}
        		}
        	}catch(e){
        		mui.toast("移除失败"+JSON.stringify(e));
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
						var key = keyList[j].keyTb;
						if(key.type==1){
							if(key.state=="1"&&keyList[j].state=="1"){
								keyList[j].keyTb.stateStr = "已启用";
							}else if(key.state=="2"||keyList[j].state=="2"){
								keyList[j].keyTb.stateStr = "已停用";
							}
						}else{
							if(keyList[j].state=="2"){
								keyList[j].keyTb.stateStr = "已停用";
							}else if(keyList[j].state=="1"){
								keyList[j].keyTb.stateStr = "已启用";
							}
						}
						if(key.type==1){
							for (var k = 0; k < deviceListFromWX.length; k++) {
								if(keyList[j].keyTb.deviceId==deviceListFromWX[k].deviceId){
									connectionCount = deviceListFromWX[k].state=="connected"?++connectionCount:connectionCount;
								    device.deviceId+=deviceListFromWX[k].state=="connected"?deviceListFromWX[k].deviceId+",":"";
								    keyList[j].keyTb.connectStatusStr = deviceListFromWX[k].state=="connected"?"已连接":"未连接";
								    keyList[j].keyTb.connectStatus = deviceListFromWX[k].state;
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
			if(deviceIds==""||deviceIds==null||deviceIds==undefined||deviceIds=="undefined"){
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
			    	 mui.toast('消息发送成功');
			    } else {  
			        mui.toast('消息发送失败'+JSON.stringify(res));
			    }       
			});
	    },
	    /**
		 * 创建消息
		 * {@link cmdId}} 格式 0x 开头的消息  比如 0x01  0x02 0x03 0x04 ...
		 * {@link Position} 位置号  为0 不存在
		 * {@link keyId} 钥匙id  为null  或 "" 不存在
		 */
		createmsg :function(cmdId,position,keyId){
			 var msg = String.fromCharCode(0x00)+String.fromCharCode(cmdId);
			if(position>0){
				msg += String.fromCharCode(parseInt(position,10));
			}
			if(keyId!=null&&keyId!=""){
				var keyIdData = UtilFactory.stringToBytes(keyId);
				for (var i=0; i<keyIdData.length; i++) {
					msg += String.fromCharCode(keyIdData[i]);
				}
			}
			return msg;
		},
		confirmBtn:function(message,title,success){
			var btnArray = ['取消', '确认'];
			mui.confirm(message, title, btnArray, success);
		},
		showWaiting:function(title,options){
			if(plus!=null){
				plus.nativeUI.showwating(title,options);
			}
		},
		closeWaiting:function(){
			if(plus==null){
				return ;
			}
			plus.nativeUI.closeWaiting();
		},
		updateKey:function(key){
			if(deviceList.length>0){
				for (var i = 0; i < deviceList.length; i++) {
					var device = deviceList[i];
					var keyList = device.keyList;
					for (var j = 0; j < keyList.length; j++) {
						if(keyList[j].keyTb.keyId==key.keyId){
							keyList[j].keyTb = key;
						} 
					}
				}
			}
		},
		updateLock:function(lock){
			if(deviceList.length>0){
				for (var i = 0; i < deviceList.length; i++) {
					var device = deviceList[i];
					if(device.locktb.lockId==lock.lockId){
						device.locktb = lock;
					} 
					 
				}
			}
		},
		getBleCountByLockId:function(lockId){
			var count = 0;
			if(deviceList.length>0){
				for (var i = 0; i < deviceList.length; i++) {
					var device = deviceList[i];
					if(device.locktb.lockId==lockId){
						var keyList = device.keyList;
						for(var j=0;j<keyList.length;j++){
							keyList[j].keyTb.type==1?count++:count;
						}
					} 
					 
				}
			}
			return count;
		},
		addSyncKey:function(data){
			syncKeyList.push(data);
		},
		getSyncKeyList:function(){
			return syncKeyList;
		}
	}
})

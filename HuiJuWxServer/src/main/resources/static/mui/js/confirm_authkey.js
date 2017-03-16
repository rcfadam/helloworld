try{
    var url = window.location.href;
	var result = -1;//时间无效 -1 ；时间有效  非-1的数  
    var getURLParam = function(name){
    	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if(r!=null) {
			return  unescape(r[2]); 
		}
		return null;
    }
   var  bytesToString= function(data) {
		var str = '';
		for (var i = 0; i < data.length; i++) {
			var temp = data.charCodeAt(i).toString(16).toUpperCase();
			if (temp.length == 1) {
				temp = '0' + temp;
			}
			str += temp;
		}
		return str;
	}
   var deviceId = getURLParam("deviceId");
   var openId = getURLParam("openId");
	var authkey = function(){
		if(result==0){
		   sendData();
		}else{
			$.get("/validtime",{deviceId:deviceId},function(resp){
				//mui.toast(JSON.stringify(resp)+"---"+resp.data);
				result = resp.data;
				if(result!=-1){
				   bindDevice();
				}else{
					mui.toast("钥匙的授权时间已经失效，请联系钥匙主人重新授权");
				}
			})
		}
	 }
	var sendData = function(){
		 var msg = String.fromCharCode(0x00)+String.fromCharCode(0x08)+String.fromCharCode(parseInt(1,10));
			wx.invoke('sendDataToWXDevice', {'deviceId':deviceId, 'connType':'blue', 'base64Data':window.btoa(msg)}, function(res) {
				if(res.err_msg=="sendDataToWXDevice:ok"){
					$("#errorInfo").html("<strong>蓝牙钥匙</strong><strong style='color:green'>授权成功</strong>");
				}else{
					mui.toast("授权失败，请打开蓝牙钥匙的开关按钮 ");
					$("#errorInfo").html("<strong>注意： 请打开蓝牙钥匙的开关按钮</strong><a  class='mui-btn mui-btn-blue' onclick='sendData()'>授权</a>")
				}
			});
	}
	
	var unbindDevice = function(){
		wx.invoke("getWXDeviceTicket",{'deviceId':deviceId,'type':'2', 'connType':'blue'},function(res){
			 var ticket = res.ticket;
			 var params= {
			  		 "openId":openId,
			  		 "deviceId":deviceId,
			  		 "ticket":ticket
			  	}
			 $.get("/unbindkey",{ 
				    "openId":openId,
		  		    "deviceId":deviceId,
		  		    "ticket":ticket
		  		   },function(resp){
			   })
		 })
	}
	var bindDevice = function(){
		wx.invoke("getWXDeviceTicket",{'deviceId':deviceId,'type':'1', 'connType':'blue'},function(res){
			 var ticket = res.ticket;
			 var params= {
			  		 "openId":openId,
			  		 "deviceId":deviceId,
			  		 "ticket":ticket
			  	}
			 $.get("/bindkey",{ 
				    "openId":openId,
		  		    "deviceId":deviceId,
		  		    "ticket":ticket
		  		   },function(resp){
					connectDevice();	
		  			  // sendData();
			   })
		 })
	}
    var connectDevice = function(){
    	wx.invoke('connectWXDevice', {'deviceId':deviceId, 'connType':'blue'}, function(res) {
    			 sendData();
    	});
    }
    var closeDeviceLib = function(){
    	wx.invoke('closeWXDeviceLib', {'connType':'blue'}, function(res) {
    		// mui.toast("关闭成功");
    	 });
    }
    $(document).ready(function(){
    	$.get("/getSysInfoByUrl",{url:window.location.href},function(resp){
    		//alert(JSON.stringify(resp)+"----"+resp.noncestr);
    		wx.config({
    			beta: true,
    			debug: false,
    			//appId:'wxb4ba14562eb9a765',   //测试   huijuwx.com
    			appId: 'wxf410780b57e7f0ad',//正式 live-smart.com.cn
    			timestamp: resp.timestamp,
    			nonceStr: resp.noncestr,
    			signature:resp.signature,
    			jsApiList: [
    					'openWXDeviceLib',
    					'closeWXDeviceLib',
    					'sendDataToWXDevice',
    					'connectWXDevice',
    					'hideOptionMenu',
    					'getWXDeviceTicket',
    					'onReceiveDataFromWXDevice',
    					'closeWindow'
    				]
    		});   			
    	})
    	wx.ready(function() {
			wx.invoke('openWXDeviceLib', {'connType':'blue'}, function(res){
				if(res.err_msg==" openWXDeviceLib:fail"){
					$("#errorInfo").html(" 请重新打开页面");
					return;
				} 
				if(res.bluetoothState=="off"){
					 $("#errorInfo").html(" 你太急了，还没有打开蓝牙设备");
					 return;
				 }
				authkey();
			});
			wx.hideOptionMenu();
			wx.on('onReceiveDataFromWXDevice',function(res){
				var recvMsg = window.atob(res.base64Data);  
				recvMsg = bytesToString(recvMsg);
				var cmdId = recvMsg.substr(0, 4); //取CMD ID
					var resultCode = recvMsg.substr(4, 2); // 取返回码
					if (resultCode == '01') {
						//unbindDevice();
						closeDeviceLib();
					}
			})
		});
    	
		wx.error(function(res){
	    	mui.toast("wx.error:" + JSON.stringify(res));
		});
		
		 
		
    })
    }catch(e){
    	mui.toast("error:"+JSON.stringify(e));
    }
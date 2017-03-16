angular.module('myApp.controllers', [])

.controller('mainController', ['$rootScope', '$scope', '$state', '$http', 'UtilFactory', 'ModelFactory', function($rootScope, $scope, $state, $http, UtilFactory, ModelFactory) {

	var signature = '';
	var noncestr = '';
	var timestamp = '';
	var stateStr = UtilFactory.getQueryString("state");
	//toaster.info(window.location.href);
	$scope.$on('$viewContentLoaded', function() {
		if (stateStr == 'addlock') {
			$state.go(stateStr);
		}
	});

	$http({
		method: 'GET',
		url: '/getSysInfoByUrl',
		params: {
			url: window.location.href
		}
	}).then(function(resp) {
		signature = resp.data.signature;
		$scope.signature = signature;
		noncestr = resp.data.noncestr;
		$scope.noncestr = noncestr;
		timestamp = resp.data.timestamp;
		$scope.timestamp = timestamp;

		wx.config({
			beta: true,
			debug: false,
			//appId:'wxb4ba14562eb9a765',   //测试   huijuwx.com
			appId: 'wxf410780b57e7f0ad',//正式 live-smart.com.cn
			timestamp: timestamp,
			nonceStr: noncestr,
			signature: signature,
			jsApiList: ['openWXDeviceLib',
						'closeWXDeviceLib',
						'getWXDeviceInfos', 
						'getWXDeviceTicket', 
						'onReceiveDataFromWXDevice',
						'sendDataToWXDevice', 
						'scanQRCode', 
						'closeWindow']
		});
	}, function(resp) {

	});

	wx.ready(function() {
		
		wx.invoke('openWXDeviceLib', {'connType':'blue'}, function(res){
    		$scope.$broadcast('transfer.initOK', '');
		});
	 
		wx.on('onReceiveDataFromWXDevice', function(res) {
			var recvMsg = window.atob(res.base64Data);  
			recvMsg = UtilFactory.bytesToString(recvMsg);
			var cmdId = recvMsg.substr(0, 4); //取CMD ID
			var resultCode = recvMsg.substr(4, 2); // 取返回码
			var position = recvMsg.substr(6,2);//取钥匙在锁芯的position位置号 
			//toaster.info("获取设备消息0"+resultCode+"---"+recvMsg+"----cmdId="+cmdId);
			//第二步，接收蓝牙钥匙发送来的消息 来决定钥匙的状态。0x01 /A0 插入钥匙  ，0x02/B1 未插入钥匙   ，0x03/A2钥匙ID非法，非本锁芯对应配置钥匙
			if (cmdId == '0001') {
				var keyState = '';
				if (resultCode == '01') {
					keyState = resultCode;//插入钥匙
					$scope.$broadcast('transfer.keyState', keyState); 
				} else if (resultCode == '02') {
					keyState = resultCode;//未插入钥匙
					$scope.$broadcast('transfer.keyState', keyState); 
				} else if (resultCode == '03') {
					keyState = resultCode;//钥匙非法
					$scope.$broadcast('transfer.keyState', keyState); 
				}
			} else if (cmdId == '0002') {
				if (resultCode == '01') {
					///toaster.info("position="+position);
					$scope.$broadcast('transfer.addkeyOK',position);
				} else if (resultCode == '02') {
					$scope.$broadcast('transfer.addkeyFailed', "已添加钥匙");
				} else if (resultCode == '03') {
					$scope.$broadcast('transfer.addkeyFailed', '位号已满');
				}
			} else if (cmdId == '0003') {
				if (resultCode == '01') {
					$scope.$broadcast('transfer.deletekeyOK');
				} else if (resultCode == '02') {
					$scope.$broadcast('transfer.deletekeyFailed', '删除钥匙失败');
				}
			} else if (cmdId == '0004') {
				if (resultCode == '01') {
					$scope.$broadcast('transfer.enablekeyOK');
				} else if (resultCode == '02') {
					$scope.$broadcast('transfer.enablekeyFailed', '启用钥匙失败');
				}
			} else if (cmdId == '0005') {
				if (resultCode == '01') {
					$scope.$broadcast('transfer.disablekeyOK');
				} else if (resultCode == '02') {
					$scope.$broadcast('transfer.disablekeyFailed', '禁用钥匙失败');
				}
			}else if(cmdId=='0006'){
				$scope.$broadcast('transfer.batteryOk',recvMsg);
			}else if(cmdId=='0007'){
				$scope.$broadcast('transfer.versionOk',recvMsg);
			} 
		});
	});

	wx.error(function(res){
    	toaster.info("wx.error:" + JSON.stringify(res));
	});
}])

.controller('devicelist', ['$scope', '$state', '$http', 'UtilFactory', 'ModelFactory','$interval','UtilService', function($scope, $state, $http, UtilFactory, ModelFactory,$interval,UtilService) {
	UtilFactory.setBodyTitle('锁列表');
	$scope.deviceList = ModelFactory.getDeviceList();
	var keyMD5 = ''; //keyID md5加密
	var selectedDeviceInfo = {}; //选择的设备信息
	var selectedDevice = '';//选择的设备信息的设备id ，是从管理钥匙列表中获取的
	var seletcedKey = {}; //选择的钥匙对象
	var op = '';	//操作标识
	/**
	 * 初始化 
	 */
	$scope.$on('transfer.initOK', function(event, data) {  
		var starttime = new Date().getTime();
        wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
            ModelFactory.setDeviceListFromWX(res.deviceInfos);
    		var codeStr = UtilFactory.getQueryString("code");
			$http({
				method: 'GET',
				url: '/getOpenId',
				params: {
					code: codeStr
				}
			}).then(function(resp) {
				var openid = resp.data.open_id;
				ModelFactory.setOpenId(openid);
				//toaster.info("openId="+openid);
	    		$http.get("/devicelist?openId=" +ModelFactory.getOpenId()).then(function(resp) {
					ModelFactory.setDeviceListFromServer(resp.data.data);
					$scope.deviceList = ModelFactory.getDeviceList();
					$scope.$apply();
					////toaster.info("耗时："+(new Date().getTime()-starttime)+"ms");
				}, function(resp) {
					toaster.info("锁列表失败1");
					
				});
			}, function(resp) {
				toaster.info("锁列表失败2");	
				
			});
		});
        $interval(function(){
        	//每隔一段时间从微信服务器上获取蓝牙钥匙的连接状态并显示到页面上
        	 wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
        		// toaster.info("定时更新微信设备信息");
        		 ModelFactory.setDeviceListFromWX(res.deviceInfos);
        		 $scope.deviceList = ModelFactory.getDeviceList();
				 $scope.$apply();
        	 });
        },5000);
    });
	
	$scope.$on('transfer.keyState', function(event, data) {  
    	//微信 设备锁与钥匙交互 之一 ——添加钥匙  第三步。正确插图管理钥匙，微信启动二维码添加钥匙
		try{
			//确保deviceid中没有非法的字符 
    	if (data == '01') {
    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.replace(/,/g,"");
    	    if(op == 'addKey'){
    	    	wx.scanQRCode({
    			    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
    			    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
    			    success: function (res) {
    			    	if (res.resultStr.length == 10) {	//普通钥匙  （type+keyId）
    			    		keyMD5 = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
    			    	}else if(res.resultStr.length>10){
    			    		var result = res.resultStr;
    			    		keyMD5 = result.substring(result.indexOf("#")+1,result.length);
    			    		/////toaster.info(keyMD5);
    			    	}
    			    	var msg = createmsg(0x02,0,keyMD5);
    			    	selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.replace(/,/g,"");
 						ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg));
    				}
    			});
    	    }else if (op == 'deleteKey') {
    	    var msg2 = createmsg(0x03,seletcedKey.position,seletcedKey.keyId);
			ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg2));
		} else if (op == 'enableKey') {
			var msg3 = createmsg(0x04,seletcedKey.position,seletcedKey.keyId);
			ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg3));
		} else if (op == 'disableKey') {
			var msg4 = createmsg(0x05,seletcedKey.position,seletcedKey.keyId);
			ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg4));
		  }
    	} else if(data=="02"){
    		toaster.info("未插入钥匙");
    	}else if(data=="03"){
    		toaster.info("非法钥匙");
    	}}catch(e){
    		toaster.info("钥匙状态改变错误："+e.message);
    	}
    });
	
	/**
	 * 创建消息
	 * {@link cmdId}} 格式 0x 开头的消息  比如 0x01  0x02 0x03 0x04 ...
	 * {@link Position} 位置号  为0 不存在
	 * {@link keyId} 钥匙id  为null  或 "" 不存在
	 */
	var createmsg = function(cmdId,position,keyId){
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
	}
  // 微信 设备锁与钥匙交互 之一 ——添加钥匙 第四步。 将接收到的设备position lockid type 添加到表中
    $scope.$on('transfer.addkeyOK', function(event, data) { 
       var keyId = keyMD5.substring(2, 10);
       $http({method:'GET',url:'/getkey',params:{keyId:keyId}}).then(function(resp){
    		   var key = resp.data.data;
    		//  toaster.info("deviceId="+key.deviceId+"-----"+key.type+"----position"+data);
    		   var params = {
     	 				lockId: selectedDeviceInfo.lockId,
     	 				keyId: keyId,
     	 				position:data,
     	 				openId:ModelFactory.getOpenId(),
     	 				ticket:"",
     	 		};
    		   // 蓝牙钥匙
    		  if(key!=null&&key.type==1){
    	        /// toaster.info("新增蓝牙钥匙");
    			  wx.invoke("getWXDeviceTicket",{'deviceId':key.deviceId,'type':'1'},function(res){
    	    	    // 新增蓝牙钥匙
    				params.ticket = res.ticket;
    	    	  },function(resp){
    	    		  toaster.info("ticket fail= "+JSON.stringify(resp));
    	    	  })
    	      }
			    UtilService.requestHttp({method:"GET",url:"/addKey",params:params},function(resp) {
					if (resp.data.result_code == 0) {
						toaster.info('钥匙' + keyMD5 + '添加成功');
						 UtilService.requestHttp({method: 'GET',url: '/devicelist',params: {openId: ModelFactory.getOpenId()}},function(resp) {
								ModelFactory.setDeviceListFromServer(resp.data.data);
								$scope.deviceList = ModelFactory.getDeviceList();
								$scope.$apply();
							}, function(resp) {
								toaster.info("锁列表失败1");
							});
					} else {
						toaster.info('添加钥匙失败：error: ' + resp.data.result_code.toString());
					}
				},function(resp) {
					toaster.info('请求失败：error: ' + JSON.stringify(resp));
				});
    	   },function(err){
    		   toaster.info("getkey 失败= "+JSON.stringify(err));
    	   })
    });
 
	$scope.$on('transfer.addkeyFailed', function(event, data) {  
		toaster.info(data);
    });
	
	/**
	 * 添加钥匙
	 * @param keylist 钥匙列表 @param deviceData 锁设备信息
	 */
    $scope.addKey = function(keylist, deviceData) {
    	return;
    	try{
    		var today = new Date().getTime();
    		var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
        	if(deviceData.deviceId!=""){
        		var deviceIds = deviceData.deviceId.split(",");
        		//toaster.info("length="+deviceIds.length);
        		for(var i=0;i<deviceIds.length;i++){
        			if(deviceIds[i]!=""&&deviceIds[i]!=","){
        		    	op = "addKey";
        		    	selectedDeviceInfo = deviceData;
        		    	selectedDeviceInfo.deviceId = deviceIds[i];
        		    	if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
        		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
        		    	}
        		    	//微信 设备锁与钥匙交互 之一 ——添加钥匙  第一步。获取蓝牙钥匙的状态。发送数据0x0001
        		    	ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg));
        		    }
        		}
        	}else{
        		toaster.info("没有连接的设备");
        	}
    	}catch(e){
    		toaster.info("添加"+e.message);
    	}
    }
    $scope.unbindLock = function(keylist, deviceData) {
    	try{
           	//获取操作凭证  {'deviceId':'设备ID','type':'操作凭证类型 1:绑定设备，2:解绑设备','connType':'设备类型:null/blue 表示蓝牙设备 ，lan局域网设备'}
           		var params = {
    					openId: ModelFactory.getOpenId(),
    					lockId: deviceData.lockId,
    					ticket: "",
    					deviceId:""
    				};
    				$http({
    					method: 'GET',
    					url: '/unbindLock',
    					params: params
    				}).then(function(resp) {
    					if (resp.data.result_code == 0) {
    						ModelFactory.removeDevice(deviceData.lockId);
    						$scope.deviceList = ModelFactory.getDeviceList();
							$scope.$apply();
    					} else {
    						toaster.info('解绑锁失败 1: ' + resp.data.result_code.toString());
    					}
    				}, function(resp) {
    					toaster.info('解绑锁失败 2: ' + resp.data.result_code.toString());
    				});
    	}catch(e){
    		toaster.info("解绑："+e.message);
    	}
    }
   $scope.isdeviceshown = function(){
	    return ModelFactory.getDeviceList().length>0?false:true;
   }
    $scope.toggleGroup = function(group) {
        group.show = !group.show;
      };
      $scope.isGroupShown = function(group) {
        return group.show;
      };
    
    $scope.$on('transfer.deletekeyOK', function(event, data) {  
    	var params = {
			lockId: selectedDeviceInfo.lockId,
			keyId: seletcedKey.keyId,
		};
		$http({
			method: 'GET',
			url: '/removeKey',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				toaster.info('钥匙' + seletcedKey.keyId + '删除成功');
				//修改devicelist 对象
				ModelFactory.removeKey(seletcedKey);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			} else {
				toaster.info('删除钥匙失败：error: ' + resp.data.result_code.toString());
			}
		}, function(resp) {
			
		});
    });
    $scope.$on('transfer.enablekeyOK', function(event, data) {  
        var params = {
			deviceId: ModelFactory.getDefaultDeviceId(),
			keyId: seletcedKey.keyId,
			keyState: "1"
		};
		$http({
			method: 'GET',
			url: '/changeKeyStatus',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				toaster.info('钥匙' + seletcedKey.keyId + '启用成功');
				//修改devicelist 对象	
				seletcedKey.state = "1";
				ModelFactory.updateKeyState(seletcedKey);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			} else {
				toaster.info('启用钥匙失败：error: ' + resp.data.result_code.toString());
			}
		}, function(resp) {
			
		});
    });
    $scope.$on('transfer.disablekeyOK', function(event, data) {  
        var params = {
			deviceId: ModelFactory.getDefaultDeviceId(),
			keyId: seletcedKey.keyId,
			keyState: "2"
		};
		$http({
			method: 'GET',
			url: '/changeKeyStatus',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				toaster.info('钥匙' + seletcedKey.keyId + '禁用成功');
				//修改devicelist 对象
				seletcedKey.state = "0";
				ModelFactory.updateKeyState(seletcedKey);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			} else {
				toaster.info('禁用钥匙失败：error: ' + resp.data.result_code.toString());
			}
		}, function(resp) {
			
		});
    });
    
	$scope.$on('transfer.deletekeyFailed', function(event, data) {  
        toaster.info(data);
    });

    $scope.$on('transfer.enablekeyFailed', function(event, data) {  
        toaster.info(data);
    });

    $scope.$on('transfer.disablekeyFailed', function(event, data) {  
        toaster.info(data);
    });
    
    
    $scope.deleteKey = function(deviceData,data) {
    	return ;
		op = 'deleteKey';
		seletcedKey =data;
		selectedDeviceInfo = deviceData;
		try{
			if(deviceData.deviceId==""){
				toaster.info("还没有连接的设备");
				return ;
			}
			var deviceIds = (deviceData.deviceId+"").split(",");
			var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
			for(var i=0;i<deviceIds.length;i++){
				if(deviceIds[i]!=""&&deviceIds[i]!=","){
					selectedDeviceInfo.deviceId = deviceIds[i];
					if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
    		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
    		    	}
					ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg));
				}
			}
		}catch(e){
			toaster.info("删除异常："+e.message);
		}
	};

	$scope.enableKey = function(deviceData,data) {
		try{
			op = 'enableKey';
			seletcedKey = data;
			selectedDeviceInfo = deviceData;
			var isok = ModelFactory.compareDeviceId(selectedDeviceInfo.deviceId,data.deviceId);
			//toaster.info("启用自己否:"+isok);
			if(isok){
				//启用自己
				    selectedDeviceInfo.deviceId = data.deviceId;
				    var msg4 = String.fromCharCode(0x00)+String.fromCharCode(0x08)+String.fromCharCode(parseInt(1,10));
				    //启用钥匙状态 1
					///UtilService.alertMsg(msg4);
					ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg4));
			}else{
				var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
				if(deviceData.deviceId==""){
					toaster.info("还没有连接的设备");
					return ;
				}
				var deviceIds = (deviceData.deviceId+"").split(",");
				for(var i=0;i<deviceIds.length;i++){
					if(deviceIds[i]!=""&&deviceIds[i]!=","){
						selectedDeviceInfo.deviceId = deviceIds[i];
						if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
							selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
						}
						ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg));
					}
				}
			}
		}catch(e){
			toaster.info("启用异常:"+e.message);
		}
	}
	
	$scope.disableKey = function(deviceData,data) {
		try{
			op = 'disableKey';
			seletcedKey =data;
			selectedDeviceInfo = deviceData;
			var isok = ModelFactory.compareDeviceId(selectedDeviceInfo.deviceId,data.deviceId);
			///toaster.info("禁用自己否:"+isok);
			if(isok==false){
				//蓝牙管理钥匙禁用别的钥匙
				var deviceIds = (selectedDeviceInfo.deviceId+"").split(",");
				var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
				for(var i=0;i<deviceIds.length;i++){
					//toaster.info(deviceIds[i]+"---");
					if(deviceIds[i]!=""&&deviceIds[i]!=","){
					selectedDeviceInfo.deviceId = deviceIds[i];
					if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
    		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
    		    	}
					 ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg));
				   }
			    }
				
			}else if(isok==true){
				//蓝牙管理钥匙禁用自己  
				selectedDeviceInfo.deviceId = data.deviceId;
				 var msg4 = String.fromCharCode(0x00)+String.fromCharCode(0x08)+String.fromCharCode(parseInt(2,10));
				//禁用钥匙状态 2
				//UtilService.alertMsg(msg4);
				ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg4));
			}
		}catch(e){
			toaster.info("禁用："+e.message);
		}
	};
	
	$scope.keyItem = function(device,key){
		ModelFactory.setSelectedDevice(device);
		ModelFactory.setSelectedKey(key);
		$state.go("keyitem");
	}
	$scope.opendialog = function(){
		
	}
	$scope.isdeviceshown = function(){
		var list = ModelFactory.getDeviceList();
		if(list.length>0){return true;}
		return false;
	}
}])
.controller('keyitem',['$scope','UtilFactory','ModelFactory','$timeout','UtilService',function($scope,UtilFactory,ModelFactory,$timeout,UtilService){
	 UtilFactory.setBodyTitle('钥匙详情');
	 var seletcedKey = ModelFactory.getSelectedKey();
	var selectDevice =  ModelFactory.getSelectedDevice();
	try{
		var isok = ModelFactory.compareDeviceId(selectDevice.deviceId,seletcedKey.deviceId);
		if(seletcedKey.type==1&&isok){
			var msg = String.fromCharCode(0x00)+String.fromCharCode(0x06);
			ModelFactory.sendDataToWxDevice(seletcedKey.deviceId,window.btoa(msg));
		}
		//UtilService.alertMsg(msg);
		//获取电量
		$scope.$on('transfer.batteryOk',function(event,data){
			if(data!=""){
				var battery = parseInt(data.substring(4,8),16);
				seletcedKey.battery = battery/1000;
				var version = data.substring(8,12);
				seletcedKey.softwareVersion = version.substring(0,1)+"."+version.substring(1,2);
				seletcedKey.firmwareVersion = version.substring(2,3)+"."+version.substring(3,4);
			}else{
				seletcedKey.battery = "---";	
			}
			ModelFactory.setSelectedKey(seletcedKey);
		})
	}catch(e){
		toaster.info("获取 固件信息失败"+e.message);
	}
	 
	$timeout(function(){
		$scope.key = ModelFactory.getSelectedKey();
		$scope.device = ModelFactory.getSelectedDevice();
		$scope.$apply();  
	},200); 
}])
.controller('addlock', ['$scope', 'UtilFactory', 'ModelFactory', function($scope, UtilFactory, ModelFactory) {
	UtilFactory.setBodyTitle('添加锁');
}])

.controller('keylist', ['$rootScope', '$scope', '$http', 'UtilFactory', 'ModelFactory', function($rootScope, $scope, $http, UtilFactory, ModelFactory) {
	UtilFactory.setBodyTitle('钥匙列表');
}])


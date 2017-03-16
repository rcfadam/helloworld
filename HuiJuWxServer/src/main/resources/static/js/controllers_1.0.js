angular.module('myApp.controllers', [])

.controller('mainController', ['$rootScope', '$scope', '$state', '$http', 'UtilFactory', 'ModelFactory', function($rootScope, $scope, $state, $http, UtilFactory, ModelFactory) {

	var signature = '';
	var noncestr = '';
	var timestamp = '';
	var stateStr = UtilFactory.getQueryString("state");
	toaster.info(window.location.href);
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
			appId:'wxb4ba14562eb9a765',   //测试   huijuwx.com
			//appId: 'wxf410780b57e7f0ad',//正式 live-smart.com.cn
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
			toaster.info("获取设备消息0"+resultCode+"---"+recvMsg+"----cmdId="+cmdId);
			//第二步，接收蓝牙钥匙发送来的消息 来决定钥匙的状态。0x01 /A0 插入钥匙  ，0x02/B1 未插入钥匙   ，0x03/A2钥匙ID非法，非本锁芯对应配置钥匙
			if (cmdId == '0001') {
				var keyState = '';
				if (resultCode == 'A0') {
					keyState = resultCode;//插入钥匙
					$scope.$broadcast('transfer.keyState', keyState); 
				} else if (resultCode == 'B1') {
					keyState = resultCode;//未插入钥匙
					$scope.$broadcast('transfer.keyState', keyState); 
				} else if (resultCode == 'A2') {
					keyState = resultCode;//钥匙非法
					$scope.$broadcast('transfer.keyState', keyState); 
				}
			} else if (cmdId == '0002') {
				if (resultCode == 'A0') {
					$scope.$broadcast('transfer.addkeyOK');
				} else if (resultCode == 'A3') {
					$scope.$broadcast('transfer.addkeyFailed', '钥匙已经被添加，请勿重复添加');
				} else if (resultCode == 'A4') {
					$scope.$broadcast('transfer.addkeyFailed', '位号已满');
				}
			} else if (cmdId == '0003') {
				if (resultCode == 'A0') {
					$scope.$broadcast('transfer.deletekeyOK');
				} else if (resultCode == 'A5') {
					$scope.$broadcast('transfer.deletekeyFailed', '钥匙id不存在');
				}
			} else if (cmdId == '0004') {
				toaster.info("获取设备消息3"+resultCode);
				if (resultCode == 'A0') {
					$scope.$broadcast('transfer.enablekeyOK');
				} else if (resultCode == 'A5') {
					toaster.info("获取设备消息4"+resultCode);
					$scope.$broadcast('transfer.enablekeyFailed', '钥匙id不存在');
				}
			} else if (cmdId == '0005') {
				toaster.info("获取设备消息5"+resultCode);
				if (resultCode == 'A0') {
					$scope.$broadcast('transfer.disablekeyOK');
				} else if (resultCode == 'A5') {
					toaster.info("获取设备消息6"+resultCode);
					$scope.$broadcast('transfer.disablekeyFailed', '钥匙id不存在');
				}
			}
		});
	});

	wx.error(function(res){
    	toaster.info("wx.error:" + JSON.stringify(res));
	});
}])

.controller('devicelist', ['$scope', '$state', '$http', 'UtilFactory', 'ModelFactory','$interval', function($scope, $state, $http, UtilFactory, ModelFactory,$interval) {
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
				toaster.info("openId="+openid);
	    		$http.get("/devicelist?openId=" +ModelFactory.getOpenId()).then(function(resp) {
					ModelFactory.setDeviceListFromServer(resp.data.data);
					$scope.deviceList = ModelFactory.getDeviceList();
					$scope.$apply();
					toaster.info("耗时："+(new Date().getTime()-starttime)+"ms");
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
    	if (data == 'A0') {
    	    if(op == 'addKey'){
    	    	wx.scanQRCode({
    			    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
    			    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
    			    success: function (res) {
    			    	if (res.resultStr.length == 12) {	//普通钥匙  蓝牙钥匙 （type+position+keyId）
    			    		keyMD5 = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
    					    var msg = String.fromCharCode(0x00)+String.fromCharCode(0x02);
    						var md5Data = UtilFactory.stringToBytes(keyMD5);
    						for (var i=0; i<md5Data.length; i++) {
    							msg += String.fromCharCode(md5Data[i]);
    						}
    						selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.replace(/,/g,"");
    						toaster.info("选择的deviceId="+selectedDeviceInfo.deviceId);
    						var jsonData = {"deviceId": selectedDeviceInfo.deviceId, "base64Data": window.btoa(msg)};
    						wx.invoke('sendDataToWXDevice', jsonData, function(res) {
    						    if (res.err_msg =="sendDataToWXDevice:ok") {  
    						    	toaster.info('添加普通钥匙信息发送成功');
    						    } else {  
    						    	toaster.info('添加普通钥匙信息发送失败');
    						    }       
    						});
    			    	}
    				}
    			});
    	    }else if (op == 'deleteKey') {
				 var msg2 = String.fromCharCode(0x00)+String.fromCharCode(0x03)
						+ String.fromCharCode(parseInt(seletcedKey.type,10))
						+ String.fromCharCode(parseInt(seletcedKey.position,10));
				var keyIdData = UtilFactory.stringToBytes(seletcedKey.keyId);
				var msg22 = "";
				for (var i=0; i<keyIdData.length; i++) {
					msg22 += String.fromCharCode(keyIdData[i]);
				} 
				msg2 = msg2.concat(msg22);
				alertMsg(msg2);
				var jsonData = {"deviceId": selectedDeviceInfo.deviceId, "base64Data": window.btoa(msg2)};
				wx.invoke('sendDataToWXDevice', jsonData, function(res) {
				    if (res.err_msg =="sendDataToWXDevice:ok") {  
				    	toaster.info('删除钥匙信息发送成功');
				    } else {  
				    	toaster.info('删除钥匙信息发送失败');
				    }       
				});
			} else if (op == 'enableKey') {
				 var msg3 = String.fromCharCode(0x00)+String.fromCharCode(0x04)
					+ String.fromCharCode(parseInt(seletcedKey.type,10))
					+ String.fromCharCode(parseInt(seletcedKey.position,10));
				var keyIdData = UtilFactory.stringToBytes(seletcedKey.keyId);
				var msg33 = "";
				for (var i=0; i<keyIdData.length; i++) {
					msg33 += String.fromCharCode(keyIdData[i]);
				}
				msg3 = msg3.concat(msg33);
				alertMsg(msg3);
				var jsonData = {"deviceId":selectedDeviceInfo.deviceId, "base64Data": window.btoa(msg3)};
				wx.invoke('sendDataToWXDevice', jsonData, function(res) {
				    if (res.err_msg =="sendDataToWXDevice:ok") {  
				    	toaster.info('启用钥匙信息发送成功');
				    } else {  
				    	toaster.info('启用钥匙信息发送失败');
				    }       
				});
			} else if (op == 'disableKey') {
				 var msg4 = String.fromCharCode(0x00)+String.fromCharCode(0x05)
					+ String.fromCharCode(parseInt(seletcedKey.type,10))
					+ String.fromCharCode(parseInt(seletcedKey.position,10));
				var keyIdData = UtilFactory.stringToBytes(seletcedKey.keyId);
				var msg44 = "";
				for (var i=0; i<keyIdData.length; i++) {
					msg44 += String.fromCharCode(keyIdData[i]);
				}
				msg4 = msg4.concat(msg44);
				alertMsg(msg4);
				var jsonData = {"deviceId": selectedDeviceInfo.deviceId, "base64Data": window.btoa(msg4)};
				wx.invoke('sendDataToWXDevice', jsonData, function(res) {
				    if (res.err_msg =="sendDataToWXDevice:ok") {  
				    	toaster.info('停用钥匙信息发送成功:'+JSON.stringify(res));
				    } else {  
				    	toaster.info('停用钥匙信息发送失败'+JSON.stringify(res));
				    }       
				});
			}
    	} else if(data=="B1"){
    		toaster.info("未插入钥匙");
    	}else if(data=="A2"){
    		toaster.info("钥匙不合法");
    	}
    });

  //微信 设备锁与钥匙交互 之一 ——添加钥匙  第四步。 将接收到的设备position  lockid type 添加到表中
    $scope.$on('transfer.addkeyOK', function(event, data) { 
    	var params = {
			lockId: selectedDeviceInfo.lockId,
			keyId: keyMD5.substring(4, 12),
			position:keyMD5.substring(2, 4),
			openId:ModelFactory.getOpenId(),
		};
		$http({
			method: 'GET',
			url: '/addKey',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				toaster.info('钥匙' + keyMD5 + '添加成功');
				//ModelFactory.insertKey(key,selectedDeviceInfo.lockId);
				//$scope.deviceList = ModelFactory.getDeviceList();
				//$scope.$apply();
				  wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
			            ModelFactory.setDeviceListFromWX(res.deviceInfos);
			        	$http({
							method: 'GET',
							url: '/devicelist',
							params: {
								openId: ModelFactory.getOpenId()
							}
						}).then(function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$scope.deviceList = ModelFactory.getDeviceList();
							$scope.$apply();
						}, function(resp) {
							toaster.info("锁列表失败1");
							
						});
				  });
			} else {
				toaster.info('添加钥匙失败：error: ' + resp.data.result_code.toString());
				
			}
		}, function(resp) {
			
		});
    });

	$scope.$on('transfer.addkeyFailed', function(event, data) {  
		toaster.info(data);
    });
	 
	/**
	 * 添加钥匙
	 * @param keylist 钥匙列表 @param deviceData 锁设备信息
	 */
    $scope.addKey = function(keylist, deviceData) {
    	try{
    		var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
        	if(deviceData.deviceId!=""){
        		var deviceIds = deviceData.deviceId.split(",");
        		toaster.info("length="+deviceIds.length);
        		for(var i=0;i<deviceIds.length;i++){
        			if(deviceIds[i]!=""){
        		    	op = "addKey";
        		    	selectedDeviceInfo = deviceData;
        		    	selectedDeviceInfo.deviceId = deviceIds[i];
        		    	if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
        		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
        		    	}
        		    	//微信 设备锁与钥匙交互 之一 ——添加钥匙  第一步。获取蓝牙钥匙的状态。发送数据0x0001
        		    	sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg));
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
    						  wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
    					            ModelFactory.setDeviceListFromWX(res.deviceInfos);
    					        	$http({
    									method: 'GET',
    									url: '/devicelist',
    									params: {
    										openId: ModelFactory.getOpenId()
    									}
    								}).then(function(resp) {
    									ModelFactory.setDeviceListFromServer(resp.data.data);
    									$scope.deviceList = ModelFactory.getDeviceList();
    									$scope.$apply();
    								}, function(resp) {
    									toaster.info("锁列表失败1");
    									
    								});
    						  });
    					} else {
    						toaster.info('unbindLock error1: ' + resp.data.result_code.toString());
    					}
    				}, function(resp) {
    					toaster.info('unbindLock error2: ' + resp.data.result_code.toString());
    				});
    	}catch(e){
    		toaster.info("解绑："+e.message);
    	}
    	if(deviceData.deviceId==""){
		    toaster.info("没有连接的设备");
		    return ;
    	}
    	
    }
    
    var alertMsg = function(msg){
		try{
			var alertMsg = UtilFactory.bytesToString(msg);
			toaster.info("-------"+alertMsg);
		}catch(e){
			toaster.info("-------异常"+JSON.stringify(e));
		}
		
	};
   $scope.isdeviceshown = function(){
	    return ModelFactory.getDeviceList().length>0?false:true;
   }
  
   
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
				toaster.info('钥匙' + seletcedKey + '删除成功');
				//修改devicelist 对象
				/* wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
			            ModelFactory.setDeviceListFromWX(res.deviceInfos);
			        	$http({
							method: 'GET',
							url: '/devicelist',
							params: {
								openId: ModelFactory.getOpenId()
							}
						}).then(function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$scope.deviceList = ModelFactory.getDeviceList();
							$scope.$apply();
						}, function(resp) {
							toaster.info("锁列表失败1");	
						});
				  });*/
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
			deviceId: selectedDeviceInfo.deviceId,
			keyId: seletcedKey.keyId,
			keyState: "1"
		};
		$http({
			method: 'GET',
			url: '/changeKeyStatus',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				toaster.info('钥匙' + seletcedKey + '启用成功');
				//修改devicelist 对象	
				seletcedKey.state = "1";
				ModelFactory.updateKeyState(seletcedKey);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
				/* wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
			            ModelFactory.setDeviceListFromWX(res.deviceInfos);
			        	$http({
							method: 'GET',
							url: '/devicelist',
							params: {
								openId: ModelFactory.getOpenId()
							}
						}).then(function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$scope.deviceList = ModelFactory.getDeviceList();
							$scope.$apply();
						}, function(resp) {
							toaster.info("锁列表失败1");	
						});
				  });*/
			} else {
				toaster.info('启用钥匙失败：error: ' + resp.data.result_code.toString());
			}
		}, function(resp) {
			
		});
    });
    $scope.toggleGroup = function(group) {
        group.show = !group.show;
      };
      $scope.isGroupShown = function(group) {
        return group.show;
      };
    $scope.openKeyList = function(device){
    	var element =  angular.element(document).find("a [class='collapsed']");
    	 toaster.info("device"+device.lockId+"----element="+element);
    	 try{
	    	element.on("show.bs.collapse",function(){
	  		    $scope.device.show = true;
	  		    $scope.$apply();
	  	    })
	  	  element.on("hide.bs.collapse",function(){
	  	      $scope.device.show = false;
	  		  $scope.$apply();
	  	  })
	  	  element.collapse("toggle");
       }catch(e){
  		  toaster.info("展开失败:"+e.message);
  	  }
    }
    $scope.$on('transfer.disablekeyOK', function(event, data) {  
        var params = {
			deviceId: selectedDeviceInfo.deviceId,
			keyId: seletcedKey.keyId,
			keyState: "2"
		};
		$http({
			method: 'GET',
			url: '/changeKeyStatus',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				toaster.info('钥匙' + seletcedKey + '禁用成功');
				//修改devicelist 对象
				seletcedKey.state = "0";
				ModelFactory.updateKeyState(seletcedKey);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
				 /*wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
			            ModelFactory.setDeviceListFromWX(res.deviceInfos);
			        	$http({
							method: 'GET',
							url: '/devicelist',
							params: {
								openId: ModelFactory.getOpenId()
							}
						}).then(function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$scope.deviceList = ModelFactory.getDeviceList();
							$scope.$apply();
						}, function(resp) {
							toaster.info("锁列表失败1");	
						});
				  });*/
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

    var sendDataToWxDevice = function(deviceId,base64Data){
    	wx.invoke('sendDataToWXDevice', {"deviceId": deviceId,"base64Data":base64Data}, function(res) {
		    if (res.err_msg =="sendDataToWXDevice:ok") {  
		    	 toaster.info('消息发送成功');
		    	 selectedDeviceInfo.deviceId = deviceId;
		    	 return true;
		    } else {  
		        toaster.info('消息发送失败，请确认蓝牙管理钥匙已连接'+JSON.stringify(res));
		        return false;
		    }       
		});
    }
    
	$scope.deleteKey = function(deviceData,data) {
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
				if(deviceIds[i]!=""){
					selectedDeviceInfo.deviceId = deviceIds[i];
					if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
    		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
    		    	}
					if(sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg))){
						return ;
					}
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
			var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
			if(deviceData.deviceId==""){
				toaster.info("还没有连接的设备");
				return ;
			}
			var deviceIds = (deviceData.deviceId+"").split(",");
			for(var i=0;i<deviceIds.length;i++){
				if(deviceIds[i]!=""){
					selectedDeviceInfo.deviceId = deviceIds[i];
					if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
    		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
    		    	}
					if(sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg))){
						return ;
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
			toaster.info("disabled====="+selectedDeviceInfo.deviceId+"和"+data.deviceId+"----"+isok);
			if(isok==false){
				//蓝牙管理钥匙禁用别的钥匙
				var deviceIds = (selectedDeviceInfo.deviceId+"").split(",");
				var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
				for(var i=0;i<deviceIds.length;i++){
					if(deviceIds[i]!=""){
					selectedDeviceInfo.deviceId = deviceIds[i];
					if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
    		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
    		    	}
					 if(sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg))){
						 return;
					 }
				   }
			    }
				
			}else{
				//蓝牙管理钥匙禁用自己  
				selectedDeviceInfo.deviceId = data.deviceId;
				 var msg4 = String.fromCharCode(0x00)+String.fromCharCode(0x05)
					+ String.fromCharCode(parseInt(seletcedKey.type,10))
					+ String.fromCharCode(parseInt(seletcedKey.position,10));
				var keyIdData = UtilFactory.stringToBytes(seletcedKey.keyId);
				var msg44 = "";
				for (var i=0; i<keyIdData.length; i++) {
					msg44 += String.fromCharCode(keyIdData[i]);
				}
				msg4 = msg4.concat(msg44);
				alertMsg(msg4);
				var jsonData = {"deviceId": selectedDeviceInfo.deviceId, "base64Data": window.btoa(msg4)};
				wx.invoke('sendDataToWXDevice', jsonData, function(res) {
				    if (res.err_msg =="sendDataToWXDevice:ok") {  
				    	toaster.info('停用钥匙信息发送成功:'+JSON.stringify(res));
				    } else {  
				    	toaster.info('停用钥匙信息发送失败'+JSON.stringify(res));
				    }       
				});
			}
		}catch(e){
			toaster.info("禁用："+e.message);
		}
	};
}])

.controller('addlock', ['$scope', 'UtilFactory', 'ModelFactory', function($scope, UtilFactory, ModelFactory) {
	UtilFactory.setBodyTitle('添加锁');
}])

.controller('keylist', ['$rootScope', '$scope', '$http', 'UtilFactory', 'ModelFactory', function($rootScope, $scope, $http, UtilFactory, ModelFactory) {
	UtilFactory.setBodyTitle('钥匙列表');
}])


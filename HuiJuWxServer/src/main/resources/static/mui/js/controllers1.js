angular.module('MyApp.controllers',[])
.controller('settingController',['$scope','ModelFactory','$state','UtilsService',function($scope,ModelFactory,$state,UtilsService){
	var user = ModelFactory.getUser();
	if(user==null){
		 UtilsService.getOpenId();
		 user = ModelFactory.getUser();
		$scope.user = {"headImgUrl":user.headImgUrl,"nickname":user.nickname};
	}else{
		 user = ModelFactory.getUser();
		 $scope.user = {"headImgUrl":user.headImgUrl,"nickname":user.nickname};
	}
	$scope.$on('transfer.initOK', function(event, data) {
		var user = ModelFactory.getUser();
		$scope.user = {"headImgUrl":user.headImgUrl,"nickname":user.nickname};
	})
	$scope.jump = function(stateStr){
		$state.go(stateStr);
	}
}])
.controller('locklistController',['$scope', '$state', '$http', 'UtilFactory', 'ModelFactory','$interval','UtilsService','$timeout', function($scope, $state, $http, UtilFactory, ModelFactory,$interval,UtilsService,$timeout){
	$scope.deviceList = ModelFactory.getDeviceList();

	var keyMD5 = ''; //keyID md5加密
	var selectedDeviceInfo = {}; //选择的设备信息
	var selectedDevice = '';//选择的设备信息的设备id ，是从管理钥匙列表中获取的
	var seletcedKey = {}; //选择的钥匙对象
	var op = '';	//操作标识
	var flag =  false ;//是否是蓝牙钥匙在锁芯的状态 改变
	var lockId = ""; //钥匙插入锁芯的lockId
	var keySelfState = 0;//钥匙自身的状态
	/**
	 * 初始化
	 */
	$scope.$on('transfer.initOK', function(event, data) {
        wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
            ModelFactory.setDeviceListFromWX(res.deviceInfos);
            var key = null;
            if(ModelFactory.getOpenId()==null){
            	//防止第一次获取openid失败的情况
            	UtilsService.getOpenId();
            	getDeviceList(ModelFactory.getOpenId());
            	assertWaitKey(ModelFactory.getOpenId());
            }else{
            	getDeviceList(ModelFactory.getOpenId());
            	assertWaitKey(ModelFactory.getOpenId());
            }
		});
    
        var assertWaitKey = function(openId){
        	$http.get("/findKey?openId=" +openId).then(function(resp) {
        		key = resp.data.data.key;
        		if(key!=null&&key.keyId!=null){
        			ModelFactory.setSelectedKey(key);
        			$timeout(function(){$state.go("waitAddKey")},100);
        		}
        	}, function(resp) {
        	});
        }
        
        var getDeviceList = function(openID){
        	$http.get("/devicelist?openId=" +openID).then(function(resp) {
    			ModelFactory.setDeviceListFromServer(resp.data.data);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			}, function(resp) {
				mui.toast("锁列表失败1");
			});
        }
        
        $interval(function(){
        	//每隔一段时间从微信服务器上获取蓝牙钥匙的连接状态并显示到页面上
        	 wx.invoke('getWXDeviceInfos', {'connType':'blue'}, function(res) {
        		 ModelFactory.setDeviceListFromWX(res.deviceInfos);
        		 $scope.deviceList = ModelFactory.getDeviceList();
				 $scope.$apply();
        	 });
        },5000);
    });
	
	$scope.$on('transfer.keyState', function(event, data) {  
    	//微信 设备锁与钥匙交互 之一 ——添加钥匙  第三步。正确插图管理钥匙，微信启动二维码添加钥匙
		try{
			if(data==""){
				mui.toast("未插入蓝牙管理钥匙");
				return;
			}
			lockId = data;
			$http.get("/lockCountByLockId?lockId=" +lockId).then(function(resp) {
	    		var count = resp.data.data;
	    		 if(count>0){
		    		//确保deviceid中没有非法的字符 
		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.replace(/,/g,"");
		    	    mui.toast(op);
		    		if(op == 'addKey'){
		    	    	wx.scanQRCode({
		    			    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
		    			    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
		    			    success: function (res) {
					    		var result = res.resultStr;
					    		//mui.toast(result);
					    		var id = result.substring(result.indexOf("#")+1,result.length);
					    			$http.get("/getKeyByqrcodeserial?qrcodeSerial=" +id).then(function(resp) {
					    				var key = resp.data.data;
					    				keyMD5 = "0"+key.type+key.keyId;
					    				var msg = createmsg(0x02,0,0,keyMD5);
					    				selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.replace(/,/g,"");
					    				ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg));
					    			}, function(resp) {
					    				mui.toast("钥匙不存在");
					    			});
		    				}
		    			});
		    	    }else if (op == 'deleteKey') {
		    	    var msg2 = createmsg(0x03,0,seletcedKey.keyTb.type,seletcedKey.keyId);
		    	    UtilsService.alertMsg(msg2);
		    	    ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg2));
				} else if (op == 'enableKey') {
					var msg3 = createmsg(0x04,0,seletcedKey.keyTb.type,seletcedKey.keyId);
					UtilsService.alertMsg(msg3);
					ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg3));
				} else if (op == 'disableKey') {
					var msg4 = createmsg(0x05,0,seletcedKey.keyTb.type,seletcedKey.keyId);
					UtilsService.alertMsg(msg4);
					ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg4));
				}
    		 }else{
    			 mui.toast("锁芯不存在");
    		 }
			}, function(resp) {
				mui.toast("锁芯不存在");
			});
    	}catch(e){
    		mui.toast("钥匙状态改变错误："+e.message);
    	}
    });
	
  // 微信 设备锁与钥匙交互 之一 ——添加钥匙 第四步。 将接收到的设备position lockid type 添加到表中
    $scope.$on('transfer.addkeyOK', function(event, data) { 
       var keyId = keyMD5.substring(2, 10);
       $http({method:'GET',url:'/getkey',params:{keyId:keyId}}).then(function(resp){
    	     var key = resp.data.data;
    		   var params = {
     	 				lockId: lockId,
     	 				keyId: keyId,
     	 				position:data,
     	 				openId:ModelFactory.getOpenId(),
     	 				ticket:"",
     	 		};
    		   // 蓝牙钥匙
    		  if(key!=null&&key.type==1){
    			  wx.invoke("getWXDeviceTicket",{'deviceId':key.deviceId,'type':'1', 'connType':'lan'},function(res){
    	    	    // 新增蓝牙钥匙
    				 params.ticket = res.ticket;
    			     savekey(params);
    	    	  },function(resp){
    	    		  mui.toast("ticket fail= "+JSON.stringify(resp));
    	    	  })
    	      }else{
    	    	 savekey(params); 
    	      }
    	   },function(err){
    		   mui.toast("getkey 失败= "+JSON.stringify(err));
    	   })
    });
    
    var savekey = function(params){
    	 UtilsService.requestHttp({method:"GET",url:"/addKey",params:params},function(resp) {
				if (resp.data.result_code == 0) {
					mui.toast('钥匙' + keyMD5 + '添加成功');
					 UtilsService.requestHttp({method: 'GET',url: '/devicelist',params: {openId: ModelFactory.getOpenId()}},function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$scope.deviceList = ModelFactory.getDeviceList();
							$scope.$apply();
						}, function(resp) {
							mui.toast("锁列表失败1");
						});
				} else {
					mui.toast('添加钥匙失败：error: ' + resp.data.result_code.toString());
				}
			},function(resp) {
				mui.toast('请求失败：error: ' + JSON.stringify(resp));
			});
    }
    
	$scope.$on('transfer.addkeyFailed', function(event, data) {  
		mui.toast(data);
    });
	
	$scope.$on('transfer.addedKey', function(event, data) {  
		 var keyId = keyMD5.substring(2, 10);
	       $http({method:'GET',url:'/getkey',params:{keyId:keyId}}).then(function(resp){
	    	  // mui.toast("resp="+JSON.stringify(resp))  
	    	     var key = resp.data.data;
	    		   var params = {
	     	 				lockId: lockId,
	     	 				keyId: keyId,
	     	 				position:data,
	     	 				openId:ModelFactory.getOpenId(),
	     	 				ticket:"",
	     	 		};
	    		   // 蓝牙钥匙
	    		  if(key!=null&&key.type==1){
	    			  wx.invoke("getWXDeviceTicket",{'deviceId':key.deviceId,'type':'1', 'connType':'lan'},function(res){
	    	    	    // 新增蓝牙钥匙
	    				 params.ticket = res.ticket;
	    			     savekey(params);
	    	    	  },function(resp){
	    	    		  mui.toast("ticket fail= "+JSON.stringify(resp));
	    	    	  })
	    	      }else{
	    	    	 savekey(params); 
	    	      }
	    	   },function(err){
	    		   mui.toast("getkey 失败= "+JSON.stringify(err));
	    	   })
    });
	
	/**
	 * 添加钥匙
	 * @param keylist 钥匙列表 @param deviceData 锁设备信息
	 */
    var addKey = function(keylist, deviceData) {
    	mui("#forward").popover("hide");
    	try{
    		var today = new Date().getTime();
    		var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
        	if(deviceData.deviceId!=""){
        		var deviceIds = deviceData.deviceId.split(",");
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
        		mui.toast("没有连接的设备");
        	}
    	}catch(e){
    		mui.toast("添加"+e.message);
    	}
    }
    var unbindLock = function(keylist, deviceData) {
    	mui("#forward").popover("hide");
    	try{
           	//获取操作凭证  {'deviceId':'设备ID','type':'操作凭证类型 1:绑定设备，2:解绑设备','connType':'设备类型:null/blue 表示蓝牙设备 ，lan局域网设备'}
           		var params = {
    					openId: ModelFactory.getOpenId(),
    					lockId: deviceData.lockId,
    					ticket: "",
    					deviceId:""
    				};
           		ModelFactory.confirmBtn("确认要解绑锁芯吗","",function(e){
           			if(e.index==1){
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
           						mui.toast('解绑锁失败 1: ' + resp.data.result_code.toString());
           					}
           				}, function(resp) {
           					mui.toast('解绑锁失败 2: ' + resp.data.result_code.toString());
           				});
           			}
           		});
    	}catch(e){
    		mui.toast("解绑："+e.message);
    	}
    }
    
    var deleteKey = function(deviceData,data) {
    	mui("#forward").popover("hide");
		op = 'deleteKey';
		seletcedKey =data;
		selectedDeviceInfo = deviceData;
		try{
			if(deviceData.deviceId==""){
				mui.toast("还没有连接的设备");
				return ;
			}
			ModelFactory.confirmBtn("确认要删除钥匙吗","",function(e){
				if(e.index==1){
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
				}
			});
		}catch(e){
			mui.toast("删除异常："+e.message);
		}
	};

	var enableKey = function(deviceData,data) {
		mui("#forward").popover("hide");
		try{
			op = 'enableKey';
			seletcedKey = data;
			selectedDeviceInfo = deviceData;
			var isok = ModelFactory.compareDeviceId(selectedDeviceInfo.deviceId,data.keyTb.deviceId);
			flag = isok;
			if(isok){
				//启用自己
				keySelfState = 1;
			    selectedDeviceInfo.deviceId = data.keyTb.deviceId;
			    var msg4 = String.fromCharCode(0x00)+String.fromCharCode(0x08)+String.fromCharCode(parseInt(keySelfState,10));
			    //启用钥匙状态 1
				ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg4));
			}else{
				var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
				if(deviceData.deviceId==""){
					mui.toast("还没有连接的设备");
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
			mui.toast("启用异常:"+e.message);
		}
	}
	
	var disableKey = function(deviceData,data) {
		mui("#forward").popover("hide");
		try{
			op = 'disableKey';
			seletcedKey =data;
			selectedDeviceInfo = deviceData;
			ModelFactory.confirmBtn("确认要禁用"+data.keyTb.keyId+"钥匙吗","",function(e){
				if(e.index==1){
					var isok = ModelFactory.compareDeviceId(selectedDeviceInfo.deviceId,data.keyTb.deviceId);
					flag = isok;
					if(isok==false){
						//蓝牙管理钥匙禁用别的钥匙
						var deviceIds = (selectedDeviceInfo.deviceId+"").split(",");
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
						
					}else if(isok==true){
						//蓝牙管理钥匙禁用自己  
						keySelfState = 2;
						selectedDeviceInfo.deviceId = data.keyTb.deviceId;
						var msg4 = String.fromCharCode(0x00)+String.fromCharCode(0x08)+String.fromCharCode(parseInt(keySelfState,10));
						//禁用钥匙状态 2
						ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg4));
					}
				}
			});
			
		}catch(e){
			mui.toast("禁用："+e.message);
		}
	};
	 /**
	 * 创建消息
	 * {@link cmdId}} 格式 0x 开头的消息  比如 0x01  0x02 0x03 0x04 ...
	 * {@link Position} 位置号  为0 不存在
	 * {@link keyId} 钥匙id  为null  或 "" 不存在
	 */
	var createmsg = function(cmdId,position,type,keyId){
		 var msg = String.fromCharCode(0x00)+String.fromCharCode(cmdId);
		if(position>0){
			msg += String.fromCharCode(parseInt(position,10));
		}
		if(type>0){
			msg += String.fromCharCode(parseInt(type,10));
		}
		if(keyId!=null&&keyId!=""){
			var keyIdData = UtilFactory.stringToBytes(keyId);
			for (var i=0; i<keyIdData.length; i++) {
				msg += String.fromCharCode(keyIdData[i]);
			}
		}
		return msg;
	}
	$scope.$on('transfer.changeSelfKeyOK',function(event,data){
		 var params = {
					deviceId: ModelFactory.getDefaultDeviceId(),
					keyId: seletcedKey.keyId,
					keyState: keySelfState,
					flag:flag,
					lockId:selectedDeviceInfo.lockId,
					openId:ModelFactory.getOpenId()
				};
		         
				$http({
					method: 'GET',
					url: '/changeKeyStatus',
					params: params
				}).then(function(resp) {
					if (resp.data.result_code == 0) {
						mui.toast('钥匙' + seletcedKey.keyTb.keyId + '启用成功');
						//修改devicelist 对象	
						seletcedKey.keyTb.state = keySelfState;
						ModelFactory.updateKeyState(seletcedKey,selectedDeviceInfo.lockId);
						$scope.deviceList = ModelFactory.getDeviceList();
						$scope.$apply();
					} else {
						mui.toast('启用钥匙失败：error: ' + resp.data.result_code.toString());
					}
				}, function(resp) {
					
				});
	})
	
	$scope.$on('transfer.changeSelfKeyFailed',function(event,data){
		mui.toast(data);
	})
	
    $scope.$on('transfer.deletekeyOK', function(event, data) { 
    	try{
    	var openId = ModelFactory.getOpenId()==null?"":ModelFactory.getOpenId();
    	var params = {
			lockId: selectedDeviceInfo.lockId,
			keyId: seletcedKey.keyId,
			openId:openId
		};
		$http({
			method: 'GET',
			url: '/removeKey',
			params: params
		}).then(function(resp) {
			mui.toast("remove:"+JSON.stringify(resp));
			if (resp.data.result_code == 0) {
				mui.toast('钥匙' + seletcedKey.keyId + '删除成功');
				//修改devicelist 对象
				ModelFactory.removeKey(seletcedKey,selectedDeviceInfo.lockId);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			} else {
				mui.toast('删除钥匙失败：error: ' + resp.data.result_code.toString());
			}
		}, function(resp) {
			
		});
		}catch(e){
			mui.toast("删除失败"+JSON.stringify(e));
		}
    });
    $scope.$on('transfer.enablekeyOK', function(event, data) {
    	try{
        var params = {
			deviceId: ModelFactory.getDefaultDeviceId(),
			keyId: seletcedKey.keyId,
			keyState: "1",
			flag:flag,
			lockId:selectedDeviceInfo.lockId,
			openId:ModelFactory.getOpenId()
		};
         
		$http({
			method: 'GET',
			url: '/changeKeyStatus',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				mui.toast('钥匙' + seletcedKey.keyTb.keyId + '启用成功');
				//修改devicelist 对象	
				seletcedKey.keyTb.state = "1";
				ModelFactory.updateKeyState(seletcedKey,selectedDeviceInfo.lockId);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			} else {
				mui.toast('启用钥匙失败：error: ' + resp.data.result_code.toString());
			}
		}, function(resp) {
			
		});
		}catch(e){
			mui.toast("启用失败"+JSON.stringify(e));
		}
    });
    $scope.$on('transfer.disablekeyOK', function(event, data) {  
    	try{
        var params = {
			deviceId: ModelFactory.getDefaultDeviceId(),
			keyId: seletcedKey.keyId,
			keyState: "2",
			flag:flag,
			lockId:selectedDeviceInfo.lockId,
			openId:ModelFactory.getOpenId()
		};
        
		$http({
			method: 'GET',
			url: '/changeKeyStatus',
			params: params
		}).then(function(resp) {
			if (resp.data.result_code == 0) {
				mui.toast('钥匙' + seletcedKey.keyTb.keyId + '禁用成功');
				//修改devicelist 对象
				seletcedKey.keyTb.state = "2";
				ModelFactory.updateKeyState(seletcedKey,selectedDeviceInfo.lockId);
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			} else {
				mui.toast('禁用钥匙失败：error: ' + resp.data.result_code.toString());
			}
		}, function(resp) {
			
		});
		}catch(e){
			mui.toast("禁用失败"+JSON.stringify(e));
		}
    });
    
	$scope.$on('transfer.deletekeyFailed', function(event, data) {  
        mui.toast(data);
    });

    $scope.$on('transfer.enablekeyFailed', function(event, data) {  
        mui.toast(data);
    });

    $scope.$on('transfer.disablekeyFailed', function(event, data) {  
        mui.toast(data);
    });
	
	var keyItem = function(device,key){
		ModelFactory.setSelectedDevice(device);
		ModelFactory.setSelectedKey(key);
		mui("#forward").popover("hide");
		$state.go("keyitem");
	}
	 
	
	$scope.edit = function(params1,params2){
		try{
		var buttons = [];
		if(params1 instanceof Array){
			//需要锁芯操作表按钮
				buttons = commonObj.locksheets;
			 
		}else{
			buttons = params2.keyTb.state=="1"&&params2.state=="1"?commonObj.keystarted:commonObj.keystop;
			//mui.alert('roleId='+JSON.stringify(params1));
			if(params1.roleId!=1){
	    	     buttons.splice(3,1);
	    	 }  	
		}
		$scope.$parent.buttons = buttons;
		$scope.$parent.params1 = params1;
		$scope.$parent.params2 = params2;
		mui("#forward").popover("show");
		}catch(e){
			mui.toast("error:"+JSON.stringify(e));
		}
	}
	 
	$scope.$parent.execute = function(funcName,params1,params2){
		var fn =  eval(funcName);  
		fn.call(this,params1,params2);
	}
	
	var remoteAuth =function(deviceData,data){
		var key = data;
		ModelFactory.setSelectedDevice(deviceData);
		ModelFactory.setSelectedKey(key);
		mui("#forward").popover("hide");
		$state.go("remotekey");
	}
	
	var updateLock = function(keylist, deviceData){
		mui("#forward").popover("hide");
		var btnArray = ['取消', '确定'];
		var lock = deviceData.locktb;
		mui.prompt('请输入锁芯名称',lock.lockName, '', btnArray, function(e) {
			if (e.index == 1) {
				lock.lockName = e.value;
				var params = {
						"lock":JSON.stringify(lock),
				};
				$http({
					method: 'GET',
					url: '/updateLock',
					params: params
				}).then(function(resp){
					if (resp.data.result_code == 0) {
						mui.toast('锁芯' + lock.lockName + '修改成功');
						//修改devicelist 对象
						ModelFactory.updateLock(lock);
						$scope.deviceList = ModelFactory.getDeviceList();
						$scope.$apply();
					} else {
						mui.toast('修改锁芯失败：error: ' + resp.data.result_code.toString());
					}
				},function(e){
					
				})
			}
		})
	}
	
}])
.controller('keyitemController',['$scope','UtilFactory','ModelFactory','$timeout','UtilsService','$state','$http',function($scope,UtilFactory,ModelFactory,$timeout,UtilsService,$state,$http){
	var seletcedKey = ModelFactory.getSelectedKey();
	 var selectDevice =  ModelFactory.getSelectedDevice();
	 var version =seletcedKey.keyTb.version;
	 if(version!=null){
		 seletcedKey.keyTb.softwareVersion = version.substring(0,1)+"."+version.substring(1,2);
		 seletcedKey.keyTb.firmwareVersion = version.substring(2,3)+"."+version.substring(3,4);
	 }
	try{
		var isok = ModelFactory.compareDeviceId(selectDevice.deviceId,seletcedKey.keyTb.deviceId);
		if(seletcedKey.keyTb.type==1&&isok){
			var msg = String.fromCharCode(0x00)+String.fromCharCode(0x06);
			ModelFactory.sendDataToWxDevice(seletcedKey.keyTb.deviceId,window.btoa(msg));
		}
		//获取电量
		$scope.$on('transfer.batteryOk',function(event,data){
			if(data!=""){
				var battery = parseInt(data.substring(4,8),16);
				seletcedKey.keyTb.battery = battery/1000;
				version = data.substring(8,12);
				seletcedKey.keyTb.softwareVersion = version.substring(0,1)+"."+version.substring(1,2);
				seletcedKey.keyTb.firmwareVersion = version.substring(2,3)+"."+version.substring(3,4);
				if(seletcedKey.keyTb.version!=version){
					seletcedKey.keyTb.version = version;
					var params = {
							"key":JSON.stringify(seletcedKey.keyTb),
					};
					$http({
						method: 'GET',
						url: '/updateKey',
						params: params
					}).then(function(resp){
						if (resp.data.result_code == 0) {
							ModelFactory.updateKey(seletcedKey.keyTb);
						} else {
							mui.toast('修改钥匙失败：error: ' + resp.data.result_code.toString());
						}
					},function(e){
						
					})
				}
			}else{
				seletcedKey.keyTb.battery = "---";	
			}
			ModelFactory.setSelectedKey(seletcedKey);
		})
	}catch(e){
		mui.toast("获取 固件信息失败"+e.message);
	}
	 
	$timeout(function(){
		$scope.key = ModelFactory.getSelectedKey();
		$scope.device = ModelFactory.getSelectedDevice();
		$scope.$apply();  
	},200); 
	$scope.isok = false;
	$scope.editKey = function(isok){
		$scope.isok = !isok;
	}
	
	$scope.updateKey = function(key){
		var params = {
				"key":JSON.stringify(key.keyTb),
		};
		$http({
			method: 'GET',
			url: '/updateKey',
			params: params
		}).then(function(resp){
			if (resp.data.result_code == 0) {
				mui.toast('钥匙' + key.keyTb.keyId + '修改成功');
				//修改devicelist 对象
				ModelFactory.updateKey(key.keyTb);
				$scope.isok = false;
			} else {
				mui.toast('修改钥匙失败：error: ' + resp.data.result_code.toString());
			}
		},function(e){
			
		})
	}
	
}])
.controller('addkeyController',['$scope','ModelFactory','$http','$state','UtilsService','UtilFactory',function($scope,ModelFactory,$http,$state,UtilsService,UtilFactory){
	$scope.key = ModelFactory.getSelectedKey();
	var op = "";
	var lockId = "";
	var selectedDeviceInfo = {};
	$scope.confirmAddBlueKey = function(key){
		op = "confirmaddkey";
		var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
		mui.toast(key.deviceId);
		if(key.type==1){
			selectedDeviceInfo.deviceId = key.deviceId;
			ModelFactory.sendDataToWxDevice(key.deviceId,window.btoa(msg));
		}else{
			 var deviceListFromWX = ModelFactory.getDeviceListFromWX();
			 for (var i = 0; i < deviceListFromWX.length; i++) {
				  if(deviceListFromWX[i].state=="connected"){
					  selectedDeviceInfo.deviceId =  deviceListFromWX[i].deviceId;
					 wx.invoke('sendDataToWXDevice', {"deviceId":  selectedDeviceInfo.deviceId,"base64Data":window.btoa(msg)}, function(res) {
						    if (res.err_msg =="sendDataToWXDevice:ok") {  
						    }
						});
				  }
				}
		}
		$scope.$on('transfer.keyState',function(event,data){
			lockId = data;//要把lockId 存放到全局变量处以便其他的controller中使用
			if(lockId==""){
				mui.toast("连接超时");
				return;
			}
			if(op=="confirmaddkey"){
				var msg2 = createmsg(0x02,0,key.type,key.keyId);
				 UtilsService.alertMsg(msg2);
				 ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg2));
			}
	    		
		})
		$scope.$on('transfer.addkeyOK',function(event,data){
			if(op=="confirmaddkey"){
				var params = {
						lockId: lockId,
						keyId: key.keyId,
						position:data,
						openId:ModelFactory.getOpenId(),
						ticket:"",
				};
				$http({
					method: 'GET',
					url: '/addKey',
					params: params
				}).then(function(resp){
					if (resp.data.result_code == 0) {
						mui.toast('钥匙' + key.keyId + '添加成功');
						$http({method: 'GET',url: '/devicelist',params: {openId: ModelFactory.getOpenId()}}).then(function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$state.go("index");
						}, function(resp) {
							mui.toast("锁列表失败1");
						});
					} else {
						mui.toast('添加钥匙失败：error: ' + resp.data.result_code.toString());
					}
				},function(e){
					
				})
			}
		})
		
		$scope.$on('transfer.addkeyFailed', function(event, data) {  
			mui.toast(data);
	    });
		
		$scope.$on('transfer.addedKey', function(event, data) {  
			if(op=="confirmaddkey"){
				var params = {
						lockId: lockId,
						keyId: key.keyId,
						position:data,
						openId:ModelFactory.getOpenId(),
						ticket:"",
				};
				$http({
					method: 'GET',
					url: '/addKey',
					params: params
				}).then(function(resp){
					if (resp.data.result_code == 0) {
						mui.toast('钥匙' + key.keyId + '添加成功');
						$http({method: 'GET',url: '/devicelist',params: {openId: ModelFactory.getOpenId()}}).then(function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$state.go("index");
						}, function(resp) {
							mui.toast("锁列表失败1");
						});
					} else {
						mui.toast('添加钥匙失败：error: ' + resp.data.result_code.toString());
					}
				},function(e){
					
				})
			}
	    });
	}
 
	 /**
	 * 创建消息
	 * {@link cmdId}} 格式 0x 开头的消息  比如 0x01  0x02 0x03 0x04 ...
	 * {@link Position} 位置号  为0 不存在
	 * {@link keyId} 钥匙id  为null  或 "" 不存在
	 */
	var createmsg = function(cmdId,position,type,keyId){
		 var msg = String.fromCharCode(0x00)+String.fromCharCode(cmdId);
		if(position>0){
			msg += String.fromCharCode(parseInt(position,10));
		}
		if(type>0){
			msg += String.fromCharCode(parseInt(type,10));
		}
		if(keyId!=null&&keyId!=""){
			var keyIdData = UtilFactory.stringToBytes(keyId);
			for (var i=0; i<keyIdData.length; i++) {
				msg += String.fromCharCode(keyIdData[i]);
			}
		}
		return msg;
	}
	
}])

.controller("configController",['$scope','ModelFactory','$state','$http',function($scope,ModelFactory,$state,$http){
	
	var lockId = "";
	var selectedDeviceInfo = {};
	var op = "";
	
	$scope.$on('transfer.keyState', function(event, data) {  
    	//微信 设备锁与钥匙交互 之一 ——添加钥匙  第三步。正确插图管理钥匙，微信启动二维码添加钥匙
		try{
			if(data==""){
				mui.toast("未插入蓝牙管理钥匙");
				return;
			}
			lockId = data;
			$http.get("/lockCountByLockId?lockId=" +lockId).then(function(resp) {
	    		var count = resp.data.data;
	    		 if(count>0){
		    		//确保deviceid中没有非法的字符 
		    		selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.replace(/,/g,"");
		    	    mui.toast(selectedDeviceInfo.deviceId);
		    		if(op == 'lockInit'){
					var msg5 = String.fromCharCode(0x00)+String.fromCharCode(0x07);;
					ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg5));
				} 
    		 }else{
    			 mui.toast("锁芯不存在");
    		 }
			}, function(resp) {
				mui.toast("锁芯不存在");
			});
    	}catch(e){
    		mui.toast("钥匙状态改变错误："+e.message);
    	}
    });
	
	
	$scope.syncKey = function(keyList,deviceData){
		op='syncKey';
		var deviceIds = (selectedDeviceInfo.deviceId+"").split(",");
		for(var i=0;i<deviceIds.length;i++){
			if(deviceIds[i]!=""&&deviceIds[i]!=","){
				selectedDeviceInfo.deviceId = deviceIds[i];
				if(selectedDeviceInfo.deviceId.indexOf(",")>-1){ 
					selectedDeviceInfo.deviceId = selectedDeviceInfo.deviceId.substring(0,selectedDeviceInfo.deviceId.indexOf(","));
				}
				var msg5 = createmsg(0x07,0,0,"");
				ModelFactory.sendDataToWxDevice(selectedDeviceInfo.deviceId,window.btoa(msg5));
			}
		}
	}
	
	$scope.$on('syncKeyOk',function(event,data){
		
	})
	$scope.$on('syncKeyFailed',function(event,data){
		mui.toast(data);
	})
	
	$scope.saoYisao = function(){
		wx.scanQRCode({
		    needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
		    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
		    success: function (res) {
	    		 
			}
		});
	}
	$scope.lockInit = function(){
		ModelFactory.confirmBtn("确认要初始化锁芯吗","",function(e){
			if(e.index==1){
				op = 'lockInit';
				var noConnCount = 0;
				var msg = String.fromCharCode(0x00)+String.fromCharCode(0x01);
				 var deviceListFromWX = ModelFactory.getDeviceListFromWX();
				 for (var i = 0; i < deviceListFromWX.length; i++) {
					  if(deviceListFromWX[i].state=="connected"){
						  selectedDeviceInfo.deviceId =  deviceListFromWX[i].deviceId;
						 wx.invoke('sendDataToWXDevice', {"deviceId":  selectedDeviceInfo.deviceId,"base64Data":window.btoa(msg)}, function(res) {
							    if (res.err_msg =="sendDataToWXDevice:ok") {  
							       mui.toast("发送成功");
							    }
							});
					  }else{
						  noConnCount+=1;
					  }
					}
				 if(noConnCount==deviceListFromWX.length){
					 mui.toast("未连接蓝牙设备");
					 return;
				 }
			}
		});
	}
	
	 $scope.$on('transfer.initLockOK', function(event, data) {
		    var params = {
					openId: ModelFactory.getOpenId(),
					lockId: lockId,
			};
			$http({
				method: 'GET',
				url: '/initLock',
				params: params
			}).then(function(resp) {
				if (resp.data.result_code == 0) {
					 UtilsService.requestHttp({method: 'GET',url: '/devicelist',params: {openId: ModelFactory.getOpenId()}},function(resp) {
							ModelFactory.setDeviceListFromServer(resp.data.data);
							$scope.deviceList = ModelFactory.getDeviceList();
							$scope.$apply();
						}, function(resp) {
							mui.toast("锁列表失败1");
						});
				} else {
					mui.toast('初始化锁失败 1: ' + resp.data.result_code.toString());
				}
			}, function(resp) {
				mui.toast('初始化锁失败 2: ' + resp.data.result_code.toString());
			});
	 })
	
	  $scope.$on('transfer.initLockFailed', function(event, data) {
		 mui.toast(data);
	 })
	 
	 $scope.jump = function(stateStr){
		 $state.go(stateStr);
	 }
	 
}])
.controller("wuyeController",['$scope','ModelFactory','$state','$http',function($scope,ModelFactory,$state,$http){
  $scope.scanDevice = function(){
	  wx.invoke('openWXDeviceLib', {'connType':'lan'}, function(res) {
		  console.log('openWXDeviceLib',res);
	  });
	  wx.invoke('startScanWXDevice', {'connType':'blue',btVersion:'ble'}, function(res) {
		  mui.toast('startScanWXDevice:'+JSON.stringify(res));
	});
	  wx.on('onScanWXDeviceResult',function(res){
			// mui.toast("设备信息："+JSON.stringify(res.devices.length));
			  var keyList = res.devices;
			 // mui.toast(res.devices[0].deviceId);
			  $scope.keyList = keyList;
			  $scope.$apply();
		})
		  wx.on('onWXDeviceBindStateChange',function(res){
			mui.toast("设备："+JSON.stringify(res));
		})
  }
  
  $scope.stopDevice = function(){
	  wx.invoke('openWXDeviceLib', {'connType':'lan'}, function(res) {
		  console.log('openWXDeviceLib',res);
		  });
	  wx.invoke('stopScanWXDevice', {'connType':'blue',btVersion:'ble'}, function(res) {
		  mui.toast('startScanWXDevice:'+JSON.stringify(res));
	});
  }
  $scope.connect = function(key){
	  
	  wx.invoke('getWXDeviceInfos', {'connType':'blue',btVersion:'ble'}, function(res) {
		  ModelFactory.setDeviceListFromWX(res.deviceInfos);
		 if(res.deviceInfos.length==0){
			 //未绑定时
			 wx.invoke("getWXDeviceTicket",{'deviceId':key.deviceId,'type':'1', 'connType':'lan'},function(res){
				  	var ticket = res.ticket;
				  	var params= {
				  		 "openId":ModelFactory.getOpenId(),
				  		 "deviceId":key.deviceId,
				  		 "ticket":ticket
				  	}
				  	$http({
						method: 'GET',
						url: '/bindkey',
						params: params
					}).then(function(resp) {
						mui.toast(resp.data.result_code);
						if (resp.data.result_code == 0) {
							 wx.invoke('connectWXDevice', {'deviceId':key.deviceId, 'connType':'blue'}, function(res) {
								  mui.toast('connectWXDevice:'+JSON.stringify(res));
							});
						} else{
							mui.toast("连接失败，请重试");
						}
					}, function(resp) {
						mui.toast('绑定失败2: ' + resp.data.result_code.toString());
					});
			  })
		 }else{
			 //已绑定有列表时
			 wx.invoke('connectWXDevice', {'deviceId':key.deviceId, 'connType':'blue'}, function(res) {
				  mui.toast('connectWXDevice:'+JSON.stringify(res));
			});
		 }
	});
  }
}])
.controller("RemoteKeyController",["$scope","$http","ModelFactory",function($scope,$http,ModelFactory){
	var key = ModelFactory.getSelectedKey();
	$scope.key = key;
	 
	$scope.confirmAuthKey =function(time){
		$http({
			method: 'GET',
			url: '/remoteAuth',
			params: {
				keyId:key.keyId,
				time:time*3600,
				authOpenId:ModelFactory.getOpenId()
			}
		}).then(function(resp){
			if (resp.data.result_code == 0) {
				mui.toast('钥匙' + key.keyId + '远程授权成功');
				$scope.deviceList = ModelFactory.getDeviceList();
				$scope.$apply();
			} else {
				mui.toast('修改锁芯失败：error: ' + resp.data.result_code.toString());
			}
		},function(e){
			
		})
	}
}])	
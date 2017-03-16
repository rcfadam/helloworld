var commonObj = {
		"appId":"",// 微信公众号标识
		"debug":true, //是否是 debug模式   true 是 false 不是。debug 模式会出现js中提示
		"locksheets":[// 锁操作表
		              {
		            	  title:"添加钥匙",
		            	  funcName:"addKey",
		              },
		              {
		            	  title:"修改锁",
		            	  funcName:"updateLock",
		              },
		              /*{
		            	  title:"初始化",
		            	  funcName:"lockInit",
		              },*/
		              {
		            	  title:"解绑锁",
		            	  funcName:"unbindLock",
		              }
		   ],
		  "keystop":[ // 钥匙已禁用
		               {
		            	   title:"删除钥匙",
			               funcName:"deleteKey",
		               },
		               {
		            	   title:"启用钥匙",
			               funcName:"enableKey",
		               },
		               {
		            	   title:"查看详情",
			               funcName:"keyItem",
		               },
		               {
		            	   title:"远程授权",
		            	   funcName:"remoteAuth"
		               }
		  ],
		  "keystarted":[ // 钥匙已启用
			               {
			            	   title:"删除钥匙",
				               funcName:"deleteKey",
			               },
			               {
			            	   title:"禁用钥匙",
				               funcName:"disableKey",
			               },
			               {
			            	   title:"查看详情",
				               funcName:"keyItem",
			               },
			               {
			            	   title:"远程授权",
			            	   funcName:"remoteAuth"
			               }
			  ],
}
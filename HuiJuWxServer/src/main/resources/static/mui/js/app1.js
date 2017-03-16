angular.module('MyApp',['ui.router','MyApp.controllers','MyApp.services'])
.config(function($stateProvider,$urlRouterProvider){
	$urlRouterProvider.otherwise('/index');
	$stateProvider.state('index',{
		url:'/index',
		views:{
			'content':{
				templateUrl:'views/locklist.html',
				controller:'locklistController'
			},
			'setting':{
				templateUrl:'views/setting.html',
				controller:'settingController'
			} 
		}
	}).state('keyitem',{
		url:'/keyitem',
		templateUrl:'views/key_item.html',
		controller:'keyitemController'
	}).state('waitAddKey',{
		url:'/addbluekey',
		templateUrl:'views/addkey.html',
		controller:'addkeyController'
	}).state('configPage',{
		url:"/configPage",
		templateUrl:"views/config.html",
		controller:"configController"
	}).state('wuye',{
		url:"/wuye",
		templateUrl:"views/scanwxdevice.html",
		controller:"wuyeController"
	}).state('remotekey',{
		url:"/remotekey",
		templateUrl:"views/remote_key.html",
		controller:"RemoteKeyController"
	});
})
.directive("timepicker",function(){
	 return {
		    scope:false,//创建指令自己的独立作用域，与父级毫无关系
	        controller: ["$scope", "$element", function ($scope, $element) {
	        	
	            $element.bind("click", onTouchEnd);
	            function onTouchEnd(event) {
	            	try{
	            		 var method = $element.attr("timepicker");
	 	                var optionsJson = $element.attr('data-options') || '{}';
	 					var options = JSON.parse(optionsJson);
	 					var id = $element.attr('id');
	 					var picker = new mui.DtPicker(options)
	 					picker.show(function(rs) {
	 						$scope.time = rs.text;
	 						picker.dispose();
	 					});
	 					  $scope.$apply();
		        	}catch(e){
		        		mui.toast("error"+JSON.stringify(e));
		        	}
	            }

	        }]
	    }	
})
.run(function(UtilsService){
	UtilsService.init();
	mui.init({
		swipeBack: true,
	});
	/*mui.init({
		swipeBack: true,
		preloadPages:[
           {
             url:"index.html",
             id:"index",
             subpages:[{
            	 url:"views/setting.html",
            	 id:"setting",
             },{
            	 url:"views/locklist.html",
            	 id:"locklist"
             }]//预加载页面的子页面
           }
         ],
         preloadLimit:5,//预加载窗口数量限制(一旦超出,先进先出)默认不限制
         pullRefresh:{
        	 container:"#devicelist",
        	 down:{
        		 contentdown : "下拉可以刷新",//可选，在下拉可刷新状态时，下拉刷新控件上显示的标题内容
        	     contentover : "释放立即刷新",//可选，在释放可刷新状态时，下拉刷新控件上显示的标题内容
        	     contentrefresh : "正在刷新...",//可选，正在刷新状态时，下拉刷新控件上显示的标题内容
        	     callback:UtilsService.pullDownRefresh,
        	 },
        	 up:{
        		 contentrefresh : "正在加载...",//可选，正在加载状态时，上拉加载控件上显示的标题内容
        	     contentnomore:'没有更多数据了',//可选，请求完毕若没有更多数据时显示的提醒内容；
        	     callback :UtilsService.pullUpRefresh //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取
        	 }
         }
	});*/
});
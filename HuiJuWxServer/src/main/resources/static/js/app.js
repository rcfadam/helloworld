angular.module('myApp', [/*'ui.router'*/'ionic', 'myApp.controllers', 'myApp.services'/*,'ngTouch'*/])
.config(function($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise("/devicelist");

	$stateProvider
     .state('devicelist', {
         url: '/devicelist',
         templateUrl: 'templates/devicelist.html', 
         //templateUrl:'templates/locklist.html',
         controller: "devicelist"
     })
     .state('keyitem',{
    	 url:'/keyitem',
    	 templateUrl:'templates/key_item.html',
    	 controller:'keyitem'
     })
})
.directive("ngTouchstart", function () {
    return {
        controller: ["$scope", "$element", function ($scope, $element) {

            $element.bind("touchstart", onTouchStart);
            function onTouchStart(event) {
                var method = $element.attr("ng-touchstart");
                $scope.$apply(method);
            }

        }]
    }
})
.directive("ngTouchmove", function () {
    return {
        controller: ["$scope", "$element", function ($scope, $element) {
        	var startPageX;
            $element.bind("touchstart", onTouchStart);
            function onTouchStart(event) {
               $element.bind("touchmove", onTouchMove);
               $element.bind("touchend", onTouchEnd);
               console.log(event.touches[0].pageX);
               $element.css('left', '0px'); // close em all
               $(event.currentTarget).addClass('open');
               startPageX = event.targetTouches[0].pageX; // anchor point
               event.preventDefault();
                 
            }
            function onTouchMove(event) {
            	var method = $element.attr("ng-touchmove");
                console.log(event);
                var change = event.targetTouches[0].pageX - startPageX;
               change = Math.min(Math.max(-100, change), 0) ;// restrict to -100px left, 0px right
               event.currentTarget.style.left = change + 'px';
               if (change < -10) event.preventDefault();  // disable scroll once we hit 10px horizontal slide
               $scope.$apply(method);
            }
            function onTouchEnd(event) {
            	console.log(event);
            	 var left = parseInt(event.currentTarget.style.left);
                 var new_left;
                 if (left < -35) {
                     new_left = '-200px';
                 } else if (left > 35) {
                     new_left = '200px';
                 } else {
                     new_left = '0px';
                 }
                 // e.currentTarget.style.left = new_left
                 $(event.currentTarget).animate({left: new_left}, 100);
                event.preventDefault();
                $element.unbind("touchmove", onTouchMove);
                $element.unbind("touchend", onTouchEnd);
            }

        }]
    }
})
.directive("ngTouchend", function () {
    return {
        controller: ["$scope", "$element", function ($scope, $element) {

            $element.bind("touchend", onTouchEnd);
            function onTouchEnd(event) {
                var method = $element.attr("ng-touchend");
                $scope.$apply(method);
            }

        }]
    }
})
.directive("ngZhankai",function(){
	return {
		link:function ($scope, $element,$attr) {
			 var href = "";
			 $attr.$observe('href', function() {
				 href = $element.attr("href");
				 var strs = href.split(",");
				 var show = strs[1];
				 href = strs[0];
				 if(show==true||show=="true"){
					 toaster.info("href="+href);
					 $(href).collapse("toggle");
				 }else{
					 toaster.info("href!="+href);
					 $(href).collapse({toggle:false});
				 }
		     });
			  $element.bind("click", onTouchEnd);
	            function onTouchEnd(event) {
	            	href = $element.attr("href");
	            	 var strs = href.split(",");
					 href = strs[0];
	     			$(href).on("show.bs.collapse",function(){
	     		  		    $scope.device.show = true;
	     		  		    $scope.$apply();
	     		  	 })
	     		  	  $(href).on("hide.bs.collapse",function(){
	     		  		  $scope.device.show = false;
	     		  		  $scope.$apply();
	     		  	  })
				   $(href).collapse("toggle");
	            }
		}
	}
});
 
/*.config(['$ionicConfigProvider','$httpProvider', function($ionicConfigProvider,$httpProvider) {
	  // other values: top
	$httpProvider.interceptors.push(function($rootScope) {
	    return {
	      request: function(config) {
	        $rootScope.$broadcast('loading:show')
	        return config
	      },
	      response: function(response) {
	        $rootScope.$broadcast('loading:hide')
	        return response
	      }
	    }
	  })
}]).run(function($ionicPlatform, $http,$ionicLoading,$rootScope) {
	  $rootScope.$on('loading:show', function() {
		    $ionicLoading.show({
                content: '加载中...',
                animation: 'fade-in',
                showBackdrop: true,
                minWidth: 200,
                showDelay: 10
            })
		  })

		  $rootScope.$on('loading:hide', function() {
		    $ionicLoading.hide()
		  })
 });*/
 
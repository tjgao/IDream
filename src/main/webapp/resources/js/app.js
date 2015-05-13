var app = angular.module('starter', ['ionic','dreamControllers','dreamServices']);

app.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    if(window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }

  });
})

app.config(function($stateProvider, $urlRouterProvider){
	$stateProvider
    .state('upload', {
    	url:'/upload',
    	views:{
    		'mainView':{
    			templateUrl:'templates/upload.html',
    			controller:'upController'
    		}
    	}
    });
	
    $stateProvider
    .state('entry', {
        url:'/entry',
        abstract:true,
        views: {
            'mainView':{
            	templateUrl:'templates/entry.html',
                controller:'topController'
            }
        }
    })
    .state('entry-activities', {
        parent:'entry',
        url:'/activities',
        views: {
            'tabView': { 
                templateUrl: 'templates/tab-activities.html',
                controller:'topController'
            }
        }
    })
    .state('entry-interested', {
        parent:'entry',
        url:'/interestedImg/:uId',
        views: {
            'tabView': { 
            	templateUrl: 'templates/tab-interestedImg.html',
            	controller:'followingImageController'
            }
        }
    });

    $stateProvider
    .state('activity',{
        url:'/activity/:aId', 
        views: {
            'mainView':{
                templateUrl:'templates/activity.html',
                controller:'activityController'
            }
        }
    })

    ;
    
    $stateProvider
    .state('image',{
        url:'/image/:imgId',
        views: {
            'mainView' : {
                templateUrl:'templates/image.html',
                controller:'imgController'
            }
        }
    });


    $stateProvider
    .state('people',{
        url:'/people/:uId',
        views: {
            'mainView' : {
                templateUrl: 'templates/people.html',
                controller:'peopleController'
            }
        }
    })

    .state('people.activities',{
        url:'/activities',
        views: {
            'statusView':{
                templateUrl: 'templates/activities.html'
            }
        }
    })

    .state('people.photos', {
        url:'/photos',
        views: {
            'statusView':{
                templateUrl:'templates/thumbs.html'
            }
        }
    })

    .state('people.interested',{
        url:'/interested',
        views: {
            'statusView':{
                templateUrl:'templates/interested.html'
            }
        }
    })

    .state('people.fans',{
        url:'/fans',
        views: {
            'statusView':{
                templateUrl: 'templates/fans.html'
            }
        }
    })
    ;

    $urlRouterProvider.otherwise("/entry/activities");
});



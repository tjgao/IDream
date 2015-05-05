// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
var app = angular.module('starter', ['ionic','dreamControllers','dreamServices']);

app.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
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
    .state('entry', {
        url:'/entry',
        abstract:true,
        views: {
            'mainView':{
            	templateUrl:'templates/entry.html'
            }
        }
    })
    .state('entry-activities', {
        parent:'entry',
        url:'/activities',
        views: {
            'tab-activities': { 
                templateUrl: 'templates/tab-activities.html',
                controller:'topController'
            }
        }
    })
    .state('entry-interested', {
        parent:'entry',
        url:'/interestedImg/:uId',
        views: {
            'tab-interested': { 
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
    .state('activity.latest', {
    	url:'/latest',
    	views: {
    		'actThumbView': {
    			templateUrl:'templates/thumbs.html',
    			controller:'actLatestController'
    		}
    	}
    })
    .state('activity.hottest',{
    	url:'/hottest',
    	views: {
    		'actThumbView':{
    			templateUrl:'templates/thumbs.html',
    			controller:'actHottestController'
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
                templateUrl:'templates/thumbs.html',
                controller:'pplPhotosController'
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



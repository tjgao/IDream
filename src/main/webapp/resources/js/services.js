var dreamServices = angular.module('dreamServices',['ngResource']);

dreamServices.factory('userService', function(){
    var _user = g_uId;
    return {
        getUser : function() {
            return _user;
        }
    }
});

dreamServices.factory('ActivityResource', function($resource) {
    return $resource('api/activity/:id', { id:'@id'});
});

dreamServices.factory('ImageResource', function($resource) {
    return $resource('api/image/:id',{ id:'@id'});
});

dreamServices.factory('CommentResource', function($resource) {
    return $resource('api/image/:id/comment',{id:'@id'});
});

dreamServices.factory('UserActivitiesResource', function($resource) {
    return $resource('api/user/:id/activity', {id:'@id'});
});

dreamServices.factory('UserStatusResource', function($resource){
    return $resource('api/user/:id/status', {id:'@id'});
});

dreamServices.factory('UserFollowingResource', function($resource) {
    return $resource('api/user/:id/following',{id:'@id'});
});

dreamServices.factory('UserFanResource', function($resource) {
    return $resource('api/user/:id/fan',{id:'@id'});
});

dreamServices.factory('UserThumbResource', function($resource){
    return $resource('api/user/:id/thumb',{id:'@id'});
});

dreamServices.factory('UserResource', function($resource){
    return $resource('api/user/:id',{id:'@id'});
});

dreamServices.factory('ActivityLatestRes', function($resource){
	return $resource('api/activity/:id/thumb/latest', {id:'@id'});
});

dreamServices.factory('ActivityHottestRes', function($resource){
	return $resource('api/activity/:id/thumb/hottest', {id:'@id'});
});

dreamServices.factory('UserFollowingImgRes', function($resource){
	return $resource('api/user/:id/following/image',{id:'@id'});
});

dreamServices.factory('AmIFollowingHer', function($resource){
	return $resource('api/:me/follow/:her', {me:'@me', her:'@her'});
});



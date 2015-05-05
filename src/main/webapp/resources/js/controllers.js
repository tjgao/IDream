var dreamControllers = angular.module('dreamControllers',['dreamServices']);

dreamControllers.controller('topController', function($scope, $stateParams, $state, userService) {
    $scope.t_uId  = userService.getUser();
});

dreamControllers.controller('followingImageController', function($scope, $stateParams, UserFollowingImgRes){
	$scope.f_uId = $stateParams.uId;
	$scope.images = UserFollowingImgRes.query({id:$scope.f_uId});
})

dreamControllers.controller('peopleController', function($http, $scope, $ionicPopup, $stateParams, $state, 
		UserResource, UserStatusResource, AmIFollowingHer, userService, UserActivitiesResource,
		UserFollowingResource, UserFanResource) {
    $scope.p_uId = $stateParams.uId;
    $scope.me = userService.getUser();
    $scope.user = UserResource.get({id:$scope.p_uId});
    $scope.userStatus = UserStatusResource.get({id:$scope.p_uId});
    if( $scope.p_uId == $scope.me || $scope.me == '')
    	$scope.showBtn = false;
    else 
    	$scope.showBtn = true;
    
    if($scope.me!='') {
    	$scope.amIFollowing = AmIFollowingHer.get({me:$scope.me,her:$scope.p_uId});
    }
    
    $scope.msgBox = function(ti, content) {
    	$ionicPopup.alert({
    		title:ti,
    		template:content
    	});
    };
    
    $scope.follow = function() {
    	$http.post('api/follow/' + $scope.p_uId).success(function(data){
    		if( data.retcode == 0 ) {
    			$scope.amIFollowing=AmIFollowingHer.get({me:$scope.me,her:$scope.p_uId});
    			$scope.userStatus = UserStatusResource.get({id:$scope.p_uId});
    			$scope.following = UserFollowingResource.query({id:$scope.p_uId});
   			 	$scope.fan = UserFanResource.query({id:$scope.p_uId});
    		}
    	}).error(function(err){
    		$scope.msgBox('错误','请确保网络连接正常。');
    	});
    }

    $scope.unfollow = function() {
    	$http.post('api/unfollow/' + $scope.p_uId).success(function(data){
    		if( data.retcode == 0 ) {
    			$scope.amIFollowing=AmIFollowingHer.get({me:$scope.me,her:$scope.p_uId});
    			$scope.userStatus = UserStatusResource.get({id:$scope.p_uId});
    			$scope.following = UserFollowingResource.query({id:$scope.p_uId});
   			 	$scope.fan = UserFanResource.query({id:$scope.p_uId});    			
    		}
    	}).error(function(err){
    		$scope.msgBox('错误','请确保网络连接正常。');
    	});
    }

    $scope.activities = UserActivitiesResource.query({id:$scope.p_uId});
    $scope.following = UserFollowingResource.query({id:$scope.p_uId}); 
    $scope.fan = UserFanResource.query({id:$scope.p_uId});
});


dreamControllers.controller('pplPhotosController', function($scope, $stateParams, UserThumbResource){
    $scope.pp_uId = $stateParams.uId;
    $scope.thumbs = UserThumbResource.query({id:$scope.pp_uId});
});


dreamControllers.controller('activityController', function($scope, $stateParams, $ionicHistory, $ionicActionSheet, ActivityResource) {
    $scope.aId = $stateParams.aId;
    $scope.activity = ActivityResource.get({id:$scope.aId});

    $scope.goBack = function() {
        var backView = $ionicHistory.backView();
        if( backView != null ) backView.go();
        else $state.go('entry-activities');
    };
    
    $scope.showActionSheet = function() {
        $ionicActionSheet.show({
            buttons:[{text:'分享到朋友圈'}, {text:'分享给朋友'}, {text: '分享到QQ'}, {text:'分享到微博'}],
            cancelText:'取消',
            buttonClicked:function(idx) {
            }
        });
    };    
});

dreamControllers.controller('actLatestController', function($scope, $stateParams, ActivityLatestRes){
	$scope.l_aId = $stateParams.aId;
	$scope.thumbs = ActivityLatestRes.query({id:$scope.l_aId});
});

dreamControllers.controller('actHottestController', function($scope,$stateParams, ActivityHottestRes){
	$scope.h_aId = $stateParams.aId;
	$scope.thumbs = ActivityHottestRes.query({id:$scope.h_aId});
});

dreamControllers.controller('imgController',function($http, $scope, $stateParams, $ionicModal, $ionicActionSheet, 
		$ionicHistory, $ionicPopup, $state, ImageResource, userService, CommentResource){
    $scope.imgId = $stateParams.imgId; 
    $scope.image = ImageResource.get({id:$scope.imgId});
    $scope.comments = CommentResource.query({id:$scope.imgId});
    $scope.comment = {};

    $scope.msgBox = function(ti, content) {
    	$ionicPopup.alert({
    		title:ti,
    		template:content
    	});
    };

    $scope.checkLogin = function() {
    	var uId = userService.getUser();
    	if( uId == '')  {
    		$scope.msgBox('提示','您还不能发表评论，请关注我们并重新进入。');
    		return false;
    	}    	
    	return true;
    };

    $scope.like = function() {
    	if(!$scope.checkLogin()) return;
    	$http.post('api/image/' + $scope.imgId + '/like').success(function(data){
    		if( data.retcode == 0 )
    			$scope.image = ImageResource.get({id:$scope.imgId});
    	}).error(function(err){
    		$scope.msgBox('错误','无法操作，请检查网络。')
    	});
    }
    
    $scope.submitComment = function() {
    	if( $scope.comment.content == null || $scope.comment.content.length == 0 ) return;
    	if( $scope.comment.content.length > 200 )  {
    		$scope.msgBox('提示','评论超过了字数上限。');
    		return;
    	} else {
    		$scope.comment.imgId = $scope.imgId;
    		$scope.comment.userId = userService.getUser();
    		$http.post('api/image/comment', $scope.comment).success(function(data){
    			if( data.retcode == 0 ) {
    				$scope.comments = CommentResource.query({id:$scope.imgId});
    				$scope.image = ImageResource.get({id:$scope.imgId});
    				$scope.comment.content='';
    			}
    			else $scope.msgBox('错误','没有权限或者服务器发生错误。');
    		}).error(function(err){
    			$scope.msgBox('错误','无法发表评论，请检查网络。');
    		});
    	}
    };
    
    $scope.goBack = function() {
        var backView = $ionicHistory.backView();
        if( backView != null ) backView.go();
        else $state.go('entry-activities');
    };

    $scope.showActionSheet = function() {
        $ionicActionSheet.show({
            buttons:[{text:'分享到朋友圈'}, {text:'分享给朋友'}, {text: '分享到QQ'}, {text:'分享到微博'}],
            cancelText:'取消',
            buttonClicked:function(idx) {
            }
        });
    };
});

dreamControllers.controller('tabActivitiesController', function($scope, $http, $ionicModal, $ionicHistory, $state, ActivityResource) {
    $scope.activities = ActivityResource.query(); 

    $scope.imgInfo = {};

    $ionicModal.fromTemplateUrl('uploadImg.html', function($ionicModal) {
        $scope.modal = $ionicModal;
    }, {
        scope: $scope,
        animation: 'slide-in-up'
    });

    $scope.uploadDlg = function(aId) {
        $scope.modal.show();
        $scope.modalActivity = aId;
    }

    $scope.exitUpload = function() {
        $scope.modal.hide();
        wx_cleanImage();
    }
    
    $scope.chooseImg = function() {
    	we_chooseImage();
    }
    
    $scope.uploadImg = function() {
    	if( $scope.imgInfo.uploadImgName == null || $scope.imgInfo.uploadImgName == '')
    		alert('图片名字为空！');

    	if( $scope.imgInfo.uploadImgDesc == null || $scope.imgInfo.uploadImgDesc == '')
    		alert('图片描述为空！');

    	var media_id = we_uploadImage();
    	imgInfo.aId = $scope.modalActivity;
    	$http.post('api/image/upload/' + media_id, $scope.imgInfo).success(function(data){
    	}).error(function(err){
    		alert('请确保网络连接正常。');
    	});
    }
});


var dreamControllers = angular.module('dreamControllers',['dreamServices']);

dreamControllers.controller('topController', function($location,$scope, $state, $stateParams, $state, userService) {
    $scope.t_uId  = userService.getUser();
    if( init_url != '') {
    	if( init_url == 'mystatus') {
    		$location.path('/people/' + $scope.t_uId + '/activities');
    	} else {
    		$location.path(init_url);
    	}
    }
});

dreamControllers.controller('followingImageController', function($scope, $stateParams, UserFollowingImgRes){
	$scope.f_uId = $stateParams.uId;
	$scope.images = UserFollowingImgRes.query({id:$scope.f_uId});
})

dreamControllers.controller('peopleController', function($http, $scope, $ionicPopup, $stateParams, $state, 
		UserResource, UserStatusResource, AmIFollowingHer, userService, UserActivitiesResource,
		UserFollowingResource, UserFanResource, UserThumbResource) {
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
    
    $scope.thumbs = UserThumbResource.query({id:$scope.p_uId});    
    
    $scope.follow = function(who) {
    	$http.post('api/follow/' + who).success(function(data){
    		if( data.retcode == 0 ) {
    			$scope.amIFollowing=AmIFollowingHer.get({me:$scope.me,her:$scope.p_uId});
    			$scope.userStatus = UserStatusResource.get({id:$scope.p_uId});
    			$scope.following = UserFollowingResource.query({id:$scope.p_uId});
   			 	$scope.fan = UserFanResource.query({id:$scope.p_uId});
    		}
    	}).error(function(err){
    		alert('请确保网络连接正常。');
    	});
    }

    $scope.unfollow = function(who) {
    	$http.post('api/unfollow/' + who).success(function(data){
    		if( data.retcode == 0 ) {
    			$scope.amIFollowing=AmIFollowingHer.get({me:$scope.me,her:$scope.p_uId});
    			$scope.userStatus = UserStatusResource.get({id:$scope.p_uId});
    			$scope.following = UserFollowingResource.query({id:$scope.p_uId});
   			 	$scope.fan = UserFanResource.query({id:$scope.p_uId});    			
    		}
    	}).error(function(err){
    		alert('请确保网络连接正常。');
    	});
    }

    $scope.activities = UserActivitiesResource.query({id:$scope.p_uId});
    $scope.following = UserFollowingResource.query({id:$scope.p_uId}); 
    $scope.fan = UserFanResource.query({id:$scope.p_uId});
});

dreamControllers.controller('upController', function($scope, $ionicModal){
	$scope.$watch("assignments", function(value){
		$scope.uploadDlg();
	});
    $ionicModal.fromTemplateUrl('uploadImg.html', function($ionicModal) {
        $scope.modal = $ionicModal;
    }, {
        scope: $scope,
        animation: 'slide-in-up'
    });

    $scope.uploadDlg = function() {
        $scope.modal.show();
    };	
    $scope.exitUpload = function() {
        $scope.modal.hide();
    };
    
});

dreamControllers.controller('activityController', function($scope, $state, $stateParams, $ionicHistory, 
		$ionicActionSheet, ActivityResource, $ionicModal, ActivityLatestRes, ActivityHottestRes) {
    $scope.aId = $stateParams.aId;
    $scope.activities = ActivityResource.query();
    $scope.activity = ActivityResource.get({id:$scope.aId});
    $scope.shareData = {
			title:$scope.activity.name,
			desc:$scope.activity.description,
			link:'http://m.idreamfactory.cn/auth?go=/activity/',
			imgUrl:'http://m.idreamfactory.cn/',
			aId:$scope.activity.id,
			logo:$scope.activity.logo
    };

	$scope.curTab = 'latest';
	$scope.latest_thumbs = ActivityLatestRes.query({id:$scope.aId});
	$scope.hottest_thumbs = ActivityHottestRes.query({id:$scope.aId});
	$scope.thumbs = $scope.latest_thumbs;
	
	$scope.showTab = function(t) {
		if( t == 'latest' ) {
			$scope.thumbs = $scope.latest_thumbs;
		} else if( t == 'hottest' ) {
			$scope.thumbs = $scope.hottest_thumbs;
		}
		$scope.curTab = t;
	};

	$scope.$watch('activity', function(){
		if($scope.activity != null) {
			$scope.latest_thumbs = ActivityLatestRes.query({id:$scope.activity.id});
			$scope.hottest_thumbs = ActivityHottestRes.query({id:$scope.activity.id});
			if( $scope.curTab == 'latest') {
				$scope.thumbs = $scope.latest_thumbs;
			}
			else if( $scope.curTab == 'hottest') {
				$scope.thumbs = $scope.hottest_thumbs;
			} else 
				$scope.thumbs = {};
			
			$scope.shareData.title = $scope.activity.name;
			$scope.shareData.desc = $scope.activity.description;
			$scope.shareData.logo = $scope.activity.logo;
		}
	}, true);    

    $scope.goBack = function() {
    	var backView = $ionicHistory.backView();
        if( backView != null ) backView.go();
        else $state.go('entry-activities');
    };
    
    $scope.prev = function() {
    	for( var i =0; i<$scope.activities.length; i++ ) {
    		if( $scope.activities[i].id == $scope.activity.id) {
    			if(i==0) i=$scope.activities.length-1;
    			else i--;
    			$scope.activity = $scope.activities[i];
    			break;
    		}
    	}
    };
    
    $scope.next = function() {
    	for( var i =0; i<$scope.activities.length; i++ ) {
    		if( $scope.activities[i].id == $scope.activity.id) {
    			if(i==$scope.activities.length-1) i=0;
    			else i++;
    			$scope.activity = $scope.activities[i];
    			break;
    		}
    	}    	
    };
    
//    $scope.shareSuccess = function() {
//    	alert('分享成功');
//    };
//
//    $scope.shareFail = function (res) {
//        alert(JSON.stringify(res));
//    };
//    
//    $scope.showActionSheet = function() {
//        $ionicActionSheet.show({
//            buttons:[{text:'分享到朋友圈'}, {text:'分享给朋友'}, {text: '分享到QQ'}, {text:'分享到微博'}],
//            cancelText:'取消',
//            buttonClicked:function(idx) {
//            	var data = {
//            			title:$scope.shareData.title,
//            			desc:$scope.shareData.desc,
//            			link:$scope.shareData.link+$scope.shareData.id,
//            			imgUrl:$scope.shareData.imgUrl+$scope.shareData.logo
//            	};
//            	if( idx == 0 ) {
//            		shareService.shareTimeline(data, $scope.shareSuccess, $scope.shareFail);
//            	}
//            	else if( idx == 1) {
//            		shareService.shareApp(data, $scope.shareSuccess, $scope.shareFail);
//            	}
//            	else if( idx == 2 ) {
//            		shareService.shareQQ(data, $scope.shareSuccess, $scope.shareFail);
//            	}
//            	else {
//            		shareService.shareWeibo(data, $scope.shareSuccess, $scope.shareFail);
//            	}
//            	return true;
//            }
//        });
//    };    
    
    //bad , should be wrapped up
    $scope.imgInfo = {
    		uploadImgName:'',
    		uploadImgDesc:'',
    		currentSel:''
    };

    $ionicModal.fromTemplateUrl('uploadImg.html', function($ionicModal) {
        $scope.modal = $ionicModal;
    }, {
        scope: $scope,
        animation: 'slide-in-up'
    });

    $scope.uploadDlg = function(aId) {
    	if( aId == null || aId == '') return;
        $scope.modal.show();
        $scope.modalActivity = aId;
    };

    $scope.exitUpload = function() {
        $scope.clean();
        $scope.modal.hide();
        $scope.activities = ActivityResource.query(); 
    };
    $scope.clean = function() {
        we_cleanImage();
        $scope.imgInfo.uploadImgName = '';
        $scope.imgInfo.uploadImgDesc = '';
        $scope.imgInfo.currentSel = '';
    };
    
    $scope.chooseImg = function() {
    	we_chooseImage($scope.imgInfo);
    };
    
    $scope.uploadCallback = function(serverId) {
    	var json = {
    			uploadImgName:$scope.imgInfo.uploadImgName,
    			uploadImgDesc:$scope.imgInfo.uploadImgDesc,
    			aId:$scope.modalActivity
    	}
    	$http.post('api/image/upload/' + serverId[0], json).success(function(data){
    		$scope.clean();
    		alert(JSON.stringify(data));
    		if( data.retcode == 0 ) {
    			alert('图片上传成功！');
    			$scope.$broadcast('curActivityChanged', $scope.activity.id);
    			exitUpload();
    		}
    		else {
    			alert('图片上传失败！');
    		}
    	}).error(function(err){
    		alert('请确保网络连接正常！');
    	});  	
    }

    $scope.uploadImg = function() {
    	if(  $scope.imgInfo.uploadImgName == '') {
    		alert('图片名字为空！');
    		return;
    	}

    	if(  $scope.imgInfo.uploadImgDesc == ''){
    		alert('图片描述为空！');
    		return;
    	}

    	if($scope.imgInfo.currentSel == '' ) {
    		alert('请先选择要上传的图片！');
    		return;
    	}
    	we_uploadImage($scope.uploadCallback);
    }    
    
});



dreamControllers.controller('imgController',function($http, $scope, $stateParams, $ionicModal, $ionicActionSheet, 
		$ionicHistory, $ionicPopup, $state, ImageResource, userService, CommentResource){
    $scope.imgId = $stateParams.imgId; 
    $scope.image = ImageResource.get({id:$scope.imgId});
    $scope.comments = CommentResource.query({id:$scope.imgId});
    $scope.comment = {};


    $scope.checkLogin = function() {
    	var uId = userService.getUser();
    	if( uId == '')  {
    		alert('您还不能发表评论，请关注我们并重新进入。');
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
    		alert('无法操作，请检查网络。')
    	});
    }
    
    $scope.submitComment = function() {
    	if( !$scope.checkLogin() ) return;
    	if( $scope.comment.content == null || $scope.comment.content.trim().length == 0 ) return;
    	if( $scope.comment.content.length > 200 )  {
    		alert('评论超过了字数上限。');
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
    			else alert('没有权限或者服务器发生错误。');
    		}).error(function(err){
    			alert('无法发表评论，请检查网络。');
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

dreamControllers.controller('tabActivitiesController', function($location, $scope, $http, $ionicModal, $ionicHistory, $state, ActivityResource) {
    $scope.activities = ActivityResource.query(); 
    $scope.imgInfo = {
    		uploadImgName:'',
    		uploadImgDesc:'',
    		currentSel:''
    };
    
    $scope.checkit = function(aid) {
    	$location.path('/activity/' + aid);
    }

    $ionicModal.fromTemplateUrl('uploadImg.html', function($ionicModal) {
        $scope.modal = $ionicModal;
    }, {
        scope: $scope,
        animation: 'slide-in-up'
    });

    $scope.uploadDlg = function(aId) {
    	if( aId == null || aId == '') return;
        $scope.modal.show();
        $scope.modalActivity = aId;
    };

    $scope.exitUpload = function() {
        $scope.clean();
        $scope.modal.hide();
    };
    $scope.clean = function() {
        we_cleanImage();
        $scope.imgInfo.uploadImgName = '';
        $scope.imgInfo.uploadImgDesc = '';
        $scope.imgInfo.currentSel = '';
    };
    
    $scope.chooseImg = function() {
    	we_chooseImage($scope.imgInfo);
    };
    
    $scope.uploadCallback = function(serverId) {
    	var json = {
    			uploadImgName:$scope.imgInfo.uploadImgName,
    			uploadImgDesc:$scope.imgInfo.uploadImgDesc,
    			aId:$scope.modalActivity
    	}
    	$http.post('api/image/upload/' + serverId[0], json).success(function(data){
    		if( data.retcode == 0 ) {
    			$scope.clean();
    			alert('图片上传成功！');
    			$scope.activities = ActivityResource.query(); 
    			$scope.exitUpload();
    		}
    		else {
    			alert('图片上传失败！');
    		}
    	}).error(function(err){
    		alert('请确保网络连接正常！');
    	});  	
    }

    $scope.uploadImg = function() {
    	if(  $scope.imgInfo.uploadImgName == '') {
    		alert('图片名字为空！');
    		return;
    	}

    	if(  $scope.imgInfo.uploadImgDesc == ''){
    		alert('图片描述为空！');
    		return;
    	}

    	if($scope.imgInfo.currentSel == '') {
    		alert('请先选择要上传的图片！');
    		return;
    	}
    	we_uploadImage($scope.uploadCallback);
    }
});


var dreamControllers = angular.module('dreamControllers',['dreamServices']);

dreamControllers.controller('topController', function($rootScope, $location,$scope,  userService, ActivityResource) {
    $scope.t_uId  = userService.getUser();
     $scope.$watch("assignments", function (value) {
    	    if( init_url != '') {
    	    	if( init_url == 'mystatus') {
    	    		$location.path('/people/' + $scope.t_uId + '/activities');
    	    	} else {
    	    		$location.path(init_url);
    	    	}
    	    	init_url = '';
    	    }    	  
    });
//     alert($location.absUrl());
     $scope.showTip = function(show) { 
     	if(show)
     		angular.element(document.querySelector( '#tipcover' )).css('display','block');
     	else
     		angular.element(document.querySelector( '#tipcover' )).css('display','');
     };

    $scope.activities = ActivityResource.query();
    
    $scope.loadActivities = function() {
    	$scope.activities = ActivityResource.query();
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

dreamControllers.controller('upController', function($http, $scope, $ionicModal, $ionicHistory, $location){
	$scope.$parent.$watch("activities", function(val, old){
		if( val == null || val === old ) return;
		$scope.activities = $scope.$parent.activities;
		if( $scope.activities.length > 0)
			$scope.selectedItem.id = $scope.activities[0].id;
	}, true);
	
    $scope.imgInfo = {
		uploadImgName:'',
		uploadImgDesc:'',
		currentSel:''
	};
	
    $scope.selectedItem = {};

	if( $scope.activities.length > 0)
		$scope.selectedItem.id = $scope.activities[0].id;	

    $scope.chooseCallback = function(sel) {
    	$scope.imgInfo.currentSel = sel;
    	angular.element(document.querySelector( '#selectedImg2' )).attr('src',sel);
    }    

    $scope.chooseImg = function() {
    	we_chooseImage($scope.chooseCallback);
    };	
    
    $scope.clean = function() {
    	$scope.imgInfo.uploadImgName = '';
    	$scope.imgInfo.uploadImgDesc = '';
    	$scope.imgInfo.currentSel = '';
    	$scope.selectedItem.id = '';
    	angular.element(document.querySelector( '#selectedImg2' )).attr('src','');
    };
    
    $scope.uploadCallback = function(serverId) {
    	var json = {
    			uploadImgName:$scope.imgInfo.uploadImgName,
    			uploadImgDesc:$scope.imgInfo.uploadImgDesc,
    			aId:$scope.selectedItem.id
    	}
    	$http.post('api/image/upload/' + serverId[0], json).success(function(data){
    		$scope.clean();
    		if( data.retcode == 0 ) {
    			$scope.clean();
    			alert('图片上传成功！');
    			//$scope.$broadcast('curActivityChanged', $scope.activity.id);
    			$scope.$parent.loadActivities();
    		}
    		else {
    			alert('图片上传失败！');
    		}
    	}).error(function(err){
    		alert('请确保网络连接正常！');
    	});  	
    };

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
    	
    	if($scope.selectedItem.id == '' ) {
    		alert('请先选择要参加的活动！');
    		return;
    	}
    	we_uploadImage($scope.uploadCallback);
    }      
    
    $scope.goBack = function() {
    	var backView = $ionicHistory.backView();
        if( backView != null ) backView.go();
        else
        	$location.path("/entry/activities");
    };
});

dreamControllers.controller('activityController', function($http, $scope, $state, $stateParams, $ionicHistory, 
		$ionicActionSheet, $location, $ionicModal,ActivityResource, ActivityLatestRes, ActivityHottestRes) {
    $scope.aId = $stateParams.aId;

    $scope.activities = $scope.$parent.activities; //ActivityResource.query();
    $scope.$parent.$watch("activities", function(val, old){
    	if(val == null || val === old ) return;
    	$scope.activities = $scope.$parent.activities;
    }, true);

    $scope.activity = null;

    $scope.getActivity = function(id) {
    	for( var i = 0; i<$scope.activities.length; i++) {
    		if( $scope.activities[i].id == id ) {
    			return $scope.activities[i];
    		}
    	}    	
    }

    if( $scope.activities == null || $scope.activities.length == 0) {
    	$http.get('api/activity').success(function(data){
    		$scope.activities = data;
    		$scope.activity = $scope.getActivity($scope.aId);
    	});
    } else {
    	$scope.activity = $scope.getActivity($scope.aId);
    }
    
	$scope.curTab = 'latest';
	$scope.latest_thumbs = ActivityLatestRes.query({id:$scope.aId});
	$scope.hottest_thumbs = ActivityHottestRes.query({id:$scope.aId});
	$scope.thumbs = $scope.latest_thumbs;
	
    $scope.showTip = function(show) { 
     	if(show)
     		angular.element(document.querySelector( '#tipcover' )).css('display','block');
     	else
     		angular.element(document.querySelector( '#tipcover' )).css('display','');
    };
    
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
			$scope.prepareShare();
			$scope.latest_thumbs = ActivityLatestRes.query({id:$scope.activity.id});
			$scope.hottest_thumbs = ActivityHottestRes.query({id:$scope.activity.id});
			if( $scope.curTab == 'latest') {
				$scope.thumbs = $scope.latest_thumbs;
			}
			else if( $scope.curTab == 'hottest') {
				$scope.thumbs = $scope.hottest_thumbs;
			} else 
				$scope.thumbs = {};
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

    $scope.prepareShare = function() {
		var share = {
				title : "我参与了青联梦工厂的「" + $scope.activity.name + "」活动，小伙伴们速来。",
				desc : "活动介绍：" + $scope.activity.description,
				link: "http://m.idreamfactory.cn/auth?go=/activity/" + $scope.activity.id,
				imgUrl : "http://m.idreamfactory.cn/" + $scope.activity.logo
		};
	  	setShare(share);    	
    };

    $scope.$watch(function(){
    	return $location.path();
    }, function(val){
    	if( val.substring(0, 10) == '/activity/') {
    		$scope.prepareShare();
    	} else {
    		recoverDefaultShare();
    	}    		
    }, true);

    //bad , should be wrapped up
    $scope.imgInfo = {
    		tag:'A',
    		uploadImgName:'',
    		uploadImgDesc:'',
    		currentSel:''
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
        $scope.$parent.loadActivities();
        //$scope.activities = ActivityResource.query(); 
		$scope.latest_thumbs = ActivityLatestRes.query({id:$scope.activity.id});
		$scope.hottest_thumbs = ActivityHottestRes.query({id:$scope.activity.id});    
		if( $scope.curTab == 'latest') {
			$scope.thumbs = $scope.latest_thumbs;
		}
		else if( $scope.curTab == 'hottest') {
			$scope.thumbs = $scope.hottest_thumbs;
		} else 
			$scope.thumbs = {};		
    };

    $scope.clean = function() {
        we_cleanImage();
        $scope.imgInfo.uploadImgName = '';
        $scope.imgInfo.uploadImgDesc = '';
        $scope.imgInfo.currentSel = '';
        angular.element(document.querySelector( '#selectedImgA' )).attr('src','');
    };
    
    $scope.chooseCallback = function(sel) {
    	$scope.imgInfo.currentSel = sel;
    	angular.element(document.querySelector( '#selectedImgA' )).attr('src',sel);
    }
    
    $scope.chooseImg = function() {
    	we_chooseImage($scope.chooseCallback);
    };
    
    $scope.uploadCallback = function(serverId) {
    	var json = {
    			uploadImgName:$scope.imgInfo.uploadImgName,
    			uploadImgDesc:$scope.imgInfo.uploadImgDesc,
    			aId:$scope.modalActivity
    	}
    	$http.post('api/image/upload/' + serverId[0], json).success(function(data){
    		$scope.clean();
    		if( data.retcode == 0 ) {
    			alert('图片上传成功！');
    			//$scope.$broadcast('curActivityChanged', $scope.activity.id);
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



dreamControllers.controller('imgController',function($http, $scope, $stateParams, $ionicModal, $ionicPopover, 
		$ionicHistory, $ionicPopup, $state, ImageResource, userService, CommentResource, $location){
    $scope.imgId = $stateParams.imgId; 
//    $scope.image = ImageResource.get({id:$scope.imgId});
    $scope.comments = CommentResource.query({id:$scope.imgId});
    $scope.comment = {};

    $scope.doneShare = function(res) {
    	$http.post('api/image/' + $scope.imgId + '/share').success(function(data){
    		if( data.retcode == 0 ) {
    			$http.get('api/image/' + $scope.imgId).success(function(img){
    				$scope.image = img;
    			});
    		}
    	});
    }
    
    $scope.prepareShare = function() {
		var share = {
				title : "好图一张，来自青联梦工厂的「" + $scope.image.name + "」。",
				desc : "图片介绍：" + $scope.image.description,
				link: "http://m.idreamfactory.cn/auth?go=/image/" + $scope.image.id,
				imgUrl : "http://m.idreamfactory.cn/" + $scope.image.thumb,
				success: $scope.doneShare
		};
	  	setShare(share)    	
    }
    
    $scope.$watch(function(){
    	return $location.path();
    }, function(val, old){
    	if( val.substring(0, 7) == '/image/') {
    		$http.get('api/image/' + $scope.imgId).success(function(data){
    			$scope.image = data;
    			$scope.prepareShare();
    		});
    	} else {
    		recoverDefaultShare();
    	}
    }, true);

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
    
    $scope.showTip = function(show) { 
     	if(show)
     		angular.element(document.querySelector( '#tipcover' )).css('display','block');
     	else
     		angular.element(document.querySelector( '#tipcover' )).css('display','');
    };
     
    $ionicPopover.fromTemplateUrl('templates/popover.html',{
    	scope:$scope
    }).then(function(popover){
    	$scope.popover = popover;
    }); 
    
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
//
//    $scope.showActionSheet = function() {
//        $ionicActionSheet.show({
//            buttons:[{text:'分享到朋友圈'}, {text:'分享给朋友'}, {text: '分享到QQ'}, {text:'分享到微博'}],
//            cancelText:'取消',
//            buttonClicked:function(idx) {
//            }
//        });
//    };
});

dreamControllers.controller('tabActivitiesController', function($location, $scope, $http, $ionicModal, 
		$ionicHistory, $state, $ionicSlideBoxDelegate) {
	$scope.activities = null;
    $scope.$parent.$watch("activities", function(val, old){
    	if( val == null || val === old) return;
    	$scope.activities = val;
    	$scope.carousel = [];
    	for( var i=0; i<$scope.activities.length; i++) {
    		if( $scope.activities[i].orderby == 0 ) {
    			$scope.carousel.push($scope.activities[i]);
    		}
    	}
    	$ionicSlideBoxDelegate.update();
//    	var arr = null;
//    	var last = null;
//    	for(var i=0; i<$scope.activities.length; i++ ) {
//    		if( $scope.activities[i].orderby != last ) {
//    			if( arr != null ) $scope.groupAct.push(arr);
//    			last = $scope.activities[i].orderby;
//    			arr = [];
//    			arr.push($scope.activities[i]);
//    		} else {
//    			arr.push($scope.activities[i]);
//    		}
//    	}
//    	if( arr != null ) $scope.groupAct.push(arr);
    }, true);

    $scope.$watch(function(){return $location.path();}, function(val, old){
    	if(val == '/entry/activities') {
    		$ionicSlideBoxDelegate.update();
    		$ionicSlideBoxDelegate.enableSlide(true);
    		$ionicSlideBoxDelegate.start();
    	}
    });
    
    $scope.imgInfo = {
    		tag:'B',
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
        angular.element(document.querySelector( '#selectedImgB' )).attr('src','');
    };

    $scope.chooseCallback = function(sel) {
    	$scope.imgInfo.currentSel = sel;
    	angular.element(document.querySelector( '#selectedImgB' )).attr('src',sel);
    }    

    $scope.chooseImg = function() {
    	we_chooseImage($scope.chooseCallback);
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
    			//$scope.activities = ActivityResource.query(); 
    			$scope.$parent.loadActivities();
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


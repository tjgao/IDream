<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page import="com.dream.wechat.model.UserLite" %>
<!DOCTYPE html>
<html ng-app="starter">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width">

    <link href="resources/lib/css/ionic.min.css" rel="stylesheet">
    <link href="resources/css/style.css" rel="stylesheet">

    <!-- IF using Sass (run gulp sass first), then uncomment below and remove the CSS includes above
    <link href="css/ionic.app.css" rel="stylesheet">
    -->

    <!-- ionic/angularjs js -->
    <script src="resources/lib/js/ionic.bundle.min.js"></script>
    <script src="resources/lib/js/angular/angular-resource.min.js"></script>

    <!-- WeChat JS interfaces -->
    <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
    <!-- your app's js -->
    <script src="resources/js/app.js"></script>
    <script src="resources/js/controllers.js"></script>
    <script src="resources/js/services.js"></script>
    <script src="resources/js/wechat.js"></script>
    
  <script type="text/javascript">
  		var g_uId = '<% UserLite o = (UserLite)session.getAttribute("USER"); if(o==null) out.print(""); else out.print(o.getId()); %>';
  		
  		wx.config({
  			debug:true,
  			appId:'${appId}',
  			timestamp:${timestamp},
  			nonce:'${nonce}',
  			signature:'${signature}',
  			jsApiList:[
  				'onMenuShareTimeline',
  				'onMenuShareAppMessage',
  				'onMenuShareQQ',
  				'onMenuShareWeibo',
  				'chooseImage',
  				'previewImage',
  				'uploadImage'
  			]
  		});
  </script>    
  </head> 
  <body ng-controller="topController">

  <ion-nav-view name="mainView"></ion-nav-view>


    <script id="templates/tab-activities.html" type="text/ng-template">
        <ion-content hide-nav-bar="true"  header-shrink scroll-event-interval="5" has-bouncing="false" ng-controller="tabActivitiesController">
	        <div class="list">
	           <div class="item" ng-repeat="a in activities">
	                <div class="row force-no-padding">
	                   <div class="col avatar-frame-small force-no-padding">
	                        <div class="row force-no-padding">
	                            <div class="col-fixed-40 force-no-padding">
								<a href="#/activity/{{a.id}}/latest"><img ng-src="{{a.logo}}"></a>
	                            </div>
	                            <div class="col force-no-padding-left15">
	                                <div class="row force-no-padding">
	                                <h2>{{a.name}}</h2>
	                                </div>
	                                <div class="row force-no-padding-top5">
	                                <p>{{a.description}}</p>
	                                </div>
	                            </div>
	                        </div>
	                    </div>
	                        <div class="force-no-padding-left15" style="text-align: right">
								<button class="button button-outline icon-left" ng-click="uploadDlg({{a.id}})"><i class="icon ion-plus"></i>&nbsp;参加</button>
	                        </div>
	                </div>
					<div class="item-body force-no-padding">
	                    <div class="row" >
	                        <div ng-repeat="thumb in a.thumbs" class="col col-33"><a href="#image/{{thumb.id}}"><img class="full-image" ng-src="{{thumb.thumb}}"/></a></div>
	                    </div>
					</div>
	            </div>
	        </div>
            <div><p>&nbsp;</p><p>&nbsp;</p></div>
        </ion-content>
    </script>

    <script id="templates/tab-interestedImg.html" type="text/ng-template">
        <ion-content hide-nav-bar="true"  header-shrink  has-bouncing="false">
        <div class="list card">
					<div class="item" ng-repeat-start="image in images">
	                   <div class="col avatar-frame-small force-no-padding">
	                        <div class="row force-no-padding">
	                            <div class="col-fixed-40 force-no-padding">
								<a href="#/people/{{image.authorId}}/activities"><img ng-src="{{image.authorHead}}"></a>
	                            </div>
	                            <div class="col force-no-padding-left15">
	                                <div class="row force-no-padding">
	                                <h2>{{image.authorName}} <i ng-if="image.authorSex==1" class="icon ion-male"></i><i ng-if="image.authorSex==2" class="icon ion-female"></i></h2>
	                                </div>
	                                <div class="row force-no-padding-top5">
	                                <p>{{image.uploadTime | date:'yyyy-MM-dd HH:mm:ss'}}</p>
	                                </div>
	                            </div>
	                        </div>
	                    </div>
					</div>
            <div class="item item-body">
                <a href='#/image/{{image.id}}'>
                <img class="full-image" ng-src="{{image.file}}"/>
                </a>
                <p>{{image.description}}</p>
            </div>
            <div class="item item-body tabs tabs-secondary tabs-icon-left" ng-repeat-end>
                <a class="tab-item" href="#/image/{{image.id}}">
                <i class="icon ion-thumbsup"></i>
                ({{image.likes}})
                </a>
                <a class="tab-item" href="#/image/{{image.id}}">
                <i class="icon ion-chatbox"></i>
                ({{image.comments}})
                </a>
                <a class="tab-item" href="#/image/{{image.id}}">
                <i class="icon ion-share"></i>
                ({{image.shared}})
                </a>
            </div>
            <div class="item">
                <p>&nbsp;</p>
            </div>
        </div>
        </ion-content> 
    </script>


    <script id="templates/entry.html" type="text/ng-template">
    <ion-tabs class="tabs-top tabs-striped has-nothing-top">
        <ion-tab title="热点" href="#/entry/activities">
        <ion-nav-view  class="has-small-tabs-top" name="tab-activities"></ion-nav-view>
        </ion-tab>
        <ion-tab title="关注" href="#/entry/interestedImg/{{t_uId}}">
        <ion-nav-view  class="has-small-tabs-top" name="tab-interested"></ion-nav-view>
        </ion-tab>
    </ion-tabs>
      <div class="bar bar-footer" hide-on-scroll="true" >
          <div class="tabs">
              <a class="tab-item" href="#/entry/activities">
                  所有活动 
              </a>
              <a class="tab-item" href="#/people/{{t_uId}}/activities" >
                  『我的』             
              </a>
          </div>    
      </div>
    </script>

    <script id="templates/image.html" type="text/ng-template">
            <header class="bar bar-header">
                <div class="button button-clear" ng-click="goBack()">
                <span class="icon ion-arrow-left-c"></span></div>
                <h1 class="title">{{image.name}}</h1>
                <div class="button button-clear" ng-click="showActionSheet()">
                <span class="ion ion-share"></span></div>
            </header>
            <ion-content class="has-small-tabs-top" padding="true">
                <div class="list card ">
					<div class="item">
	                   <div class="col avatar-frame-small force-no-padding">
	                        <div class="row force-no-padding">
	                            <div class="col-fixed-40 force-no-padding">
								<a href="#/people/{{image.authorId}}/activities"><img ng-src="{{image.authorHead}}"></a>
	                            </div>
	                            <div class="col force-no-padding-left15">
	                                <div class="row force-no-padding">
	                                <h2>{{image.authorName}} <i ng-if="image.authorSex==1" class="icon ion-male"></i><i ng-if="image.authorSex==2" class="icon ion-female"></i></h2>
	                                </div>
	                                <div class="row force-no-padding-top5">
	                                <p>{{image.uploadTime | date:'yyyy-MM-dd HH:mm:ss'}}</p>
	                                </div>
	                            </div>
	                        </div>
	                    </div>
					</div>
                    <div class="item item-body">
                        <img class="full-image" ng-src="{{image.file}}"/>
                        <p>{{image.description}}</p>
                    </div>
                    <div class="item tabs tabs-secondary tabs-icon-left">
                        <a class="tab-item" ng-click="like()"><i class="icon ion-thumbsup"></i> ({{image.likes}})</a>
                        <a class="tab-item"><i class="icon ion-chatbox"></i> ({{image.comments}})</a>
                        <a ng-click="showActionSheet()" class="tab-item"><i class="icon ion-share"></i> ({{image.shared}})</a>
                    </div>
                </div>
                <div class="card">
                    <div class="item">
                        <h2>评论:</h2>
                        <div class="list">
                          <div class="item item-input-inset">
                            <label class="item-input-wrapper">
                              <input ng-maxlength="200" ng-minlength="5" style="width:100%;" type="text" ng-model="comment.content" placeholder="限200字"></input>
                            </label>
							<div ng-messages="comment.$error" style="color:maroon" role="alert">
							  <div ng-message="minlength">评论不得少于5字</div>
							  <div ng-message="maxlength">评论不得超过200字</div>
							</div>
                            <button class="button button-small" ng-click="submitComment()">
                                提交
                            </button>
                          </div>
                        </div>
                    </div>
                    <div class="item item-body">
                       <div class="item item-avatar" ng-repeat-start="c in comments">
                            <img ng-src="{{c.userHead}}"/>
                            <div class="row">
                            <div class="col">
                            <h3>{{c.userName}} <i ng-if="c.userSex==1" class="icon ion-male"></i><i ng-if="c.userSex==2" class="icon ion-female"></i></h3>
                            </div>
                            <div class="col-60" style="text-align: right">
                            <p>{{c.createTime | date:'yyyy-MM-dd HH:mm:ss'}}</p>
                            </div>
                            </div>
                        </div>
                        <div class="item" ng-repeat-end>
                            <p>{{c.content}}</p>
                        </div>
                    </div>
                </div>
            </ion-content>
    </script>

    <script id="uploadImg.html" type="text/ng-template">
        <div class="modal">
            <header class="bar bar-header">
                <div class="button button-clear" ng-click="exitUpload()">
                <span class="icon ion-close"></span></div>
                <h1 class="title">上传照片</h1>
            </header>
            <ion-content class="has-small-tabs-top" has-header="true" padding="true">
                <div class="list">
                    <label class="item item-input">
                        <input ng-model="imgInfo.uploadImgName" type="text" placeholder="图片名字"/>
                    </label>
                    <label class="item item-input">
                        <textarea ng-model="imgInfo.uploadImgDesc"  placeholder="图片描述"/>
                    </label>
                    <a class="item item-icon-left" href="#">
                        <i class="icon ion-image"></i>
                        选取图片
                    </a>
                    <a class="item item-icon-left" href="#">
                        <i class="icon ion-eye"></i>
                        预览图片
                    </a>
                    <a class="item item-icon-left" href="#">
                        <i class="icon ion-upload"></i>
                        上传图片
                    </a>
                </div>
            </ion-content>
        </div>
    </script>

    <script id="templates/people.html" type="text/ng-template">
        <ion-content class="has-nothing-top" hide-nav-bar="true" header-shrink  has-bouncing="false">
      <div class="list card" class="has-nothing-top">
         <div class="item item-body" style="text-align:center;">
            <div class="avatar-frame">
            <img ng-src="{{user.headImg}}">
            </div>
            <h2>{{user.nickname}} <i ng-if="user.sex==1" class="icon ion-male"></i><i ng-if="user.sex==2" class="icon ion-female"></i></i>&nbsp;</h2>
            <p><i class="icon ion-thumbsup"></i>&nbsp;({{userStatus.totalLikes}})&nbsp;&nbsp;
			<button ng-if="showBtn && (amIFollowing.result==0)" class="button button-small icon-let" ng-click="follow()"><i class="icon ion-plus"></i>&nbsp;关注</button></p>
			<button ng-if="showBtn && (amIFollowing.result!=0)" class="button button-small icon-let" ng-click="unfollow()"><i class="icon ion-minus-circled"></i>&nbsp;取消关注</button></button></p>
         </div>
        <div class="item item-body tabs ">
            <a class="tab-item" href="#/people/{{p_uId}}/activities">活动&nbsp;({{userStatus.totalActivities}})</a>
            <a class="tab-item" href="#/people/{{p_uId}}/photos">照片&nbsp;({{userStatus.totalImages}})</a>
            <a class="tab-item" href="#/people/{{p_uId}}/interested">关注&nbsp;({{userStatus.totalFollowing}})</a>
            <a class="tab-item" href="#/people/{{p_uId}}/fans">粉丝&nbsp;({{userStatus.totalFans}})</a>
        </div>
      </div>
      <ion-nav-view name="statusView" ></ion-nav-view>
        </ion-content>
      <div class="bar bar-footer" hide-on-scroll="true" >
          <div class="tabs">
              <a class="tab-item" href="#/entry/activities">
                  所有活动 
              </a>
              <a class="tab-item" href="#/people/{{me}}/activities" >
                  『我的』             
              </a>
          </div>    
      </div>
    </script>

    <script id="templates/activities.html" type="text/ng-template">
        <ion-scroll>
        <div class="list">
           <div class="item" ng-repeat="a in activities">
                <div class="row force-no-padding">
                       <div class="col avatar-frame-small force-no-padding">
                            <div class="row force-no-padding">
                                <div class="col-fixed-40 force-no-padding">
                                <a href="#/activity/{{a.id}}/latest"><img ng-src="{{a.logo}}"></a>
                                </div>
                                <div class="col force-no-padding-left15">
                                    <div class="row force-no-padding">
                                    <h2>{{a.name}}</h2>
                                    </div>
                                    <div class="row force-no-padding-top5">
                                    <p>{{a.description}}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="force-no-padding-left15" style="text-align: right">
							<a href="#/image/{{a.thumbs[0].id}}">
							<img ng-src="{{a.thumbs[0].thumb}}" style="width:50px; height:50px"/>
							</a>
                        </div>
                </div>
            </div>
        </div>
        </ion-scroll>
    </script>


    <script id="templates/thumbs.html" type="text/ng-template">
    <ion-scroll>
          <div class="row" ng-repeat="t in thumbs">
              <div class="col" ng-if="$index%3==0"><a href="#/image/{{thumbs[$index].id}}"><img class="full-image" ng-src="{{thumbs[$index].thumb}}"/></a></div>
              <div class="col" ng-if="$index%3==0"><a href="#/image/{{thumbs[$index+1].id}}"><img class="full-image" ng-src="{{thumbs[$index+1].thumb}}"/></a></div>
              <div class="col" ng-if="$index%3==0"><a href="#/image/{{thumbs[$index+2].id}}"><img class="full-image" ng-src="{{thumbs[$index+2].thumb}}"/></a></div>
          </div>
    </ion-scroll>
    </script>

    <script id="templates/interested.html" type="text/ng-template">
    <ion-scroll>
        <div class="list">
			<div class="item" ng-repeat="user in following">
			 	<div class="row force-no-padding">
					<div class="col avatar-frame-small force-no-padding">
                        <div class="row force-no-padding">
                            <div class="col-fixed-40 force-no-padding">
                            	<a href="#/people/{{user.id}}/activities"><img ng-src="{{user.headImg}}"></a>
                            </div>
                            <div class="col force-no-padding-left15">
                                <div class="row force-no-padding">
                                	<h2>{{user.nickname}}</h2>
                                </div>
                                <div class="row force-no-padding-top5">
                                	<p><i ng-if="user.sex==1" class="icon ion-male"></i><i ng-if="user.sex==2" class="icon ion-female"></i></p>
                                </div>
                            </div>
							<div class="force-no-padding-left15" style="text-align: right">
								<button class="button button-striped" ng-click="unfollow({{user.id}})"><i class="icon ion-minus-circled"></i>&nbsp;取消关注</button>
							</div>
                        </div>
					</div>
				</div>
			</div>
        </div>
    </ion-scroll>
    </script>

    <script id="templates/fans.html" type="text/ng-template">
    <ion-scroll>
        <div class="list">
			<div class="item" ng-repeat="user in fan">
			 	<div class="row force-no-padding">
					<div class="col avatar-frame-small force-no-padding">
                        <div class="row force-no-padding">
                            <div class="col-fixed-40 force-no-padding">
                            	<a href="#/people/{{user.id}}/activities"><img ng-src="{{user.headImg}}"></a>
                            </div>
                            <div class="col force-no-padding-left15">
                                <div class="row force-no-padding">
                                	<h2>{{user.nickname}}</h2>
                                </div>
                                <div class="row force-no-padding-top5">
                                	<p><i ng-if="user.sex==1" class="icon ion-male"></i><i ng-if="user.sex==2" class="icon ion-female"></i></p>
                                </div>
                            </div>
							<div class="force-no-padding-left15" style="text-align: right">
								<button class="button button-striped" ng-click="follow({{user.id}})"><i class="icon ion-minus-circled"></i>&nbsp;关注</button>
							</div>
                        </div>
					</div>
				</div>
			</div>
        </div>
    </ion-scroll>
    </script>

	<script id="templates/activity.html" type="text/ng-template">
      <header class="bar bar-header">
          <div class="button button-clear" ng-click="goBack()">
          <span class="icon ion-arrow-left-c"></span></div>
          <h1 class="title">{{activity.name}}</h1>
          <div class="button button-clear" ng-click="showActionSheet()">
          <span class="ion ion-share"></span></div>
      </header>
      <div class="bar bar-footer" hide-on-scroll="true" >
          <div class="button button-clear" ng-click="">
          <span class="icon ion-chevron-left"></span></div>
          <h1 class="title">
          <div class="button button-clear" ng-click="">
          <span class="icon ion-plus-round"></span></div>
          </h1>
          <div class="button button-clear" ng-click="">
          <span class="icon ion-chevron-right"></span></div>
      </div>
        <ion-content class="has-small-tabs-top" hide-nav-bar="true" header-shrink  has-bouncing="false">
      <div class="list card" class="has-nothing-top">
         <div class="item item-body" style="text-align:center;">
            <div class="avatar-frame">
            <img ng-src="{{activity.logo}}">
            </div>
            <p>{{activity.description}}</p>
         </div>
        <div class="item item-body tabs ">
            <a class="tab-item" href="#/activity/{{aId}}/latest">最新作品</a>
            <a class="tab-item" href="#/activity/{{aId}}/hottest">最热作品</a>
        </div>
      </div>
      <ion-nav-view name="actThumbView" ></ion-nav-view>
        </ion-content>		
	</script>
  </body>
</html>

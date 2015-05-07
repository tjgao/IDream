        
var g_images = { localId:[], serverId:[]};

var _curSel = '';

var we_chooseImage = function(o) {
	wx.chooseImage({
		success: function(res) {
			if( res.localIds.length > 1 ) {
				alert('请只选择一张图片！');
				_curSel = '';
			} else {
				g_images.localId = res.localIds;
				_curSel = res.localIds[0];
				o.currentSel = _curSel;
			}
		},
		fail:function(res) {
			alert('选择图片失败！');
			_curSel = '';
		}
	});
	return _curSel;
};

var we_cleanImage = function() {
	g_images.localId = [];
	g_images.serverId = [];
};

var we_uploadImage = function( uploadCallback) {
	if( g_images.localId.length == 0 ) {
		return;
	}
	var i = 0; 
	var len = g_images.localId.length;
	function upload() {
		wx.uploadImage({
			localId:g_images.localId[i],
			success: function(res) {
				i++;
				g_images.serverId.push(res.serverId);
				if( i < len ) upload();
				uploadCallback(g_images.serverId);
			},
			fail: function(res) {
				alert('上传失败！');
			}
		});
	}
	upload();
	return g_images.serverId;
};

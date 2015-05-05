        
var g_images = { localId:[], serverId:[]};

var we_chooseImage = function() {
	wx.chooseImage({
		success: function(res) {
			if( res.localId.length > 1 ) {
				alert('请只选择一张图片');
				return;
			}
			g_images.localId = res.localId;
		}
	});
};

var we_cleanImage = function() {
	g_images = { localId:[], serverId:[]};
};

var we_uploadImage = function() {
	if( g_images.localId.length == 0 ) {
		return;
	}
	int i = 0, len = g_images.localId.length;
	function upload() {
		wx.uploadImage({
			localId:g_images.localId[i],
			success: function(res) {
				i++;
				g_images.serverId.push(res.serverId);
				if( i < len ) upload();
			},
			fail: function(res) {
				alert('上传失败！');
			}
		});
	}
	upload();
	return g_images.serverId;
};

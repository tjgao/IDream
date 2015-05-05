package com.dream.wechat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.dream.wechat.model.ImgComment;

public interface ImgCommentMapper {
	@Select("select * from imgComment where id=#{id}")
	ImgComment getImgComment(@Param("id") int id);
	
	@Insert("insert into imgComment (imgId, userId, createtime, content) values (#{imgId},#{userId},now(),#{content})")
	void insertImgComment(ImgComment i);
	
	@Update("update imgComment set imgId=#{imgId}, userId=#{userId}, content=#{content} where id=#{id}")
	void updateImgComment(ImgComment i);
	
	@Select("select a.id, a.imgId as imgId, a.createtime as createtime,"
			+ "a.content as content, a.userId as userId, b.nickname as username,"
			+ "b.sex as usersex, b.headimgurl as userhead from imgcomment as a,user as b "
			+ "where a.userId = b.id and a.imgId=#{id}")
	List<ImgComment> getImgCommentsByImageId(@Param("id") int imgId);
}

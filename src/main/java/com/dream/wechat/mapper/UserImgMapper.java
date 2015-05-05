package com.dream.wechat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.dream.wechat.model.ImgComment;
import com.dream.wechat.model.Thumb;
import com.dream.wechat.model.UserImg;

public interface UserImgMapper {
	@Select(
			"select a.id as id, a.name as name, a.description as description, b.id as authorid,"
			+ " b.nickname as authorName, b.sex as authorSex, a.comments as comments,"
			+ " a.uploadTime as uploadTime, a.likes as likes, a.shared as shared, "
			+ " a.aId as aId, a.thumb as thumb, a.file as file, b.headimgurl as authorHead	"
			+ " from userimg as a, user as b where a.authorId = b.id and a.id=#{id}"
			)
	UserImg getImage(@Param("id") int id);
	
	
	@Update("update userimg set likes=likes+1 where id=#{id}")
	void plusLikes(@Param("id") int id);
	
	@Update("update userimg set shared=shared+1 where id=#{id}")
	void plusShared(@Param("id") int id);
	
	@Update("update userimg set comments=comments+1 where id=#{id}")
	int plusComments(@Param("id") int id);

	@Insert("insert into imglike (imgid, userid, createtime) values (#{imgId}, #{userId}, now())")
	void putLikes(@Param("imgId") int imgId, @Param("userId") int uId);
	
	@Insert("insert into imgcomment (imgid, userid, createtime, content) values "
			+ "(#{imgId}, #{userId}, now(), #{content})")
	int putComment(ImgComment com);
	
	@Select("select count(*) from imglike where userid=#{userId}")
	int totalLikesByUser(@Param("userId") int uId);
	
	@Select("select a.id as id, a.imgId as imgId, a.createtime as createtime,"
			+ "a.content as content, a.userId as userId, b.nickname as username,"
			+ "b.sex as usersex, b.headimgurl as userhead from imgcomment as a,user as b "
			+ "where a.userId = b.id and a.imgId=#{id} order by id desc")
	List<ImgComment> getImgComments(@Param("id") int imgId);	
	
	
	@Insert("insert into userimg (name, description,imgurl,authorId,uploadTime,likes, "
			+ "shared, file, aid, thumb, comments) values "
			+ "(#{name},#{description},#{imgurl},#{authorId},now(),#{likes}, #{shared},"
			+ "#{file}, #{aid}, #{thumb}, #{comments})")
	void insertUserImg(UserImg ui);
	
	@Select("select id, file, thumb from userimg where aid=#{id} order by id desc limit #{start}, #{limit}")
	List<Thumb> getThumbsByActivityIdPage(@Param("id") int id, @Param("start") int start, @Param("limit") int limit);
	
	@Select("select id, file, thumb from userimg where aid=#{id} order by id desc")
	List<Thumb> getThumbsByActivityId(@Param("id") int id);
	
	@Select("select id, file, thumb from userimg where authorid = #{id} order by id desc limit #{start}, #{limit}")
	List<Thumb> getLatestThumbsByUser(@Param("id") int id, @Param("start") int start, @Param("limit") int limit);
	
	@Select("select id, file, thumb from userimg where authorid = #{id} order by likes desc limit #{start}, #{limit}")
	List<Thumb> getHottestThumbsByUser(@Param("id") int id, @Param("start") int start, @Param("limit") int limit);
	
	@Select("select id, name, description, file, thumb from userimg order by likes, comments, shared, id desc limit 10")
	List<UserImg> getHottestTop10() ;
}

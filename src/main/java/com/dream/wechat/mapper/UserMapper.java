package com.dream.wechat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import com.dream.wechat.model.Activity;
import com.dream.wechat.model.Thumb;
import com.dream.wechat.model.User;
import com.dream.wechat.model.UserImg;
import com.dream.wechat.model.UserLite;

public interface UserMapper {
	@Insert("insert into user (openid, unionid, nickname, sex, country, city, province, language,"
			+ "headimgurl, subscribe, subscribe_time, updatetime) values (#{openid}, #{unionid},"
			+ "#{nickname}, #{sex}, #{country}, #{city}, #{province}, #{language}, #{headimgurl},"
			+ "#{subscribe}, unix_timestamp(), now() )")
	@SelectKey(before=false,keyProperty="User.id",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="SELECT MAX(id) AS id")  
	public Integer insertUser(User u);
	
	@Update("update user set openid=#{openid}, unionid=#{unionid}, nickname=#{nickname}, sex=#{sex},"
			+ "country=#{country}, city=#{city}, province=#{province}, language=#{language},"
			+ "headimgurl=#{headimgurl}, subscribe=#{subscribe}, subscribe_time=#{subscribe_time}, "
			+ "updatetime=now() where id=#{id}")
	public void updateUser(User u);
	
	@Update("update user set unionid=#{unionid}, nickname=#{nickname}, sex=#{sex},"
			+ "country=#{country}, city=#{city}, province=#{province}, language=#{language},"
			+ "headimgurl=#{headimgurl}, subscribe=#{subscribe}, subscribe_time=#{subscribe_time}, "
			+ "updatetime=now() where openid=#{openid}")
	public void updateUserByOpenid(User u);
	
	
	@Update("update user set unionid=#{unionid}, nickname=#{nickname}, sex=#{sex},"
			+ "country=#{country}, city=#{city}, province=#{province}, language=#{language},"
			+ "headimgurl=#{headimgurl},  "
			+ "updatetime=now() where openid=#{openid}")
	public void updateUserPartByOpenid(User u);
	
	@Select("select * from user where id=#{id}")
	public User getUser(@Param("id") int id);
	
	@Select("select id, nickname, sex, headimgurl as headimg from user where id=#{id} ")
	public UserLite getUserLite(@Param("id") int id);
	
	@Select("select ifnull(sum(likes),0) from userimg where authorid=#{id}")
	public int getTotalLikes(@Param("id") int id);
	
	@Select("select count(*) from userimg where authorid=#{id}")
	public int getTotalImages(@Param("id") int id);
	
	@Select("select count(*) from userfollowing where who=#{id}")
	public int getTotalFans(@Param("id") int id);
	
	@Select("select count(*) from userfollowing where follower=#{id}")
	public int getTotalFollow(@Param("id") int id);
	
	@Select("select count(distinct aid) from userimg where authorid=#{id}")
	public int getTotalActivities(@Param("id") int id);
	
//	@Delete("delete from user where id=#{id}")
//	public void deleteUser(@Param("id") int id);
	
	@Select("select * from user where openid=#{openid}")
	public User getUserByOpenid(@Param("openid") String openid);
	
	@Update("update user set subscribe=0 where openid=#{openid}")
	public void unsubscribe(@Param("openid") String openid);
	
	@Select("select distinct b.id as id,b.name as name,b.logo as logo, b.description"
			+ " as description from userimg as a, activity as b where a.aid=b.id and "
			+ "a.authorId=#{id} order by id desc")
	public List<Activity> getActivitiesByUser(@Param("id") int id);
	
	@Select("select id,thumb from userimg where authorid=#{id} order by id desc")
	public List<Thumb> getThumbsByUser(@Param("id") int id);
	
	@Select("select a.id as id, a.sex as sex, a.nickname as nickname, a.headimgurl as headimg "
			+ "from user as a, userfollowing as b where a.id=b.who and b.follower=#{id}")
	public List<UserLite> getFollowing(@Param("id") int id);
	
	@Select("select a.id as id, a.sex as sex, a.nickname as nickname, a.headimgurl as headimg "
			+ "from user as a, userfollowing as b where a.id=b.follower and b.who=#{id}")
	public List<UserLite> getFans(@Param("id") int id);
	
	@Select("select a.id as id, a.name as name, a.description as description,"
			+ " a.file as file, a.authorId as authorId, b.nickname as authorName,"
			+ " b.sex as authorSex, b.headimgurl as authorHead, a.uploadTime as uploadTime,"
			+ " a.likes as likes, a.comments as comments, a.shared as shared,a.aid as aId"
			+ " from userimg as a, user as b, userfollowing as c where a.authorId=b.id and"
			+ " c.who=a.authorId and c.follower=#{id} order by id desc")
	public List<UserImg> getUserFollowingImg(@Param("id") int id);
	
	@Select("(select a.id as id, a.name as name, a.description as description,"
			+ " a.file as file, a.authorId as authorId, b.nickname as authorName,"
			+ " b.sex as authorSex, b.headimgurl as authorHead, a.uploadTime as uploadTime,"
			+ " a.likes as likes, a.comments as comments, a.shared as shared,a.aid as aId"
			+ " from userimg as a, user as b, userfollowing as c where a.authorId=b.id and"
			+ " c.who=a.authorId and c.follower=#{id})"
			+ " union all"
			+ " (select a.id as id, a.name as name, a.description as description,"
			+ " a.file as file, a.authorId as authorId, b.nickname as authorName,"
			+ " b.sex as authorSex, b.headimgurl as authorHead, a.uploadTime as uploadTime,"
			+ " a.likes as likes, a.comments as comments, a.shared as shared,a.aid as aId"
			+ " from userimg as a, user as b where "
			+ " a.authorid=#{id} and a.authorId=b.id)"
			+ " order by id desc")
	public List<UserImg> getInterestedImg(@Param("id") int id);
	
	@Select("select a.id as id, a.name as name, a.description as description, "
			+ "a.file as file, a.authorId as authorId, b.nickname as authorName, "
			+ "b.sex as authorSex, b.headimgurl as authorHead, a.uploadTime as uploadTime, "
			+ "a.likes as likes, a.comments as comments, a.shared as shared,a.aid as aId "
			+ "from userimg as a, user as b where a.authorId=b.id and a.authorId=#{id} "
			+ "order by id desc;")
	public List<UserImg> getUserImages(@Param("id") int id);
	
	@Insert("insert into userfollowing (who, follower, createtime) values (#{follow}, #{id}, now())")
	@Options(useGeneratedKeys=true, keyProperty="id")
	public int follow(@Param("id") int id, @Param("follow") int follow);
	
	@Delete("delete from userfollowing where who=#{follow} and follower=#{id}")
	public int unfollow(@Param("id") int id, @Param("follow") int follow);
	
	@Select("select count(*) from userfollowing where who=#{who} and follower=#{id}")
	public int followed(@Param("id") int me, @Param("who") int her);
}

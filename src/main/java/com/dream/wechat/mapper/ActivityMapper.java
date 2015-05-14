package com.dream.wechat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.dream.wechat.model.Activity;
import com.dream.wechat.model.Thumb;

public interface ActivityMapper {
	@Select("select * from activity where id=#{id}")
	Activity getActivity(@Param("id") int id);
	
	@Insert("insert into activity ( name, description ) values ( #{name}, #{description})")
	void insertActivity(Activity a);
	
	@Update("update activity set name=#{name}, description=#{description} where id=#{id}")
	void updateActivity(Activity a);
	
	@Select("select * from activity order by orderby limit #{start}, #{limit}")
	List<Activity> getActivitiesByPage(@Param("start") int start, @Param("limit") int limit);
	
	@Select("select * from activity order by orderby ")
	List<Activity> getActivities();
	
	@Select("select count(*) from userimg where aid=#{id}")
	int totalImgByActivity(int id);
	
	@Select("select a.id as id, a.thumb as thumb from userimg as a, activity as b "
			+ "where a.aId = b.id and b.id=#{id} order by a.id desc")
	List<Thumb> getLatestThumbs(int id);
	
	@Select("select a.id as id, a.thumb as thumb from userimg as a, activity as b "
			+ "where a.aId = b.id and b.id=#{id} order by a.likes desc, a.comments desc, a.shared desc")
	List<Thumb> getHottestThumbs(int id);
}

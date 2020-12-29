package com.ncse.zhhygis.mapper;

import com.ncse.zhhygis.entity.SpeedAlarmInfos;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpeedAlarmInfosMapper {
    int deleteByPrimaryKey(String id);

    int insert(SpeedAlarmInfos record);

    int insertSelective(SpeedAlarmInfos record);

    SpeedAlarmInfos selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SpeedAlarmInfos record);

    int updateByPrimaryKey(SpeedAlarmInfos record);
	
	 List<SpeedAlarmInfos> selectByParms(Map<String, String> pramsMap);
}
package com.ncse.zhhygis.mapper;

import com.ncse.zhhygis.entity.HeightAlarmInfos;
import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface HeightAlarmInfosMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HeightAlarmInfos record);

    int insertSelective(HeightAlarmInfos record);

    HeightAlarmInfos selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HeightAlarmInfos record);

    int updateByPrimaryKey(HeightAlarmInfos record);

    List<HeightAlarmInfos> selectByParms(Map<String, String> pramsMap);
}
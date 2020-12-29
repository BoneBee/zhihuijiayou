package com.ncse.zhhygis.mapper;

import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TransboundaryAlarmInfosMapper {
    int deleteByPrimaryKey(String id);

    int insert(TransboundaryAlarmInfos record);

    int insertSelective(TransboundaryAlarmInfos record);

    TransboundaryAlarmInfos selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TransboundaryAlarmInfos record);

    int updateByPrimaryKey(TransboundaryAlarmInfos record);
	
	List<TransboundaryAlarmInfos> selectByParms(Map<String, String> pramsMap);
}
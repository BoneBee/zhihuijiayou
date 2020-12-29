package com.ncse.zhhygis.mapper;

import com.ncse.zhhygis.entity.CarIntoAreaalarmInfos;
import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface CarIntoAreaalarmInfosMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarIntoAreaalarmInfos record);

    int insertSelective(CarIntoAreaalarmInfos record);

    CarIntoAreaalarmInfos selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarIntoAreaalarmInfos record);

    int updateByPrimaryKey(CarIntoAreaalarmInfos record);

    List<CarIntoAreaalarmInfos> selectByParms(Map<String, String> pramsMap);
}
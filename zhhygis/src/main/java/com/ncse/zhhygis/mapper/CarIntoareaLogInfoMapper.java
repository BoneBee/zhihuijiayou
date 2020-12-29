package com.ncse.zhhygis.mapper;

import com.ncse.zhhygis.entity.CarIntoareaLogInfo;
import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface CarIntoareaLogInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarIntoareaLogInfo record);

    int insertSelective(CarIntoareaLogInfo record);

    CarIntoareaLogInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarIntoareaLogInfo record);

    int updateByPrimaryKey(CarIntoareaLogInfo record);

    List<CarIntoareaLogInfo> selectByParms(Map<String, String> pramsMap);
}
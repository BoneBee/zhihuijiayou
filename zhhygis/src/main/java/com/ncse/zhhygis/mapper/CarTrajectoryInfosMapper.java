package com.ncse.zhhygis.mapper;

import com.ncse.zhhygis.entity.CarTrajectoryInfos;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CarTrajectoryInfosMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarTrajectoryInfos record);

    int insertSelective(CarTrajectoryInfos record);

    CarTrajectoryInfos selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarTrajectoryInfos record);

    int updateByPrimaryKey(CarTrajectoryInfos record);
	
	List<CarTrajectoryInfos> selectByParms(Map<String, String> pramsMap);

    int deleteByDate(String updatetime);
}
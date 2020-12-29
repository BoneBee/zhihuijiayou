package com.ncse.zhhygis.service;


import com.ncse.zhhygis.entity.CarTrajectoryInfos;

import java.util.List;
import java.util.Map;

/**
 * 车辆轨迹信息
 */
public interface CarTrajectoryInfosService {

    CarTrajectoryInfos selectByPrimaryKey(Integer id);

    List<CarTrajectoryInfos> selectByParms(Map<String,String> pramsMap);

    int deleteByDate(String updatetime);
}

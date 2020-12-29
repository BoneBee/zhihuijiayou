package com.ncse.zhhygis.service;

import com.ncse.zhhygis.entity.SpeedAlarmInfos;

import java.util.List;
import java.util.Map;

/**
 * 超速报警信息
 */
public interface SpeedAlarmInfosService {

    SpeedAlarmInfos selectByPrimaryKey(String id);

    int addSpeedAlarmInfos(SpeedAlarmInfos speedAlarmInfos);

    List<SpeedAlarmInfos> selectByParms(Map<String,String> pramsMap);
}

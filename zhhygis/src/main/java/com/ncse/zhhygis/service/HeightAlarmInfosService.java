package com.ncse.zhhygis.service;

import com.ncse.zhhygis.entity.CarIntoAreaalarmInfos;
import com.ncse.zhhygis.entity.HeightAlarmInfos;

import java.util.List;
import java.util.Map;

/**
 * 超高报警
 */
public interface HeightAlarmInfosService {

    List<HeightAlarmInfos> selectByParms(Map<String, String> pramsMap);
}

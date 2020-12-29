package com.ncse.zhhygis.service;

import com.ncse.zhhygis.entity.CarIntoAreaalarmInfos;
import com.ncse.zhhygis.entity.CarIntoareaLogInfo;

import java.util.List;
import java.util.Map;

/**
 * 进出区域报警
 */
public interface CarIntoareaLogInfoService {

    List<CarIntoareaLogInfo> selectByParms(Map<String, String> pramsMap);
}

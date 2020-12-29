package com.ncse.zhhygis.service;

import com.ncse.zhhygis.entity.CarIntoAreaalarmInfos;
import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;

import java.util.List;
import java.util.Map;

/**
 * 超时报警
 */
public interface CarIntoAreaalarmInfosService {

    /**
     * 根据条件查询超时报警信息
     * @param pramsMap
     * @return
     */
    List<CarIntoAreaalarmInfos> selectByParms(Map<String, String> pramsMap);
}

package com.ncse.zhhygis.service;

import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;

import java.util.List;
import java.util.Map;

/**
 * 越界报警信息
 */
public interface TransboundaryAlarmInfosService {

    TransboundaryAlarmInfos selectByPrimaryKey(String id);

    int addTransboundaryAlarmInfos(TransboundaryAlarmInfos transboundaryAlarmInfos);

    List<TransboundaryAlarmInfos> selectByParms(Map<String,String> pramsMap);
}

package com.ncse.zhhygis.service;


import com.ncse.zhhygis.entity.ParkBaseInfo;

/**
 * 获取机场信息
 */
public interface ParkBaseInfoService {

    ParkBaseInfo selectByAircode(String aircode);

}

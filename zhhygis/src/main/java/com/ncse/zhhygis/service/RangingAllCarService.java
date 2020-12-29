package com.ncse.zhhygis.service;

import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [测距]
 * Date:  2018/11/30 16 10
 *
 * @author Xugn
 * @version 1.0.0
 */
public interface RangingAllCarService {


    //根据所有车辆和机位号，获取导航及测距信息
    public Map getAllSeatRanging(Map map);
}

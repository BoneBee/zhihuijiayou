package com.ncse.zhhygis.service;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [功能名称]
 * Description:  [油井信息]
 * Date:  2018/11/26 10 54
 *
 * @author Xugn
 * @version 1.0.0
 */
public interface MapOilWellService {

    /**
     * 获取油井信息
     * @param airCode 机位编号
     * @return
     */
    List getOilWell(String airCode,String aircraft);
}

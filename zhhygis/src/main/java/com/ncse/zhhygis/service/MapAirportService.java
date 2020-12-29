package com.ncse.zhhygis.service;


import java.util.Map;

/**
 * 关于机场地图方面相关操作（机位和油井）
 * @author Administrator
 *
 */
public interface MapAirportService {
    
	/**
	 * 机位添加
	 * @param paramMap
	 */
    void geoAirportRegionAdd(Map paramMap) throws Exception;

    /**
	 * 油井添加
	 * @param paramMap
	 */
    void geoOilRegionAdd(Map paramMap) throws Exception;

    /**
     * 删除机位面
     * @param paramMap
     */
    void deleteAirportRegion(Map paramMap);

    /**
     * 获取机位面
     * @param code
     * @return
     */
    Map getAirportRegion(String code);
    
    /**
     * 修改机位面
     * @param paramMap
     */
    void updateAirportRegion(Map paramMap);
    
    /**
     * 获取油井
     * @param code
     * @return
     */
    Map getOilRegion(String code);

    /**
     * 获取单个机位面详细信息
     * @param code
     * @return
     */
    Map getAirportGeoRegionById(Map paramMap);
}
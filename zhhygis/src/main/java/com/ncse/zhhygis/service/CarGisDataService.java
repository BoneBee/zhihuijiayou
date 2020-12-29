package com.ncse.zhhygis.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CarGisDataService {
	/**
	 * 汽车gis数据处理
	 * @param gisdata    gis数据
	 * @param datatype  数据类型，real 最新数据，noReal 历史数据
	 * @return
	 */
    String carGisDataHandle(String gisdata,String datatype) throws IOException;

    void carGisDataHandle(List<ConsumerRecord<?, ?>> records) throws IOException;
    
    /**
     * 离线数据处理
     * @param aircodeList
     */
    void offlinecheck(List<String> aircodeList);
    //监听油量改变量，并推给前端
	void oilChange(String value,String key);


}

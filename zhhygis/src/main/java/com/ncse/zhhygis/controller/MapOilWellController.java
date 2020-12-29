package com.ncse.zhhygis.controller;

import com.ncse.zhhygis.entity.OilEntity;
import com.ncse.zhhygis.service.MapManagerService;
import com.ncse.zhhygis.service.MapOilWellService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.zh.bean.login.MyStaff;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ClassName:  [MapOilWellController]
 * Description:  [地图油井信息查询]
 * Date:  2018/11/26 10 36
 *
 * @author Xugn
 * @version 1.0.0
 */
/**自动返回的是json格式数据***/
@RestController
@RequestMapping("mapOilWell")
@CrossOrigin(allowCredentials = "true")
public class MapOilWellController {

    private final Logger log = Logger.getLogger(this.getClass());
    @Autowired
    private MapOilWellService mapOilWellService;

    /**
     * 根据机位号获取油井信息（提供客户接口）
     * @param oilEntity staff aircode 机场编码
     * @param oilEntity aircraft 机位编码
     * @return
     */
    @RequestMapping("/getOilWell")
    public ServerResponse getOilWell(@RequestBody OilEntity oilEntity) {
    	
        //log.info("getOilWell机位编码："+staff.getStaffAirportCode());
        List<String> oilWell= mapOilWellService.getOilWell(oilEntity.getLoginUserIn().getStaffAirportCode(),oilEntity.getAircraft());
        log.info("getOilWell查询结果："+oilWell);
        return ServerResponse.createBySuccess(oilWell);
    }
    
    
}

package com.ncse.zhhygis.controller;

import com.alibaba.fastjson.JSONObject;
import com.ncse.zhhygis.service.RangingAllCarService;
import com.ncse.zhhygis.service.SeatRangingService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;
import com.zh.bean.login.MyStaff;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * ClassName:  [功能名称]
 * Description:  [获取所有车辆距离机位距离]
 * Date:  2019/1/23 13 40
 *
 * @author Xugn
 * @version 1.0.0
 */
@RestController
/**自动返回的是json格式数据***/
@RequestMapping("rangingAllCar")
@CrossOrigin(allowCredentials = "true")
public class RangingAllCarController {
    @Autowired
    private RangingAllCarService rangingAllCarService;

    @Resource
    private RedisUtils redisUtils;

    private final Logger log = Logger.getLogger(this.getClass());
    /**
     * 根据所有车辆和机位号，获取导航及测距信息（提供客户接口）
     * @param map aircode 机场,carcode 车牌号，aircraft 机位
     * @return
     */
    @RequestMapping("/getAllSeatRanging")
    public ServerResponse getAllSeatRanging(MyStaff staff, @RequestParam Map map) {
        List resultList = new ArrayList();
        try {
            Set<String> keys = redisUtils.getKeys(staff.getStaffAirportCode());
//            Set<String> keys = redisUtils.getKeys("2901");
            for (String key : keys) {
                String resoult = redisUtils.get(key);
                JSONObject resoultJs = JSONObject.parseObject(resoult);
                map.put("carcode",resoultJs.get("hp"));
                Map m = new HashMap();
                if (resoultJs.get("jd")!=null && resoultJs.get("wd")!=null) {
                    m = rangingAllCarService.getAllSeatRanging(map);
                    m.put("carcode",resoultJs.get("hp"));
                    resultList.add(m);
                }
            }
        }catch (Exception e){
            log.error(e);
        }
        return ServerResponse.createBySuccess(resultList);
    }
}

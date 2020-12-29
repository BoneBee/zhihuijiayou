package com.ncse.zhhygis.controller;

import com.ncse.zhhygis.service.SeatRangingService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;
import com.zh.bean.login.MyStaff;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:  [功能名称]
 * Description:  [测距及判断是否走错机位]
 * Date:  2018/11/30 15 15
 *
 * @author Xugn
 * @version 1.0.0
 */
@RestController
/**自动返回的是json格式数据***/
@RequestMapping("ranging")
@CrossOrigin(allowCredentials = "true")
public class SeatRangingController {

    @Autowired
    private SeatRangingService seatRangingService;
    @Resource
    private RedisUtils redisUtils;

    private final Logger log = Logger.getLogger(this.getClass());
//    @Autowired
//    @Resource(name = "DefaultStringRedisTemplate")
//    private StringRedisTemplate defaultStringRedis;
//
//    @Autowired
//    @Resource(name = "ArticleStringRedisTemplate")
//    private StringRedisTemplate articleStringRedis;
//
//    @Autowired
//    @Resource(name = "DefaultRedisTemplate")
//    private RedisTemplate defaultRedis;
//
//    @Autowired
//    @Resource(name = "ArticleRedisTemplate")
//    private RedisTemplate articleRedis;

    /**
     * 根据车辆编号和机位号，获取导航及测距信息（提供客户接口）
     * @param map aircode 机场,carcode 车牌号，aircraft 机位
     * @return
     */
        @RequestMapping("/getSeatRanging")
    public ServerResponse getSeatRanging(MyStaff staff, @RequestParam Map map) {
//        map.put("aircode", staff.getStaffAirportCode());
        Map m = new HashMap();
        try {
            m=seatRangingService.getSeatRanging(map);
        }catch (Exception e){
            log.error(e);
        }
        return ServerResponse.createBySuccess(m);
    }


    /**
     * 根据机位号和车辆gps信息，判断是否走错机位（提供客户接口）
     * @param map aircode 机场,carcode 车牌号，aircraft 机位
     * @return
     */
    @RequestMapping("/wrongPosition")
    public ServerResponse wrongPosition(MyStaff staff, @RequestParam Map map) {
//        map.put("aircode", staff.getStaffAirportCode());
        Map m=seatRangingService.wrongPosition(map);
        return ServerResponse.createBySuccess(m);
    }

    /**
     * 后台管理测距方法
     * @param map aircode 机场,beginxy 开始坐标，endxy 结束坐标
     * @return
     */
    @RequestMapping("/getAirRanging")
    public ServerResponse getAirRanging(MyStaff staff, @RequestParam Map map) {
        map.put("aircode", staff.getStaffAirportCode());
        Map m = new HashMap();
        try {
            m=seatRangingService.getAirRanging(map);
        }catch (Exception e){
            log.error(e);
            return ServerResponse.createByErrorMessage("路径规划异常信息是="+e.getMessage());
        }
        return ServerResponse.createBySuccess(m);
    }

    @RequestMapping("/addRedis")
    public ServerResponse redisdd() {
        //测试是否超高
        /*boolean aa = redisUtils.set("cargps:2901:001","{\n" +
                "\"flag\": 0,\n" +
                "\"gd\": \"3\",\n" +
                "\"hp\": \"001\",\n" +
                "\"jd\": \"102.93296771085447\",\n" +
                "\"stars\": 0,\n" +
                "\"timeStamp\": \"2018-12-04 16:05:10\",\n" +
                "\"wd\": \"25.103526043848568\"\n" +
                "}",3600L);*/
        //测试是否走错机位
        boolean aa = redisUtils.set("cargps:2901:001","{\n" +
                "\"flag\": 0,\n" +
                "\"gd\": \"3\",\n" +
                "\"hp\": \"001\",\n" +
                "\"jd\": \"102.9290309227846\",\n" +
                "\"stars\": 0,\n" +
                "\"timeStamp\": \"2018-12-04 16:05:10\",\n" +
                "\"wd\": \"25.095597833001097\"\n" +
                "}",3600L);
        return ServerResponse.createBySuccess(aa);
    }

   /* @RequestMapping("/addRedis")
    public ServerResponse redisdd(MyStaff staff) {
        defaultStringRedis.opsForValue().set("cargps:111:001","{\n" +
                "\"flag\": 0,\n" +
                "\"gd\": \"6\",\n" +
                "\"hp\": \"001\",\n" +
                "\"jd\": \"102.9290309227846\",\n" +
                "\"stars\": 0,\n" +
                "\"timeStamp\": \"2018-12-04 16:05:10\",\n" +
                "\"wd\": \"25.095597833001097\"\n" +
                "}");
        defaultStringRedis.expire("cargps:111:001",3600L,TimeUnit.SECONDS);//设置过期时间1小时
//        articleStringRedis.opsForValue().set("zzzz","22222222222");
//        System.out.println("defaultStringRedis的数据======"+defaultStringRedis.opsForValue().get("cargps:111:001"));
//        System.out.println("defaultStringRedis生存时间======"+defaultStringRedis.getExpire("cargps:111:001"));
//        System.out.println("articleStringRedis======"+articleRedis.opsForValue().get("zzzz"));
        Map map = new HashMap();
        map.put("key","cargps:111:001");
        map.put("time",defaultStringRedis.getExpire("cargps:111:001"));
        map.put("value",defaultStringRedis.opsForValue().get("cargps:111:001"));
        return ServerResponse.createBySuccess(map);
    }*/
}

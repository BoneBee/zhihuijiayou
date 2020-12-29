package com.ncse.zhhygis.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ncse.zhhygis.entity.CarTrajectoryInfos;
import com.ncse.zhhygis.entity.DateUtil;
import com.ncse.zhhygis.entity.SpeedAlarmInfos;
import com.ncse.zhhygis.service.CarTrajectoryInfosService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.zh.bean.login.MyStaff;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆轨迹信息
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("/carTrajectoryInfos")
@CrossOrigin(allowCredentials = "true")
public class CarTrajectoryInfosController {

    @Autowired
    private CarTrajectoryInfosService carTrajectoryInfosService;

    private final Logger log = Logger.getLogger(this.getClass());

    @RequestMapping("select")
    public ServerResponse list(MyStaff staff,@RequestParam Integer id){
        log.info("select参数为："+id);
        CarTrajectoryInfos carTrajectoryInfos = carTrajectoryInfosService.selectByPrimaryKey(id);
        log.info("select查询结果为："+carTrajectoryInfos);
        return ServerResponse.createBySuccess(carTrajectoryInfos);
    }

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询轨迹信息]
     * @param: [ carnum 车牌号, airportid 机场id, createtimeb 发生时间, createtimee 结束时间, alarmsign 报警标志, drivername 司机姓名]
     * @return: com.ncse.zhhygis.utils.ServerResponse
     */
    @RequestMapping("selectByParms")
    public ServerResponse selectByParms(MyStaff staff,@RequestParam Map parmsMap){
        //时间戳转换成日期格式,解决前端传入时间格式不正确
        String updatetimeb = (String) parmsMap.get("updatetimeb");
        String updatetimee = (String) parmsMap.get("updatetimee");
        if (!StringUtils.isEmpty(updatetimeb) && !StringUtils.isEmpty(updatetimee)){
            String date1 = DateUtil.timeStamp2Date(updatetimeb, "yyyy-MM-dd HH:mm:ss");
            String date2 = DateUtil.timeStamp2Date(updatetimee, "yyyy-MM-dd HH:mm:ss");
            parmsMap.put("updatetimeb",date1);
            parmsMap.put("updatetimee",date2);
        }
        //轨迹列表暂时只查询在线的
        parmsMap.put("isonline","1");

        Map map = new HashMap();
        try{
            log.info("selectByParms查询条件："+parmsMap);
            if (parmsMap.get("pageNum") != null && parmsMap.get("pageSize")!=null){
                //进行分页
                PageHelper.startPage(Integer.parseInt((String) parmsMap.get("pageNum")),Integer.parseInt((String) parmsMap.get("pageSize")));
                map.put("pageSize",parmsMap.get("pageSize"));
                map.put("pageNum",parmsMap.get("pageNum"));
            }
            List<CarTrajectoryInfos> carTrajectoryInfos = carTrajectoryInfosService.selectByParms(parmsMap);
            PageInfo<CarTrajectoryInfos> info = new PageInfo<>(carTrajectoryInfos);
            map.put("count",info.getTotal());//获取数据总条数
            map.put("data",carTrajectoryInfos);
            map.put("code","0");
        }catch (Exception e){
            map.put("code","1");
            log.error(e);
        }
        return ServerResponse.createBySuccess(map);
    }
}
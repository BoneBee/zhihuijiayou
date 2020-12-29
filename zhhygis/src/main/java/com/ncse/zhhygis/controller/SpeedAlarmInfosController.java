package com.ncse.zhhygis.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ncse.zhhygis.entity.DateUtil;
import com.ncse.zhhygis.entity.SpeedAlarmInfos;
import com.ncse.zhhygis.service.SpeedAlarmInfosService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.zh.bean.login.MyStaff;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 超速报警信息
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("/speedAlarmInfos")
@CrossOrigin(allowCredentials = "true")
public class SpeedAlarmInfosController {

    @Autowired
    private SpeedAlarmInfosService speedAlarmInfosService;

    private final Logger log = Logger.getLogger(this.getClass());

    @RequestMapping("select")
    public ServerResponse selectByPrimaryKey(MyStaff staff,@RequestParam String id){
        log.info("select参数为："+id);
        SpeedAlarmInfos speedAlarmInfos = speedAlarmInfosService.selectByPrimaryKey(id);
        log.info("select查询结果为："+speedAlarmInfos);
        return ServerResponse.createBySuccess(speedAlarmInfos);
    }

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询超速报警信息]
     * @param: [ carnum 车牌号, createtimeb 发生时间, createtimee 结束时间, isalarm 是否报警 ：1 已报，0 未报, drivername 司机姓名]
     * @return: com.ncse.zhhygis.utils.ServerResponse
     */
    @RequestMapping("selectByParms")
    public ServerResponse selectByParms(MyStaff staff,@RequestParam Map parmsMap){
        //时间戳转换成日期格式,解决前端传入时间格式不正确
        String createtimeb = (String) parmsMap.get("createtimeb");
        String createtimee = (String) parmsMap.get("createtimee");
        if (!StringUtils.isEmpty(createtimeb) && !StringUtils.isEmpty(createtimee)){
            String date1 = DateUtil.timeStamp2Date(createtimeb, "yyyy-MM-dd HH:mm:ss");
            String date2 = DateUtil.timeStamp2Date(createtimee, "yyyy-MM-dd HH:mm:ss");
            parmsMap.put("createtimeb",date1);
            parmsMap.put("createtimee",date2);
        }
        //暂定只查询在线的信息
        parmsMap.put("isonline","1");
        Map map = new HashMap();
        try{
            if (parmsMap.get("pageNum") != null && parmsMap.get("pageSize")!=null){
                //进行分页
                PageHelper.startPage(Integer.parseInt((String) parmsMap.get("pageNum")),Integer.parseInt((String) parmsMap.get("pageSize")));
                map.put("pageSize",parmsMap.get("pageSize"));
                map.put("pageNum",parmsMap.get("pageNum"));
            }
            List<SpeedAlarmInfos> speedAlarmInfos = speedAlarmInfosService.selectByParms(parmsMap);
            PageInfo<SpeedAlarmInfos> info = new PageInfo<>(speedAlarmInfos);
            map.put("count",info.getTotal());//获取数据总条数
            map.put("data",speedAlarmInfos);
            map.put("code","0");
        }catch (Exception e){
            map.put("code","1");
            log.error(e);
        }
        return ServerResponse.createBySuccess(map);
    }
  /*  @RequestMapping("addSpeedAlarmInfos")
    public ServerResponse addSpeedAlarmInfos(String id,String carnum,String airportId,String createTime,String longiTude,String latiTude,String alarmSign,String driveRid,String driverName){
        log.info("addSpeedAlarmInfos方法参数为："+id);
        SpeedAlarmInfos speedAlarmInfos = new SpeedAlarmInfos();
        int resoult = speedAlarmInfosService.addSpeedAlarmInfos(speedAlarmInfos);
        log.info("addSpeedAlarmInfos添加结果为："+resoult);
        return ServerResponse.createBySuccess(resoult);
    }*/
}
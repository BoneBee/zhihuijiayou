package com.ncse.zhhygis.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ncse.zhhygis.entity.CarTrajectoryInfos;
import com.ncse.zhhygis.entity.TransboundaryAlarmInfos;
import com.ncse.zhhygis.service.TransboundaryAlarmInfosService;
import com.ncse.zhhygis.utils.ServerResponse;
import com.zh.bean.login.MyStaff;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 越界报警信息
 */
@RestController/**自动返回的是json格式数据***/
@RequestMapping("/transboundaryAlarmInfos")
@CrossOrigin(allowCredentials = "true")
public class TransboundaryAlarmInfosController {

    @Autowired
    private TransboundaryAlarmInfosService transboundaryAlarmInfosService;

    private final Logger log = Logger.getLogger(this.getClass());

    @RequestMapping("select")
    public ServerResponse list(MyStaff staff,@RequestParam String id){
        log.info("select参数为："+id);
        TransboundaryAlarmInfos transboundaryAlarmInfos = transboundaryAlarmInfosService.selectByPrimaryKey(id);
        log.info("select查询结果为："+transboundaryAlarmInfos);
        return ServerResponse.createBySuccess(transboundaryAlarmInfos);
    }

    /***
     * MethodName:  [selectByParms]
     * Description:  [根据条件查询越界报警信息]
     * @param: [ areaid 区域id，carnum 车牌号, createtimeb 发生时间, createtimee 结束时间, isalarm 是否报警 ：1 已报，0 未报, drivername 司机姓名]
     * @return: com.ncse.zhhygis.utils.ServerResponse
     */
    @RequestMapping("selectByParms")
    public ServerResponse selectByParms(MyStaff staff,@RequestParam  Map parmsMap){
        Map map = new HashMap();
        try{
            log.info("selectByParms查询条件："+parmsMap);
            if (parmsMap.get("pageNum") != null && parmsMap.get("pageSize")!=null){
                //进行分页
                PageHelper.startPage(Integer.parseInt((String) parmsMap.get("pageNum")),Integer.parseInt((String) parmsMap.get("pageSize")));
            }
            List<TransboundaryAlarmInfos> transboundaryAlarmInfos = transboundaryAlarmInfosService.selectByParms(parmsMap);
            PageInfo<TransboundaryAlarmInfos> info = new PageInfo<>(transboundaryAlarmInfos);
            map.put("count",info.getTotal());//获取数据总条数
            map.put("data",transboundaryAlarmInfos);
            map.put("code","0");
        }catch (Exception e){
            map.put("code","1");
            log.error(e);
        }
        return ServerResponse.createBySuccess(map);
    }
}
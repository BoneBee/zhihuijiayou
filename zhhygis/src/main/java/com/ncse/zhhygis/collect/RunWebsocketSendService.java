package com.ncse.zhhygis.collect;

import com.alibaba.fastjson.JSON;
import com.ncse.zhhygis.service.CarAirInterfaceService;
import com.ncse.zhhygis.service.CarGisDataService;
import com.ncse.zhhygis.service.CarTrajectoryInfosService;
import com.ncse.zhhygis.utils.baseUtils.DateUtil;
import com.ncse.zhhygis.utils.baseUtils.MessageUtils;
import com.ncse.zhhygis.utils.baseUtils.TokenUtil;
import com.zh.bean.login.MyStaff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


@Service
@Transactional
public class RunWebsocketSendService{
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CarAirInterfaceService carAirInterfaceService;
    @Autowired
    private CarTrajectoryInfosService carTrajectoryInfosService;
    @Autowired
    private CarGisDataService carGisDataService;

    @Autowired
    private UserLoginService userLoginService;

    /*Timer timer = new Timer();
    Timer timer2 = new Timer();
    Timer timer3 = new Timer();

    @Override
    public void run(ApplicationArguments args) {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
//            		log.info("开始推送..");
                    tjWebSendInfo();
                    Thread.sleep(10000);
//					log.info("推送结束..");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    log.error("#推送异常#" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 10000, 5000);


		ArrayList<String[]> list=new ArrayList();
		list.add(new String[]{"25.10666319895339", "102.94283866882326"});
		list.add(new String[]{"25.10413722870487", "102.94107913970949"});
		list.add(new String[]{"25.101650068558666", "102.939190864563"});
		int t=0;
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                try {
                    offlineHand();
                } catch (Exception e) {
                    log.info("报警程序问题：" + e.getMessage());
                    // TODO: handle exception
                }
            	JSONObject json=JSON.parseObject("{\"airport\":\"111\",\"bool_data\":\"\",\"wd\":\"25.10666319895339\",\"timeStamp\":\"2018-11-14 15:01:34\",\"termVer\":\"1.0\",\"gd\":\"\",\"hp\":\"民航C6602\",\"jd\":\"102.94283866882326\",\"num_data\":\"0.0,0.0,2.1\",\"stars\":0,\"flag\":0,\"code_param\":0,\"code\":-1}");
            	try {
					WebSocketServer.sendInfo(JSON.toJSONString(json) ,  "111carGis");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }, 10000, 10 * 60 * 1000);

        timer3.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                try {
                    deleteHistry();
                } catch (Exception e) {
                    log.info("定时删除历史数据程序问题：" + e.getMessage());
                }
            }
        }, 10000, 24 * 60 * 60 * 1000);
    }*/

    /**
     * 推送统计信息
     */
    @Scheduled(initialDelay = 10000, fixedRate = 5000)
    public void tjWebSend(){
        try {
            tjWebSendInfo();
            Thread.sleep(10000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("#推送异常#" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 报警程序处理
     */
    /*@Scheduled(initialDelay = 10000, fixedRate = 10 * 60 * 1000)
    public void offLine(){
        try {
            offlineHand();
        } catch (Exception e) {
            log.error("报警程序问题：" + e.getMessage());
            e.printStackTrace();
        }
        JSONObject json=JSON.parseObject("{\"airport\":\"111\",\"bool_data\":\"\",\"wd\":\"25.10666319895339\",\"timeStamp\":\"2018-11-14 15:01:34\",\"termVer\":\"1.0\",\"gd\":\"\",\"hp\":\"民航C6602\",\"jd\":\"102.94283866882326\",\"num_data\":\"0.0,0.0,2.1\",\"stars\":0,\"flag\":0,\"code_param\":0,\"code\":-1}");
        try {
            WebSocketServer.sendInfo(JSON.toJSONString(json) ,  "111carGis");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 定时删除历史数据
     */
    @Scheduled(initialDelay = 10000, fixedRate = 24 * 60 * 60 * 1000)
    public void deleteHistory(){
        try {
            deleteHistry();
        } catch (Exception e) {
            log.error("定时删除历史数据程序问题：" + e.getMessage());
        }
    }

    private void offlineHand() {

        List<String> sids = new ArrayList<>();
        Map<String, MyStaff> users = userLoginService.getUsers();
        Iterator<Map.Entry<String, MyStaff>> entries = users.entrySet().iterator();
        Map map;
        String msg;
        Entry<String, MyStaff> entry;
        String aircode;
        while (entries.hasNext()) {
            entry = entries.next();
            aircode = entry.getValue().getStaffAirportCode();
            if (!sids.contains(aircode)) {
                sids.add(aircode);
            }
        }
        if (sids.size() > 0) {
            carGisDataService.offlinecheck(sids);
        }


    }

    //定时清理历史记录
    private void deleteHistry() {
        Date firstMonth = DateUtil.getFirstMonth(3);
        String updatetime = DateUtil.getDateTimeFormat(firstMonth);
        int i = carTrajectoryInfosService.deleteByDate(updatetime);
        if (i != 0) {
            log.info("定时清理数据执行成功，执行日期" + updatetime);
        } else if (i == 0) {
            log.info("定时清理数据执行成功，无清理数据，执行日期" + updatetime);
        } else {
            log.info("定时清理数据执行失败，执行日期" + updatetime);
        }
    }

    private void tjWebSendInfo() throws IOException {
        //websockert推送，先判断缓存
        String token = "";
        synchronized (TokenUtil.class) {
            token = TokenUtil.getMap().get("token");
        }
        //List<String> sids=userLoginService.getAirCodes();
        Map<String, MyStaff> users = userLoginService.getUsers();
        Iterator<Map.Entry<String, MyStaff>> entries = users.entrySet().iterator();
        Map map;
        String msg;
        Entry<String, MyStaff> entry;
        String aircode, staffId;
        MyStaff mystaff;
        /* if (StringUtils.isNotEmpty(token)){*/
        while (entries.hasNext()) {
            entry = entries.next();
            //aircode=entry.getValue().getStaffAirportCode();
            staffId = entry.getKey();
            if (staffId == null) continue;
            mystaff = entry.getValue();
            map = new HashMap();
            map.put(MessageUtils.INTERFACE_TJ_AIRCOUNT_NAME, JSON.parseObject(carAirInterfaceService.tjAirCount(mystaff, token)));
            map.put(MessageUtils.INTERFACE_TJ_TASKAMCOUNT_NAME, JSON.parseObject(carAirInterfaceService.tjTaskamCount(mystaff, token)));
            map.put(MessageUtils.INTERFACE_TJ_SOULCOUNT_NAME, JSON.parseObject(carAirInterfaceService.tjSoulCount(mystaff, token)));
            map.put(MessageUtils.INTERFACE_TJ_ALOIL_NAME, JSON.parseObject(carAirInterfaceService.tjAlOil(mystaff, token)));
            map.put(MessageUtils.INTERFACE_TJ_VEHISUM_NAME, JSON.parseObject(carAirInterfaceService.tjVehiSum(mystaff, token)));
            msg = JSON.toJSONString(map);
            WebSocketServer.sendInfo(msg, staffId);
            log.info("推送消息到：" + staffId + ",大屏主页统计接口推送信息为.." + msg);

			/*entry = entries.next();
			if(Geometrist.hasIntersection(region,entry.getValue())) {
				parkList.append(entry.getKey()).append(",");
			}*/
        }
        /*else {
            log.error("获取到的token值是空，请返回登陆");
        }*/


    }

    public void carGisDataSend() {
        List<String> sids = userLoginService.getAirCodes();
        for (String sid : sids) {
            //模拟数据
            String data = "{\"airport\":\"KMG\",\"bool_data\":\"\",\"wd\":\"\",\"timeStamp\":\"2018-11-14 15:01:07\",\"termVer\":\"1.0\",\"gd\":\"\",\"hp\":\"001\",\"jd\":\"\",\"num_data\":\"0.0,0.0\",\"stars\":0,\"flag\":0,\"code_param\":0,\"code\":-1}\r\n" +
                    "{\"airport\":\"KMG\",\"bool_data\":\"\",\"wd\":\"\",\"timeStamp\":\"2018-11-14 15:01:10\",\"termVer\":\"1.0\",\"gd\":\"\",\"hp\":\"001\",\"jd\":\"\",\"num_data\":\"0.0,0.0\",\"stars\":0,\"flag\":0,\"code_param\":0,\"code\":-1}";
        }
    }

}

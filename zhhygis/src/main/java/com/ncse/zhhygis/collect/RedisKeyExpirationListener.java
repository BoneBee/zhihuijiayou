package com.ncse.zhhygis.collect;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.ncse.zhhygis.utils.baseUtils.RedisUtils;
import com.zh.bean.login.MyStaff;


/**
 * 监听所有db的过期事件__keyevent@*__:expired"
 * @author wj
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserLoginService userLoginService;
	
	@Autowired
	private RedisUtils redisUtils;
	
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
        String expiredKey = message.toString();

        List<String> sids=new ArrayList();
		Map<String,MyStaff> users = userLoginService.getUsers();
		Iterator<Map.Entry<String, MyStaff>> entries = users.entrySet().iterator();
		Map map;
		String msg;
		Entry<String, MyStaff> entry;
		String aircode;
		while (entries.hasNext()) {
			entry = entries.next();
			aircode=entry.getValue().getStaffAirportCode();
			if(!sids.contains(aircode)) {
				sids.add(aircode);
			}
		}
		if(sids.size()>0) {
			for(String air:sids) {
				//如果 是这个开头的，清空数据
				if(expiredKey.startsWith(air+"carStatus")){
		            //如果是carStatus:开头的key，进行处理
					String carNum=expiredKey.replace(air+"carStatus", "");
					//清空这个key
					String keys=redisUtils.get(air+"carStatusKey");//获取汽车数据所有key
					if(redisUtils.exists(air+"carStatusKey")) {
						
						redisUtils.remove(air+"carStatusKey");//先删除redis汽车key数据
						if(keys.indexOf(carNum)<0){
							keys =keys.replace(carNum+",", "");
						}
						redisUtils.set(air+"carStatusKey",keys,600L);
					}
					try {
						logger.info("缓存时间到，删除数据，并推送到前端："+expiredKey);
						
						WebSocketServer.sendInfo(carNum ,  air+ "carHide");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
			}
		}
        
    }
}
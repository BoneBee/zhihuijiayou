package com.ncse.zhhygis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableCaching//开启redis缓存
@EnableScheduling
public class ZhhygisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZhhygisApplication.class, args);
		/*ApplicationContext app = SpringApplication.run(ZhhygisApplication.class, args);
	    SpringContextUtil.setApplicationContext(app);*/
	}
	
	
}
package com.ncse.zhhygis.collect;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Workspace;

@EnableAsync
@Configuration
public class MyTaskPoolConfig {
	
	@Bean("myTaskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10); //线程池创建时候初始化的线程数
		executor.setMaxPoolSize(100); //线程池最大线程数，只有在缓冲中列满了之后才会申请超过核心线程数的线程
		executor.setQueueCapacity(300);//用来缓冲执行任务的队列
		executor.setKeepAliveSeconds(60);//允许线程空闲时间，当超过了核心线程数之外的线程在空闲时间到大之后会被销毁
		executor.setThreadNamePrefix("myTaskExecutor-");
		//线程池对拒绝任务的处理策略，callerrun。。当线程池没有处理能力的时候，该策略会直接在execute方法的调用线程中运行
		//被拒绝的任务，如果执行程序已关闭，则会丢弃该任务
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		//设置线程池关闭的时候等待所有任务都完成再继续销毁其他的bean，这样异步任务的销毁会先于redis线程池的销毁
		executor.setWaitForTasksToCompleteOnShutdown(true);
		//设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被管理，而不是阻塞。
		executor.setAwaitTerminationSeconds(60);
		return executor;
	}

	@Bean
	public ExecutorService getExecutorService(){
		return Executors.newFixedThreadPool(8);
	}
}

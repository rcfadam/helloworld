package com.huiju.weixin.WeixinServer.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

/**
 * 创建线程池类
 * @author rencf
 *s2016年11月19日13:28:59
 */
@Component
public class CreateExecutorService {

	private static ExecutorService cacheThreadPool = null;
	
	/**
	 * 获取连接池对象
	 * @return
	 */
	public static ExecutorService getThreadPool(){
		if(cacheThreadPool==null){
			cacheThreadPool = Executors.newCachedThreadPool();
		}
		return cacheThreadPool;
	}
}

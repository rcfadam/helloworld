/*package com.huiju.weixin.WeixinServer.util;


import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
//@EnableCaching
public class RedisUtils extends CachingConfigurerSupport {
	
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	
	@Resource(name="redisTemplate")
	private ValueOperations<Object, Object> valueOper;
	
	@Bean
	public CacheManager cacheManager(RedisTemplate<Object,Object> redisTemplate) {
		CacheManager cacheManager = new RedisCacheManager(redisTemplate);
		return cacheManager;
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
		redisTemplate.setConnectionFactory(factory);
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(redisSerializer);
		redisTemplate.setHashKeySerializer(redisSerializer);
		redisTemplate.setHashKeySerializer(redisSerializer);
		return redisTemplate;
	}
	   
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator(){

			public Object generate(Object object, Method method, Object... objects) {
				StringBuilder sb = new StringBuilder(); 
				sb.append(object.getClass().getName());
				sb.append(method.getName()); 
				for (Object obj : objects) { 
					sb.append(obj.toString()); 
				}
				System.out.println("keyGenerator=" + sb.toString()); 
				return sb.toString();
			}
			
		};
	}



	public void setRedisTemplate(Object key,Object value,long timeout){
	   if(timeout>0)
		valueOper.set(key, value,timeout,TimeUnit.MINUTES);
	   else 
	    valueOper.set(key, value);
	}
	
	public Object getRedisTemplate(Object key){
		return valueOper.get(key);
	}
	 
	
	public void deleteRedisTemplate(Object key){
		redisTemplate.delete(key);
	} 
	 
}
*/
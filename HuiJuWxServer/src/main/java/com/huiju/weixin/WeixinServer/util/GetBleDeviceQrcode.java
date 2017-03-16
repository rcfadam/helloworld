package com.huiju.weixin.WeixinServer.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.huiju.weixin.WeixinServer.conf.StartupConfig;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpService;

 

/***
 * 获取蓝牙设备的二维码图片
 * @author rcfad
 *
 */
@Component
public class GetBleDeviceQrcode {
	@Autowired
	private  StartupConfig startupConfig;
	private Logger logger = Logger.getLogger(GetBleDeviceQrcode.class);
	/**
	 * 获取蓝牙设备ID和二维码
	 * @param access_token
	 * @param product_id
	 * @return
	 * @throws IOException
	 * @throws WxErrorException 
	 */
	public JSONObject getBleDeviceQrcode(String product_id) throws IOException, WxErrorException{
		WxMpService wxMpService = startupConfig.getWxMpService();
		//String url = WxConfig.GET_QRIMAGE_BLE_DEVICE.replace("ACCESS_TOKEN", wxMpService.getAccessToken()).replace("PRODUCT_ID", product_id);
		String qrcode =wxMpService.execute(new SimpleGetRequestExecutor(), "https://api.weixin.qq.com/device/getqrcode?product_id="+product_id,null);
		logger.info("二维码："+qrcode);
		JSONObject deviceQrcode = JSONObject.parseObject(qrcode);
	    logger.info(deviceQrcode.getString("qrticket"));
		return deviceQrcode;
	}
	
	
	
}

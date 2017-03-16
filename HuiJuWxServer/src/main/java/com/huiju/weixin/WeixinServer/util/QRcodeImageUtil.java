package com.huiju.weixin.WeixinServer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.springframework.stereotype.Component;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;


/**
 * 生成二维码的工具类
 * @author rencf
 *2016年11月25日11:12:45
 */
@Component
public class QRcodeImageUtil {

	
	/**
	 * 生成二维码
	 * @param content 二维码内容
	 * @param imageType 图片类型   ImageType.PNG  ImageType.JPG
	 * @return OutputStream
	 */
	 public static  ByteArrayOutputStream createQrcodeStream(String content,ImageType imageType,int width,int height){
		init(width,height);
		 return QRCode.from(content).to(imageType).withSize(width, height).stream();
	 }
	 
	 private static void init(int width, int height) {
		 if(width==0){
			 width=120;
		 }
		 if(height==0){
			 height=120;
		 }
	}
	/**
		 * 生成二维码 并生成文件
		 * @param content 二维码内容
		 * @param imageType 图片类型   ImageType.PNG  ImageType.JPG
		 * @return OutputStream
		 */
		 public static  File createQrcodeFile(String content,ImageType imageType,int width,int height){
			 init(width, height);
			 return QRCode.from(content).to(imageType).withSize(width, height).file();
		 }
}

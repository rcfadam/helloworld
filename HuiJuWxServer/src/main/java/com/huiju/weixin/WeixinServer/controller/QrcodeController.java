package com.huiju.weixin.WeixinServer.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huiju.weixin.WeixinServer.util.QRcodeImageUtil;

import net.glxn.qrgen.image.ImageType;

@Controller
public class QrcodeController {

	@RequestMapping("/qrcode")
	@ResponseBody
	public void qrcode(HttpServletResponse response) throws IOException{
		ByteArrayOutputStream out = QRcodeImageUtil.createQrcodeStream("你好", ImageType.PNG, 120, 120);
		  response.setContentType("image/png");  
         // response.setContentType("text/html");
		 // response.setContentLength(out.size());  
          out.writeTo(response.getOutputStream()); 
	}
}

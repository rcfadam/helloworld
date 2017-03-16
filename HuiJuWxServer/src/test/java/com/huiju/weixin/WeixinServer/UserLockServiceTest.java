package com.huiju.weixin.WeixinServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONObject;
import com.huiju.weixin.WeixinServer.conf.StartupConfig;
import com.huiju.weixin.WeixinServer.model.KeyTb;
import com.huiju.weixin.WeixinServer.model.LockKeyTb;
import com.huiju.weixin.WeixinServer.model.UserLockTb;
import com.huiju.weixin.WeixinServer.service.LockKeyService;
import com.huiju.weixin.WeixinServer.service.UserLockService;
import com.huiju.weixin.WeixinServer.service.UserService;
import com.huiju.weixin.WeixinServer.service.UserService.WxBindDevice.Device;
import com.huiju.weixin.WeixinServer.util.BleDeviceAuth;
import com.huiju.weixin.WeixinServer.util.BleDeviceAuth.KeyDeviceAuthBean;
import com.huiju.weixin.WeixinServer.util.BleDeviceAuth.NewParams;
import com.huiju.weixin.WeixinServer.util.GetBleDeviceQrcode;
import com.huiju.weixin.WeixinServer.util.GetDateUtils;
import com.huiju.weixin.WeixinServer.util.IdGen;
import com.huiju.weixin.WeixinServer.util.WxCustomeMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TomcatSslApplication.class)
@WebAppConfiguration
public class UserLockServiceTest {

	@Autowired
	private UserLockService userLockService;
	
	@Autowired
	private LockKeyService lockKeyService;
	private Logger logger = Logger.getLogger(UserLockServiceTest.class);
	
	@Autowired
	private BleDeviceAuth  bleDeviceAuth;
	
	@Autowired
	private StartupConfig startupConfig;
	
	@Autowired
	private UserService userService;
	
	//@Test
	public void test(){
		String openId="123";
		String deviceId="sdffki_123123_2";
		List<UserLockTb> list = userLockService.getLockByOpenIdAndDeviceId(openId, deviceId);
		logger.debug(list);
	}
	
	//@Test
	public void testTransactional(){
		String openId = "111";
		String lockId = "111";
		UserLockTb userLockTb = new UserLockTb();
		userLockTb.setLockId(lockId);
		userLockTb.setOpenId(openId);
		int i = userLockService.bindDevice(userLockTb);
		logger.debug("事务处理:改变的行数："+i);
	}
	
	//@Test
	public void testDeleteUserLock(){
		String openId = "oJ3fKwDDkQ6ykN1RrXOB3rENJrKY";
		String deviceId="gh_127f2e348655_759937fb6e625ec7";
		userLockService.unbindAllLockKey(openId, userLockService.getLockByOpenIdAndDeviceId(openId, deviceId));
	}
	
	//@Test
	public void testFindLockListByDeviceId(){
		String deviceId="gh_127f2e348655_759937fb6e625ec7";
		List<Object>  locks = lockKeyService.findLocksbydeviceId(deviceId);
		LockKeyTb lockkey = (LockKeyTb) locks.get(0);
		logger.debug(lockkey.getLockId());
	}

	//@Test
	public void testlockList(){
		String openId = "oJ3fKwDDkQ6ykN1RrXOB3rENJrKY";
		List<UserLockTb> list = userLockService.getLockByOpenId(openId);
		logger.debug(list);
	}
	//@Test
	public void testaddkey(){
		KeyTb key = new KeyTb();
		key.setState("1");//1启动 2禁止 首次添加 默认启动
		key.setPosition(1);
		key.setKid(2);
		LockKeyTb lockkey = new LockKeyTb();
		lockkey.setKeyId("002E003A");
		lockkey.setLockId("12321312312");
		lockKeyService.addkey(key,lockkey);
	}
	//@Test
	public void testremovekey(){
		String lockId = "12321312312";
		String keyId="002E003A";
		KeyTb userLockKeyBean = lockKeyService.getKeyByKeyId(keyId);
		 logger.debug(userLockKeyBean.getKid());
		 lockKeyService.deleteKey(lockId,keyId); 
			List<KeyTb> list = lockKeyService.findKeyListByLockId(lockId,1);
			logger.debug(list);
	}
 	@Test
	public void testfindkey() throws WxErrorException{
		/*String keyId = "25364C66";
		String openId = "oJ3fKwDDkQ6ykN1RrXOB3rENJrKY";
		int i = lockKeyService.findLockKeyRelationCount(keyId, openId);*/
 	/*	 String openId = "oJ3fKwDDkQ6ykN1RrXOB3rENJrKY";
 		 WxMpService wxMpService = startupConfig.getWxMpService();
		 TemplateData temlateData = new TemplateData();
		 temlateData.setFirst("这是一个模板消息");
		 temlateData.setColor("#000000");
		 temlateData.setRemark("授权");
		 String[] keyword = {"张三","李四","待确认授权"};
		 temlateData.setToUser(openId);
		 temlateData.setTemplateId("Nf1VrLhsISZC_Z1qLKHb57-lWGRo3geved0595w60T8");
		 temlateData.setUrl("");
		temlateData.setKeyword(keyword);
		WxMpTemplateMessage templateMessage = WxSendTemplateMessage.buildTemplateMessage(temlateData );
		WxSendTemplateMessage.sendTemplatemessage(wxMpService, templateMessage);*/
 	}
 	
 	
 	public static class TemplateMessage{
 		  /**
 	     * 模板消息id
 	     */
 	    private String template_id;
 	    /**
 	     * 用户openId
 	     */
 	    private String touser;
 	    /**
 	     * URL置空，则在发送后，点击模板消息会进入一个空白页面（ios），或无法点击（android）
 	     */
 	    private String url;
 	    /**
 	     * 标题颜色
 	     */
 	    private String topcolor;
 	    /**
 	     * 详细内容
 	     */
 	    private Map<String,WxMpTemplateData> datas;
		 
		 public String getTemplate_id() {
			return template_id;
		}

		public void setTemplate_id(String template_id) {
			this.template_id = template_id;
		}

		public String getTouser() {
			return touser;
		}

		public void setTouser(String touser) {
			this.touser = touser;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getTopcolor() {
			return topcolor;
		}

		public void setTopcolor(String topcolor) {
			this.topcolor = topcolor;
		}

		public Map<String, WxMpTemplateData> getDatas() {
			return datas;
		}

		public void setDatas(Map<String, WxMpTemplateData> datas) {
			this.datas = datas;
		}

		public String toJson() {
			    return WxMpGsonBuilder.INSTANCE.create().toJson(this);
	     }
 	  
 	}
 	
	//@Test
	public void testSaveDeviceIdAndQrcode(){
		List<KeyTb> keyList = new ArrayList<KeyTb>();
		for(int i=0;i<3;i++){
			KeyTb key = new KeyTb();
			key.setDeviceId("12312312");
			key.setQrcodeSerial(IdGen.createQrSerial(i+1));
			key.setTicketId("4324232323");
			keyList.add(key);
		}
		try {
			lockKeyService.saveDeviceIdAndQrcode(1, "24315", "D:/wx_device_qrcode.xls");
			//ImportDataToExcel.execute(keyList, "");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	///@Test
	public void testUpdateDeviceParam(){
		
		List<KeyDeviceAuthBean> device_list = new ArrayList<KeyDeviceAuthBean>();
		 /*
		  * 修改设备属性参数说明：
		  * deviceId 
		  * mac 
		  * 连接协议
		  * 写入到设备的加密key
		  * 断开策略
		  * 连接策略
		  * 加密方法 
		  * 授权版本
		  * manufature 含有mac地址偏移
		  * serial number 含有mac地址偏移 
		  * 精简协议类型
		  */
		KeyDeviceAuthBean deviceBean = new BleDeviceAuth().new KeyDeviceAuthBean("gh_127f2e348655_1f3c6c508b37e34e","DA3F323BC296","3","","1","1","0","0","-1","-2","0");
		device_list.add(deviceBean);
		/*
		 * op_type:0 授权,1更新
		 */
		NewParams newParams=bleDeviceAuth.getNewParams(device_list.size()+"", device_list, "1", "25549");//24315
		String params = JSONObject.toJSONString(newParams);
		try {
			bleDeviceAuth.moreBleDeviceAuth("",params);
		} catch (IOException | WxErrorException e) {
			e.printStackTrace();
		}
	}
	
	@Autowired
	private GetBleDeviceQrcode getBleDeviceQrcode;
	
	//@Test
	public void testGetDeviceId(){
		try {
			JSONObject object = getBleDeviceQrcode.getBleDeviceQrcode("25549");
			/*KeyTb key  = new KeyTb();
			key.setDeviceId(object.getString("deviceid"));
			key.setTicketId(object.getString("qrticket"));
			key.setKeyId("");
			key.setMac("F6D4B3EED3E0");
			key.setState("1");
			key.setType(1);
			lockKeyService.addKey(key);*/
			logger.info("设备id="+object.toJSONString());
		} catch (IOException | WxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testRemotebind(){
	//	WxMpService wxMpService = startupConfig.getWxMpService();
		//String url = "https://api.weixin.qq.com/device/compel_bind";
		//String lockId = "123";
		String openId = "oJ3fKwDDkQ6ykN1RrXOB3rENJrKY";
	//	String deviceId = "gh_127f2e348655_8d466e6d137182ba";
		// try {
			 //String data = JSONObject.toJSONString(new Data(deviceId,openId));
			
			//String result2 = wxMpService.execute(new SimplePostRequestExecutor() , "https://api.weixin.qq.com/device/compel_unbind", data);
			
			// String result = wxMpService.execute(new SimpleGetRequestExecutor() , url, null);
			//  logger.info("-----"+result2);
	 //	} catch (WxErrorException e) {
	 //		e.printStackTrace();
	// 	}
		List<Device> list = userService.getWxBindDevice(openId, "");
		System.out.println(list);
	}
	//@Test
    public void testCreateWxMenu(){
    	WxMpService wxMpService = startupConfig.getWxMpService();
		try {
			new WxCustomeMenu().createCustomeMenu2(wxMpService.getAccessToken());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (WxErrorException e) {
			System.out.println(e.getMessage());
		}
    }
	  //@Test
	public void testCreateQrcode(){
		/**
		 * 生成临时二维码 POST请求， 数据格式json，格式是{"expire_seconds": 604800, "action_name":
		 * "QR_SCENE", "action_info": {"scene": {"scene_id": 123}}}
		 */
		//String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESSTOKEN";
		  WxMpService wxMpService = startupConfig.getWxMpService();
		 try {
			WxMpQrCodeTicket qrticket = wxMpService.qrCodeCreateLastTicket("6");
			logger.info(qrticket.getTicket()+"---"+qrticket.getUrl()+"----"+qrticket.getExpire_seconds());
		} catch (WxErrorException e) {
			e.printStackTrace();
		} 
		//String lockId = "qrscene_lock12345678";
		//lockId = lockId.substring(lockId.indexOf("lock")+4, lockId.length());
		//System.out.println(lockId);
	}
	 
 
	public static class Data {
		
		public String device_id;
		
		public String openid;
		public Data( String deviceId, String openId) {
			 this.device_id = deviceId;
			 this.openid = openId;
					 
		}
	}
	
	//@Test
	public void testStrToTimestamp() {
		String ss= "Jan 12, 2017 3:05:57 PM";
	   //String ss = "一月 12, 2017 04:10:21 下午";
			Date date = null;
			try {
				date = GetDateUtils.getDateByStr(ss);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//	Date date = new Date();
			//String sss = new SimpleDateFormat(" MMM dd, yyyy hh:mm:ss aaa").format(date);
			System.out.print("---"+date);
		 
	}
}

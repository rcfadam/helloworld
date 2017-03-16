package com.huiju.weixin.WeixinServer.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.huiju.weixin.WeixinServer.conf.StartupConfig;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpService;


/**
 * 蓝牙设备授权
 * 
 * @author rcfad
 * 
 */
@Component
public class BleDeviceAuth {
	@Autowired
	private  StartupConfig startupConfig;
	//代授权的蓝牙设备参数
	private  NewParams newParams;
	private Logger logger = Logger.getLogger(BleDeviceAuth.class);
	private List<KeyDeviceAuthBean> deviceList = new ArrayList<BleDeviceAuth.KeyDeviceAuthBean>();
	
	 
	/**
	 * 蓝牙设备授权
	 * 
	 * @param deviceId
	 * @param mac
	 * @return
	 * @throws IOException
	 * @throws WxErrorException 
	 */
	public JSONObject bleDeviceAuth(String deviceId, String mac) throws IOException, WxErrorException {
		WxMpService wxMpService = startupConfig.getWxMpService();
		 String params = "{\"device_num\":\"1\",\"device_list\":[{"
				+ "\"id\":\"" + deviceId + "\"," + "\"mac\":\"" + mac + "\","
				+ "\"connect_protocol\":\"3\"," + "\"auth_key\":\"\","
				+ "\"close_strategy\":\"1\"," + "\"conn_strategy\":\"1\","
				+ "\"crypt_method\":\"0\"," + "\"auth_ver\":\"0\","
				+ "\"manu_mac_pos\":\"-1\"," + "\"ser_mac_pos\":\"-2\","
				+ "\"ble_simple_protocol\": \"0\"" + "}],"
				+ "\"op_type\":\"1\"" + "}"; 
		String result = wxMpService.execute(new SimplePostRequestExecutor(), "https://api.weixin.qq.com/device/authorize_device",params);
		logger.info(result);
		JSONObject bleDeviceAuthModel = JSONObject.parseObject(result);
		logger.info(bleDeviceAuthModel);
		return bleDeviceAuthModel;
	}
	
    public JSONObject bleDeviceAuth(JSONObject jsonObject) throws WxErrorException {
    	WxMpService wxMpService = startupConfig.getWxMpService();
		 String params = "{\"device_num\":\"1\",\"device_list\":[{"
				+ "\"id\":\"" + jsonObject.getString("deviceId")
				+ "\"," + "\"mac\":\"" + jsonObject.getString("mac") + "\","
				+ "\"connect_protocol\":\""+jsonObject.getString("connect_protocol")+"\","
				+ "\"auth_key\":\"\","+ "\"close_strategy\":\""+jsonObject.getString("close_strategy")
				+"\"," + "\"conn_strategy\":\""+jsonObject.getString("conn_strategy")+"\","
				+ "\"crypt_method\":\""+jsonObject.getString("crypt_method")+"\","
				+ "\"auth_ver\":\"0\","+ "\"manu_mac_pos\":\"-1\"," + "\"ser_mac_pos\":\"-2\","
				+ "\"ble_simple_protocol\": \"0\"" + "}],"
				+ "\"op_type\":\"1\""
				+ "}"; 
		String result = wxMpService.execute(new SimplePostRequestExecutor(), "https://api.weixin.qq.com/device/authorize_device",params);
		logger.info(result);
		JSONObject bleDeviceAuthModel = JSONObject.parseObject(result);
		logger.info(bleDeviceAuthModel);
		return bleDeviceAuthModel;
	}
	
	/**
	 * 蓝牙设备授权
	 * @param accessToken
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws WxErrorException 
	 */
	public JSONObject moreBleDeviceAuth(String accessToken,
			String params) throws IOException, WxErrorException {
		WxMpService wxMpService = startupConfig.getWxMpService();
	//	String url = WxConfig.BLE_DEVICE_AUTH.replace("ACCESS_TOKEN",
	//			accessToken);
		String result = wxMpService.execute(new SimplePostRequestExecutor(), "https://api.weixin.qq.com/device/authorize_device",params);
		logger.info(result);
		JSONObject bleDeviceAuthModel = JSONObject.parseObject(result);
		logger.info(bleDeviceAuthModel);
		return bleDeviceAuthModel;
	}
	/**
	 * 添加待授权的钥匙
	 * @param deviceId 设备ID
	 * @param macs 蓝牙钥匙的mac地址数组
	 * @return
	 */
	public List<KeyDeviceAuthBean> getAuthKeyDeviceList(String deviceId,String...macs){
		if(macs!=null&&macs.length>0){
			for(String mac:macs){
				KeyDeviceAuthBean key = new KeyDeviceAuthBean(deviceId,mac);
				deviceList.add(key);
			}
		} 
		return deviceList;
	}
    
	public int getDeviceNum(){
		return deviceList.size();
	}
	
	public NewParams getNewParams(String device_num, List<KeyDeviceAuthBean> device_list,String op_type, String product_id){
		newParams = new NewParams();
		newParams.device_list = device_list;
		newParams.device_num = device_num;
		newParams.product_id = product_id;
		newParams.op_type = op_type;
		return this.newParams;
	}
	
	public class NewParams {
		private String device_num;// 授权设备的个数 最好与设备device_list大小同步
		private List<KeyDeviceAuthBean> device_list;// 要授权的设备列表
		/* 请求操作的类型，限定取值为：0：设备授权（缺省值为0） 1：设备更新（更新已授权设备的各属性值） */
		private String op_type;
		/*
		 * 设备的产品编号（由微信硬件平台分配）。可在公众号设备功能管理页面查询。 当 op_type 为‘0’，product_id
		 * 为‘1’时，不要填写 product_id 字段（会引起不必要错误）； 当 op_typy 为‘0’，product_id
		 * 不为‘1’时，必须填写 product_id 字段； 当 op_type 为 1 时，不要填写 product_id 字段。
		 */
		private String product_id;
		
		
		public NewParams(String device_num, List<KeyDeviceAuthBean> device_list,String op_type, String product_id) {
			super();
			this.device_num = device_num;
			this.device_list = device_list;
			this.op_type = op_type;
			this.product_id = product_id;
		}
		public NewParams() {
			super();
		}
		public String getDevice_num() {
			return device_num;
		}
		public void setDevice_num(String device_num) {
			this.device_num = device_num;
		}
		public List<KeyDeviceAuthBean> getDevice_list() {
			return device_list;
		}
		public void setDevice_list(List<KeyDeviceAuthBean> device_list) {
			this.device_list = device_list;
		}
		public String getOp_type() {
			return op_type;
		}
		public void setOp_type(String op_type) {
			this.op_type = op_type;
		}
		public String getProduct_id() {
			return product_id;
		}
		public void setProduct_id(String product_id) {
			this.product_id = product_id;
		}
		
		
	}

	public class KeyDeviceAuthBean {
		private String id;// 设备的deviceid
		private String mac;// 设备的mac地址 格式采用16进制串的方式（长度为12字节），不需要0X前缀，如：
							// 1234567890AB
		private String connect_protocol;// 连接协议 android classic bluetooth – 1
										// ios classic bluetooth – 2 ble – 3
										// wifi -- 4
		private String auth_key;// auth及通信的加密key，第三方需要将key烧制在设备上
		private String close_strategy;/*
									 * 断开策略，目前支持： 1：退出公众号页面时即断开连接
									 * 2：退出公众号之后保持连接不断开
									 * 3：退出公众号之后一直保持连接（设备主动断开连接后，微信尝试重连）
									 */
		private String conn_strategy;
		/*
		 * 连接策略，32位整型，按bit位置位，目前仅第1bit和第3bit位有效（bit置0为无效，1为有效；第2bit已被废弃），且bit位可以按或置位
		 * （如1|4=5），各bit置位含义说明如下： 1：（第1bit置位）在公众号对话页面，不停的尝试连接设备
		 * 4：（第3bit置位）处于非公众号页面（如主界面等），微信自动连接。当用户切换微信到前台时，可能尝试去连接设备，连上后一定时间会断开
		 * 8：（第4bit置位）进入微信后即可开始连接，只要微信在进程中运行，设备不会主动断开
		 */
		private String crypt_method;/*
									 * auth加密方法，目前支持两种取值： 0：不加密
									 * 1：AES加密（CBC模式，PKCS7填充方式）
									 */
		/*
		 * auth version，设备和微信进行auth时，会根据该版本号来确认auth buf和auth
		 * key的格式（各version对应的auth buf及key的具体格式可以参看“客户端蓝牙外设协议”），该字段目前支持取值：
		 * 0：不加密的version 1：version 1
		 */
		private String auth_ver;
		/*
		 * 表示mac地址在厂商广播manufature data里含有mac地址的偏移，取值如下： -1：在尾部、 -2：表示不包含mac地址
		 * 其他：非法偏移
		 */
		private String manu_mac_pos;
		/*
		 * 表示mac地址在厂商serial number里含有mac地址的偏移，取值如下： -1：表示在尾部 -2：表示不包含mac地址
		 * 其他：非法偏移
		 */
		private String ser_mac_pos;
		/* 精简协议类型，取值如下：计步设备精简协议：1 （若该字段填1，connect_protocol 必须包括3。非精简协议设备切勿填写该字段） */
		private String ble_simple_protocol;
		
		
		
		
		public KeyDeviceAuthBean() {
			super();
			// TODO Auto-generated constructor stub
		}
			
		public KeyDeviceAuthBean(String id, String mac) {
			super();
			this.id = id;
			this.mac = mac;
			this.auth_key="";
			this.connect_protocol="3";
			this.close_strategy="1";
			this.crypt_method="0";
			this.auth_ver="0";
			this.manu_mac_pos = "-1";
			this.ser_mac_pos="-2";
			this.ble_simple_protocol="0";
		}
		
		public KeyDeviceAuthBean(String id, String mac, String connect_protocol, String auth_key, String close_strategy,
				String conn_strategy, String crypt_method, String auth_ver, String manu_mac_pos, String ser_mac_pos,
				String ble_simple_protocol) {
			super();
			this.id = id;
			this.mac = mac;
			this.connect_protocol = connect_protocol;
			this.auth_key = auth_key;
			this.close_strategy = close_strategy;
			this.conn_strategy = conn_strategy;
			this.crypt_method = crypt_method;
			this.auth_ver = auth_ver;
			this.manu_mac_pos = manu_mac_pos;
			this.ser_mac_pos = ser_mac_pos;
			this.ble_simple_protocol = ble_simple_protocol;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}

		public String getConnect_protocol() {
			return connect_protocol;
		}

		public void setConnect_protocol(String connect_protocol) {
			this.connect_protocol = connect_protocol;
		}

		public String getAuth_key() {
			return auth_key;
		}

		public void setAuth_key(String auth_key) {
			this.auth_key = auth_key;
		}

		public String getClose_strategy() {
			return close_strategy;
		}

		public void setClose_strategy(String close_strategy) {
			this.close_strategy = close_strategy;
		}

		public String getCrypt_method() {
			return crypt_method;
		}

		public void setCrypt_method(String crypt_method) {
			this.crypt_method = crypt_method;
		}

		public String getAuth_ver() {
			return auth_ver;
		}

		public void setAuth_ver(String auth_ver) {
			this.auth_ver = auth_ver;
		}

		public String getManu_mac_pos() {
			return manu_mac_pos;
		}

		public void setManu_mac_pos(String manu_mac_pos) {
			this.manu_mac_pos = manu_mac_pos;
		}

		public String getSer_mac_pos() {
			return ser_mac_pos;
		}

		public void setSer_mac_pos(String ser_mac_pos) {
			this.ser_mac_pos = ser_mac_pos;
		}

		public String getBle_simple_protocol() {
			return ble_simple_protocol;
		}

		public void setBle_simple_protocol(String ble_simple_protocol) {
			this.ble_simple_protocol = ble_simple_protocol;
		}

		public String getConn_strategy() {
			return conn_strategy;
		}

		public void setConn_strategy(String conn_strategy) {
			this.conn_strategy = conn_strategy;
		}

	}

}

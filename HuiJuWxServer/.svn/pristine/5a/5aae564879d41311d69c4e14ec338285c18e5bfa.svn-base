package com.huiju.weixin.WeixinServer.util;

/**
 * 请求微信API接口调用以及全局参数配置
 * 
 * @author rcfad
 * 
 */
public final class WxConfig {

	/**
	 * 获取access_token 接口 需要替换的参数是APPID 和APPSECRET
	 */
	public static String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	/**
	 * 创建菜单 需要替换的参数是ACCESSTOKEN
	 */
	public static String CREATE_WX_MENU = " https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESSTOKEN";

	/**
	 * 用户授权 需要替换的参数是 APPID ，重定向URL REDIRECTURL， STATE
	 * 
	 */
	public static String USER_AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECTURL&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

	/**
	 * 通过code获取网页授权access_token的接口， 此token与全局token不是一个东西 需要替换的参数是APPID
	 * 和CODE,SECRET
	 */
	public static String GET_AUTH_ACCESS_TOKEN_BY_CODE = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	/**
	 * 网页授权获取access_token后，如果需要就刷新token值 需要替换的参数是APPID 和REFRESH_TOKEN
	 */
	public static String REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";

	/***
	 * 通过openid获取微信用户信息
	 */
	public static String GET_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	/**
	 * 生成临时二维码 POST请求， 数据格式json，格式是{"expire_seconds": 604800, "action_name":
	 * "QR_SCENE", "action_info": {"scene": {"scene_id": 123}}}
	 */
	public static String GET_TEMPORARY_QRIMAGE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESSTOKEN";

	/**
	 * 生成永久二维码 POST 请求，请求格式：{"action_name": "QR_LIMIT_SCENE", "action_info":
	 * {"scene": {"scene_id": 123}}}
	 */
	public static String GET_PERMANENT_QRIMAGE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";

	/**
	 * 通过ticket换取二维码，其中ticket需要使用URLEncode编码。
	 */
	public static String GET_QRIMAGE_BY_TICK = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
	/***
	 * 获取蓝牙设备的二维码 ACCESS_TOKEN PRODUCT_ID 替换
	 */
	public static String GET_QRIMAGE_BLE_DEVICE = "https://api.weixin.qq.com/device/getqrcode?access_token=ACCESS_TOKEN&product_id=PRODUCT_ID";

	/**
	 * 蓝牙设备授权 ACCESS_TOKEN 需要替换 POST
	 */
	public static String BLE_DEVICE_AUTH = "https://api.weixin.qq.com/device/authorize_device?access_token=ACCESS_TOKEN";

	/**
	 * 获取jsapi 的接口凭证接口 GET 需要替换ACCESS_TOKEN
	 */
	public static String GET_JSAPI_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&&type=jsapi";

	/**
	 * 主动发送消息给设备 　　　ＡＣＣＥＳＳ＿ＴＯＫＥＮ条件　　　ＰＯＳＴ请求参数　{ "device_type":"DEVICETYPE",
	 * "device_id":"DEVICEID", "open_id": "OPEN_ID", "content": "BASE64编码内容" }
	 */
	public static String SEND_MSG_TO_DEVICE = "https://api.weixin.qq.com/device/transmsg?access_token=ACCESS_TOKEN";

	/**
	 * 批量获取设备二维码 。POST请求参数 ：{ "device_num":"2",
	 * "device_id_list":["01234","56789"] }
	 */
	public static String GET_QRCODE_BATCH = "https://api.weixin.qq.com/device/create_qrcode?access_token=ACCESS_TOKEN";

	/**
	 * 绑定设备API POST请求：{ "ticket": "TICKET", "device_id": "DEVICEID", "openid":
	 * " OPENID" }
	 */
	public static String BIND_DEVICE = "https://api.weixin.qq.com/device/bind?access_token=ACCESS_TOKEN";

	/**
	 * 解绑设备 API POST请求 ：{ "ticket": "TICKET", "device_id": "DEVICEID", "openid":
	 * " OPENID" }
	 */
	public static String UNBIND_DEVICE = "https://api.weixin.qq.com/device/unbind?access_token=ACCESS_TOKEN";

	/**
	 * 第三方强制绑定用户和设备 POST数据说明 { "device_id": "DEVICEID", "openid": " OPENID" }
	 */
	public static String COMPEL_BIND = "https://api.weixin.qq.com/device/compel_bind?access_token=ACCESS_TOKEN";

	/**
	 * 第三方强制解绑用户和设备 POST数据说明 { "device_id": "DEVICEID", "openid": " OPENID" }
	 */
	public static String COMPEL_UNBIND = "https://api.weixin.qq.com/device/compel_unbind?access_token=ACCESS_TOKEN";

	/**
	 * 设备状态查询 GET ACCESS_TOKEN DEVICE_ID 返回 { "errcode":0, "errmsg":"ok",
	 * "status":2, "status_info":"bind" }
	 */
	public static String GET_STAT = "https://api.weixin.qq.com/device/get_stat?access_token=ACCESS_TOKEN&device_id=DEVICE_ID";
    
	/**
	 * 获取设备绑定的openid GET 正常返回{ "open_id":["omN7ljrpaxQgK4NW4H5cRzFRtfa8",
	 * "omN7ljtqrTZuvYLkjPEX_t_Pmmlg",],
	 * "resp_msg":{"ret_code":0,"error_info":"get open id list OK!"} }
	 */
	public static String GET_OPENID = "https://api.weixin.qq.com/device/get_openid?access_token=ACCESS_TOKEN&device_type=DEVICE_TYPE&device_id=DEVICE_ID";

	/**
	 * 通过openid 获取用户绑定的deviceID  GET 正确返回 { "resp_msg": { "ret_code": 0,
	 * "error_info": "ok" }, "openid": "OPENID", "device_list": [ {
	 * "device_type": "dt1", "device_id": "di1" } ] }
	 */
	public static String GET_BIND_DEVICEID = "https://api.weixin.qq.com/device/get_bind_device?access_token=ACCESS_TOKEN&openid=OPENID";
	
	/**
	 * 微信 公众的appid
	 */
	public static String APPID = "wxb4ba14562eb9a765";

	public static String APPSECRET = "b5123e6cd7a5eefabec0c193cb1102af";

	/**
	 * Token 用作生成签名
	 */
	public static String TOKEN = "1234";

}

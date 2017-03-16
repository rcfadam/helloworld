package com.huiju.weixin.WeixinServer.conf;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;

import java.io.InputStream;


@XStreamAlias("xml")
class HJWxMpInMemoryConfigStorage extends WxMpInMemoryConfigStorage {
  @Override
  public String toString() {
    return "SimpleWxConfigProvider [appId=" + appId + ", secret=" + secret + ", accessToken=" + accessToken
        + ", expiresTime=" + expiresTime + ", token=" + token + ", aesKey=" + aesKey + "]";
  }


  public static HJWxMpInMemoryConfigStorage fromXml(InputStream is) {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(HJWxMpInMemoryConfigStorage.class);
    return (HJWxMpInMemoryConfigStorage) xstream.fromXML(is);
  }
}

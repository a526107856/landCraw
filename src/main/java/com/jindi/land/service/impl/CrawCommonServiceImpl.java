package com.jindi.land.service.impl;

import com.jindi.land.service.ICrawCommonService;
import com.jindi.land.util.HttpClientUtils;
import com.jindi.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class CrawCommonServiceImpl implements ICrawCommonService {

  @Autowired
  private ProxyService proxyService;
  private static final String BAIDU = "http://www.baidu.com";

  @Override
  public HttpHost getHost() {
    HttpHost retreiveProxy = null;
    try {
      HttpGet get = new HttpGet(BAIDU);
      initHeader(get);
      do {
        retreiveProxy = proxyService.retreiveProxy();
        String html = HttpClientUtils.getInstance().get(get, retreiveProxy);
        if (!StringUtils.isEmpty(html)) {
          log.info("get good proxy={}", retreiveProxy.toString());
          return retreiveProxy;
        } else {
          log.info("failed proxy={}", retreiveProxy.toString());
        }
      } while (true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 请求列表页header
   */
  private void initHeader(HttpGet request) {
    request.addHeader("User-Agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
    request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    request.addHeader("Accept-Encoding", "gzip, deflate");
    request.addHeader("Accept-Language", "en-US,en;q=0.5");
  }
}

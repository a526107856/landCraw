package com.jindi.land.util;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * 支持post 302跳转策略实现类 HttpClient默认跳转：httpClientBuilder.setRedirectStrategy(new
 * LaxRedirectStrategy()); 上述代码在post/redirect/post这种情况下不会传递原有请求的数据信息。所以参考了下SeimiCrawler这个项目的重定向策略。
 * 原代码地址：https://github.com/zhegexiaohuozi/SeimiCrawler/blob/master/project/src/main/java/cn/wanghaomiao/seimi/http/hc/SeimiRedirectStrategy.java
 */
@Slf4j
public class CustomRedirectStrategy extends LaxRedirectStrategy {

  @Override
  public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
      throws ProtocolException {
    URI uri = getLocationURI(request, response, context);
    String method = request.getRequestLine().getMethod();
    if ("post".equalsIgnoreCase(method)) {
      try {
        HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) request;
        httpRequestWrapper.setURI(uri);
        httpRequestWrapper.removeHeaders("Content-Length");
        return httpRequestWrapper;
      } catch (Exception e) {
        log.error("强转为HttpRequestWrapper出错");
      }
      return new HttpPost(uri);
    } else {
      return new HttpGet(uri);
    }
  }
}

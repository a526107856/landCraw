package com.jindi.land.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抓取高德地图工具类
 */
public class HttpClientUtils {

  private Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

  private volatile static HttpClientUtils utils;

  private PoolingHttpClientConnectionManager clientConnectionManager;
  private HttpRequestRetryHandler httpRequestRetryHandler;
  private RequestConfig requestConfig;

  private HttpClientUtils() {
    initClientConnectionManager();
    initRequestRetryHandler();
    initRequestConfig();
  }

  public static HttpClientUtils getInstance() {
    if (null == utils) {
      synchronized (HttpClientUtils.class) {
        if (null == utils) {
          utils = new HttpClientUtils();
        }
      }
    }
    return utils;
  }

  /**
   * 初始化HTTP连接池
   */
  private void initClientConnectionManager() {
    ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
    LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", plainsf)
        .register("https", sslsf)
        .build();
    clientConnectionManager = new PoolingHttpClientConnectionManager(registry);
    // Config
    // 将最大连接数增加到200
    clientConnectionManager.setMaxTotal(50);
    // 将每个路由基础的连接增加到20
    clientConnectionManager.setDefaultMaxPerRoute(10);
    // 将目标主机的最大连接数增加到80
    //HttpHost localhost = new HttpHost("http://blog.csdn.net/gaolu",80);
    //clientConnectionManager.setMaxPerRoute(new HttpRoute(localhost), 80);
    clientConnectionManager.setValidateAfterInactivity(5000);
  }

  private PoolingHttpClientConnectionManager getClientConnectionManager() {
    if (null == clientConnectionManager) {
      initClientConnectionManager();
    }
    return clientConnectionManager;
  }

  private void initRequestRetryHandler() {
    //请求重试处理
    //StandardHttpRequestRetryHandler httpRequestRetryHandler = new StandardHttpRequestRetryHandler(2, true);
    httpRequestRetryHandler = new HttpRequestRetryHandler() {
      public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= 3) {// 如果已经重试了5次，就放弃
          return false;
        }
        if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
          return true;
        }
        if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
          return false;
        }
        if (exception instanceof InterruptedIOException) {// 超时
          return true;
        }
        if (exception instanceof UnknownHostException) {// 目标服务器不可达
          return true;
        }
        if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
          return true;
        }
        if (exception instanceof SSLException) {// ssl握手异常
          return false;
        }
        if (exception instanceof SocketTimeoutException) {
          return true;
        }

        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        // 如果请求是幂等的，就再次尝试
        if (!(request instanceof HttpEntityEnclosingRequest)) {
          return true;
        }
        return false;
      }
    };
  }

  private void initRequestConfig() {
    requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(30 * 1000)
        .setConnectTimeout(30 * 1000)
        .setSocketTimeout(5000 * 6)
        .setCookieSpec(CookieSpecs.STANDARD_STRICT)
        .build();
  }

  private RequestConfig getRequestConfig() {
    if (null == requestConfig) {
      initRequestConfig();
    }
    return requestConfig;
  }

  private RequestConfig setProxy(RequestConfig config, HttpHost proxy) {
    //设置代理服务器的ip地址和端口
    //HttpHost proxy = new HttpHost("120.32.146.190", 8088);// 设置代理ip
    return RequestConfig.copy(config)
        .setProxy(proxy)
        .build();
  }


  private void configRequest(HttpRequestBase httpRequestBase, HttpHost proxyHost) {
    if (null != proxyHost) {
      httpRequestBase.setConfig(setProxy(getRequestConfig(), proxyHost));
    } else {
      httpRequestBase.setConfig(getRequestConfig());
    }

  }


  /**
   * 获取client
   */
  private CloseableHttpClient getClient() {
    //DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
    //设置可关闭的httpclient
    //CloseableHttpClient httpClient = HttpClients.custom().setRoutePlanner(routePlanner).setDefaultRequestConfig(config).build();

    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(getClientConnectionManager())
        .setRetryHandler(httpRequestRetryHandler)
        .setDefaultRequestConfig(getRequestConfig())
        .build();
    if (logger.isDebugEnabled()) {
      if (null != getClientConnectionManager().getTotalStats()) {
        logger.debug("HttpClient 连接池状态 : " + getClientConnectionManager().getTotalStats());
      }
    }

    return httpClient;
  }

  /**
   * 获取client
   */
  private CloseableHttpClient getClient(HttpHost proxy) {
    DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
    //设置可关闭的httpclient
    //CloseableHttpClient httpClient = HttpClients.custom().setRoutePlanner(routePlanner).setDefaultRequestConfig(config).build();

    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(getClientConnectionManager())
        .setRetryHandler(httpRequestRetryHandler)
        .setDefaultRequestConfig(getRequestConfig())
        .setRoutePlanner(routePlanner)
        .build();
    if (logger.isDebugEnabled()) {
      if (null != getClientConnectionManager().getTotalStats()) {
        logger.debug("HttpClient 连接池状态 : " + getClientConnectionManager().getTotalStats());
      }
    }

    return httpClient;
  }

  public void close(CloseableHttpClient httpClient, HttpRequestBase httpRequestBase,
      CloseableHttpResponse response) throws Exception {
    try {
      if (null != response) {
        EntityUtils.consume(response.getEntity());
        response.close();
      }
      if (null != httpRequestBase) {
        httpRequestBase.abort();
        httpRequestBase.releaseConnection();
      }
    } catch (Exception e) {
      throw e;
    }
  }

  public String post(HttpPost post, Map<String, String> param, HttpHost httpHost) throws Exception {
    String context = "";
    configRequest(post, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      /*post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));*/
      // 创建参数列表
      if (param != null) {
        List<NameValuePair> paramList = new ArrayList<>();
        for (String key : param.keySet()) {
          paramList.add(new BasicNameValuePair(key, param.get(key)));
        }
        // 模拟表单
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
        post.setEntity(entity);
      }
      //post.setEntity(new StringEntity(params, Charset.forName("UTF-8")));
      response = httpClient.execute(post);
      HttpEntity entity = response.getEntity();
      context = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);
    } catch (Exception e) {
      throw e;
    } finally {
      this.close(httpClient, post, response);
    }
    return context;
  }

  public String get(String url, HttpHost httpHost) {
    String context = "";
    HttpGet get = new HttpGet(url);
    get.addHeader("X-Requested-With", "XMLHttpRequest");
    configRequest(get, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      /*post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));*/

      response = httpClient.execute(get);
      HttpEntity entity = response.getEntity();
      context = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);
    } catch (Exception e) {
      try {
        throw e;
      } catch (IOException e1) {
        e1.fillInStackTrace();
      }
    } finally {
      try {
        this.close(httpClient, get, response);
      } catch (Exception e) {
        e.fillInStackTrace();
      }
    }
    return context;
  }

  public String getCookie(HttpGet get, HttpHost httpHost) {
    String context = "";
    configRequest(get, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      response = httpClient.execute(get);
      Header[] allHeaders = response.getAllHeaders();
      for (Header allHeader : allHeaders) {
        if ("Set-Cookie".equals(allHeader.getName())) {
          return allHeader.getValue().replace("path=/", "");
        }
      }
    } catch (Exception e) {
      try {
        throw e;
      } catch (IOException e1) {
        e1.fillInStackTrace();
      }
    } finally {
      try {
        this.close(httpClient, get, response);
      } catch (Exception e) {
        e.fillInStackTrace();
      }
    }
    return context;
  }

  public String get(HttpGet get, HttpHost httpHost) {
    String context = "";

    configRequest(get, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      /*post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));*/
      response = httpClient.execute(get);
      HttpEntity entity = response.getEntity();
      context = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);
    } catch (Exception e) {
      try {
        throw e;
      } catch (IOException e1) {
        e1.fillInStackTrace();
      }
    } finally {
      try {
        this.close(httpClient, get, response);
      } catch (Exception e) {
        e.fillInStackTrace();
      }
    }
    return context;
  }

  public String get(HttpGet get, String charset, HttpHost httpHost) {
    String context = "";
    configRequest(get, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      response = httpClient.execute(get);

      BufferedReader br = new BufferedReader(
          new InputStreamReader(response.getEntity().getContent(), charset));
      String tempbf;
      StringBuffer html = new StringBuffer(100);
      while ((tempbf = br.readLine()) != null) {
        html.append(tempbf + "\n");
      }

      return html.toString();
    } catch (Exception e) {
      try {
        throw e;
      } catch (IOException e1) {
        e1.fillInStackTrace();
      }
    } finally {
      try {
        this.close(httpClient, get, response);
      } catch (Exception e) {
        e.fillInStackTrace();
      }
    }
    return context;
  }

  public String post(HttpPost post, HttpHost httpHost) {
    String context = "";
    configRequest(post, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      response = httpClient.execute(post);
      HttpEntity entity = response.getEntity();
      context = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);
    } catch (Exception e) {
      try {
        throw e;
      } catch (IOException e1) {
        e1.fillInStackTrace();
      }
    } finally {
      try {
        this.close(httpClient, post, response);
      } catch (Exception e) {
        e.fillInStackTrace();
      }
    }
    return context;
  }

  public String getBaidu(String url, HttpHost httpHost) {
    String context = "";
    HttpGet get = new HttpGet(url);
    //setHeader(get);
    get.addHeader("Host", "ugcapi.baidu.com");
    configRequest(get, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      /*post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));*/

      response = httpClient.execute(get);
      HttpEntity entity = response.getEntity();
      context = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);
    } catch (Exception e) {
      try {
        throw e;
      } catch (IOException e1) {
        e1.fillInStackTrace();
      }
    } finally {
      try {
        this.close(httpClient, get, response);
      } catch (Exception e) {
        e.fillInStackTrace();
      }
    }
    return context;
  }

  public String post(String url, String params) throws Exception {
    String context = "";
    HttpPost post = new HttpPost(url);
    configRequest(post, null);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getClient();
      /*post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));*/
      post.setEntity(new StringEntity(params, Charset.forName("UTF-8")));
      response = httpClient.execute(post);
      HttpEntity entity = response.getEntity();
      context = EntityUtils.toString(entity, "UTF-8");
      EntityUtils.consume(entity);
    } catch (Exception e) {
      throw e;
    } finally {
      this.close(httpClient, post, response);
    }
    return context;
  }

  public boolean getCode(HttpHost httpHost) {
    HttpGet get = new HttpGet("https://www.baidu.com");
    configRequest(get, httpHost);
    CloseableHttpResponse response = null;
    CloseableHttpClient httpClient = null;
    boolean is200 = false;
    try {
      httpClient = getClient();
      response = httpClient.execute(get);
      if (response.getStatusLine().getStatusCode() == 200) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      try {
        throw e;
      } catch (IOException e1) {
        e1.fillInStackTrace();
      }
    } finally {
      try {
        this.close(httpClient, get, response);
      } catch (Exception e) {
        e.fillInStackTrace();
      }
    }
    return is200;
  }


  public static void main(String[] args) throws Exception {//222.169.193.162

  }
}


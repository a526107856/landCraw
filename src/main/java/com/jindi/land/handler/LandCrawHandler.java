package com.jindi.land.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jindi.land.service.ICrawCommonService;
import com.jindi.land.util.HttpClientUtils;
import com.jindi.land.util.ResourceUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class LandCrawHandler {

  @Autowired
  private StringRedisTemplate redis;
  @Autowired
  private ICrawCommonService proxyService;

  private String redisDateKey;
  private String redisListUrlKey;
  private String redisDetailUrlKey;
  private String listUrl;

  private String URL = "http://www.landchina.com/";
  private static final String COMMON_DATE = "yyyy-MM-dd";
  private static Map<String, String> provinceMap = new HashMap<>();
  private static HashMap<String, Map> fontDic = new HashMap<>();

  static {
    provinceMap.put("北京市", "11");
    provinceMap.put("天津市", "12");
    provinceMap.put("河北省", "13");
    provinceMap.put("山西省", "14");
    provinceMap.put("内蒙古", "15");
    provinceMap.put("辽宁省", "21");
    provinceMap.put("吉林省", "22");
    provinceMap.put("黑龙江省", "23");
    provinceMap.put("上海市", "31");
    provinceMap.put("江苏省", "32");
    provinceMap.put("浙江省", "33");
    provinceMap.put("安徽省", "34");
    provinceMap.put("福建省", "35");
    provinceMap.put("江西省", "36");
    provinceMap.put("山东省", "37");
    provinceMap.put("河南省", "41");
    provinceMap.put("湖北省", "42");
    provinceMap.put("湖南省", "43");
    provinceMap.put("广东省", "44");
    provinceMap.put("广西壮族", "45");
    provinceMap.put("海南省", "46");
    provinceMap.put("重庆市", "50");
    provinceMap.put("四川省", "51");
    provinceMap.put("贵州省", "52");
    provinceMap.put("云南省", "53");
    provinceMap.put("西藏", "54");
    provinceMap.put("陕西省", "61");
    provinceMap.put("甘肃省", "62");
    provinceMap.put("青海省", "63");
    provinceMap.put("宁夏回族", "64");
    provinceMap.put("新疆维吾尔", "65");
    provinceMap.put("新疆建设兵团", "66");
  }

  static {
    String[] paths = new String[]{"9dmAedenSfgpg3DohqQX3ooa22B71jqw"
        , "dO7SrQoa3y3yvyF37PxOIBKGxQvSPb3F"
        , "vcWMpM88o1GOYCKCpDtZpdu9PwXUDNJM"
        , "IT7bzGhN5CxBYcXds3uLSmMuRLsDolvX"
        , "fWezGsME9BXQ1W3sGFEqzhlPxznzf5nQ"
        , "xXnzZIAmQMyXkMKm3c8hj5ERCamaAjKg"
        , "N2BQFizNQ7aV6gymJy75hcEXV7Bqqcah"
        , "pieXATBGyLsPEWSOUSu1wfcC3r3vM8aa"
        , "YQrFiPvT3SaifS4J3BLtxTgci0q3oe0B"
        , "vJ3z6kV9Oo0MYQNQhaEWLTMF54ysPI1p"};
    for (String path : paths) {
      try {
        HashMap<String, String> dic = new HashMap<>();
        String txts = ResourceUtil.readResource("fontDict/" + path + ".txt");
        String[] lines = txts.split("\n");
        for (String line : lines) {
          dic.put(line.split(":")[0], line.split(":")[1].replace("\n", ""));
        }
        fontDic.put(path, dic);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  protected void init(String redisKey,
      String listUrl) {
    this.redisDateKey = redisKey + "StartDate";
    this.redisListUrlKey = redisKey + "List";
    this.redisDetailUrlKey = redisKey + "DetailUrl";
    this.listUrl = listUrl;
  }

  /**
   * 抓取调度入口
   */
  protected void crawSchedule() {

    if (StringUtils.isEmpty(redisDateKey) ||
        StringUtils.isEmpty(redisListUrlKey) ||
        StringUtils.isEmpty(redisDetailUrlKey) ||
        StringUtils.isEmpty(URL) ||
        StringUtils.isEmpty(listUrl)) {
      log.warn("参数未正确初始化");
      return;
    }

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 15,
        60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    int listThreadNum = 5;
    for (int i = 0; i < listThreadNum; i++) {
      threadPoolExecutor.execute(new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              if (!isExistsCondition(redisListUrlKey)) {
                Thread.sleep(5 * 1000);
                if (!isExistsCondition(redisListUrlKey)) {
                  String[] arrDate = getSearchDate();
                  String startDate = arrDate[0];
                  String endDate = arrDate[1];
                  putCondition(startDate, endDate);
                }
                continue;
              }
              spiderList(redisListUrlKey);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      });
    }

    int detailThreadNum = 5;
    for (int i = 0; i < detailThreadNum; i++) {
      threadPoolExecutor.execute(new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              if (!isExistsCondition(redisDetailUrlKey)) {
                Thread.sleep(10 * 1000);
                continue;
              }

              String[] strings = getZsetValueDouble(
                  redisDetailUrlKey);
              String detailUrl = strings[0];

              boolean isExists = isCraw(detailUrl);
              if (isExists) {
                log.info("exists, not craw, listUrl={}", detailUrl);
                continue;
              }

              double score = Double.parseDouble(strings[1]);
              spiderDetail(detailUrl, score);

            } catch (InterruptedException e) {
              e.printStackTrace();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      });
    }
  }

  protected String[] getSearchDate() {
    //设置搜索相隔日期
    int datInterval = 10;

    String[] arrDate = new String[2];
    String startDate = "";
    String endDate = "";
    SimpleDateFormat format = new SimpleDateFormat(COMMON_DATE);

    if (!isExistsCondition(redisDateKey)) {
      //初始化
      Calendar calendar = Calendar.getInstance();
      calendar.set(1989, 01, 01);
      startDate = format.format(calendar.getTime());
      calendar.add(Calendar.DAY_OF_MONTH, datInterval);
      endDate = format.format(calendar.getTime());
      arrDate[0] = startDate;
      arrDate[1] = endDate;
      redis.opsForZSet().add(redisDateKey, startDate, 1);
      return arrDate;
    }

    startDate = getZsetValueDouble(redisDateKey)[0];
    //当前日期
    Calendar now = Calendar.getInstance();
    //当前日期+1月
    Calendar nowAdd1M = Calendar.getInstance();
    int year = Integer.parseInt(startDate.split("-")[0]);
    int month = Integer.parseInt(startDate.split("-")[1]);
    int day = Integer.parseInt(startDate.split("-")[2]);
    //java日期0-11，设置month代表+1月
    nowAdd1M.set(year, month, day);

    Calendar tmp = Calendar.getInstance();

    if (nowAdd1M.getTime().compareTo(now.getTime()) > 0) {
      //抓未来一个月的数据
      tmp.add(Calendar.DAY_OF_MONTH, -datInterval);
      startDate = format.format(now.getTime());
      tmp.add(Calendar.MONTH, 1);
      tmp.add(Calendar.DAY_OF_MONTH, datInterval);
      endDate = format.format(now.getTime());
    } else {
      tmp.set(year, month - 1, day);
      tmp.add(Calendar.DAY_OF_MONTH, datInterval);
      startDate = format.format(tmp.getTime());
      tmp.add(Calendar.DATE, datInterval);
      endDate = format.format(tmp.getTime());
    }

    arrDate[0] = startDate;
    arrDate[1] = endDate;
    redis.opsForZSet().add(redisDateKey, startDate, 1);
    return arrDate;
  }

  protected String getHexStr(String str) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("js");
      String js = "function stringToHex() {"
          + "    var val = \"\";"
          + "    var str = \"" + str + "\";"
          + "    for (var i = 0; i < str.length; i++) {"
          + "        if (val == \"\") val = str.charCodeAt(i).toString(16);"
          + "        else val += str.charCodeAt(i).toString(16);"
          + "    }"
          + "    return val;"
          + "}";
      engine.eval(js);
      Invocable invocable = (Invocable) engine;
      String result = (String) invocable.invokeFunction("stringToHex");
      return result;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取加密cookie
   *
   * @param proxy 代理
   * @return 可访问网站cookie
   */
  protected String crackCookie(HttpHost proxy) {
    String crackCookie = "";
    try {
      //添加浏览器大小信息
      String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
      Connection conn = Jsoup.connect(listUrl)
          .proxy(proxy.getHostName(), proxy.getPort()).userAgent(userAgent).timeout(60 * 1000);
      Response response = conn.execute();

      String yunsuoSessionVerify = response.cookie("security_session_verify");
      String securityVerifyData = getHexStr("1920,1080");
      String srcurl = getHexStr(listUrl);

      //通过第一次cookie获取 security_session_mid_verify
      conn = Jsoup.connect(listUrl + "&security_verify_data=" + securityVerifyData)
          .proxy(proxy.getHostName(), proxy.getPort()).userAgent(userAgent)
          .cookie("security_session_verify", yunsuoSessionVerify).cookie("srcurl", srcurl)
          .cookie("path", "/")
          .timeout(60 * 1000);

      response = conn.execute();
      String securitySessionMidVerify = response.cookie("ASP.NET_SessionId");
      // 全部cookie拼接
      crackCookie = "ASP.NET_SessionId=2q5vacmcywquguow00s0koh4; security_session_verify="
          + yunsuoSessionVerify +
          "; srcurl=" + srcurl +
          "; ASP.NET_SessionId=" + securitySessionMidVerify;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return crackCookie;
  }

  /**
   * 字体反爬处理
   *
   * @param detailHtml 详情页html文本
   * @return 将文本中反爬字体替换成正常文本
   */
  protected String crackFontHandler(String detailHtml) {
    String pattern = "styles/fonts/(.*)\\.woff\\?(.*?)'";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(detailHtml);
    if (m.find()) {
      String fontUrl = m.group(0);
      fontUrl = fontUrl.substring(0, fontUrl.length() - 1);
      fontUrl = URL + fontUrl;
      String key = fontUrl.replace(URL + "styles/fonts/", "").split("\\.")[0];
      if (fontDic.containsKey(key)) {
        Map<String, String> mapFont = fontDic.get(key);
        // 遍历字体映射map,用value替换掉key
        for (Map.Entry<String, String> entry : mapFont.entrySet()) {
          detailHtml = detailHtml.replace(entry.getKey(), entry.getValue());
        }
        return detailHtml;
      }
      log.warn("new font {}", fontUrl);
    }
    return null;
  }

  /**
   * 将网站的按行政区域查询的检索条件装入map
   */
  protected void putCondition(String startDate, String endDate) {
    for (Map.Entry<String, String> entry : provinceMap.entrySet()) {
      JsonObject jsonSearch = new JsonObject();
      jsonSearch.addProperty("province", entry.getKey());
      jsonSearch.addProperty("provinceValue", entry.getValue());
      jsonSearch.addProperty("startDate", startDate);
      jsonSearch.addProperty("endDate", endDate);
      redis.opsForZSet().add(redisListUrlKey, jsonSearch.toString(), 1);
    }
  }

  protected boolean isExistsCondition(String zsetKey) {
    Set<TypedTuple<String>> zset =
        redis.opsForZSet().reverseRangeByScoreWithScores(zsetKey, 0, 1, 0, 1);
    return zset.size() == 0 ? false : true;
  }

  protected String[] getZsetValueDouble(String zsetKey) {
    Set<TypedTuple<String>> set =
        redis.opsForZSet().reverseRangeByScoreWithScores(zsetKey, 0, 2, 0, 1);
    if (set != null && set.size() == 1) {
      Iterator<TypedTuple<String>> iterator = set.iterator();
      TypedTuple<String> typedTuple = iterator.next();
      redis.opsForZSet().remove(zsetKey, typedTuple.getValue());
      String[] strings = new String[2];
      strings[0] = typedTuple.getValue();
      strings[1] = typedTuple.getScore() + "";
      return strings;
    } else {
      return null;
    }
  }

  /**
   * 爬取列表页数据、解析、把详细页url保存到redis
   */
  protected void spiderList(String redisKey) {
    try {
      //获取redis中检索条件、对应sorce
      String[] strings = getZsetValueDouble(redisKey);
      if (strings == null) {
        log.info("列表页，等待redis扔词====");
        return;
      }
      JSONObject JSONObj = JSONObject.parseObject(strings[0]);
      String province = JSONObj.get("province").toString();
      String provinceValue = JSONObj.get("provinceValue").toString();
      String startDate = JSONObj.get("startDate").toString();
      String endDate = JSONObj.get("endDate").toString();

      //TODO
      startDate = "2000-01-01";

      String dateArea = startDate + '~' + endDate;
      String selectDistrict = provinceValue + "▓~" + province;

      Double conditionSorce = Double.valueOf(strings[1]);

      int pageNo = 0;
      int pageCount = 1;
      while (pageNo <= pageCount) {
        pageNo++;
        log.info("抓取列表页：province = {}, date = {} - {}, pageCount = {}, pageNo={}",
            province, startDate, endDate, pageCount, pageNo);
        // 封装查询时的参数
        Map<String, String> mapParams = new HashMap<>(4);
        mapParams.put("dateArea", dateArea);
        mapParams.put("selectDistrict", selectDistrict);
        mapParams.put("pageNo", pageNo + "");
        //请求list
        //TODO 301错误
        String strHtml = getListHtml(mapParams);

        // 解析列表页
        if (!StringUtils.isEmpty(strHtml)) {
          if (strHtml.contains("没有检索到相关数据")) {
            log.info("没有检索到相关数据：province = {}, date = {} - {}", province, startDate, endDate);
            return;
          }

          Document document = Jsoup.parse(strHtml);
          if (document != null) {
            Elements elements =
                document
                    .select("table#TAB_contentTable > tbody > tr > td.queryCellBordy > a[href]");
            if (elements != null && elements.size() > 0) {

              //遍历列表页
              for (int j = 0; j < elements.size(); j++) {
                String detailUrl = URL
                    + elements.get(j).getElementsByAttribute("href").first().attr("href");
                redis.opsForZSet().add(redisDetailUrlKey,
                    detailUrl, 1);
                log.info("success，detailUrl put redis, detailUrl={}", detailUrl);
              }

              //发现总页数
              if (pageNo == 1) {
                String pageTmp = document.select(
                    "table > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(2) > td > div > table > tbody > tr > td:nth-child(1)")
                    .text();
                pageCount = Integer
                    .parseInt(pageTmp.split("页")[0].replace("共", ""));
              }
            }
          }
        } else {
          //抓取失败、降低优先级
          log.warn("craw failed：province = {}, date = {} - {}", province, startDate, endDate);
          double d = conditionSorce - 0.01;
          if (d <= 0) {
            d = 0.01;
          }
          redis.opsForZSet().add(redisKey, JSONObj.toString(), d);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected String getListHtml(Map<String, String> map) {
    try {
      HttpHost host = proxyService.getHost();
      //模拟浏览器信息
      String securityVerifyData = getHexStr("1920,1080");
      //获取加密cookie
      String cookie = crackCookie(host);
      if (StringUtils.isEmpty(cookie)) {
        log.warn("get cookie failed");
        return null;
      }
      HttpPost post = new HttpPost(listUrl + "&security_verify_data=" + securityVerifyData);

      post.addHeader("User-Agent",
          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
      post.addHeader("Host", "www.landchina.com");
      post.addHeader("Referer", listUrl);
      post.addHeader("Upgrade-Insecure-Requests", "1");
      post.addHeader("Cookie",
          cookie);
      //封装post参数

      List<NameValuePair> nvps = getListParams(map);
      post.setEntity(new UrlEncodedFormEntity(nvps, "gbk"));

      return HttpClientUtils.getInstance().post(post, host);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 爬取详细页面数据、解析、入库
   */
  protected void spiderDetail(String url, double score) {

    log.info("抓取详情页、 detailUrl = {}", url);

    HttpHost proxy = proxyService.getHost();
    String securityVerifyData = getHexStr("1920,1080");
    String cookie = crackCookie(proxy);
    url = url + "&security_verify_data=" + securityVerifyData;
    HttpGet get = new HttpGet(url);
    get.addHeader("Content-Type", "text/html; charset=utf8");
    get.addHeader("User-Agent",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
    get.addHeader("cookie", cookie);

    String detailHtml = HttpClientUtils.getInstance().get(get, "gbk", proxy);

    boolean isSpiderSuccess = !StringUtils.isEmpty(detailHtml) && detailHtml
        .contains("contentItem");
    if (isSpiderSuccess) {
      parserDetail(detailHtml, url);

    } else {
      redis.opsForZSet().add(redisDetailUrlKey,
          url, score - 0.01);
      log.info("craw failed detail={}", url);
    }
  }

  protected abstract boolean isCraw(String detailUrl);

  protected abstract List<NameValuePair> getListParams(Map<String, String> map);

  /**
   * 将参数中的数据封装到LandEntity对象中
   */
  protected abstract void parserDetail(String detailHtml, String detailUrl);

}

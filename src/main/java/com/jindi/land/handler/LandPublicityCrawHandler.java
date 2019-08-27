package com.jindi.land.handler;

import com.alibaba.fastjson.JSONObject;
import com.jindi.land.entity.LandPublicityEntity;
import com.jindi.land.service.ILandPublicityService;
import com.jindi.land.service.impl.LandPublicityServiceImpl;
import com.jindi.land.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class LandPublicityCrawHandler extends LandCrawHandler implements ApplicationRunner {

  @Autowired
  private ILandPublicityService landPublicityService;

  @Override
  protected List<NameValuePair> getListParams(Map<String, String> map) {

    String dateArea = map.get("dateArea");
    String select_district = map.get("selectDistrict");
    String pageNo = map.get("pageNo");

    List<NameValuePair> nvps = new ArrayList<>();
    nvps.add(new BasicNameValuePair("__VIEWSTATE",
        "/wEPDwUJNjkzNzgyNTU4D2QWAmYPZBYIZg9kFgICAQ9kFgJmDxYCHgdWaXNpYmxlaGQCAQ9kFgICAQ8WAh4Fc3R5bGUFIEJBQ0tHUk9VTkQtQ09MT1I6I2YzZjVmNztDT0xPUjo7ZAICD2QWAgIBD2QWAmYPZBYCZg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHgRUZXh0ZWRkAgEPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFhwFDT0xPUjojRDNEM0QzO0JBQ0tHUk9VTkQtQ09MT1I6O0JBQ0tHUk9VTkQtSU1BR0U6dXJsKGh0dHA6Ly93d3cubGFuZGNoaW5hLmNvbS9Vc2VyL2RlZmF1bHQvVXBsb2FkL3N5c0ZyYW1lSW1nL3hfdGRzY3dfc3lfamhnZ18wMDAuZ2lmKTseBmhlaWdodAUBMxYCZg9kFgICAQ9kFgJmDw8WAh8CZWRkAgIPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPFgQfAQUgQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjsfAGgWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAICD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCAgEPZBYCZg8WBB8BBYYBQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjtCQUNLR1JPVU5ELUlNQUdFOnVybChodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vVXNlci9kZWZhdWx0L1VwbG9hZC9zeXNGcmFtZUltZy94X3Rkc2N3X3p5X2RrZ3NfMDEuZ2lmKTsfAwUCNDYWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAIBD2QWAmYPZBYCZg9kFgJmD2QWAgIBD2QWAmYPFgQfAQUgQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjsfAGgWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAIDD2QWAgIDDxYEHglpbm5lcmh0bWwF+gY8cCBhbGlnbj0iY2VudGVyIj48c3BhbiBzdHlsZT0iZm9udC1zaXplOiB4LXNtYWxsIj4mbmJzcDs8YnIgLz4NCiZuYnNwOzxhIHRhcmdldD0iX3NlbGYiIGhyZWY9Imh0dHA6Ly93d3cubGFuZGNoaW5hLmNvbS8iPjxpbWcgYm9yZGVyPSIwIiBhbHQ9IiIgd2lkdGg9IjI2MCIgaGVpZ2h0PSI2MSIgc3JjPSIvVXNlci9kZWZhdWx0L1VwbG9hZC9mY2svaW1hZ2UvdGRzY3dfbG9nZS5wbmciIC8+PC9hPiZuYnNwOzxiciAvPg0KJm5ic3A7PHNwYW4gc3R5bGU9ImNvbG9yOiAjZmZmZmZmIj5Db3B5cmlnaHQgMjAwOC0yMDE5IERSQ25ldC4gQWxsIFJpZ2h0cyBSZXNlcnZlZCZuYnNwOyZuYnNwOyZuYnNwOyA8c2NyaXB0IHR5cGU9InRleHQvamF2YXNjcmlwdCI+DQp2YXIgX2JkaG1Qcm90b2NvbCA9ICgoImh0dHBzOiIgPT0gZG9jdW1lbnQubG9jYXRpb24ucHJvdG9jb2wpID8gIiBodHRwczovLyIgOiAiIGh0dHA6Ly8iKTsNCmRvY3VtZW50LndyaXRlKHVuZXNjYXBlKCIlM0NzY3JpcHQgc3JjPSciICsgX2JkaG1Qcm90b2NvbCArICJobS5iYWlkdS5jb20vaC5qcyUzRjgzODUzODU5YzcyNDdjNWIwM2I1Mjc4OTQ2MjJkM2ZhJyB0eXBlPSd0ZXh0L2phdmFzY3JpcHQnJTNFJTNDL3NjcmlwdCUzRSIpKTsNCjwvc2NyaXB0PiZuYnNwOzxiciAvPg0K54mI5p2D5omA5pyJJm5ic3A7IOS4reWbveWcn+WcsOW4guWcuue9kSZuYnNwOyZuYnNwO+aKgOacr+aUr+aMgTrmtZnmsZ/oh7vlloTnp5HmioDogqHku73mnInpmZDlhazlj7gmbmJzcDs8YnIgLz4NCuWkh+ahiOWPtzog5LqsSUNQ5aSHMDkwNzQ5OTLlj7cg5Lqs5YWs572R5a6J5aSHMTEwMTAyMDAwNjY2KDIpJm5ic3A7PGJyIC8+DQo8L3NwYW4+Jm5ic3A7Jm5ic3A7Jm5ic3A7PGJyIC8+DQombmJzcDs8L3NwYW4+PC9wPh8BBWRCQUNLR1JPVU5ELUlNQUdFOnVybChodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vVXNlci9kZWZhdWx0L1VwbG9hZC9zeXNGcmFtZUltZy94X3Rkc2N3MjAxM195d18xLmpwZyk7ZGRtLIlmSFy/NrdhwIRsDOLWxo0XO1utXA8Q8eHnGZw/fA=="));
    nvps.add(new BasicNameValuePair("__EVENTVALIDATION",
        "/wEdAAKWkFBGgHzcFBn870XbAVoKCeA4P5qp+tM6YGffBqgTjcFHUKZRsb/YbJKRkYP7F/ME6r0zm67EJ08idwJeo33r"));
    nvps.add(new BasicNameValuePair("hidComName", "default"));
    nvps.add(new BasicNameValuePair("TAB_QueryConditionItem",
        "42ecfb7c-75b0-4106-8b88-556d2b5622f9"));
    nvps.add(new BasicNameValuePair("TAB_QuerySortItemList",
        "20da3312-3b36-4e96-9398-fc8c5174b02c:False"));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitConditionData",
        String.format(
            "4a611fc4-42b1-4861-ac43-8d25b002dc2b:%s|4a611fc4-75b2-9531-ac26-8d25b002dc2b:%s",
            select_district, dateArea)));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitOrderData",
        "20da3312-3b36-4e96-9398-fc8c5174b02c:False"));
    nvps.add(new BasicNameValuePair("TAB_RowButtonActionControl", ""));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitPagerData", pageNo));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitSortData", ""));
    return nvps;
  }

  /**
   * 将参数中的数据封装到LandEntity对象中
   */
  @Override
  protected void parserDetail(String detailHtml, String detailUrl) {
    ArrayList<LandPublicityEntity> entities = new ArrayList<>();
    //字体反爬处理
    detailHtml = crackFontHandler(detailHtml);

    //region 解析
    if (!StringUtils.isEmpty(detailHtml)) {
      Document detailDocument = Jsoup.parse(detailHtml);
      Elements table_elements = detailDocument
          .select("#tdContent > table > tbody > tr > td > table");
      //页面可能存在多条数据，且html结构不固定，部分字段css与xpath不可用，需要结合正则使用
      for (int i = 0; i < table_elements.size(); i++) {
        JSONObject data = new JSONObject();

        Elements elements = table_elements.get(i).select("tr > td");
        for (int j = 0; j < elements.size(); j++) {
          if (j % 2 == 0 && j < elements.size() - 1) {
            data.put(elements.get(j).text().trim(), elements.get(j + 1).text().trim());
          }
        }

        //优先使用json.toJavaObject的值
        LandPublicityEntity entity = JSONObject
            .toJavaObject(data, LandPublicityEntity.class);

        if (!StringUtils.isEmpty(entity.getFinalPrice()) &&
            elements.text().contains("成交价(万元)") || elements.text().contains("成交价（万元）")) {
          entity.setFinalPrice(entity.getFinalPrice() + "万元");
        }
        //判断为当前页面第几条数据
        entity.setIndex(i + 1);
        entity.setHtml(detailHtml);

        if (StringUtils.isEmpty(entity.getTitle())) {
          Element element = detailDocument
              .selectFirst("#tdContent > table > tbody > tr:nth-child(1) > td");
          if (element != null) {
            entity.setTitle(element.text().trim());
          }
          element = detailDocument.selectFirst("#lblXzq");
          if (element != null) {
            entity.setAdministrativeDistrict(element.text().trim().replace("行政区：", ""));
          }
        }

        if (StringUtils.isEmpty(entity.getPublicAnnouncementPeriod())) {
          Element element = detailDocument
              .selectFirst("#tdContent > table > tbody > tr:nth-child(2) > td > p:nth-child(6)");
          if (element != null) {
            String public_announcement_period = element.text().trim().replace("二、公示期：", "");
            if (!StringUtils.isEmpty(public_announcement_period)) {
              //公示期：.*?</p>
              String pattern_period = "公示期：.*?</p>";
              Pattern r_period = Pattern.compile(pattern_period);
              Matcher m_period = r_period.matcher(detailHtml);
              if (m_period.find()) {
                //公示期：<u>2016年08月12日</u> 至 <u>2016年08月17日</u> </p>
                String period = m_period.group(0);
                period = period.replace(" ", "").replace("<u>", "").replace("</u>", "")
                    .replace("</p>", "").replace("公示期：", "");
                entity.setPublicAnnouncementPeriod(period);
              }
            }
          }
        }

        if (StringUtils.isEmpty(entity.getFeedbackMethod())) {
          Pattern feedback_method = Pattern.compile("三、(.*?)</p>");
          Matcher m_period = feedback_method.matcher(detailHtml);
          if (m_period.find()) {
            //三、 该宗地双方已签订成交确认书，在30日内签订出让合同，相关事宜在合同中约定</p>
            String period = m_period.group(0);
            period = period.replace(" ", "").replace("意见反馈方式:", "").replace("三、", "")
                .replace("</p>", "");
            entity.setFeedbackMethod(period);
          }
        }

        if (StringUtils.isEmpty(entity.getContactOrganize())) {
          String pattern_period = "联系单位：(.*?)<br";
          Pattern r_period = Pattern.compile(pattern_period);
          Matcher m_period = r_period.matcher(detailHtml);
          if (m_period.find()) {
            //联系单位：五原县国土资源局<br>
            String period = m_period.group(0);
            period = period.replace(" ", "").replace("联系单位：", "").replace("<br", "");
            entity.setContactOrganize(period);
          }
        }

        if (StringUtils.isEmpty(entity.getOrganizeLocation())) {
          String pattern_period = "单位地址：(.*?)<br";
          Pattern r_period = Pattern.compile(pattern_period);
          Matcher m_period = r_period.matcher(detailHtml);
          if (m_period.find()) {
            //单位地址：五原县隆兴昌镇新华北路西侧<br>
            String period = m_period.group(0);
            period = period.replace(" ", "").replace("单位地址：", "").replace("<br", "");
            entity.setOrganizeLocation(period);
          }
        }

        if (StringUtils.isEmpty(entity.getPostalCode())) {
          String pattern_period = "邮政编码：(.*?)<br";
          Pattern r_period = Pattern.compile(pattern_period);
          Matcher m_period = r_period.matcher(detailHtml);
          if (m_period.find()) {
            //邮政编码：015100<br>
            String period = m_period.group(0);
            period = period.replace("邮政编码：", "").replace("<br", "");
            entity.setPostalCode(period);
          }
        }

        if (StringUtils.isEmpty(entity.getContactNumber())) {
          String pattern_period = "联系电话：(.*?)<br";
          Pattern r_period = Pattern.compile(pattern_period);
          Matcher m_period = r_period.matcher(detailHtml);
          if (m_period.find()) {
            //邮政编码：015100<br>
            String period = m_period.group(0);
            period = period.replace("联系电话：", "").replace("<br", "");
            entity.setContactNumber(period);
          }
        }

        if (StringUtils.isEmpty(entity.getContactPerson())) {
          String pattern_period = "联 系 人：(.*?)<br";
          Pattern r_period = Pattern.compile(pattern_period);
          Matcher m_period = r_period.matcher(detailHtml);
          if (m_period.find()) {
            //邮政编码：015100<br>
            String period = m_period.group(0);
            period = period.replace("联 系 人：", "").replace("<br", "");
            entity.setContactPerson(period);
          }
        }

        if (StringUtils.isEmpty(entity.getElectronicMail())) {
          String pattern_period = "电子邮件：(.*?)<br";
          Pattern r_period = Pattern.compile(pattern_period);
          Matcher m_period = r_period.matcher(detailHtml);
          if (m_period.find()) {
            //邮政编码：015100<br>
            String period = m_period.group(0);
            period = period.replace("电子邮件：", "").replace("<br", "");
            entity.setElectronicMail(period);
          }
        }

        if (StringUtils.isEmpty(entity.getPublicationOrganize())) {
          Element element = detailDocument
              .selectFirst("#tdContent > table > tbody > tr:nth-child(3) > td");
          if (element != null) {
            String tmp = element.text().trim()
                .replace(" ", "");
            String pub_ora = tmp.substring(0, tmp.length() - 11);
            entity.setPublicationOrganize(pub_ora);
          }
        }

        if (StringUtils.isEmpty(entity.getPublicationDate())) {
          Element element =
              detailDocument
                  .selectFirst("#tdContent > table > tbody > tr:nth-child(3) > td");
          if (element != null) {
            String tmp = detailDocument
                .selectFirst("#tdContent > table > tbody > tr:nth-child(3) > td").text().trim()
                .replace(" ", "");
            String pub_date = tmp.substring(tmp.length() - 11);
            entity.setPublicationDate(pub_date);
          }
        }

        if (StringUtils.isEmpty(entity.getPublicationStartDateClean())) {
          entity.setPublicationStartDateClean(
              DateUtils.formatDate(entity.getPublicAnnouncementPeriod().split("至")[0].trim()));
        }

        if (StringUtils.isEmpty(entity.getPublicationEndDateClean())) {
          entity.setPublicationEndDateClean(
              DateUtils.formatDate(entity.getPublicAnnouncementPeriod().split("至")[1].trim()));
        }

        entity.setUrl(detailUrl);
        entities.add(entity);
      }
    }
    //endregion

    for (LandPublicityEntity entity : entities) {
      landPublicityService.insert(entity);
    }
  }

  @Override
  protected boolean isCraw(String detailUrl) {
    LandPublicityEntity entity = new LandPublicityEntity();
    entity.setUrl(detailUrl);
    return landPublicityService.exists(entity);
  }

  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {

    String redisKey = "zsetLandPublicity";
    String listUrl = "https://www.landchina.com/default.aspx?tabid=262";
    init(redisKey, listUrl);
    crawSchedule();
  }
}

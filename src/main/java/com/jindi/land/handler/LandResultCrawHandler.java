package com.jindi.land.handler;

import com.google.gson.Gson;
import com.jindi.land.entity.LandInstalmentPaymentEntity;
import com.jindi.land.entity.LandResultEntity;
import com.jindi.land.service.ILandResultService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public class LandResultCrawHandler extends LandCrawHandler implements ApplicationRunner {

  @Autowired
  private ILandResultService landResultService;

  @Override
  protected List<NameValuePair> getListParams(Map<String, String> map) {

    String dateArea = map.get("dateArea");
    String select_district = map.get("select_district");
    String pageNo = map.get("pageNo");
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("__VIEWSTATE",
        "/wEPDwUJNjkzNzgyNTU4D2QWAmYPZBYIZg9kFgICAQ9kFgJmDxYCHgdWaXNpYmxlaGQCAQ9kFgICAQ8WAh4Fc3R5bGUFIEJBQ0tHUk9VTkQtQ09MT1I6I2YzZjVmNztDT0xPUjo7ZAICD2QWAgIBD2QWAmYPZBYCZg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHgRUZXh0ZWRkAgEPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFhwFDT0xPUjojRDNEM0QzO0JBQ0tHUk9VTkQtQ09MT1I6O0JBQ0tHUk9VTkQtSU1BR0U6dXJsKGh0dHA6Ly93d3cubGFuZGNoaW5hLmNvbS9Vc2VyL2RlZmF1bHQvVXBsb2FkL3N5c0ZyYW1lSW1nL3hfdGRzY3dfc3lfamhnZ18wMDAuZ2lmKTseBmhlaWdodAUBMxYCZg9kFgICAQ9kFgJmDw8WAh8CZWRkAgIPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPFgQfAQUgQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjsfAGgWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAICD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCAgEPZBYCZg8WBB8BBYYBQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjtCQUNLR1JPVU5ELUlNQUdFOnVybChodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vVXNlci9kZWZhdWx0L1VwbG9hZC9zeXNGcmFtZUltZy94X3Rkc2N3X3p5X2pnZ2dfMDEuZ2lmKTsfAwUCNDYWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAIBD2QWAmYPZBYCZg9kFgJmD2QWAgIBD2QWAmYPFgQfAQUgQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjsfAGgWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAIDD2QWAgIDDxYEHglpbm5lcmh0bWwF+gY8cCBhbGlnbj0iY2VudGVyIj48c3BhbiBzdHlsZT0iZm9udC1zaXplOiB4LXNtYWxsIj4mbmJzcDs8YnIgLz4NCiZuYnNwOzxhIHRhcmdldD0iX3NlbGYiIGhyZWY9Imh0dHA6Ly93d3cubGFuZGNoaW5hLmNvbS8iPjxpbWcgYm9yZGVyPSIwIiBhbHQ9IiIgd2lkdGg9IjI2MCIgaGVpZ2h0PSI2MSIgc3JjPSIvVXNlci9kZWZhdWx0L1VwbG9hZC9mY2svaW1hZ2UvdGRzY3dfbG9nZS5wbmciIC8+PC9hPiZuYnNwOzxiciAvPg0KJm5ic3A7PHNwYW4gc3R5bGU9ImNvbG9yOiAjZmZmZmZmIj5Db3B5cmlnaHQgMjAwOC0yMDE4IERSQ25ldC4gQWxsIFJpZ2h0cyBSZXNlcnZlZCZuYnNwOyZuYnNwOyZuYnNwOyA8c2NyaXB0IHR5cGU9InRleHQvamF2YXNjcmlwdCI+DQp2YXIgX2JkaG1Qcm90b2NvbCA9ICgoImh0dHBzOiIgPT0gZG9jdW1lbnQubG9jYXRpb24ucHJvdG9jb2wpID8gIiBodHRwczovLyIgOiAiIGh0dHA6Ly8iKTsNCmRvY3VtZW50LndyaXRlKHVuZXNjYXBlKCIlM0NzY3JpcHQgc3JjPSciICsgX2JkaG1Qcm90b2NvbCArICJobS5iYWlkdS5jb20vaC5qcyUzRjgzODUzODU5YzcyNDdjNWIwM2I1Mjc4OTQ2MjJkM2ZhJyB0eXBlPSd0ZXh0L2phdmFzY3JpcHQnJTNFJTNDL3NjcmlwdCUzRSIpKTsNCjwvc2NyaXB0PiZuYnNwOzxiciAvPg0K54mI5p2D5omA5pyJJm5ic3A7IOS4reWbveWcn+WcsOW4guWcuue9kSZuYnNwOyZuYnNwO+aKgOacr+aUr+aMgTrmtZnmsZ/oh7vlloTnp5HmioDogqHku73mnInpmZDlhazlj7gmbmJzcDs8YnIgLz4NCuWkh+ahiOWPtzog5LqsSUNQ5aSHMDkwNzQ5OTLlj7cg5Lqs5YWs572R5a6J5aSHMTEwMTAyMDAwNjY2KDIpJm5ic3A7PGJyIC8+DQo8L3NwYW4+Jm5ic3A7Jm5ic3A7Jm5ic3A7PGJyIC8+DQombmJzcDs8L3NwYW4+PC9wPh8BBWRCQUNLR1JPVU5ELUlNQUdFOnVybChodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vVXNlci9kZWZhdWx0L1VwbG9hZC9zeXNGcmFtZUltZy94X3Rkc2N3MjAxM195d18xLmpwZyk7ZGR/7GXN5DIIOV/26iXPadE6/H1NZChSKWznh9WkA9yZZg=="));
    nvps.add(new BasicNameValuePair("__EVENTVALIDATION",
        "/wEWAgKZ8cmUBALN3cj/BKkUPcv3bDTFfdWn64G1DimV/gX9Bg6y+rvXVCgJ5LZz"));
    nvps.add(new BasicNameValuePair("hidComName", "default"));
    nvps.add(new BasicNameValuePair("TAB_QueryConditionItem",
        "9f2c3acd-0256-4da2-a659-6949c4671a2a"));
    nvps.add(new BasicNameValuePair("TAB_QueryConditionItem",
        "42ad98ae-c46a-40aa-aacc-c0884036eeaf"));
    nvps.add(new BasicNameValuePair("TAB_QuerySortItemList",
        "282:False"));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitConditionData",
        String.format(
            "9f2c3acd-0256-4da2-a659-6949c4671a2a:%s|42ad98ae-c46a-40aa-aacc-c0884036eeaf:%s",
            dateArea, select_district)));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitOrderData",
        "282:False"));
    nvps.add(new BasicNameValuePair("TAB_RowButtonActionControl", ""));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitPagerData", pageNo + ""));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitSortData", ""));
    return nvps;
  }

  /**
   * 将参数中的数据封装到LandEntity对象中
   */
  @Override
  protected void parserDetail(String detailHtml, String detailUrl) {
    //字体反爬处理
    detailHtml = crackFontHandler(detailHtml);

    //region 解析
    if (StringUtils.isEmpty(detailHtml)) {
      LandResultEntity entity = new LandResultEntity();
      Document detailDocument = Jsoup.parse(detailHtml);

      Element element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r1_c4_ctrl");
      if (element != null) {
        // 电子监管号
        entity.setElectronicRegulatoryNumber(element.text().trim());
      }
      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r1_c2_ctrl");
      if (element != null) {
        // 行政区
        entity.setDistrict(element.text().trim());
      }
      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r17_c2_ctrl");
      if (element != null) {
        // 项目名称
        entity.setProjectName(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r16_c2_ctrl");
      if (element != null) {
        // 项目位置
        entity.setProjectLocation(element.text()
            .trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r2_c2_ctrl");
      if (element != null) {
        // 面积
        entity.setArea(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r2_c4_ctrl");
      if (element != null) {
        // 土地来源
        entity.setLandSourceValue(element.text().trim());
      }

      String land_source_view = "";
      if (!"".equals(entity.getArea()) && !"".equals(entity.getLandSourceValue())) {
        if (Float.parseFloat(entity.getArea()) == Float.parseFloat(entity.getLandSourceValue())) {
          land_source_view = "现有建设用地";
        } else if (Float.parseFloat(entity.getLandSourceValue()) == 0) {
          land_source_view = "新增建设用地";
        } else {
          land_source_view = "新增建设用地(来自存量库)";
        }
      }
      entity.setLandSourceView(land_source_view);

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r3_c2_ctrl");
      if (element != null) {
        // 土地用途
        entity.setLandUseType(element.text()
            .trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r3_c4_ctrl");
      if (element != null) {
        // 供地方式
        entity.setLandSupplyMethod(element.text()
            .trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r19_c2");
      if (element != null) {
        // 土地使用年限
        entity.setLandUsePeriod(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r19_c4_ctrl");
      if (element != null) {
        // 行业分类
        entity.setCategory(element.text()
            .trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r20_c2_ctrl");
      if (element != null) {
        // 土地级别
        entity.setLandLevel(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r20_c4_ctrl");
      if (element != null) {
        // 成交价格
        entity.setTransactionPrice(element.text()
            .trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f2_r1_c2_ctrl");
      if (element != null) {
        // 约定容积率下限
        entity.setContractedVolumeRate(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f2_r1_c4_ctrl");
      if (element != null) {
        // 约定容积率上限
        entity.setContractedVolumeRateCeiling(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r21_c4_ctrl");
      if (element != null) {
        // 约定交地时间
        entity.setCommittedTime(ConvertToDate((element.text().trim())));
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r22_c2");
      if (element != null) {
        // 约定开工时间
        entity.setAgreementStartTime(ConvertToDate(element.text().trim()));
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r10_c2");
      if (element != null) {
        // 实际开工时间
        entity.setActualStartTime(ConvertToDate(element.text().trim()));
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r22_c4");
      if (element != null) {
        // 约定竣工时间
        entity.setScheduledCompletion(ConvertToDate(element.text().trim()));
      }
      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r10_c4");
      if (element != null) {
        // 实际竣工时间
        entity.setActualCompletionTime(ConvertToDate(element.text().trim()));
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r14_c2_ctrl");
      if (element != null) {
        // 批准单位
        entity.setAuthority(element.text().trim());
      }
      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r14_c4_ctrl");
      if (element != null) {
        // 合同签订日期
        entity.setContractDate(ConvertToDate(element.text().trim()));
      }

      entity.setUrl(detailUrl);

      Elements elements =
          detailDocument
              .select("table#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f3 > tbody > tr");
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[ ");
      if (elements.size() > 3) {
        List<LandInstalmentPaymentEntity> list = new ArrayList<>();
        // 三个tr是无数据的状态，每加一，数据量加一
        for (int i = 3; i < elements.size(); i++) {
          Elements elementsTd = elements.get(i).children();
          LandInstalmentPaymentEntity landInstalmentPaymentEntity = new LandInstalmentPaymentEntity();
          landInstalmentPaymentEntity
              .setElectronic_regulatory_number(entity.getElectronicRegulatoryNumber());
          landInstalmentPaymentEntity
              .setInstalment_payment_contract_payment_period_number(
                  elementsTd.get(0).text().trim());
          landInstalmentPaymentEntity
              .setInstalment_payment_convention_payment_date((elementsTd.get(1).text().trim()));
          landInstalmentPaymentEntity
              .setInstalment_payment_agreed_payment_amount(elementsTd.get(2).text().trim());
          landInstalmentPaymentEntity
              .setInstalment_payment_agreement_notes(elementsTd.get(3).text().trim());
          list.add(landInstalmentPaymentEntity);

          Gson gson = new Gson();
          String strJson = gson.toJson(landInstalmentPaymentEntity);
          stringBuffer.append(strJson + ",");
        }
      }
      stringBuffer.deleteCharAt(stringBuffer.length() - 1);
      stringBuffer.append("]");
      if (!"[]".equals(stringBuffer.toString())) {
        entity.setStrInsPaytoJson(stringBuffer.toString());
      }

      String landUser = "";
      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r9_c2_ctrl");
      if (element != null) {
        landUser = element.text()
            .replace("(", "（").replace(")", "）").trim();
      }
      if (StringUtils.isEmpty(landUser)) {
        element = detailDocument
            .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r23_c2_ctrl");
        if (element != null) {
          landUser = element.text()
              .replace("(", "（").replace(")", "）").trim();
        }
      }
      entity.setLandUseRightPerson(landUser);
      landResultService.insert(entity);
    }
    //endregion

  }

  @Override
  protected boolean isCraw(String detailUrl) {
    LandResultEntity entity = new LandResultEntity();
    entity.setUrl(detailUrl);
    return landResultService.exists(entity);
  }

  private Date ConvertToDate(String strDate) {
    // strDate : 2022年05月13日
    if ("".equals(strDate)) {
      return null;
    } else {
      DateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日");
      Date date = null;
      try {
        date = fmt.parse(strDate);
      } catch (java.text.ParseException e) {
        e.printStackTrace();
      }
      return date;
    }
  }

  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {

    String redisKey = "zsetLandChinaResult";
    String listUrl = "http://www.landchina.com/default.aspx?tabid=263";
    init(redisKey, listUrl);
    crawSchedule();
  }
}

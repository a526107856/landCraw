package com.jindi.land.handler;

import com.jindi.land.entity.LandMortgageEntity;
import com.jindi.land.service.ILandMortgageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class LandMortgageCrawHandler extends LandCrawHandler implements ApplicationRunner {

  @Autowired
  private ILandMortgageService landMortgageService;

  @Override
  protected List<NameValuePair> getListParams(Map<String, String> map) {

    String dateArea = map.get("dateArea");
    String select_district = map.get("selectDistrict");
    String pageNo = map.get("pageNo");
    String security_verify_data = map.get("security_verify_data");
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("__VIEWSTATE",
        "/wEPDwUJODc4NDQ4NTk0D2QWAmYPZBYIZg9kFgICAQ9kFgJmDxYCHgdWaXNpYmxlaGQCAQ9kFgICAQ8WAh4Fc3R5bGUFIEJBQ0tHUk9VTkQtQ09MT1I6I2YzZjVmNztDT0xPUjo7ZAICD2QWAgIBD2QWAmYPZBYCZg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHgRUZXh0ZWRkAgEPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFhwFDT0xPUjojRDNEM0QzO0JBQ0tHUk9VTkQtQ09MT1I6O0JBQ0tHUk9VTkQtSU1BR0U6dXJsKGh0dHA6Ly93d3cubGFuZGNoaW5hLmNvbS9Vc2VyL2RlZmF1bHQvVXBsb2FkL3N5c0ZyYW1lSW1nL3hfdGRzY3dfc3lfamhnZ18wMDAuZ2lmKTseBmhlaWdodAUBMxYCZg9kFgICAQ9kFgJmDw8WAh8CZWRkAgIPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPFgQfAQUgQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjsfAGgWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAICD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCAgEPZBYCZg8WBB8BBYQBQ09MT1I6IzAwMDAwMDtCQUNLR1JPVU5ELUNPTE9SOjtCQUNLR1JPVU5ELUlNQUdFOnVybChodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vVXNlci9kZWZhdWx0L1VwbG9hZC9zeXNGcmFtZUltZy94X3Rkc2N3X3p5X2R5XzAxLmdpZik7HwMFAjQ2FgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAQ9kFgJmD2QWAmYPZBYCZg9kFgICAQ9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAw9kFgICAw8WBB4JaW5uZXJodG1sBfoGPHAgYWxpZ249ImNlbnRlciI+PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogeC1zbWFsbCI+Jm5ic3A7PGJyIC8+DQombmJzcDs8YSB0YXJnZXQ9Il9zZWxmIiBocmVmPSJodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vIj48aW1nIGJvcmRlcj0iMCIgYWx0PSIiIHdpZHRoPSIyNjAiIGhlaWdodD0iNjEiIHNyYz0iL1VzZXIvZGVmYXVsdC9VcGxvYWQvZmNrL2ltYWdlL3Rkc2N3X2xvZ2UucG5nIiAvPjwvYT4mbmJzcDs8YnIgLz4NCiZuYnNwOzxzcGFuIHN0eWxlPSJjb2xvcjogI2ZmZmZmZiI+Q29weXJpZ2h0IDIwMDgtMjAxOSBEUkNuZXQuIEFsbCBSaWdodHMgUmVzZXJ2ZWQmbmJzcDsmbmJzcDsmbmJzcDsgPHNjcmlwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiPg0KdmFyIF9iZGhtUHJvdG9jb2wgPSAoKCJodHRwczoiID09IGRvY3VtZW50LmxvY2F0aW9uLnByb3RvY29sKSA/ICIgaHR0cHM6Ly8iIDogIiBodHRwOi8vIik7DQpkb2N1bWVudC53cml0ZSh1bmVzY2FwZSgiJTNDc2NyaXB0IHNyYz0nIiArIF9iZGhtUHJvdG9jb2wgKyAiaG0uYmFpZHUuY29tL2guanMlM0Y4Mzg1Mzg1OWM3MjQ3YzViMDNiNTI3ODk0NjIyZDNmYScgdHlwZT0ndGV4dC9qYXZhc2NyaXB0JyUzRSUzQy9zY3JpcHQlM0UiKSk7DQo8L3NjcmlwdD4mbmJzcDs8YnIgLz4NCueJiOadg+aJgOaciSZuYnNwOyDkuK3lm73lnJ/lnLDluILlnLrnvZEmbmJzcDsmbmJzcDvmioDmnK/mlK/mjIE65rWZ5rGf6Ie75ZaE56eR5oqA6IKh5Lu95pyJ6ZmQ5YWs5Y+4Jm5ic3A7PGJyIC8+DQrlpIfmoYjlj7c6IOS6rElDUOWkhzA5MDc0OTky5Y+3IOS6rOWFrOe9keWuieWkhzExMDEwMjAwMDY2NigyKSZuYnNwOzxiciAvPg0KPC9zcGFuPiZuYnNwOyZuYnNwOyZuYnNwOzxiciAvPg0KJm5ic3A7PC9zcGFuPjwvcD4fAQVkQkFDS0dST1VORC1JTUFHRTp1cmwoaHR0cDovL3d3dy5sYW5kY2hpbmEuY29tL1VzZXIvZGVmYXVsdC9VcGxvYWQvc3lzRnJhbWVJbWcveF90ZHNjdzIwMTNfeXdfMS5qcGcpO2RkS2ETSkZAlt7A8G71cI6dpzN3ES1LQmIfh93cksbajLc="));
    nvps.add(new BasicNameValuePair("__EVENTVALIDATION",
        "/wEdAAJhs9gmhUBmKXXmF2bN4sx+CeA4P5qp+tM6YGffBqgTjbf044uXZxv4eauJKBOwRElZRTsttdEFRqXc3tnhd9b1"));
    nvps.add(new BasicNameValuePair("hidComName", "default"));
    nvps.add(new BasicNameValuePair("TAB_QueryConditionItem",
        "86451846-6268-424d-b258-a294b5050f98"));

    nvps.add(new BasicNameValuePair("TAB_QuerySubmitConditionData",
        String.format(
            "21f209f9-8b65-4740-87aa-62f90e5faa47:%s|86451846-6268-424d-b258-a294b5050f98:%s",
            select_district, dateArea)));
    //21f209f9-8b65-4740-87aa-62f90e5faa47:11▓~北京市|86451846-6268-424d-b258-a294b5050f98:2016-4-15~2019-8-26
    nvps.add(new BasicNameValuePair("TAB_RowButtonActionControl", ""));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitPagerData", pageNo + ""));
    nvps.add(new BasicNameValuePair("TAB_QuerySubmitSortData", ""));
    nvps.add(new BasicNameValuePair("security_verify_data",security_verify_data));

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
    if (!StringUtils.isEmpty(detailHtml)) {
      LandMortgageEntity entity = new LandMortgageEntity();
      Document detailDocument = Jsoup.parse(detailHtml);
      Element element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r1_c2");
      if (element != null) {
        entity.setLandMark(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r1_c4");
      if (element != null) {
        entity.setLandNum(element.text().trim());
      }

      String landAministrativeArea = "";
      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r13_c2");
      if (element != null) {
        landAministrativeArea = element.text().trim();
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r13_c4");
      if (element != null) {
        landAministrativeArea = landAministrativeArea + " " + element.text().trim();
      }
      entity.setLandAministrativeArea(landAministrativeArea);

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r2_c2_ctrl");
      if (element != null) {
        entity.setLandLoc(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r2_c4_ctrl");
      if (element != null) {
        entity.setLandArea(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r3_c2_ctrl");
      if (element != null) {
        entity.setOtherItemApplicationNameNum(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r3_c4_ctrl");
      if (element != null) {
        entity.setUseRightNum(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r4_c2");
      if (element != null) {
        entity.setMortgagePerson(element.text().trim().replace("(", "（").replace(")", "）"));
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r4_c4");
      if (element != null) {
        entity.setNature(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r5_c2_ctrl");
      if (element != null) {
        entity
            .setMortgageApplicationName(element.text().trim().replace("(", "（").replace(")", "）"));
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r5_c4_ctrl");
      if (element != null) {
        entity.setMortgageToUser(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r9_c2_ctrl");
      if (element != null) {
        entity.setUserType(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r9_c4_ctrl");
      if (element != null) {
        entity.setMortgageArea(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r8_c2_ctrl");
      if (element != null) {
        entity.setEvaluateAmount(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r8_c4");
      if (element != null) {
        entity.setMortgageAmount(element.text().trim().replace(" ", ""));
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r11_c2_ctrl");
      if (element != null) {
        entity.setStartDate(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r11_c4");
      if (element != null) {
        entity.setEndDate(element.text().trim());
      }

      entity.setDetailUrl(detailUrl);
      landMortgageService.insert(entity);
    }
    //endregion

  }

  @Override
  protected boolean isCraw(String detailUrl) {
    LandMortgageEntity entity = new LandMortgageEntity();
    entity.setDetailUrl(detailUrl);
    return landMortgageService.exists(entity);
  }

  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {

    String redisKey = "zsetLandMortgage";
    String listUrl = "https://www.landchina.com/DesktopDefault.aspx?tabid=351";
    init(redisKey, listUrl);
    crawSchedule();
  }
}

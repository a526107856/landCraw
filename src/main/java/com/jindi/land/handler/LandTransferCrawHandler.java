package com.jindi.land.handler;

import com.jindi.land.entity.LandTransferEntity;
import com.jindi.land.service.ILandTransferService;
import com.jindi.land.util.DateUtils;
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
public class LandTransferCrawHandler extends LandCrawHandler implements ApplicationRunner {

  @Autowired
  private ILandTransferService landTransferService;

  @Override
  protected List<NameValuePair> getListParams(Map<String, String> map) {

    String dateArea = map.get("dateArea");
    String select_district = map.get("select_district");
    String pageNo = map.get("pageNo");
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("__VIEWSTATE",
        "/wEPDwUJODc4NDQ4NTk0D2QWAmYPZBYIZg9kFgICAQ9kFgJmDxYCHgdWaXNpYmxlaGQCAQ9kFgICAQ8WAh4Fc3R5bGUFIEJBQ0tHUk9VTkQtQ09MT1I6I2YzZjVmNztDT0xPUjo7ZAICD2QWAgIBD2QWAmYPZBYCZg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHgRUZXh0ZWRkAgEPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFhwFDT0xPUjojRDNEM0QzO0JBQ0tHUk9VTkQtQ09MT1I6O0JBQ0tHUk9VTkQtSU1BR0U6dXJsKGh0dHA6Ly93d3cubGFuZGNoaW5hLmNvbS9Vc2VyL2RlZmF1bHQvVXBsb2FkL3N5c0ZyYW1lSW1nL3hfdGRzY3dfc3lfamhnZ18wMDAuZ2lmKTseBmhlaWdodAUBMxYCZg9kFgICAQ9kFgJmDw8WAh8CZWRkAgIPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPZBYEZg9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAg9kFgJmD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCZg9kFgJmD2QWAmYPFgQfAQUgQ09MT1I6I0QzRDNEMztCQUNLR1JPVU5ELUNPTE9SOjsfAGgWAmYPZBYCAgEPZBYCZg8PFgIfAmVkZAICD2QWBGYPZBYCZg9kFgJmD2QWAmYPZBYCAgEPZBYCZg8WBB8BBYQBQ09MT1I6IzAwMDAwMDtCQUNLR1JPVU5ELUNPTE9SOjtCQUNLR1JPVU5ELUlNQUdFOnVybChodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vVXNlci9kZWZhdWx0L1VwbG9hZC9zeXNGcmFtZUltZy94X3Rkc2N3X3p5X3pyXzAxLmdpZik7HwMFAjQ2FgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAQ9kFgJmD2QWAmYPZBYCZg9kFgICAQ9kFgJmDxYEHwEFIENPTE9SOiNEM0QzRDM7QkFDS0dST1VORC1DT0xPUjo7HwBoFgJmD2QWAgIBD2QWAmYPDxYCHwJlZGQCAw9kFgICAw8WBB4JaW5uZXJodG1sBfoGPHAgYWxpZ249ImNlbnRlciI+PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogeC1zbWFsbCI+Jm5ic3A7PGJyIC8+DQombmJzcDs8YSB0YXJnZXQ9Il9zZWxmIiBocmVmPSJodHRwOi8vd3d3LmxhbmRjaGluYS5jb20vIj48aW1nIGJvcmRlcj0iMCIgYWx0PSIiIHdpZHRoPSIyNjAiIGhlaWdodD0iNjEiIHNyYz0iL1VzZXIvZGVmYXVsdC9VcGxvYWQvZmNrL2ltYWdlL3Rkc2N3X2xvZ2UucG5nIiAvPjwvYT4mbmJzcDs8YnIgLz4NCiZuYnNwOzxzcGFuIHN0eWxlPSJjb2xvcjogI2ZmZmZmZiI+Q29weXJpZ2h0IDIwMDgtMjAxOCBEUkNuZXQuIEFsbCBSaWdodHMgUmVzZXJ2ZWQmbmJzcDsmbmJzcDsmbmJzcDsgPHNjcmlwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiPg0KdmFyIF9iZGhtUHJvdG9jb2wgPSAoKCJodHRwczoiID09IGRvY3VtZW50LmxvY2F0aW9uLnByb3RvY29sKSA/ICIgaHR0cHM6Ly8iIDogIiBodHRwOi8vIik7DQpkb2N1bWVudC53cml0ZSh1bmVzY2FwZSgiJTNDc2NyaXB0IHNyYz0nIiArIF9iZGhtUHJvdG9jb2wgKyAiaG0uYmFpZHUuY29tL2guanMlM0Y4Mzg1Mzg1OWM3MjQ3YzViMDNiNTI3ODk0NjIyZDNmYScgdHlwZT0ndGV4dC9qYXZhc2NyaXB0JyUzRSUzQy9zY3JpcHQlM0UiKSk7DQo8L3NjcmlwdD4mbmJzcDs8YnIgLz4NCueJiOadg+aJgOaciSZuYnNwOyDkuK3lm73lnJ/lnLDluILlnLrnvZEmbmJzcDsmbmJzcDvmioDmnK/mlK/mjIE65rWZ5rGf6Ie75ZaE56eR5oqA6IKh5Lu95pyJ6ZmQ5YWs5Y+4Jm5ic3A7PGJyIC8+DQrlpIfmoYjlj7c6IOS6rElDUOWkhzA5MDc0OTky5Y+3IOS6rOWFrOe9keWuieWkhzExMDEwMjAwMDY2NigyKSZuYnNwOzxiciAvPg0KPC9zcGFuPiZuYnNwOyZuYnNwOyZuYnNwOzxiciAvPg0KJm5ic3A7PC9zcGFuPjwvcD4fAQVkQkFDS0dST1VORC1JTUFHRTp1cmwoaHR0cDovL3d3dy5sYW5kY2hpbmEuY29tL1VzZXIvZGVmYXVsdC9VcGxvYWQvc3lzRnJhbWVJbWcveF90ZHNjdzIwMTNfeXdfMS5qcGcpO2RkfTwcLjY5rC+lDUaJ2XvMr/XPFt/wdhG5Z/K3ICI0Ar0="));
    nvps.add(new BasicNameValuePair("__EVENTVALIDATION",
        "/wEWAgLkubbfCQLN3cj/BBxA7dVX+0sfrVMuTl5f81pSmVubPowwYBoKMmnTGEta"));
    nvps.add(new BasicNameValuePair("hidComName", "default"));
    nvps.add(new BasicNameValuePair("TAB_QueryConditionItem",
        "e1098f89-81bb-4e36-bfb7-be69c34d8b4b"));

    nvps.add(new BasicNameValuePair("TAB_QuerySubmitConditionData",
        String.format(
            "8f464b85-2802-458a-8ee6-66ce6186d803:%s|e1098f89-81bb-4e36-bfb7-be69c34d8b4b:%s",
            select_district, dateArea)));
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
      LandTransferEntity entity = new LandTransferEntity();
      Document detailDocument = Jsoup.parse(detailHtml);

      Element element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r1_c2_ctrl");
      if (element != null) {
        entity.setMark(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r1_c4_ctrl");
      if (element != null) {
        entity.setNum(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r2_c2_ctrl");
      if (element != null) {
        entity.setLocation(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r11_c2");
      if (element != null) {
        entity.setAministrativeArea(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r3_c2");
      if (element != null) {
        entity.setUserChangePre(element.text().trim().replace("(", "（").replace(")", "）"));
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r3_c4_ctrl");
      if (element != null) {
        entity.setUserChangeNow(element.text().trim().replace("(", "（").replace(")", "）"));
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r4_c2");
      if (element != null) {
        entity.setArea(element.text().trim());
      }
      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r4_c4_ctrl");
      if (element != null) {
        entity.setUseful(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r5_c2_ctrl");
      if (element != null) {
        entity.setUseType(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r5_c4");
      if (element != null) {
        entity.setYearsOfUse(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r6_c2_ctrl");
      if (element != null) {
        entity.setSituation(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r6_c4_ctrl");
      if (element != null) {
        entity.setLevel(element.text().trim());
      }

      element = detailDocument
          .selectFirst(
              "#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r8_c2_ctrl");
      if (element != null) {
        entity.setMerchandiseType(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r8_c4_ctrl");
      if (element != null) {
        entity.setMerchandisePrice(element.text().trim());
      }

      element = detailDocument
          .selectFirst("#mainModuleContainer_1855_1856_ctl00_ctl00_p1_f1_r7_c2_ctrl");
      if (element != null) {
        entity.setMerchandiseTime(element.text().trim());
      }
      entity.setUrl(detailUrl);
      entity.setMerchandiseTimeClean(DateUtils.formatDate(entity.getMerchandiseTime()));
      landTransferService.insert(entity);
    }
    //endregion

  }

  @Override
  protected boolean isCraw(String detailUrl) {
    LandTransferEntity entity = new LandTransferEntity();
    entity.setUrl(detailUrl);
    return landTransferService.exists(entity);
  }


  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {

    String redisKey = "zsetLandTransfer";
    String listUrl = "http://www.landchina.com/DesktopDefault.aspx?tabid=349";
    init(redisKey, listUrl);
    crawSchedule();
  }
}

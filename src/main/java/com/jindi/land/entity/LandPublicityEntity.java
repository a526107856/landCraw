package com.jindi.land.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import lombok.Data;

@Data
public class LandPublicityEntity {

  private Long id;
  private String title;
  private String administrativeDistrict;
  @JSONField(name = "地块编号")
  private String landNum;
  @JSONField(name = "地块位置")
  private String landLocation;
  @JSONField(name = "土地用途")
  private String landUsefulness;
  @JSONField(name = "土地面积(公顷)")
  private String landArea;
  @JSONField(name = "项目名称")
  private String projectName;
  @JSONField(name = "受让单位")
  private String landUseRightPerson;
  @JSONField(name = "备注：")
  private String remark;
  @JSONField(alternateNames = {"成交价(万元)", "成交价（万元）", "成交价"})
  private String finalPrice;
  @JSONField(name = "出让年限")
  private String transferPeriod;
  private String landUserClean;
  private String publicAnnouncementPeriod;
  private String feedbackMethod;
  private String contactOrganize;
  private String organizeLocation;
  private String postalCode;
  private String contactNumber;
  private String contactPerson;
  private String electronicMail;
  private String publicationOrganize;
  private String publicationDate;
  private String chainLink;
  private String url;
  private String md5;
  private Date publicationEndDateClean;
  private Date publicationStartDateClean;
  private String html;
  private int index;
}

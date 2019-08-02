package com.jindi.land.entity;

import java.util.Date;
import lombok.Data;

@Data
public class LandResultEntity  {

  // 主键 自增
  private Long id;
  // 电子监管号
  private String electronicRegulatoryNumber;
  // 土地使用权人
  private String landUseRightPerson;
  //打内链清洗土地使用权人
  private String landUserClean;
  // 行政区
  private String district;
  // 项目名称
  private String projectName;
  // 项目位置
  private String projectLocation;
  // 面积(公顷)
  private String area;
  // 土地来源实际值
  private String landSourceValue;
  // 土地来源显示值
  private String landSourceView;
  // 土地用途
  private String landUseType;
  // 供地方式
  private String landSupplyMethod;
  // 土地使用年限
  private String landUsePeriod;
  // 行业分类
  private String category;
  // 土地级别
  private String landLevel;
  // 成交价格(万元)
  private String transactionPrice;
  // 约定容积率下限
  private String contractedVolumeRate;
  // 约定容积率上限
  private String contractedVolumeRateCeiling;
  // 约定交地时间
  private Date committedTime;
  // 约定开工时间
  private Date agreementStartTime;
  // 实际开工时间
  private Date actualStartTime;
  // 约定竣工时间
  private Date scheduledCompletion;
  // 实际竣工时间
  private Date actualCompletionTime;
  // 批准单位
  private String authority;
  // 合同签订日期
  private Date contractDate;
  // 获取数据url
  private String url;
  // 分期支付约定Json
  private String strInsPaytoJson;
  //内链清洗，带链接
  private String chainLink;
}

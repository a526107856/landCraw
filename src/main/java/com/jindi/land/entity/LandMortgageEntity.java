package com.jindi.land.entity;

import java.util.Date;
import lombok.Data;

@Data
public class LandMortgageEntity  {

  private Long id;
  private String landMark;
  private String landNum;
  private String landAministrativeArea;
  private String landLoc;
  private String landArea;
  private String otherItemApplicationNameNum;
  private String useRightNum;
  private String mortgagePerson;
  private String nature;
  private String mortgageApplicationName;
  private String mortgageToUser;
  private String userType;
  private String mortgageArea;
  private String evaluateAmount;
  private String mortgageAmount;
  private String startDate;
  private String endDate;
  private String detailUrl;
  private String mortgagePersonClean;
  private String mortgageApplicationNameClean;
  private Date startDateClean;
  private Date endDateClean;
}

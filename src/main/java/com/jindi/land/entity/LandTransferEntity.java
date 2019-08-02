package com.jindi.land.entity;

import java.util.Date;
import lombok.Data;

@Data
public class LandTransferEntity  {

  private Long id;
  private String mark;
  private String num;
  private String location;
  private String aministrativeArea;
  private String userChangePre;
  private String userChangeNow;
  private String userChangePreClean;
  private String userChangeNowClean;
  private String area;
  private String useful;
  private String useType;
  private String yearsOfUse;
  private String situation;
  private String level;
  private String merchandiseType;
  private String merchandisePrice;
  private String merchandiseTime;
  private String url;
  private Date merchandiseTimeClean;
}

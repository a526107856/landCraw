package com.jindi.land.entity;

import lombok.Data;

@Data
public class LandInstalmentPaymentEntity  {

  // 主键
  private Long id;
  // 电子监管号
  private String electronic_regulatory_number;
  // 分期支付约定支付期号
  private String instalment_payment_contract_payment_period_number;
  // 分期支付约定约定支付日期
  private String instalment_payment_convention_payment_date;
  // 约定支付金额(万元)
  private String instalment_payment_agreed_payment_amount;
  // 分期支付约定备注
  private String instalment_payment_agreement_notes;

}
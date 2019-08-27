package com.jindi.land.mapper;

import com.jindi.land.entity.LandResultEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LandResultMapper {

  @Insert(
      "insert ignore into `land_result_announcement`(`electronic_regulatory_number`, `land_use_right_person`, `landUser_clean`, `district` ,`project_name` ,`project_location` ,`area` ,`land_source_value` ,`land_source_view` ,`land_use_type`  ,`land_supply_method` ,`land_use_period`  ,`category` ,`land_level` ,`transaction_price` , `contracted_volume_rate` ,`contracted_volume_rate_ceiling` ,`committed_time` ,`agreement_start_time` ,`actual_start_time` ,`scheduled_completion` ,`actual_completion_time` ,`authority` ,`contract_date`,`url` ,`instalment_payment`,`chain_link`)"
          + "values (#{electronicRegulatoryNumber}, #{landUseRightPerson}, #{landUserClean}, #{district} ,#{projectName} ,#{projectLocation} ,#{area} ,#{landSourceValue} ,#{landSourceView},#{landUseType} ,#{landSupplyMethod} ,#{landUsePeriod} ,#{category} ,#{landLevel} ,#{transactionPrice} ,#{contractedVolumeRate} ,#{contractedVolumeRateCeiling} ,#{committedTime} ,#{agreementStartTime} ,#{actualStartTime} ,#{scheduledCompletion} ,#{actualCompletionTime} ,#{authority} ,#{contractDate}, #{url}, #{strInsPaytoJson}, #{chainLink});")
  @Options(useGeneratedKeys = true)
  int insert(LandResultEntity entity);

  @Select("select id from `land_result_announcement` where `url` = #{url} limit 1;")
  Long exist(LandResultEntity entity);

}

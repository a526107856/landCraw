package com.jindi.land.mapper;

import com.jindi.land.entity.LandTransferEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LandTransferMapper {

  @Insert(
      "insert ignore into `prism_cq`.`land_transfer`(`mark`, `num`, `location`, `aministrativeArea`, `user_change_pre`, `user_change_now`, `user_change_pre_clean`, `user_change_now_clean`, `area`, `useful`, `use_type`, `years_of_use`, `situation`, `level`, `merchandise_type`, `merchandise_price`, `merchandise_time`, `url`,`merchandise_time_clean`) "
          + "VALUES (#{mark}, #{num}, #{location}, #{aministrativeArea}, #{userChangePre}, #{userChangeNow}, #{userChangePreClean}, #{userChangeNowClean}, #{area}, #{useful}, #{useType}, #{yearsOfUse}, #{situation}, #{level}, #{merchandiseType}, #{merchandisePrice}, #{merchandiseTime},#{url},#{merchandiseTimeClean});")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(LandTransferEntity entity);

  @Select("select id from land_transfer where url=#{url} limit 1 ")
  Long exists(@Param("url") String url);

}

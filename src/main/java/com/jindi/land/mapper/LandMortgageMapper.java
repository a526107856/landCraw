package com.jindi.land.mapper;

import com.jindi.land.entity.LandMortgageEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LandMortgageMapper {

  @Insert(
      "insert ignore into `prism_cq`.`land_mortgage`(`landMark`, `landNum`, `landAministrativeArea`, `land_loc`, `land_area`, `otherItemApplicationNameNum`, `useRightNum`, `mortgagePerson`, `mortgagePersonClean`, `nature`, `mortgageApplicationName`, `mortgageApplicationNameClean`, `mortgageToUser`, `userType`, `mortgageArea`, `evaluateAmount`, `mortgageAmount`, `startDate`, `endDate`, `detailUrl`,`startDateClean`,`endDateClean`) VALUES"
          + "(#{landMark}, #{landNum}, #{landAministrativeArea}, #{landLoc}, #{landArea}, #{otherItemApplicationNameNum}, #{useRightNum}, #{mortgagePerson}, #{mortgagePersonClean}, #{nature}, #{mortgageApplicationName}, #{mortgageApplicationNameClean}, #{mortgageToUser}, #{userType}, #{mortgageArea}, #{evaluateAmount}, #{mortgageAmount}, #{startDate}, #{endDate}, #{detailUrl},#{startDateClean},#{endDateClean});")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(LandMortgageEntity entity);

  @Select("select id from land_mortgage where detailUrl=#{url} limit 1")
  Long exists(@Param("url") String url);


}

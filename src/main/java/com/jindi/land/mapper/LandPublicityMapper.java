package com.jindi.land.mapper;

import com.jindi.land.entity.LandPublicityEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LandPublicityMapper {

  @Insert(
      "insert into `prism_cq`.`land_publicity_20190611`(`title`,`administrative_district`, `land_num`, `land_location`, `land_usefulness`, `landArea`, `projectName`, `landUseRightPerson`, `landUserClean`,`remark`, `public_announcement_period`, `feedback_method`, `contact_organize`, `organize_location`, `postal_code`, `contact_number`, `contact_person`, `electronic_mail`, `publication_organize`, `publication_date`, `chainLink`, `url`,`final_price`,`transfer_period`,`publication_end_date_clean`,`publication_start_date_clean`,`index`,`html`) "
          + "values (#{title},#{administrativeDistrict}, #{landNum}, #{landLocation}, #{landUsefulness}, #{landArea}, #{projectName}, #{landUseRightPerson}, #{landUserClean},#{remark}, #{publicAnnouncementPeriod}, #{feedbackMethod}, #{contactOrganize}, #{organizeLocation}, #{postalCode}, #{contactNumber}, #{contactPerson}, #{electronicMail}, #{publicationOrganize}, #{publicationDate}, #{chainLink}, #{url},#{finalPrice},#{transferPeriod},#{publicationEndDateClean},#{publicationStartDateClean},#{index},#{html})")
  @Options(useGeneratedKeys = true)
  int insert(LandPublicityEntity entity);

  @Select("select id from `land_publicity_20190611` where `url` = #{url} and `index` = #{index};")
  Long exist(LandPublicityEntity entity);


}

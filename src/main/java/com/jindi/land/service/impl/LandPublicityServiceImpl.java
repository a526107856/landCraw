package com.jindi.land.service.impl;

import com.jindi.land.entity.LandPublicityEntity;
import com.jindi.land.mapper.LandPublicityMapper;
import com.jindi.land.service.ILandPublicityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LandPublicityServiceImpl implements ILandPublicityService {

  @Autowired
  private LandPublicityMapper infoMapper;

  public void insert(LandPublicityEntity entity) {
    try {
      if (!exists(entity)) {
        infoMapper.insert(entity);
        if (entity.getId() != null) {
          log.info("    ========insert 土地公示 success , id = {}", entity.getId());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean exists(LandPublicityEntity entity) {
    Long id = infoMapper.exist(entity);
    if (id != null) {
      log.info("=======already exists 土地公示 id = {}", id);
      return true;
    }
    return false;
  }

  public void m1() {

  }

}

package com.jindi.land.service.impl;

import com.jindi.land.entity.LandMortgageEntity;
import com.jindi.land.mapper.LandMortgageMapper;
import com.jindi.land.service.ILandMortgageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LandMortgageServiceImpl implements ILandMortgageService {

  @Autowired
  private LandMortgageMapper infoMapper;


  @Override
  public void insert(LandMortgageEntity entity) {
    try {
      if (!exists(entity)) {
        infoMapper.insert(entity);
        if (entity.getId() != null) {
          log.info("    ========insert 土地抵押 success , id = {}", entity.getId());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean exists(LandMortgageEntity entity) {
    Long id = infoMapper.exists(entity.getDetailUrl());
    if (id != null) {
      log.info("=======already exists 土地抵押 id = {}", id);
      return true;
    }
    return false;
  }


}

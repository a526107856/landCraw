package com.jindi.land.service.impl;

import com.jindi.land.entity.LandTransferEntity;
import com.jindi.land.mapper.LandTransferMapper;
import com.jindi.land.service.ILandTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LandTransferServiceImpl implements ILandTransferService {

  @Autowired
  private LandTransferMapper mapper;

  @Override
  public void insert(LandTransferEntity entity) {
    try {
      if (!exists(entity)) {
        mapper.insert(entity);
        if (entity.getId() != null) {
          log.info("    ========insert 土地转让 success , id = {}", entity.getId());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean exists(LandTransferEntity entity) {
    Long id = mapper.exists(entity.getUrl());
    if (id != null) {
      log.info("=======already exists 土地转让 id = {}", id);
      return true;
    }
    return false;
  }
}

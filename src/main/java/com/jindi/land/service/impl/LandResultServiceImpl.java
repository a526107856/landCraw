package com.jindi.land.service.impl;

import com.jindi.land.entity.LandResultEntity;
import com.jindi.land.mapper.LandResultMapper;
import com.jindi.land.service.ILandResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LandResultServiceImpl implements ILandResultService {

  @Autowired
  private LandResultMapper infoMapper;

  @Override
  public void insert(LandResultEntity entity) {
    infoMapper.insert(entity);
    if (entity.getId() != null) {
      log.info("    ========insert 结果公告 success , id = {}", entity.getId());
    }
  }

  @Override
  public boolean exists(LandResultEntity entity) {
    Long id = infoMapper.exist(entity);
    if (id != null) {
      log.info("=======already exists 结果公告 id = {}", id);
      return true;
    }
    return false;
  }

}

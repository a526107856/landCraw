package com.jindi.land.service;

import com.jindi.land.entity.LandResultEntity;

public interface ILandResultService {

  void insert(LandResultEntity entity);

  boolean exists(LandResultEntity entity);

}

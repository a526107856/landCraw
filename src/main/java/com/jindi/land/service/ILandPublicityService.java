package com.jindi.land.service;

import com.jindi.land.entity.LandPublicityEntity;

public interface ILandPublicityService {

  void insert(LandPublicityEntity entity);

  boolean exists(LandPublicityEntity entity);
}

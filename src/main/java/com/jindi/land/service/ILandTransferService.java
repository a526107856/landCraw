package com.jindi.land.service;

import com.jindi.land.entity.LandTransferEntity;

public interface ILandTransferService {

  void insert(LandTransferEntity entity);

  boolean exists(LandTransferEntity entity);

}


package com.jindi.land.service;

import com.jindi.land.entity.LandMortgageEntity;
import org.springframework.stereotype.Service;

public interface ILandMortgageService {

  void insert(LandMortgageEntity entity);

  boolean exists(LandMortgageEntity entity);
}

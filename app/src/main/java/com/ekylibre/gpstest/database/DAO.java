package com.ekylibre.gpstest.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.ekylibre.gpstest.database.models.Point;


@Dao
public interface DAO {

    @Insert void insert(Point... points);

}
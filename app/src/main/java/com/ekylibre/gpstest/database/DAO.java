package com.ekylibre.gpstest.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ekylibre.gpstest.database.models.Point;


@Dao
public interface DAO {

    @Insert void insert(Point... points);

    @Query("SELECT * FROM points ORDER BY id DESC LIMIT 1")
    LiveData<Point> getLastPoint();

}
package com.ekylibre.gpstest.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "points")
public class Point {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public long time;
    public double lat;
    public double lon;
    public float speed;
    public int accuracy;


    public Point(long time, double lat, double lon, float speed, int accuracy) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
        this.accuracy = accuracy;
    }
}
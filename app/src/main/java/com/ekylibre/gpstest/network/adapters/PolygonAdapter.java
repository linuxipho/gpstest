package com.ekylibre.gpstest.network.adapters;

import com.ekylibre.gpstest.network.Polygon;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public class PolygonAdapter {

    @ToJson
    String toJson(Polygon polygon) {

        return null;
    }

    @FromJson
    Polygon fromJson(String points) {
        StringBuilder sb = new StringBuilder(points);


        return null;

    }

}

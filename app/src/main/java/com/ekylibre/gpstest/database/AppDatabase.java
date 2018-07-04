package com.ekylibre.gpstest.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ekylibre.gpstest.database.models.Point;


@Database(entities = {Point.class}, exportSchema = false, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DAO dao();
    private static AppDatabase database;

    public static synchronized AppDatabase getInstance(Context context) {
        if (database == null)
            database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "db")
                    .build();
        return database;
    }
}
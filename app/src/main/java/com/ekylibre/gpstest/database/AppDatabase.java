package com.ekylibre.gpstest.database;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ekylibre.gpstest.database.models.Point;

import java.util.Date;
import java.util.concurrent.Executors;


@Database(entities = {Point.class}, exportSchema = false, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DAO dao();

    private static AppDatabase database;

    public static synchronized AppDatabase getInstance(Context context) {
        if (database == null)
            database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(() ->
                                    getInstance(context).dao().insert(new Point(new Date().getTime(), 0, 0, 0, 0)));
                        }
                    }).build();
        return database;
    }
}
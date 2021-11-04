package com.example.todoc.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import com.example.todoc.database.dao.TaskDao;
import com.example.todoc.entity.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class CleanUPTodocDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    private static final int NUMBER_OF_THREADS = 4;

   public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile CleanUPTodocDatabase INSTANCE;

    public static CleanUPTodocDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (CleanUPTodocDatabase.class) {
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),CleanUPTodocDatabase.class, "cleanup_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}

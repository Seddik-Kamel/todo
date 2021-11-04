package com.example.todoc.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.todoc.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Delete()
    void delete(Task task);

    @Query("SELECT* FROM task_table")
    LiveData<List<Task>> getTasks();

}

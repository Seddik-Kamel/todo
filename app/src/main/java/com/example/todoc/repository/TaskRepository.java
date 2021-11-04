package com.example.todoc.repository;


import android.app.Application;

import androidx.lifecycle.LiveData;


import com.example.todoc.database.CleanUPTodocDatabase;
import com.example.todoc.database.dao.TaskDao;
import com.example.todoc.entity.Task;

import java.util.List;

public class TaskRepository {

    private final TaskDao taskDao;

    public TaskRepository(Application application) {
        CleanUPTodocDatabase cleanUPTodocDatabase = CleanUPTodocDatabase.getDatabase(application);
        taskDao = cleanUPTodocDatabase.taskDao();
    }

    public LiveData<List<Task>> getAllTask() {
        return taskDao.getTasks();
    }

    public void insert(Task task){
        CleanUPTodocDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
        });
    }

    public void delete(Task task){
        CleanUPTodocDatabase.databaseWriteExecutor.execute(() -> {
           taskDao.delete(task);
        });
    }
}

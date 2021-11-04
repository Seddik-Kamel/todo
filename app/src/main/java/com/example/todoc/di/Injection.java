package com.example.todoc.di;

import android.app.Application;

import com.example.todoc.repository.TaskRepository;


public class Injection {

    public static TaskRepository provideTaskRepository(Application application) {
        return new TaskRepository(application);
    }
}

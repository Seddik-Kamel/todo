package com.example.todoc.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todoc.di.Injection;
import com.example.todoc.entity.Task;
import com.example.todoc.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;

    public TaskViewModel(Application application) {
        super(application);
        this.taskRepository = Injection.provideTaskRepository(application);
    }

    public LiveData<List<Task>> getAllTasks() {
        return taskRepository.getAllTask();
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }
}

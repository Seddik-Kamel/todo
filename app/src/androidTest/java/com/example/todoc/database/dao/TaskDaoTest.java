package com.example.todoc.database.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.todoc.LiveDataTestUtil;
import com.example.todoc.database.CleanUPTodocDatabase;
import com.example.todoc.entity.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class TaskDaoTest {

    private CleanUPTodocDatabase cleanUPTodocDatabase;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private final Task TASK_DEMO = new Task(2, "kamel", new Date().getTime());

    @Before
    public void initDb() {
        this.cleanUPTodocDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),
                CleanUPTodocDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        cleanUPTodocDatabase.close();
    }

    @Test
    public void should_insert_task() throws InterruptedException {
        cleanUPTodocDatabase.taskDao().insert(TASK_DEMO);
        List<Task> taskList = LiveDataTestUtil.getValue(this.cleanUPTodocDatabase.taskDao().getTasks());
        Task task = taskList.get(0);
        assertEquals(TASK_DEMO.getName(), task.getName());
    }

    @Test
    public void should_delete_task() throws InterruptedException {
        cleanUPTodocDatabase.taskDao().insert(TASK_DEMO);
        List<Task> taskList = LiveDataTestUtil.getValue(this.cleanUPTodocDatabase.taskDao().getTasks());
        Task task = taskList.get(0);
        cleanUPTodocDatabase.taskDao().delete(task);
        taskList = LiveDataTestUtil.getValue(this.cleanUPTodocDatabase.taskDao().getTasks());
        assertTrue(taskList.isEmpty());
    }
}
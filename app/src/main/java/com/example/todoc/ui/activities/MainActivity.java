package com.example.todoc.ui.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoc.R;
import com.example.todoc.entity.Project;
import com.example.todoc.entity.Task;
import com.example.todoc.enums.SortMethod;
import com.example.todoc.ui.recyclerView.TasksAdapter;
import com.example.todoc.ui.viewmodels.TaskViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TasksAdapter.DeleteTaskListener {

    private final Project[] allProjects = Project.getAllProjects();

    private TasksAdapter adapter;

    private final List<Task> taskList = new ArrayList<>();

    /**
     * The sort method to be used to display tasks
     */
    @NonNull
    private SortMethod sortMethod = SortMethod.NONE;

    @Nullable
    public AlertDialog dialog = null;

    @Nullable
    private EditText dialogEditText = null;

    @Nullable
    private Spinner dialogSpinner = null;

    private RecyclerView listTasks;
    private TextView lblNoTasks;

    private TaskViewModel taskViewModel;
    private boolean isTest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        configureViewModel();

        listTasks = findViewById(R.id.list_tasks);
        lblNoTasks = findViewById(R.id.lbl_no_task);
        isTest = getIntent().getBooleanExtra("isTest", false);

        if (isTest) {
            updateUi(taskList);
            configureRecyclerView(taskList);
        } else {
            taskViewModel.getAllTasks().observe(this, tasks -> {
                updateUi(tasks);
                configureRecyclerView(tasks);
            });
        }

        findViewById(R.id.fab_add_task).setOnClickListener(view -> showAddTaskDialog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical) {
            sortMethod = SortMethod.ALPHABETICAL;
        } else if (id == R.id.filter_alphabetical_inverted) {
            sortMethod = SortMethod.ALPHABETICAL_INVERTED;
        } else if (id == R.id.filter_oldest_first) {
            sortMethod = SortMethod.OLD_FIRST;
        } else if (id == R.id.filter_recent_first) {
            sortMethod = SortMethod.RECENT_FIRST;
        }

        if (isTest)
            updateTasks(taskList);
        else
            taskViewModel.getAllTasks().observe(this, this::updateTasks);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteTask(Task task) {
        if (isTest) {
            taskList.remove(task);
            updateUi(taskList);
            adapter.updateTasks(taskList);
        } else {
            taskViewModel.delete(task);
            this.taskViewModel.getAllTasks().observe(this, tasks -> {
                updateUi(tasks);
                adapter.updateTasks(tasks);
            });
        }
    }

    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {
                
                Task task = new Task(
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );

                addTask(task, isTest);
                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else {
                dialogInterface.dismiss();
            }
        }
        // If dialog is already closed
        else {
            dialogInterface.dismiss();
        }
    }

    /**
     * Shows the Dialog for adding a Task
     */
    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }

    private void addTask(@NonNull Task task, Boolean isTest) {
        if (isTest) {
            taskList.add(task);
            updateUi(taskList);
            adapter.updateTasks(taskList);
        } else {
            taskViewModel.insert(task);
            taskViewModel.getAllTasks().observe(this, tasks -> {
                updateUi(tasks);
                adapter.updateTasks(tasks);
            });
        }
    }


    private void updateTasks(List<Task> tasks) {
        switch (sortMethod) {
            case ALPHABETICAL:
                Collections.sort(tasks, new Task.TaskAZComparator());
                break;
            case ALPHABETICAL_INVERTED:
                Collections.sort(tasks, new Task.TaskZAComparator());
                break;
            case RECENT_FIRST:
                Collections.sort(tasks, new Task.TaskRecentComparator());
                break;
            case OLD_FIRST:
                Collections.sort(tasks, new Task.TaskOldComparator());
                break;

        }
        adapter.updateTasks(tasks);
    }


    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogEditText = null;
                dialogSpinner = null;
                dialog = null;
            }
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(dialogInterface -> {

            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    onPositiveButtonClick(dialog);
                }
            });
        });

        return dialog;
    }

    private void populateDialogSpinner() {
        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
    }

    private void updateUi(List<Task> tasks) {
        if (tasks.size() == 0) {
            lblNoTasks.setVisibility(View.VISIBLE);
            listTasks.setVisibility(View.GONE);
        } else {
            lblNoTasks.setVisibility(View.GONE);
            listTasks.setVisibility(View.VISIBLE);
        }
    }

    private void configureViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void configureRecyclerView(List<Task> tasks) {
        adapter = new TasksAdapter(tasks, this);
        listTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if ((adapter != null))
            listTasks.setAdapter(adapter);
    }
}
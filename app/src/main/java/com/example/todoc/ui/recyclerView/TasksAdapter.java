package com.example.todoc.ui.recyclerView;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todoc.R;
import com.example.todoc.entity.Project;
import com.example.todoc.entity.Task;

import java.util.List;

/**
 * <p>Adapter which handles the list of tasks to display in the dedicated RecyclerView.</p>
 *
 * @author Gaëtan HERFRAY
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    @NonNull
    private List<Task> tasks;

    @NonNull
    private final DeleteTaskListener deleteTaskListener;


    public TasksAdapter(@NonNull final List<Task> tasks, @NonNull final DeleteTaskListener deleteTaskListener) {
        this.tasks = tasks;
        this.deleteTaskListener = deleteTaskListener;
    }

    /**
     * Updates the list of tasks the adapter deals with.
     *
     * @param tasks the list of tasks the adapter deals with to set
     */
    public void updateTasks(@NonNull final List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        return new TaskViewHolder(view, deleteTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Listener for deleting tasks
     */
    public interface DeleteTaskListener {
        /**
         * Called when a task needs to be deleted.
         *
         * @param task the task that needs to be deleted
         */
        void onDeleteTask(Task task);
    }


    class TaskViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView imgProject;
        private final TextView lblTaskName;
        private final TextView lblProjectName;
        private final AppCompatImageView imgDelete;
        private final DeleteTaskListener deleteTaskListener;

        TaskViewHolder(@NonNull View itemView, @NonNull DeleteTaskListener deleteTaskListener) {
            super(itemView);

            this.deleteTaskListener = deleteTaskListener;

            imgProject = itemView.findViewById(R.id.img_project);
            lblTaskName = itemView.findViewById(R.id.lbl_task_name);
            lblProjectName = itemView.findViewById(R.id.lbl_project_name);
            imgDelete = itemView.findViewById(R.id.img_delete);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Object tag = view.getTag();
                    if (tag instanceof Task) {
                        TaskViewHolder.this.deleteTaskListener.onDeleteTask((Task) tag);
                    }
                }
            });
        }

        void bind(Task task) {
            lblTaskName.setText(task.getName());
            imgDelete.setTag(task);

            final Project taskProject = task.getProject();
            if (taskProject != null) {
                imgProject.setSupportImageTintList(ColorStateList.valueOf(taskProject.getColor()));
                lblProjectName.setText(taskProject.getName());
            } else {
                imgProject.setVisibility(View.INVISIBLE);
                lblProjectName.setText("");
            }

        }
    }
}

package com.example.focusfuel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;
    private Runnable onTaskChanged;

    public TaskAdapter(Context context, List<Task> tasks, Runnable onTaskChanged) {
        this.context = context;
        this.taskList = tasks;
        this.onTaskChanged = onTaskChanged;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TaskViewHolder(View view) {
            super(view);
            textView = view.findViewById(android.R.id.text1);
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textView.setText(task.toString());

        holder.itemView.setOnClickListener(v -> showOptionsDialog(task, position));
    }

    private void showOptionsDialog(Task task, int position) {
        String[] options = {
                task.isCompleted() ? "Mark as Incomplete" : "Mark as Completed",
                "Delete Task"
        };

        new AlertDialog.Builder(context)
                .setTitle(task.getName())
                .setMessage(task.getDescription())
                .setPositiveButton(task.isCompleted() ? "Mark as Incomplete" : "Mark as Completed", (dialog, which) -> {
                    task.setCompleted(!task.isCompleted());
                    notifyItemChanged(position);
                    onTaskChanged.run();
                })
                .setNeutralButton("Delete Task", (dialog, which) -> {
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    onTaskChanged.run();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void addTask(Task task) {
        taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }
}


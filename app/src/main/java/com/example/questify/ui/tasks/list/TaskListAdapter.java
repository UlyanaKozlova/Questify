package com.example.questify.ui.tasks.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    public interface Listener {
        void onTaskClicked(Task task);
        void onTaskChecked(Task task, boolean isChecked);
    }

    private final Listener listener;
    private List<Task> tasks = new ArrayList<>();

    public TaskListAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Task> newTasks) {
        tasks = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView deadline;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTaskTitle);
            deadline = itemView.findViewById(R.id.textTaskDeadline);
            checkBox = itemView.findViewById(R.id.checkboxDone);
        }

        void bind(Task task) {
            title.setText(task.getTaskName());

            deadline.setText( new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(task.getDeadline())));

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isDone());
            checkBox.setOnCheckedChangeListener((button, isChecked) ->
                    listener.onTaskChecked(task, isChecked)
            );

            itemView.setOnClickListener(v -> listener.onTaskClicked(task));
        }
    }
}

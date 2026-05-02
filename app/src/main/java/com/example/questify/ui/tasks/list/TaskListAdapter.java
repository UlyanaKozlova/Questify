package com.example.questify.ui.tasks.list;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Priority;

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
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new TaskDiffCallback(tasks, newTasks));
        tasks = new ArrayList<>(newTasks);
        result.dispatchUpdatesTo(this);
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
        Task task = tasks.get(position);

        holder.title.setText(task.getTaskName());
        long now = System.currentTimeMillis();
        boolean overdue = !task.isDone() && task.getDeadline() < now;
        String dateStr = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(new Date(task.getDeadline()));
        holder.deadline.setText(dateStr);
        holder.deadline.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                overdue ? R.color.task_deadline_overdue : R.color.task_deadline_normal));

        if (holder.priorityAccent != null) {
            int color = getPriorityColor(task.getPriority());
            holder.priorityAccent.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), color));
        }

        if (task.isDone()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(0.5f);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(1f);
        }

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isDone());
        holder.checkBox.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                holder.itemView.animate()
                        .scaleX(0.97f).scaleY(0.97f).setDuration(80)
                        .withEndAction(() -> holder.itemView.animate()
                                .scaleX(1f).scaleY(1f).setDuration(80).start())
                        .start();
            }
            listener.onTaskChecked(task, isChecked);
        });

        holder.itemView.setOnClickListener(v -> listener.onTaskClicked(task));
    }

    private int getPriorityColor(Priority priority) {
        if (priority == null) return R.color.priority_medium;
        switch (priority) {
            case VERY_HIGH:
                return R.color.priority_very_high;
            case HIGH:
                return R.color.priority_high;
            case LOW:
                return R.color.priority_low;
            case VERY_LOW:
                return R.color.priority_very_low;
            default:
                return R.color.priority_medium;
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView deadline;
        final CheckBox checkBox;
        final View priorityAccent;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTaskTitle);
            deadline = itemView.findViewById(R.id.textTaskDeadline);
            checkBox = itemView.findViewById(R.id.checkboxDone);
            priorityAccent = itemView.findViewById(R.id.viewPriorityAccent);
        }
    }

    private static class TaskDiffCallback extends DiffUtil.Callback {
        private final List<Task> oldList;
        private final List<Task> newList;

        TaskDiffCallback(List<Task> oldList, List<Task> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).getGlobalId().equals(newList.get(newPos).getGlobalId());
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            Task a = oldList.get(oldPos);
            Task b = newList.get(newPos);
            return a.isDone() == b.isDone()
                    && a.getDeadline() == b.getDeadline()
                    && a.getUpdatedAt() == b.getUpdatedAt()
                    && java.util.Objects.equals(a.getTaskName(), b.getTaskName())
                    && a.getPriority() == b.getPriority()
                    && a.getDifficulty() == b.getDifficulty();
        }
    }
}

package com.example.questify.ui.tasks.edit;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Subtask;

import java.util.ArrayList;
import java.util.List;

public class SubtaskListAdapter extends RecyclerView.Adapter<SubtaskListAdapter.ViewHolder> {

    public interface Callbacks {
        void onToggle(Subtask subtask, boolean isDone);

        void onDelete(Subtask subtask);

        void onEditRequest(Subtask subtask);
    }

    private List<Subtask> subtasks = new ArrayList<>();
    private final Callbacks callbacks;

    public SubtaskListAdapter(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void submitList(List<Subtask> newList) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SubtaskDiffCallback(subtasks, newList));
        subtasks = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subtask, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subtask subtask = subtasks.get(position);

        holder.title.setText(subtask.getSubtaskName());
        applyDoneStyle(holder, subtask.isDone());

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(subtask.isDone());
        holder.checkbox.setOnCheckedChangeListener((btn, isChecked) -> {
            applyDoneStyle(holder, isChecked);
            callbacks.onToggle(subtask, isChecked);
        });

        holder.itemView.setOnClickListener(v -> holder.checkbox.toggle());

        holder.itemView.setOnLongClickListener(v -> {
            callbacks.onEditRequest(subtask);
            return true;
        });

        holder.deleteBtn.setOnClickListener(v -> callbacks.onDelete(subtask));
    }

    private void applyDoneStyle(ViewHolder holder, boolean isDone) {
        if (isDone) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(0.5f);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkbox;
        final TextView title;
        final ImageButton deleteBtn;

        ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkboxSubtaskDone);
            title = itemView.findViewById(R.id.textSubtaskTitle);
            deleteBtn = itemView.findViewById(R.id.buttonDeleteSubtask);
        }
    }

    private static class SubtaskDiffCallback extends DiffUtil.Callback {
        private final List<Subtask> oldList;
        private final List<Subtask> newList;

        SubtaskDiffCallback(List<Subtask> oldList, List<Subtask> newList) {
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
            Subtask o = oldList.get(oldPos);
            Subtask n = newList.get(newPos);
            return o.isDone() == n.isDone() && o.getSubtaskName().equals(n.getSubtaskName());
        }
    }
}

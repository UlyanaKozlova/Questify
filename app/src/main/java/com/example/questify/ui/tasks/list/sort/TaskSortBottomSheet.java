package com.example.questify.ui.tasks.list.sort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.questify.R;
import com.example.questify.domain.usecase.plans.tasks.sort.SortOrder;
import com.example.questify.domain.usecase.plans.tasks.sort.SortType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class TaskSortBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "TaskSortBottomSheet";
    public static final String REQUEST_KEY = "sort_request";
    public static final String ARG_TYPE = "sort_type";
    public static final String ARG_ORDER = "sort_order";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_sort, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RadioGroup radioGroup = view.findViewById(R.id.radioGroupSort);
        MaterialButton buttonApply = view.findViewById(R.id.buttonApplySort);

        buttonApply.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            SortType type = SortType.DEADLINE;
            SortOrder order = SortOrder.ASC;

            if (checkedId == R.id.radioDeadlineAsc) {
                order = SortOrder.ASC;
            } else if (checkedId == R.id.radioDeadlineDesc) {
                type = SortType.DEADLINE;
                order = SortOrder.DESC;
            } else if (checkedId == R.id.radioPriorityAsc) {
                type = SortType.PRIORITY;
                order = SortOrder.ASC;
            } else if (checkedId == R.id.radioPriorityDesc) {
                type = SortType.PRIORITY;
                order = SortOrder.DESC;
            } else if (checkedId == R.id.radioDifficultyAsc) {
                type = SortType.DIFFICULTY;
                order = SortOrder.ASC;
            } else if (checkedId == R.id.radioDifficultyDesc) {
                type = SortType.DIFFICULTY; order = SortOrder.DESC;
            }

            Bundle result = new Bundle();
            result.putSerializable(ARG_TYPE, type);
            result.putSerializable(ARG_ORDER, order);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });
    }
}

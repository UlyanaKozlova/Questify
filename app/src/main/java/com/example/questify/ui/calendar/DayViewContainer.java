package com.example.questify.ui.calendar;

import android.view.View;
import android.widget.TextView;

import com.example.questify.R;
import com.kizitonwose.calendar.view.ViewContainer;

public class DayViewContainer extends ViewContainer {

    public TextView textView;
    public View dotDeadline;
    public View view;

    public DayViewContainer(View view) {
        super(view);
        this.view = view;
        textView = view.findViewById(R.id.calendarDayText);
        dotDeadline = view.findViewById(R.id.dotDeadline);
    }
}
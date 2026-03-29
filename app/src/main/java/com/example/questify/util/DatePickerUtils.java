package com.example.questify.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

public class DatePickerUtils {

    public static void attach(EditText field, Context context) {

        field.setFocusable(false);
        field.setClickable(true);

        field.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            Long existing = DateUtils.parseToMillis(field.getText().toString());
            if (existing != null) {
                calendar.setTimeInMillis(existing);
            }

            new DatePickerDialog(
                    context,
                    (view, year, month, day) -> {
                        calendar.set(year, month, day);
                        field.setText(DateUtils.format(calendar.getTimeInMillis()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }
}
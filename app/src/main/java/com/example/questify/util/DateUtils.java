package com.example.questify.util;

import java.text.SimpleDateFormat;

import java.util.Locale;
import java.util.Objects;

public class DateUtils {

    private static final String FORMAT = "dd.MM.yyyy";

    public static Long parseToMillis(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return Objects.requireNonNull(new SimpleDateFormat(FORMAT, Locale.getDefault()).parse(text)).getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(Long millis) {
        return millis == null
                ? ""
                : new SimpleDateFormat(FORMAT, Locale.getDefault()).format(millis);
    }
}
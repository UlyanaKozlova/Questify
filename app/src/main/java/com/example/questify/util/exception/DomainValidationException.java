package com.example.questify.util.exception;

import androidx.annotation.StringRes;

public class DomainValidationException extends RuntimeException {

    @StringRes
    public final int resId;

    public DomainValidationException(@StringRes int resId) {
        super("Validation error: " + resId);
        this.resId = resId;
    }
}

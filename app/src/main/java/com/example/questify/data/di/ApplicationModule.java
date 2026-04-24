package com.example.questify.data.di;

import android.content.Context;

import com.example.questify.UserSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ApplicationModule {

    @Provides
    @Singleton
    public UserSession provideUserSession(@ApplicationContext Context context) {
        return new UserSession(context);
    }
}
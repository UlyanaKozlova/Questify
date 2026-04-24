package com.example.questify.data.di;

import android.content.Context;

import com.example.questify.sync.AuthenticationManager;
import com.example.questify.sync.SyncManager;
import com.example.questify.UserSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AuthenticationModule {

    @Provides
    @Singleton
    public AuthenticationManager provideAuthenticationManager(
            UserSession userSession,
            @ApplicationContext Context context) {
        return new AuthenticationManager(userSession, context);
    }

    @Provides
    @Singleton
    public SyncManager provideSyncManager(
            @ApplicationContext Context context,
            com.example.questify.data.local.dao.TaskDao taskDao,
            com.example.questify.data.local.dao.ProjectDao projectDao,
            com.example.questify.data.local.dao.SubtaskDao subtaskDao,
            com.example.questify.data.local.dao.UserDao userDao,
            com.example.questify.data.local.dao.PetDao petDao,
            com.example.questify.data.local.dao.ClothingDao clothingDao,
            com.example.questify.data.local.dao.PetClothingRefDao petClothingRefDao) {
        return new SyncManager(
                context,
                taskDao,
                projectDao,
                subtaskDao,
                userDao,
                petDao,
                clothingDao,
                petClothingRefDao
        );
    }
}
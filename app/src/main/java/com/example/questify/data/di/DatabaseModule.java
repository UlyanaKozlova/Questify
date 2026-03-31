package com.example.questify.data.di;

import android.content.Context;

import com.example.questify.data.local.AppDatabase;
import com.example.questify.data.local.dao.ClothingDao;
import com.example.questify.data.local.dao.PetClothingRefDao;
import com.example.questify.data.local.dao.PetDao;
import com.example.questify.data.local.dao.ProjectDao;
import com.example.questify.data.local.dao.SubtaskDao;
import com.example.questify.data.local.dao.TaskDao;
import com.example.questify.data.local.dao.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return AppDatabase.getInstance(context);
    }

    @Provides
    public TaskDao provideTaskDao(AppDatabase db) {
        return db.taskDao();
    }

    @Provides
    public ProjectDao provideProjectDao(AppDatabase db) {
        return db.projectDao();
    }

    @Provides
    public SubtaskDao provideSubtaskDao(AppDatabase db) {
        return db.subtaskDao();
    }

    @Provides
    public UserDao provideUserDao(AppDatabase db) {
        return db.userDao();
    }

    @Provides
    public PetDao providePetDao(AppDatabase db) {
        return db.petDao();
    }

    @Provides
    public ClothingDao provideClothingDao(AppDatabase db) {
        return db.clothingDao();
    }

    @Provides
    public PetClothingRefDao providePetClothingRefDao(AppDatabase db) {
        return db.petClothingRefDao();
    }
}


package com.example.questify.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.questify.data.local.dao.ClothingDao;
import com.example.questify.data.local.dao.PetClothingRefDao;
import com.example.questify.data.local.dao.PetDao;
import com.example.questify.data.local.dao.ProjectDao;
import com.example.questify.data.local.dao.TaskDao;
import com.example.questify.data.local.dao.SubtaskDao;
import com.example.questify.data.local.dao.UserDao;
import com.example.questify.data.local.entity.ClothingEntity;
import com.example.questify.data.local.entity.PetClothingRefEntity;
import com.example.questify.data.local.entity.PetEntity;
import com.example.questify.data.local.entity.ProjectEntity;
import com.example.questify.data.local.entity.TaskEntity;
import com.example.questify.data.local.entity.SubtaskEntity;
import com.example.questify.data.local.entity.UserEntity;

@Database(
        entities = {
                ClothingEntity.class,
                PetEntity.class,
                PetClothingRefEntity.class,
                ProjectEntity.class,
                TaskEntity.class,
                SubtaskEntity.class,
                UserEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ClothingDao clothingDao();

    public abstract PetDao petDao();

    public abstract PetClothingRefDao petClothingCrossRefDao();

    public abstract ProjectDao projectDao();

    public abstract TaskDao taskDao();

    public abstract SubtaskDao subtaskDao();

    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "questify_database"
                            )// todo миграции
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

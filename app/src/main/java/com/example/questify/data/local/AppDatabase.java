package com.example.questify.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

import org.jspecify.annotations.NonNull;

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
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ClothingDao clothingDao();

    public abstract PetDao petDao();

    public abstract PetClothingRefDao petClothingRefDao();

    public abstract ProjectDao projectDao();

    public abstract TaskDao taskDao();

    public abstract SubtaskDao subtaskDao();

    public abstract UserDao userDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE clothing ADD COLUMN imageResId INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE subtasks ADD COLUMN userGlobalId TEXT");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "questify_database"
                            )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

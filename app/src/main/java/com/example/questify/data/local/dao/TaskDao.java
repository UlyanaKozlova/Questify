package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.questify.data.local.entity.TaskEntity;
import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(TaskEntity entity);

    @Update
    void update(TaskEntity entity);

    @Query("SELECT * FROM tasks WHERE isDeleted = 0")
    List<TaskEntity> getTasksForUser();

    @Query("SELECT * FROM tasks WHERE projectGlobalId = :projectGlobalId AND isDeleted = 0")
    LiveData<List<TaskEntity>> getTasksForProject(String projectGlobalId);

    @Query("SELECT * FROM tasks WHERE globalId = :globalId LIMIT 1")
    TaskEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM tasks WHERE needsSync = 1")
    List<TaskEntity> getNeedingSync();
}

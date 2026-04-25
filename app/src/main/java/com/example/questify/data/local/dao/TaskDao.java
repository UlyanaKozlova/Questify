package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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

    @Delete
    void delete(TaskEntity taskEntity);


    @Query("SELECT * FROM tasks WHERE isDeleted = 0")
    List<TaskEntity> getAll();

    @Query("SELECT * FROM tasks WHERE globalId = :globalId LIMIT 1")
    TaskEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM tasks WHERE needsSync = 1")
    List<TaskEntity> getNeedingSync();

    @Query("SELECT * FROM tasks WHERE isDeleted = 0")
    LiveData<List<TaskEntity>> getAllLive();

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND projectGlobalId = :projectGlobalId")
    List<TaskEntity> getTasksByProject(String projectGlobalId);

    @Query("UPDATE tasks SET projectGlobalId = :toProjectId WHERE projectGlobalId = :fromProjectId")
    void moveTasksToProject(String fromProjectId, String toProjectId);


    @Query("UPDATE tasks SET isDeleted = 1, needsSync = 1, updatedAt = :updatedAt WHERE globalId = :globalId")
    void softDelete(String globalId, long updatedAt);
    @Query("SELECT * FROM tasks WHERE isDeleted = 1 AND needsSync = 1")
    List<TaskEntity> getSoftDeletedNeedingSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TaskEntity> entities);

    @Update
    void updateAll(List<TaskEntity> entities);
}

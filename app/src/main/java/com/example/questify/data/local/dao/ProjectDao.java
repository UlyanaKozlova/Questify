package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.ProjectEntity;

import java.util.List;


@Dao
public interface ProjectDao {

    @Insert
    long insert(ProjectEntity entity);

    @Update
    void update(ProjectEntity entity);

    @Delete
    void delete(ProjectEntity projectEntity);


    @Query("SELECT * FROM projects WHERE isDeleted = 0")
    List<ProjectEntity> getAll();

    @Query("SELECT * FROM projects WHERE globalId = :globalId LIMIT 1")
    ProjectEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM projects WHERE needsSync = 1 AND isDeleted = 0")
    List<ProjectEntity> getNeedingSync();

    @Query("DELETE FROM projects WHERE globalId = :globalId")
    void deleteByGlobalId(String globalId);

    @Query("SELECT * FROM projects WHERE isDeleted = 0")
    LiveData<List<ProjectEntity>> getAllLive();

    @Query("SELECT * FROM projects WHERE isDeleted = 0 AND projectName= :projectName LIMIT 1")
    ProjectEntity getByProjectName(String projectName);


    @Query("UPDATE projects SET isDeleted = 1, needsSync = 1, updatedAt = :updatedAt WHERE globalId = :globalId")
    void softDelete(String globalId, long updatedAt);

    @Query("SELECT * FROM projects WHERE isDeleted = 1 AND needsSync = 1")
    List<ProjectEntity> getSoftDeletedNeedingSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProjectEntity> entities);

    @Update
    void updateAll(List<ProjectEntity> entities);
}

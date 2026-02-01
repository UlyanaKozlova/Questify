package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.questify.data.local.entity.ProjectEntity;
import com.example.questify.data.local.entity.TaskEntity;

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

    @Query("SELECT * FROM projects WHERE needsSync = 1")
    List<ProjectEntity> getNeedingSync();

    @Query("SELECT * FROM projects WHERE isDeleted = 0")
    LiveData<List<ProjectEntity>> getAllLive();

    @Query("SELECT * FROM projects WHERE isDeleted = 0 AND projectName= :projectName LIMIT 1")
    ProjectEntity getByProjectName(String projectName);
}

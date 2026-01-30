package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
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

    @Query("SELECT * FROM projects WHERE isDeleted = 0")
    List<ProjectEntity> getProjectsForUser();

    @Query("SELECT * FROM projects WHERE globalId = :globalId LIMIT 1")
    ProjectEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM projects WHERE needsSync = 1")
    List<ProjectEntity> getNeedingSync();
}

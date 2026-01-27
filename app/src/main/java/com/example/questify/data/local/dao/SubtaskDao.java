package com.example.questify.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.questify.data.local.entity.SubtaskEntity;
import java.util.List;


@Dao
public interface SubtaskDao {

    @Insert
    long insert(SubtaskEntity entity);

    @Update
    void update(SubtaskEntity entity);

    @Query("SELECT * FROM subtasks WHERE taskGlobalId = :taskGlobalId AND isDeleted = 0")
    LiveData<List<SubtaskEntity>> getSubtasksForTask(String taskGlobalId);

    @Query("SELECT * FROM subtasks WHERE globalId = :globalId LIMIT 1")
    SubtaskEntity getByGlobalId(String globalId);

    @Query("SELECT * FROM subtasks WHERE needsSync = 1")
    List<SubtaskEntity> getNeedingSync();
}

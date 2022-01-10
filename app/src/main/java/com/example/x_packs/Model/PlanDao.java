package com.example.x_packs.Model;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PlanDao {
    @Insert
    public void insertPlan(Plan plan);

    @Delete
    public void deletePlan(Plan... plan);

    @Update
    public void updatePlan(Plan plan);

    @Query("SELECT * FROM Plan ORDER BY id")
    public LiveData<List<Plan>> getAllPlans();

    @Query("DELETE FROM Plan")
    void deleteAll();
}

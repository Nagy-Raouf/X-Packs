package com.example.x_packs.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.x_packs.Model.Plan;
import com.example.x_packs.Model.PlanRepo;

import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private PlanRepo repo;
    private LiveData<List<Plan>> allPlans;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        repo = new PlanRepo(application);
        allPlans = repo.getAllPlans();
    }

    public LiveData<List<Plan>> getAllPlans() {
        return allPlans;
    }
    public void update(Plan plan){
        repo.updatePlan(plan);
    }
    public void insertPlan(Plan plan){repo.insertPlan(plan);}
    public void init(){repo.init();}
    public void deleteAll(){repo.deleteAll();}

}

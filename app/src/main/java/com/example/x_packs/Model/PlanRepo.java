package com.example.x_packs.Model;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.x_packs.Utils.SharedData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlanRepo {
    private PlanDao dao;
    private LiveData<List<Plan>> allPlans;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public PlanRepo(Application app){
        PlanRoomDatabase crd = PlanRoomDatabase.getDatabase(app);
        dao = crd.planDao();
        allPlans = dao.getAllPlans();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(SharedData.USERNAME);
    }

    public LiveData<List<Plan>> getAllPlans(){
        return allPlans;
    }
    public void updatePlan(Plan plan){
        new updateAsyncTask(dao).execute(plan);
    }
    public void insertPlan(Plan plan){
        new insertAsyncTask(dao).execute(plan);
    }
    public void init(){new init(dao).execute();}
    public void deleteAll(){new deleteAllAsyncTask(dao).execute();}

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private PlanDao mAsyncTaskDao;

        deleteAllAsyncTask(PlanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Plan, Void, Void> {

        private PlanDao mAsyncTaskDao;

        insertAsyncTask(PlanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Plan... params) {
            mAsyncTaskDao.insertPlan(params[0]);
            return null;
        }
    }

    private class updateAsyncTask extends AsyncTask<Plan, Void, Void>{
        private PlanDao mAsyncTaskDao;

        public updateAsyncTask(PlanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Plan... plans) {
            mAsyncTaskDao.updatePlan(plans[0]);
            myRef.child(String.valueOf(plans[0].id)).setValue(plans[0]);
            return null;
        }
    }
    private class init extends AsyncTask<Void, Void, Void>{
        private PlanDao mAsyncTaskDao;

        public init(PlanDao dao) {
            mAsyncTaskDao = dao;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            myRef.removeValue();
            ArrayList<Plan> arrayList = initValues();
            for(int i=0;i<arrayList.size();i++){
                myRef.child(String.valueOf(i+1)).setValue(arrayList.get(i));
                mAsyncTaskDao.insertPlan(arrayList.get(i));
            }

            return null;
        }
    }


    private ArrayList<Plan> initValues(){
        ArrayList<Plan> arrayList = new ArrayList<Plan>();
        arrayList.add(new Plan(1,"Day "+1,"2 - 3 - 4 - 3 - 2",0));
        arrayList.add(new Plan(2,"Day "+2,"3 - 4 - 4 - 3 - 2",0));
        arrayList.add(new Plan(3,"Day "+3,"4 - 4 - 3 - 5 - 4",0));
        arrayList.add(new Plan(4,"Day "+4,"5 - 6 - 6 - 4 - 4",0));
        arrayList.add(new Plan(5,"Day "+5,"7 - 6 - 6 - 5 - 3",0));
        arrayList.add(new Plan(6,"Day "+6,"8 - 8 - 6 - 7 - 6",0));
        arrayList.add(new Plan(7,"Day "+7,"10 - 6 - 10 - 8 - 6",0));
        arrayList.add(new Plan(8,"Day "+8,"12 - 8 - 8 - 10 - 8",0));
        arrayList.add(new Plan(9,"Day "+9,"14 - 10 - 14 - 8 - 8",0));
        arrayList.add(new Plan(10,"Day "+10,"20",0));
        arrayList.add(new Plan(11,"Day "+11,"16 - 12 - 14 - 10 - 10",0));
        arrayList.add(new Plan(12,"Day "+12,"16 - 16 - 12 - 11 - 10",0));
        arrayList.add(new Plan(13,"Day "+13,"18 - 16 - 12 - 11 - 10",0));
        arrayList.add(new Plan(14,"Day "+14,"20 - 16 - 14 - 12 - 12",0));
        arrayList.add(new Plan(15,"Day "+15,"22 - 16 - 14 - 12 - 12",0));
        arrayList.add(new Plan(16,"Day "+16,"24 - 20 - 16 - 12 - 8",0));
        arrayList.add(new Plan(17,"Day "+17,"26 - 20 - 14 - 12 - 10",0));
        arrayList.add(new Plan(18,"Day "+18,"28 - 22 - 14 - 12 - 12",0));
        arrayList.add(new Plan(19,"Day "+19,"32 - 22 - 18 - 14 - 10",0));
        arrayList.add(new Plan(20,"Day "+20,"50",0));
        return arrayList;
    }

}

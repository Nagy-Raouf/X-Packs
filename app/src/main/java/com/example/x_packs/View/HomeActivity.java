package com.example.x_packs.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.x_packs.Model.Plan;
import com.example.x_packs.R;
import com.example.x_packs.Utils.SharedData;
import com.example.x_packs.ViewModel.PlanViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private GoogleSignInClient mSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new MainFragment());
        pagerAdapter.addFragment(new ChartFragment());
        pagerAdapter.addFragment(new PlanFragment());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("MAIN");
        tabLayout.getTabAt(1).setText("STATISTICS");
        tabLayout.getTabAt(2).setText("PLAN");

        mFirebaseAuth = FirebaseAuth.getInstance();
        Intent intent = this.getIntent();
        if(intent != null){
            String s = intent.getStringExtra("NAME");
            if(s.contains("Main")){
                initValues(intent.getBooleanExtra("USER_STATE",false));
            }
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.alarm) {
            Intent intent = new Intent(HomeActivity.this, AlarmActivity.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.reset){
            reset();
        }else if(item.getItemId() == R.id.sign_out){
            mFirebaseAuth.signOut();
            mSignInClient.signOut();
            SharedData.USERNAME = "ANONYMOUS";
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void reset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Are you sure you want to reset ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which){
                PlanViewModel viewModel = ViewModelProviders.of(HomeActivity.this).get(PlanViewModel.class);
                viewModel.init();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,int which){
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initValues(boolean isNewUser){
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        SharedData.USERNAME = mFirebaseUser.getDisplayName();

        PlanViewModel viewModel = ViewModelProviders.of(this).get(PlanViewModel.class);
        
        FirebaseDatabase database;
        DatabaseReference myRef;
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference(SharedData.USERNAME);

        if(!isNewUser){
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Plan p = dataSnapshot.getValue(Plan.class);
                        viewModel.insertPlan(p);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else {
            viewModel.init();
        }
    }


}

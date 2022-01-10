package com.example.x_packs.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.x_packs.Model.Plan;
import com.example.x_packs.R;
import com.example.x_packs.Utils.SharedData;
import com.example.x_packs.ViewModel.PlanViewModel;
import com.example.x_packs.databinding.ActivityTrainingBinding;
import com.example.x_packs.databinding.FragmentMainBinding;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private PlanViewModel viewModel;
    private FragmentMainBinding binding;
    //private Plan currentPlan;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PLAN_ID = "planID";
    public static final String MAX_REC = "maxRec";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment main.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUI();
        binding.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TrainingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUI() {
        viewModel = ViewModelProviders.of(this).get(PlanViewModel.class);
        viewModel.getAllPlans().observe(getActivity(), new Observer<List<Plan>>() {

            @Override
            public void onChanged(@Nullable final List<Plan> plans) {
                int max = 0;
                int totalDone=0;
                int total=0;
                int indexCurrent=0;
                for(int i=0;i<plans.size();i++){
                        total+=plans.get(i).getTotalNum();
                        if(plans.get(i).getDone()==1){
                            totalDone+=plans.get(i).getTotalNum();
                            max=plans.get(i).getMax();
                        }
                }
                for(int i=0;i<plans.size();i++){
                    System.out.println(plans.size());
                    if(plans.get(i).getDone()==0){
                        SharedData.currentPlan = plans.get(i);
                        indexCurrent = i;
                        break;
                    }
                }
                binding.maxRecord.setText("HIGH RECORD\n"+max);
                binding.progressNum.setText(indexCurrent+"/"+plans.size());
                binding.progressBar.setProgress((int) ((totalDone/(float)total)*100));
                binding.pushDone.setText(totalDone+"");
                binding.pushRemains.setText(total-totalDone+"");
                if(SharedData.currentPlan != null) {
                    binding.nextSession.setText(SharedData.currentPlan.getData());
                    binding.totalSets.setText(SharedData.currentPlan.getTotalNum() + "");
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
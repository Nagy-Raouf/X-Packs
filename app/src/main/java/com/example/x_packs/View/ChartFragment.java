package com.example.x_packs.View;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.x_packs.Model.Plan;
import com.example.x_packs.R;
import com.example.x_packs.ViewModel.PlanViewModel;
import com.example.x_packs.databinding.FragmentChartBinding;
import com.example.x_packs.databinding.FragmentMainBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {
    private BarChart barChart;
    private BarData barData;
    private BarDataSet barDataSet;
    private ArrayList barArrayList;
    private PlanViewModel viewModel;
    private FragmentChartBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChartBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barChart= (BarChart)view.findViewById(R.id.barChart);
        binding.textLabel.setText("CALORIES BURNED");
        binding.rightArrow.setVisibility(View.INVISIBLE);
        getEnt();
        binding.leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rightArrow.setVisibility(View.VISIBLE);
                binding.leftArrow.setVisibility(View.INVISIBLE);
                binding.textLabel.setText("PLAN PROGRESS");
                getProgress();
            }
        });
        binding.rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.leftArrow.setVisibility(View.VISIBLE);
                binding.rightArrow.setVisibility(View.INVISIBLE);
                binding.textLabel.setText("CALORIES BURNED");
                getEnt();
            }
        });
    }
    private void getProgress(){
        viewModel = ViewModelProviders.of(this).get(PlanViewModel.class);
        viewModel.getAllPlans().observe(getActivity(), new Observer<List<Plan>>() {
            @Override
            public void onChanged(@Nullable final List<Plan> plans) {
                barArrayList= new ArrayList();
                int total=0;
                for(int i=0;i<plans.size();i++){
                    if(plans.get(i).getDone()==1){
                        barArrayList.add(new BarEntry(i+1, plans.get(i).getMax()));
                        total+=plans.get(i).getTotalNum();
                    }else{
                        barArrayList.add(new BarEntry(i+1, 0));
                    }
                }
                binding.totalDone.setText(total+"");
                binding.calories.setText(total*3+"");
                setUI();
            }
        });

    }
    private void setUI(){
        barDataSet = new BarDataSet(barArrayList,"Data Set");
        barData = new BarData(barDataSet);
        barChart.getXAxis().setTextColor(Color.rgb(255, 165, 0));
        barChart.getAxisLeft().setTextColor(Color.rgb(255, 165, 0));
        barChart.getAxisLeft().setGridColor(Color.rgb(255, 165, 0));
        barChart.setData(barData);
        barChart.animateY(1000);
        barDataSet.setColors(Color.rgb(255, 165, 0));
        barDataSet.setValueTextSize(1f);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barDataSet.setFormSize(0);
        barChart.setDoubleTapToZoomEnabled(false);
    }
    private void getEnt(){

        viewModel = ViewModelProviders.of(this).get(PlanViewModel.class);
        viewModel.getAllPlans().observe(getActivity(), new Observer<List<Plan>>() {
            @Override
            public void onChanged(@Nullable final List<Plan> plans) {
                barArrayList= new ArrayList();
                int total=0;
                for(int i=0;i<plans.size();i++){
                    if(plans.get(i).getDone()==1){
                        barArrayList.add(new BarEntry(i+1, plans.get(i).getTotalNum()*3));
                        total+=plans.get(i).getTotalNum();
                    }else{
                        barArrayList.add(new BarEntry(i+1, 0));
                    }
                }
                binding.totalDone.setText(total+"");
                binding.calories.setText(total*3+"");
                setUI();
            }

        });

    }
}
package com.example.x_packs.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import com.example.x_packs.Model.Plan;
import com.example.x_packs.R;
//import com.example.x_packs.ViewModel.ProgressViewModel;

import java.util.ArrayList;


public class PlanAdapter extends ArrayAdapter<Plan> {
    private Context context;
    private int resource;
    public PlanAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Plan> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater =LayoutInflater.from(this.context);
        convertView = layoutInflater.inflate(resource,parent,false);

        TextView textView01 = convertView.findViewById(R.id.day);
        TextView textView02 = convertView.findViewById(R.id.sets);
        ImageView imageView = convertView.findViewById(R.id.mark);
        LinearLayout linearLayout = convertView.findViewById(R.id.linear_plan);


        if(getItem(position).getDone()==1){
            linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_dark));
            imageView.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            imageView.setVisibility(View.INVISIBLE);
        }
        textView01.setText(getItem(position).getDayString());
        textView02.setText(getItem(position).getData());


        return convertView;
    }
}

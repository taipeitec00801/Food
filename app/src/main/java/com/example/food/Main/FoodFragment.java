package com.example.food.Main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.food.R;

public class FoodFragment extends Fragment {
    private Food food;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable("food");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.foodpic_fragment, container, false);
        ImageView ivImage = view.findViewById(R.id.ivImage);
        ivImage.setImageResource(food.getImage());

//        TextView tvId = view.findViewById(R.id.tvId);
//        tvId.setText(String.valueOf(food.getId()));
//
//        TextView tvName = view.findViewById(R.id.tvName);
//        tvName.setText(food.getName());
        return view;
    }
}

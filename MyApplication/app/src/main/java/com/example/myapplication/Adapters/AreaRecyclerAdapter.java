package com.example.myapplication.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CalculateAreaActivity;
import com.example.myapplication.Models.AreaModel;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;

public class AreaRecyclerAdapter extends RecyclerView.Adapter<AreaRecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    List<AreaModel> areasList;
    List<AreaModel> areasListAll;
    private Context context;

    public AreaRecyclerAdapter(List<AreaModel> areasList, Context context) {
        this.areasList = areasList;
        this.context = context;
        areasListAll = new ArrayList<>();
        areasListAll.addAll(areasList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.area_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleText.setText(areasList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return areasList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleText;
        MaterialButton infoBtn, focusBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.area_title);
            infoBtn = itemView.findViewById(R.id.info_btn);
            focusBtn = itemView.findViewById(R.id.focus_btn);


            focusBtn.setOnClickListener(this);
            infoBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.focus_btn:
                    if (context.getClass().equals(CalculateAreaActivity.class)) {

                        Polygon polygon = Polygon.fromJson(areasList.get(getAdapterPosition()).getCoordinates());
                        List<Point>  list = polygon.coordinates().get(0);
                        ((CalculateAreaActivity) context).addPoints(list);
                        ((CalculateAreaActivity) context).bottomAreaListFragment.dismiss();
                    }
                    break;
                case R.id.info_btn:
                    ((CalculateAreaActivity) context).bottomAreaListFragment.dismiss();
                    ((CalculateAreaActivity) context).areaDetailFragment.setAreaModel(areasList.get(getAdapterPosition()));
                    ((CalculateAreaActivity) context).areaDetailFragment.show(((CalculateAreaActivity) context).getSupportFragmentManager(), "BottomAreaDetail");
                    break;

            }

        }
    }
}
















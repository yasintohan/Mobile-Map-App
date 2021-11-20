package com.example.myapplication.Adapters;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.MarkerModel;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.location.LocationComponent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerAdapter";
    List<MarkerModel> markersList;
    List<MarkerModel> markersListAll;
    private Context context;

    public RecyclerAdapter(List<MarkerModel> markersList, Context context) {
        this.markersList = markersList;
        this.context = context;
        markersListAll = new ArrayList<>();
        markersListAll.addAll(markersList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.bottom_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleText.setText(markersList.get(position).getTitle());
        holder.markerIcon.setImageResource(markersList.get(position).getIcon());
        holder.markerIcon.setColorFilter(markersList.get(position).getColor());




    }

    @Override
    public int getItemCount() {
        return markersList.size();
    }

    @Override
    public Filter getFilter() {

        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {


            List<MarkerModel> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            Log.i("jsonquery", charSequence.toString());

            try {

                JSONObject obj = new JSONObject(charSequence.toString());

                if (obj.getInt("type") == 0 && obj.getString("query").equals("")) {
                    Log.i("jsonquery", "calisti");
                    filteredList.addAll(markersListAll);
                } else if(obj.getInt("type") != 0 && obj.getString("query").equals("")) {
                    Log.i("jsonquery", "ilki calisti");
                    for (MarkerModel marker: markersListAll) {
                        if (marker.getIcon() == obj.getInt("type")) {
                            filteredList.add(marker);
                        }
                    }
                }
                else if(obj.getInt("type") == 0 && !obj.getString("query").equals("")){
                    Log.i("jsonquery", "ikinci calisti");
                    for (MarkerModel marker: markersListAll) {
                        if (marker.getTitle().contains(obj.getString("query"))) {
                            filteredList.add(marker);
                        }
                    }
                } else {
                    Log.i("jsonquery", "son calisti");
                    for (MarkerModel marker: markersListAll) {
                        if (marker.getTitle().contains(obj.getString("query")) && marker.getIcon() == obj.getInt("type")) {
                            filteredList.add(marker);
                        }
                    }
                }


                filterResults.values = filteredList;

            } catch (Throwable t) {
                Log.e("jsonquery", "Could not parse malformed JSON");
            }


            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            markersList.clear();
            markersList.addAll((Collection<? extends MarkerModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView markerIcon;
        TextView titleText;
        MaterialButton infoBtn, focusBtn, directionBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            markerIcon = itemView.findViewById(R.id.marker_icon);
            titleText = itemView.findViewById(R.id.marker_title);
            infoBtn = itemView.findViewById(R.id.info_btn);
            focusBtn = itemView.findViewById(R.id.focus_btn);
            directionBtn = itemView.findViewById(R.id.direction_btn);

          //  descriptionText = itemView.findViewById(R.id.marker_description);

           // itemView.setOnClickListener(this);
            focusBtn.setOnClickListener(this);
            infoBtn.setOnClickListener(this);
            directionBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i("adapter", "asd");

            switch (view.getId()){

                case R.id.focus_btn:
                    if (context.getClass().equals(MainActivity.class)) {
                        Log.i("adapter", context.getClass().toString() + " asd");
                       Location temp = new Location(LocationManager.GPS_PROVIDER);
                       temp.setLatitude(markersList.get(getAdapterPosition()).getLatidude());
                       temp.setLongitude(markersList.get(getAdapterPosition()).getLongitude());
                       ((MainActivity) context).focusPoint(temp);
                        ((MainActivity) context).bottomListFragment.dismiss();
                    }
                    break;
                case R.id.info_btn:
                    ((MainActivity) context).bottomListFragment.dismiss();
                    ((MainActivity) context).markerDetailFragment.setMarkerModel(markersList.get(getAdapterPosition()));
                    ((MainActivity) context).markerDetailFragment.show(((MainActivity) context).getSupportFragmentManager(), "BottomMarkerDetail");
                    break;

                case R.id.direction_btn:
                    LocationComponent locationComponent = ((MainActivity) context).mapboxMap.getLocationComponent();
                    Point origin = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(), locationComponent.getLastKnownLocation().getLatitude());
                    Point destination = Point.fromLngLat(markersList.get(getAdapterPosition()).getLongitude(), markersList.get(getAdapterPosition()).getLatidude());
                    ((MainActivity) context).getRoute(origin, destination);
                    ((MainActivity) context).bottomListFragment.dismiss();
                    break;
            }

        }
    }
}
















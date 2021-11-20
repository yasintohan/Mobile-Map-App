package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.MarkerModel;
import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textview.MaterialTextView;
import com.mapbox.mapboxsdk.annotations.Marker;

import java.util.List;

public class MarkerDetailFragment extends BottomSheetDialogFragment {

    private MarkerModel markerModel = new MarkerModel();


    private MaterialTextView titleText;
    private MaterialTextView descriptionText;
    private ImageView markerIcon;
    private ImageView backBtn;
    private ImageView deleteBtn;
    private ImageView editBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_marker_detail_modal, container, false);

         titleText = view.findViewById(R.id.marker_title);
         descriptionText = view.findViewById(R.id.marker_description);
         markerIcon = view.findViewById(R.id.marker_icon_detail);
         backBtn = view.findViewById(R.id.arrow_back_btn);
         deleteBtn = view.findViewById(R.id.delete_btn);
         editBtn = view.findViewById(R.id.edit_btn);

       initState();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getContext()).markerDetailFragment.dismiss();
                ((MainActivity) getContext()).bottomListFragment.show(((MainActivity) getContext()).getSupportFragmentManager(), "BottomList");
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAlertDialog();

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getContext()).markerDetailFragment.dismiss();
                ((MainActivity) getContext()).updateMarkerFragment.onStop();
                ((MainActivity) getContext()).updateMarkerFragment.setMarkerModel(getMarkerModel());
                ((MainActivity) getContext()).updateMarkerFragment.show(((MainActivity) getContext()).getSupportFragmentManager(), "BottomUpdate");


            }
        });


        return view;
    }


    public void initState() {
        titleText.setText(markerModel.getTitle());
        descriptionText.setText(markerModel.getDescription());
        markerIcon.setImageResource(markerModel.getIcon());
        markerIcon.setColorFilter(markerModel.getColor());
    }

    public MarkerModel getMarkerModel() {
        return markerModel;
    }

    public void setMarkerModel(MarkerModel markerModel) {
        this.markerModel = markerModel;
    }


    private void getAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Alert");

        builder.setMessage("Are you sure want to delete?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) getContext()).db.deleteMarker(markerModel.getMarkerId());
                List<Marker> markers = ((MainActivity) getContext()).mapboxMap.getMarkers();
                for (Marker marker : markers) {
                    if (marker.getPosition().getLongitude() == markerModel.getLongitude() && marker.getPosition().getLatitude() == markerModel.getLatidude()) {
                        ((MainActivity) getContext()).mapboxMap.removeMarker(marker);
                        ((MainActivity) getContext()).markerDetailFragment.dismiss();
                    }
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}



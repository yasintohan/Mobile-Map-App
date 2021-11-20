package com.example.myapplication.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Helpers.BitmapConverter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.MarkerModel;
import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;

import java.util.List;

public class UpdateMarkerFragment extends BottomSheetDialogFragment {

    private MarkerModel markerModel = new MarkerModel();

    private TextInputEditText markerTitle;
    private TextInputEditText markerDescription;

    private int markerIconDrawable;
    private int markerIconColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_update_marker_modal, container, false);

         markerTitle = view.findViewById(R.id.marker_name_edittext);
        markerDescription = view.findViewById(R.id.marker_description_edittext);

        initState();

        Log.i("updatefrag", markerModel.getTitle()+ "deneme");

        MaterialButtonToggleGroup markerTypeButtonGroup = view.findViewById(R.id.marker_type_button_group);
        markerTypeButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.home_type_btn:
                            markerIconDrawable = R.drawable.ic_home;
                            break;
                        case R.id.business_type_btn:
                            markerIconDrawable = R.drawable.ic_business;
                            break;
                        case R.id.school_type_btn:
                            markerIconDrawable = R.drawable.ic_school;
                            break;
                        case R.id.default_type_btn:
                            markerIconDrawable = R.drawable.ic_location_on;
                            break;
                    }

                }
            }
        });


        MaterialButtonToggleGroup markerColorButtonGroup = view.findViewById(R.id.marker_color_button_group);
        markerColorButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.marker_red_btn:
                            markerIconColor = ContextCompat.getColor(getContext(), R.color.color_red);
                            break;
                        case R.id.marker_blue_btn:
                            markerIconColor = ContextCompat.getColor(getContext(), R.color.color_blue);
                            break;
                        case R.id.marker_green_btn:
                            markerIconColor = ContextCompat.getColor(getContext(), R.color.color_green);
                            break;
                        case R.id.marker_yellow_btn:
                            markerIconColor = ContextCompat.getColor(getContext(), R.color.color_yellow);
                            break;
                    }

                }
            }
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        view.findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                MarkerModel newMarkerModel = new MarkerModel(markerModel.getMarkerId(),
                        markerTitle.getText().toString().trim(),
                        markerDescription.getText().toString().trim(),
                        markerModel.getLongitude(),
                        markerModel.getLatidude(),
                        markerIconDrawable,
                        markerIconColor);


                List<Marker> markers = ((MainActivity) getContext()).mapboxMap.getMarkers();
                for (Marker marker : markers) {
                    if (marker.getPosition().getLongitude() == markerModel.getLongitude() && marker.getPosition().getLatitude() == markerModel.getLatidude()) {

                        // Create an Icon object for the marker to use
                        IconFactory iconFactory = IconFactory.getInstance(((MainActivity) getContext()));
                        Icon icon = iconFactory.fromBitmap(BitmapConverter.getBitmapFromVectorDrawable(getContext(), markerIconDrawable, markerIconColor));


                        marker.setTitle(markerTitle.getText().toString().trim());
                        marker.setIcon(icon);
                        ((MainActivity) getContext()).mapboxMap.updateMarker(marker);
                    }
                }

                ((MainActivity) getContext()).db.updateMarker(newMarkerModel);

                dismiss();
            }
        });

        return view;
    }

    public void initState(){
        markerTitle.setText(markerModel.getTitle());
        markerDescription.setText(markerModel.getDescription());
        markerIconDrawable = markerModel.getIcon();
        markerIconColor = markerModel.getColor();
    //    Toast.makeText(getContext(), markerModel.getTitle(), Toast.LENGTH_LONG).show();

    }

    public void setMarkerModel(MarkerModel markerModel) {
        this.markerModel = markerModel;
    }


    @Override
    public void onResume() {
        super.onResume();
        initState();
    }

}

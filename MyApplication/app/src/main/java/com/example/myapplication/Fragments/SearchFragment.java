package com.example.myapplication.Fragments;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.CompoundButton;


        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.widget.SearchView;
        import androidx.recyclerview.widget.DividerItemDecoration;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.myapplication.Adapters.RecyclerAdapter;
        import com.example.myapplication.Helpers.DatabaseHelper;
        import com.example.myapplication.MainActivity;
        import com.example.myapplication.Models.MarkerModel;
        import com.example.myapplication.R;
        import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
        import com.google.android.material.button.MaterialButton;
        import com.google.android.material.button.MaterialButtonToggleGroup;
        import com.google.android.material.checkbox.MaterialCheckBox;
        import com.google.android.material.textfield.TextInputEditText;
        import com.google.android.material.textfield.TextInputLayout;
        import com.mapbox.api.geocoding.v5.models.CarmenFeature;
        import com.mapbox.geojson.Feature;
        import com.mapbox.geojson.FeatureCollection;
        import com.mapbox.geojson.Point;
        import com.mapbox.mapboxsdk.Mapbox;
        import com.mapbox.mapboxsdk.camera.CameraPosition;
        import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
        import com.mapbox.mapboxsdk.geometry.LatLng;
        import com.mapbox.mapboxsdk.location.LocationComponent;
        import com.mapbox.mapboxsdk.maps.Style;
        import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
        import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
        import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

        import java.util.ArrayList;
        import java.util.List;

public class SearchFragment extends BottomSheetDialogFragment {

    private MaterialCheckBox currentLocationCheckBox;
    private TextInputLayout originField;
    private TextInputEditText originEdittext;
    private MaterialButton searchBtn;
    private MaterialButtonToggleGroup destinationTypeButtonGroup;

    private boolean checked;
    private Point destination;
    private Point origin;
    private Point searchOrigin;
    private Point currentOrigin;

    public SearchFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_search_modal, container, false);

        initState(view);

        currentLocationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                SearchFragment.this.checked = checked;
                if(checked) {
                    originField.setHint("Başlangıç Noktası : Şuanki Konum");
                    originEdittext.setVisibility(View.INVISIBLE);
                } else {
                    originField.setHint("Başlangıç Noktası");
                    originEdittext.setVisibility(View.VISIBLE);
                }
            }
        });


        destinationTypeButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.car_btn:
                            ((MainActivity) getContext()).setSelectedCarType(0);
                            ((MainActivity) getContext()).setLineColorString("#009688");
                            break;
                        case R.id.bike_btn:
                            ((MainActivity) getContext()).setSelectedCarType(1);
                            ((MainActivity) getContext()).setLineColorString("#A4036F");
                            break;
                        case R.id.walk_btn:
                            ((MainActivity) getContext()).setSelectedCarType(2);
                            ((MainActivity) getContext()).setLineColorString("#49B6FF");
                            break;
                        default:
                            ((MainActivity) getContext()).setSelectedCarType(0);
                            ((MainActivity) getContext()).setLineColorString("#009688");
                            break;
                    }
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checked){
                    LocationComponent locationComponent = ((MainActivity) getContext()).mapboxMap.getLocationComponent();
                    currentOrigin = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(), locationComponent.getLastKnownLocation().getLatitude());
                    origin = currentOrigin;
                } else {
                    if(searchOrigin != null)
                    origin = searchOrigin;
                }

                Log.i("searchModal", originEdittext.getText().toString().trim());
                ((MainActivity) getContext()).getRoute(origin, destination);
                ((MainActivity) getContext()).searchFragment.dismiss();


            }
        });


        originEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build((MainActivity) getActivity());
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }


    private void initState(View view){
        currentLocationCheckBox = view.findViewById(R.id.current_location_checkbox);
        originField = view.findViewById(R.id.origin_field);
        originEdittext = view.findViewById(R.id.origin_edittext);
        searchBtn = view.findViewById(R.id.modal_search_btn);
        destinationTypeButtonGroup = view.findViewById(R.id.destination_type_button_group);

    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {

            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            searchOrigin = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(), ((Point) selectedCarmenFeature.geometry()).latitude());
            originEdittext.setText(selectedCarmenFeature.text());
        }
    }

}

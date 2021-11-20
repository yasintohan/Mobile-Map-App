package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.myapplication.Fragments.BottomListFragment;
import com.example.myapplication.Fragments.MarkerDetailFragment;
import com.example.myapplication.Fragments.SearchFragment;
import com.example.myapplication.Fragments.UpdateMarkerFragment;
import com.example.myapplication.Helpers.BitmapConverter;
import com.example.myapplication.Helpers.DatabaseHelper;
import com.example.myapplication.Helpers.NavigationHelper;
import com.example.myapplication.Models.MarkerModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {


    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private PermissionsManager permissionsManager;
    public MapboxMap mapboxMap;
    private MapView mapView;
    private String mapboxStyle;

    private Dialog layersDialog;
    public BottomListFragment bottomListFragment;
    public MarkerDetailFragment markerDetailFragment;
    public UpdateMarkerFragment updateMarkerFragment;
    public SearchFragment searchFragment;

    private Marker lastClickedMarker;
    private Dialog addMarkerDialog;
    private MaterialButton addMarkerBtn;
    private MaterialButtonToggleGroup destinationTypeButtonGroup;

    public int markerIconDrawable = R.drawable.ic_location_on;
    public int markerIconColor;

    private LineLayer routeLayer;
    private String lineColorString = "#009688";

    private MaterialButton destinationBtn;


    private Point origin = Point.fromLngLat(-99.13037323366, 19.40488375253);
    private Point destination = Point.fromLngLat(-99.167663574, 19.426984786987);
    private int selectedCarType = 0;

    public DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapboxStyle = Style.MAPBOX_STREETS;
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);


        db = new DatabaseHelper(getApplicationContext());


        markerIconColor = ContextCompat.getColor(getApplicationContext(), R.color.color_red);
        addMarkerDialog = new Dialog(this);

        //Setup Toolbar & Navigation Drawer

        toolbar = (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);




        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationHelper(getApplicationContext()));
        //#Setup Toolbar & Navigation Drawer


        //map
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //#map


        //buttons




        MaterialButton focusLocationBtn = findViewById(R.id.focus_location);
        focusLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationComponent locationComponent = mapboxMap.getLocationComponent();
                setCameraPosition(locationComponent.getLastKnownLocation());
            }
        });
        bottomListFragment = new BottomListFragment();
        markerDetailFragment = new MarkerDetailFragment();
        updateMarkerFragment = new UpdateMarkerFragment();
        searchFragment = new SearchFragment();
        MaterialButton bottomListBtn = findViewById(R.id.bottom_list_btn);
        bottomListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomListFragment.show(getSupportFragmentManager(), "BottomList");
            }
        });


        layersDialog = new Dialog(this);
        MaterialButton layersBtn = findViewById(R.id.layers_button);
        layersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayersDialog();
            }
        });

        addMarkerBtn = findViewById(R.id.add_marker_button);
        addMarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMarkerDialog(lastClickedMarker.getPosition());
            }
        });


        destinationBtn = findViewById(R.id.search_direction_button);
        destinationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 searchFragment.show(getSupportFragmentManager(), "SearchFragment");
            }
        });


        destinationTypeButtonGroup = findViewById(R.id.destination_type_button_group);
        destinationTypeButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.car_btn:
                            selectedCarType = 0;
                            lineColorString = "#009688";
                            break;
                        case R.id.bike_btn:
                            selectedCarType = 1;
                            lineColorString = "#A4036F";
                            break;
                        case R.id.walk_btn:
                            selectedCarType = 2;
                            lineColorString = "#49B6FF";
                            break;
                        default:
                            selectedCarType = 0;
                            lineColorString = "#009688";
                            break;
                    }
                    getRoute(origin, destination);
                }
            }
        });
        //#buttons

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(mapboxStyle, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

                UiSettings uiSettings = mapboxMap.getUiSettings();

                uiSettings.setCompassGravity(Gravity.AXIS_SPECIFIED);
                uiSettings.setCompassMargins(0, 8, 0, 0);
                uiSettings.setDoubleTapGesturesEnabled(true);

                getMarkers();

                //map click for marker
                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public boolean onMapLongClick(@NonNull LatLng point) {
                        if (lastClickedMarker != null)
                            mapboxMap.removeMarker(lastClickedMarker);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(point.getLatitude(), point.getLongitude()));

                        lastClickedMarker = mapboxMap.addMarker(markerOptions);
                        addMarkerBtn.setVisibility(View.VISIBLE);
                        destinationBtn.setVisibility(View.INVISIBLE);
                        destinationTypeButtonGroup.setVisibility(View.INVISIBLE);
                        GeoJsonSource source = style.getSourceAs("geojsonSourceLayerId");
                        if (source != null) {
                            source.setGeoJson(FeatureCollection.fromFeatures(
                                    new Feature[] {}));
                        }
                        GeoJsonSource source2 = style.getSourceAs("ROUTE_SOURCE_ID");
                        if (source2 != null) {
                            source2.setGeoJson(FeatureCollection.fromFeatures(
                                    new Feature[] {}));
                        }

                        return true;
                    }
                });
                //#mapclick for marker

                //marker click
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {


                        if(marker != lastClickedMarker) {
                            MarkerModel markerModel = db.getMarker(Long.parseLong(marker.getTitle()));
                            markerDetailFragment.setMarkerModel(markerModel);
                            markerDetailFragment.show(getSupportFragmentManager(), "MarkerDetail");
                        }

                        return true;
                    }
                });
                //#marker click


                initSource(style, origin, destination);

                initLayers(style);

            }
        });
    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .pulseEnabled(true)
                    .build();

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Explanation Needed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    void setCameraPosition(Location cameraposition) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cameraposition.getLatitude(), cameraposition.getLongitude()), 12.0));
    }


    private void openLayersDialog() {
        layersDialog.setContentView(R.layout.layers_modal);
        layersDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        layersDialog.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layersDialog.dismiss();
            }
        });

        //layer Buttons
        layersDialog.findViewById(R.id.defaultCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxStyle = Style.MAPBOX_STREETS;
                onMapReady(mapboxMap);
                layersDialog.dismiss();
            }
        });

        layersDialog.findViewById(R.id.satelliteCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxStyle = Style.SATELLITE_STREETS;
                onMapReady(mapboxMap);
                layersDialog.dismiss();
            }
        });

        layersDialog.findViewById(R.id.trafficCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxStyle = Style.TRAFFIC_DAY;
                onMapReady(mapboxMap);
                layersDialog.dismiss();
            }
        });

        layersDialog.findViewById(R.id.terrainCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxStyle = Style.SATELLITE;
                onMapReady(mapboxMap);
                layersDialog.dismiss();
            }
        });
        //#layer buttons


        layersDialog.show();

    }

    private void openAddMarkerDialog(@NonNull LatLng point) {
        addMarkerDialog.setContentView(R.layout.add_marker_modal);
        addMarkerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addMarkerDialog.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarkerDialog.dismiss();
            }
        });
        addMarkerDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarkerDialog.dismiss();
            }
        });

        MaterialButtonToggleGroup markerTypeButtonGroup = addMarkerDialog.findViewById(R.id.marker_type_button_group);
        markerTypeButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.home_type_btn:
                            MainActivity.this.markerIconDrawable = R.drawable.ic_home;
                            break;
                        case R.id.business_type_btn:
                            MainActivity.this.markerIconDrawable = R.drawable.ic_business;
                            break;
                        case R.id.school_type_btn:
                            MainActivity.this.markerIconDrawable = R.drawable.ic_school;
                            break;
                        case R.id.default_type_btn:
                            MainActivity.this.markerIconDrawable = R.drawable.ic_location_on;
                            break;
                    }

                }
            }
        });


        MaterialButtonToggleGroup markerColorButtonGroup = addMarkerDialog.findViewById(R.id.marker_color_button_group);
        markerColorButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.marker_red_btn:
                            MainActivity.this.markerIconColor = ContextCompat.getColor(getApplicationContext(), R.color.color_red);
                            break;
                        case R.id.marker_blue_btn:
                            MainActivity.this.markerIconColor = ContextCompat.getColor(getApplicationContext(), R.color.color_blue);
                            break;
                        case R.id.marker_green_btn:
                            MainActivity.this.markerIconColor = ContextCompat.getColor(getApplicationContext(), R.color.color_green);
                            break;
                        case R.id.marker_yellow_btn:
                            MainActivity.this.markerIconColor = ContextCompat.getColor(getApplicationContext(), R.color.color_yellow);
                            break;
                    }

                }
            }
        });

        addMarkerDialog.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxMap.removeMarker(lastClickedMarker);
                lastClickedMarker = null;


                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                Icon icon = iconFactory.fromBitmap(BitmapConverter.getBitmapFromVectorDrawable(getApplicationContext(), markerIconDrawable, markerIconColor));

                TextInputEditText markerNameLayout = addMarkerDialog.findViewById(R.id.marker_name_edittext);
                TextInputEditText markerDescLayout = addMarkerDialog.findViewById(R.id.marker_description_edittext);



                MarkerModel markerModel = new MarkerModel(markerNameLayout.getText().toString().trim(),
                        markerDescLayout.getText().toString().trim(),
                        point.getLongitude(),
                        point.getLatitude(),
                        markerIconDrawable,
                        markerIconColor);

                long id = db.createMarker(markerModel);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(point.getLatitude(), point.getLongitude()))
                        .title(id+"")
                        .icon(icon);


                mapboxMap.addMarker(markerOptions);
                addMarkerBtn.setVisibility(View.INVISIBLE);
                markerIconDrawable = R.drawable.ic_location_on;
                addMarkerDialog.dismiss();
            }
        });


        // Toast.makeText(MainActivity.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_SHORT).show();


        addMarkerDialog.show();

    }


    private void getMarkers() {


        List<MarkerModel> markers = db.getAllMarkers();

        for (MarkerModel marker : markers) {
            // Create an Icon object for the marker to use
            IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
            Icon icon = iconFactory.fromBitmap(BitmapConverter.getBitmapFromVectorDrawable(getApplicationContext(), marker.getIcon(), marker.getColor()));

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(marker.getLatidude(), marker.getLongitude()))
                    .title(marker.getMarkerId()+"")
                    .icon(icon);

            mapboxMap.addMarker(markerOptions);
        }

    }


    public void focusPoint(Location location) { setCameraPosition(location); }


//draw route
    private void initLayers(@NonNull Style loadedMapStyle) {

        loadedMapStyle.addImage("symbolIconId", BitmapConverter.getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_location_on, R.color.color_red));


        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", "geojsonSourceLayerId").withProperties(
                iconImage("symbolIconId"),
                iconOffset(new Float[] {0f, -8f})
        ));


        routeLayer = new LineLayer("ROUTE_LAYER_ID", "ROUTE_SOURCE_ID");

        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor(lineColorString))
        );
        loadedMapStyle.addLayer(routeLayer);


        loadedMapStyle.addImage("RED_PIN_ICON_ID", BitmapConverter.getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_location_on, R.color.color_red));

        loadedMapStyle.addLayer(new SymbolLayer("ICON_LAYER_ID", "ICON_SOURCE_ID").withProperties(
                iconImage("RED_PIN_ICON_ID"),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));

    }


    private void initSource(@NonNull Style loadedMapStyle, Point origin, Point destination) {
        loadedMapStyle.addSource(new GeoJsonSource("ROUTE_SOURCE_ID"));
        loadedMapStyle.addSource(new GeoJsonSource("geojsonSourceLayerId"));


        GeoJsonSource iconGeoJsonSource = new GeoJsonSource("ICON_SOURCE_ID", FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);


    }



    public void getRoute(Point origin, Point destination) {

        destinationTypeButtonGroup.setVisibility(View.VISIBLE);
        this.origin = origin;
        this.destination = destination;

        String profile = DirectionsCriteria.PROFILE_DRIVING;
        switch (selectedCarType) {
            case 0:
                profile = DirectionsCriteria.PROFILE_DRIVING;
                break;
            case 1:
                profile = DirectionsCriteria.PROFILE_CYCLING;
                break;
            case 2:
                profile =  DirectionsCriteria.PROFILE_WALKING;
                break;
            default:
                break;
        }

        routeLayer.setProperties(
                lineColor(Color.parseColor(lineColorString))
        );


        MapboxDirections client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(profile)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Log.e("responsebody","No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("responsebody","No routes found");
                    return;
                }
                DirectionsRoute currentRoute = response.body().routes().get(0);



                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            GeoJsonSource source = style.getSourceAs("ROUTE_SOURCE_ID");


                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });



    }
//#draw route




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {

            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {

                    GeoJsonSource source = style.getSourceAs("geojsonSourceLayerId");
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);

                    addMarkerBtn.setVisibility(View.INVISIBLE);
                    destinationBtn.setVisibility(View.VISIBLE);
                    if(lastClickedMarker != null)
                        mapboxMap.removeMarker(lastClickedMarker);

                    Point destination = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(), ((Point) selectedCarmenFeature.geometry()).latitude());
                    searchFragment.setDestination(destination);
                }



            }
        }
    }

    public void setSelectedCarType(int selectedCarType) {
        this.selectedCarType = selectedCarType;
    }

    public void setLineColorString(String lineColorString) {
        this.lineColorString = lineColorString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.findItem(R.id.searchOption).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                search();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void search(){
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS))
                .build(MainActivity.this);
        startActivityForResult(intent, 1);
    }
}

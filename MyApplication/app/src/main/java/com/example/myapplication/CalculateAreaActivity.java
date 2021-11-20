package com.example.myapplication;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.Fragments.AreaDetailFragment;
import com.example.myapplication.Fragments.BottomAreaListFragment;

import com.example.myapplication.Helpers.CalculatorHelper;
import com.example.myapplication.Helpers.DatabaseHelper;
import com.example.myapplication.Helpers.NavigationHelper;
import com.example.myapplication.Models.AreaModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class CalculateAreaActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public BottomAreaListFragment bottomAreaListFragment;
    public AreaDetailFragment areaDetailFragment;

    public MapboxMap mapboxMap;
    private MapView mapView;
    private String mapboxStyle;

    private TextView areaText;
    private TextView perimeterText;
    private MaterialButton clearBoundariesFab;
    private MaterialButton saveButton;
    private MaterialButton bottomListBtn;
    private MaterialButton layersBtn;


    private List<Point> fillLayerPointList = new ArrayList<>();
    private List<Point> lineLayerPointList = new ArrayList<>();
    private List<Feature> circleLayerFeatureList = new ArrayList<>();
    private List<List<Point>> listOfList;
    private GeoJsonSource circleSource;
    private GeoJsonSource fillSource;
    private GeoJsonSource lineSource;
    private GeoJsonSource buildingSource;

    private Point firstPointOfPolygon;

    private FillLayer fillLayer;
    private FillLayer buildingLayer;

    private CalculatorHelper areaCalculator;
    public DatabaseHelper db;
    private Dialog addAreaDialog;
    private Dialog updateAreaDialog;
    private Dialog layersDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapboxStyle = Style.MAPBOX_STREETS;
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_calculate_area);

        db = new DatabaseHelper(getApplicationContext());
        areaCalculator = new CalculatorHelper();

        bottomAreaListFragment = new BottomAreaListFragment(getApplicationContext());
        areaDetailFragment = new AreaDetailFragment();
        addAreaDialog = new Dialog(this);
        updateAreaDialog = new Dialog(this);
        layersDialog = new Dialog(this);


        areaText = findViewById(R.id.area_text);
        perimeterText = findViewById(R.id.perimeter_text);

        //Setup Toolbar & Navigation Drawer
        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

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



    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        CalculateAreaActivity.this.mapboxMap = mapboxMap;


        mapboxMap.setStyle(mapboxStyle, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .zoom(1)
                                .build()), 4000);


                UiSettings uiSettings = mapboxMap.getUiSettings();

                uiSettings.setCompassEnabled(false);

// Add sources to the map
                circleSource = initCircleSource(style);
                fillSource = initFillSource(style);
                lineSource = initLineSource(style);
                buildingSource = initBuildingSource(style);

// Add layers to the map
                initCircleLayer(style);
                initLineLayer(style);
                initFillLayer(style);
                initBuildingLayer(style);

                mapboxMap.addOnMapClickListener(CalculateAreaActivity.this);

                initButtonClickListeners();
            }
        });


    }

    /**
     * Set the button click listeners
     */
    private void initButtonClickListeners() {
        clearBoundariesFab = findViewById(R.id.clear_path_btn);
        saveButton = findViewById(R.id.save_path_btn);
        bottomListBtn = findViewById(R.id.list_btn);
        layersBtn = findViewById(R.id.layers_button);


        bottomListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomAreaListFragment.show(getSupportFragmentManager(), "BottomList");
            }
        });

        clearBoundariesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEntireMap();
                clearBoundariesFab.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddAreaDialog();
            }
        });

        layersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLayersDialog();
            }
        });

        FloatingActionButton dropPinFab = findViewById(R.id.drop_pin_button);
        dropPinFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

// Use the map click location to create a Point object
                Point mapTargetPoint = Point.fromLngLat(mapboxMap.getCameraPosition().target.getLongitude(),
                        mapboxMap.getCameraPosition().target.getLatitude());

                addPoint(mapTargetPoint);


            }
        });
    }

    public void addPoints(List<Point> pointlist) {
        clearEntireMap();
        for (Point point : pointlist) {
            addPoint(point);
        }
        setCameraPosition(new LatLng(pointlist.get(0).latitude(), pointlist.get(0).longitude()));

        saveButton.setVisibility(View.INVISIBLE);
    }


    /**
     * Add the click point to the map layer and update the display of the layer data
     */
    private void addPoint(Point mapTargetPoint) {

        // Make note of the first map click location so that it can be used to create a closed polygon later on
        if (circleLayerFeatureList.size() == 0) {
            firstPointOfPolygon = mapTargetPoint;
        }

// Add the click point to the circle layer and update the display of the circle layer data
        circleLayerFeatureList.add(Feature.fromGeometry(mapTargetPoint));
        if (circleSource != null) {
            circleSource.setGeoJson(FeatureCollection.fromFeatures(circleLayerFeatureList));
        }

// Add the click point to the line layer and update the display of the line layer data
        if (circleLayerFeatureList.size() < 3) {
            lineLayerPointList.add(mapTargetPoint);
        } else if (circleLayerFeatureList.size() == 3) {
            lineLayerPointList.add(mapTargetPoint);
            lineLayerPointList.add(firstPointOfPolygon);
        } else {
            lineLayerPointList.remove(circleLayerFeatureList.size() - 1);
            lineLayerPointList.add(mapTargetPoint);
            lineLayerPointList.add(firstPointOfPolygon);
        }
        if (lineSource != null) {
            lineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]
                    {Feature.fromGeometry(LineString.fromLngLats(lineLayerPointList))}));
        }

// Add the click point to the fill layer and update the display of the fill layer data
        if (circleLayerFeatureList.size() < 3) {
            fillLayerPointList.add(mapTargetPoint);
        } else if (circleLayerFeatureList.size() == 3) {
            fillLayerPointList.add(mapTargetPoint);
            fillLayerPointList.add(firstPointOfPolygon);
        } else {
            fillLayerPointList.remove(fillLayerPointList.size() - 1);
            fillLayerPointList.add(mapTargetPoint);
            fillLayerPointList.add(firstPointOfPolygon);
        }
        listOfList = new ArrayList<>();
        listOfList.add(fillLayerPointList);
        List<Feature> finalFeatureList = new ArrayList<>();
        finalFeatureList.add(Feature.fromGeometry(Polygon.fromLngLats(listOfList)));
        FeatureCollection newFeatureCollection = FeatureCollection.fromFeatures(finalFeatureList);
        if (fillSource != null) {
            fillSource.setGeoJson(newFeatureCollection);
        }


        areaText.setText("Area: " + String.format("%.2f", areaCalculator.calculateArea(Polygon.fromLngLats(listOfList))) + " m\u00B2");
        perimeterText.setText("Perimeter: " + String.format("%.2f", areaCalculator.calculatePerimeter(Polygon.fromLngLats(listOfList))) + " m");
        if (listOfList.size() != 0) {
            clearBoundariesFab.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Remove the drawn area from the map by resetting the FeatureCollections used by the layers' sources
     */
    private void clearEntireMap() {
        areaText.setText("");
        perimeterText.setText("");
        clearBoundariesFab.setVisibility(View.INVISIBLE);

        fillLayerPointList = new ArrayList<>();
        circleLayerFeatureList = new ArrayList<>();
        lineLayerPointList = new ArrayList<>();
        if (circleSource != null) {
            circleSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{}));
        }
        if (lineSource != null) {
            lineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{}));
        }
        if (fillSource != null) {
            fillSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{}));
        }
    }

    /**
     * Set up the CircleLayer source for showing map click points
     */
    private GeoJsonSource initCircleSource(@NonNull Style loadedMapStyle) {
        FeatureCollection circleFeatureCollection = FeatureCollection.fromFeatures(new Feature[]{});
        GeoJsonSource circleGeoJsonSource = new GeoJsonSource("CIRCLE_SOURCE_ID", circleFeatureCollection);
        loadedMapStyle.addSource(circleGeoJsonSource);
        return circleGeoJsonSource;
    }

    /**
     * Set up the CircleLayer for showing polygon click points
     */
    private void initCircleLayer(@NonNull Style loadedMapStyle) {
        CircleLayer circleLayer = new CircleLayer("CIRCLE_LAYER_ID",
                "CIRCLE_SOURCE_ID");
        circleLayer.setProperties(
                circleRadius(7f),
                circleColor(Color.parseColor("#d004d3"))
        );
        loadedMapStyle.addLayer(circleLayer);
    }

    /**
     * Set up the FillLayer source for showing map click points
     */
    private GeoJsonSource initFillSource(@NonNull Style loadedMapStyle) {
        FeatureCollection fillFeatureCollection = FeatureCollection.fromFeatures(new Feature[]{});
        GeoJsonSource fillGeoJsonSource = new GeoJsonSource("FILL_SOURCE_ID", fillFeatureCollection);
        loadedMapStyle.addSource(fillGeoJsonSource);
        return fillGeoJsonSource;
    }

    /**
     * Set up the BuildingLayer source for showing map click points
     */
    private GeoJsonSource initBuildingSource(@NonNull Style loadedMapStyle) {
        FeatureCollection fillFeatureCollection = FeatureCollection.fromFeatures(new Feature[]{});
        GeoJsonSource fillGeoJsonSource = new GeoJsonSource("BUILDING_SOURCE_ID", fillFeatureCollection);
        loadedMapStyle.addSource(fillGeoJsonSource);
        return fillGeoJsonSource;
    }


    /**
     * Set up the FillLayer for showing the set boundaries' polygons
     */
    private void initFillLayer(@NonNull Style loadedMapStyle) {
        fillLayer = new FillLayer("FILL_LAYER_ID",
                "FILL_SOURCE_ID");
        fillLayer.setProperties(
                fillOpacity(.6f),
                fillColor(Color.parseColor("#00e9ff"))
        );
        loadedMapStyle.addLayerBelow(fillLayer, "LINE_LAYER_ID");
    }


    /**
     * Set up the BuildingLayer for showing the set boundaries' polygons
     */
    private void initBuildingLayer(@NonNull Style loadedMapStyle) {
        buildingLayer = new FillLayer("BUILDING_LAYER_ID",
                "BUILDING_SOURCE_ID");
        buildingLayer.setProperties(fillColor(Color.parseColor("#8A8ACB"))
        );
        loadedMapStyle.addLayer(buildingLayer);
    }

    /**
     * Set up the LineLayer source for showing map click points
     */
    private GeoJsonSource initLineSource(@NonNull Style loadedMapStyle) {
        FeatureCollection lineFeatureCollection = FeatureCollection.fromFeatures(new Feature[]{});
        GeoJsonSource lineGeoJsonSource = new GeoJsonSource("LINE_SOURCE_ID", lineFeatureCollection);
        loadedMapStyle.addSource(lineGeoJsonSource);
        return lineGeoJsonSource;
    }

    /**
     * Set up the LineLayer for showing the set boundaries' polygons
     */
    private void initLineLayer(@NonNull Style loadedMapStyle) {
        LineLayer lineLayer = new LineLayer("LINE_LAYER_ID",
                "LINE_SOURCE_ID");
        lineLayer.setProperties(
                lineColor(Color.WHITE),
                lineWidth(5f)
        );
        loadedMapStyle.addLayerBelow(lineLayer, "CIRCLE_LAYER_ID");
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

    void setCameraPosition(LatLng cameraPosition) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 16.0));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {

            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                        ((Point) selectedCarmenFeature.geometry()).longitude()))
                                .zoom(14)
                                .build()), 4000);


            }
        }
    }


    /**
     * Set up the Add Area Dialog
     */
    private void openAddAreaDialog() {
        addAreaDialog.setContentView(R.layout.add_area_modal);
        addAreaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addAreaDialog.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAreaDialog.dismiss();
            }
        });
        addAreaDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAreaDialog.dismiss();
            }
        });


        addAreaDialog.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputEditText areaNameLayout = addAreaDialog.findViewById(R.id.area_name_edittext);
                TextInputEditText areaDescLayout = addAreaDialog.findViewById(R.id.area_description_edittext);

                AreaModel areaModel = new AreaModel(areaNameLayout.getText().toString().trim(),
                        areaDescLayout.getText().toString().trim(),
                        Polygon.fromLngLats(listOfList).toJson());


                db.createArea(areaModel);
                addAreaDialog.dismiss();
            }
        });


        addAreaDialog.show();

    }


    /**
     * Set up the Update Area Dialog
     */
    public void openUpdateAreaDialog(AreaModel areaModel) {
        updateAreaDialog.setContentView(R.layout.add_area_modal);
        updateAreaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView modaltitle = updateAreaDialog.findViewById(R.id.modal_title);
        modaltitle.setText("Alan Güncelle");

        updateAreaDialog.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAreaDialog.dismiss();
            }
        });
        updateAreaDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAreaDialog.dismiss();
            }
        });

        TextInputEditText areaNameEdittext = updateAreaDialog.findViewById(R.id.area_name_edittext);
        TextInputEditText areaDescEdittext = updateAreaDialog.findViewById(R.id.area_description_edittext);

        areaNameEdittext.setText(areaModel.getTitle());
        areaDescEdittext.setText(areaModel.getDescription());


        Button add_button = updateAreaDialog.findViewById(R.id.add_button);
        add_button.setText("Güncelle");
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AreaModel newAreaModel = new AreaModel(areaModel.getAreaId(),
                        areaNameEdittext.getText().toString().trim(),
                        areaDescEdittext.getText().toString().trim(),
                        Polygon.fromLngLats(listOfList).toJson());


                db.updateArea(newAreaModel);
                updateAreaDialog.dismiss();
            }
        });


        updateAreaDialog.show();

    }


    /**
     * onMapClick to select building
     */
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                final PointF finalPoint = mapboxMap.getProjection().toScreenLocation(point);
                List<Feature> features = mapboxMap.queryRenderedFeatures(finalPoint, "building");
                if (features.size() > 0) {
                    GeoJsonSource selectedBuildingSource = style.getSourceAs("BUILDING_SOURCE_ID");
                    if (selectedBuildingSource != null) {
                        selectedBuildingSource.setGeoJson(FeatureCollection.fromFeatures(features));
                    }
                    areaText.setText("Area: " + String.format("%.2f", areaCalculator.calculateArea(Polygon.fromJson(features.get(0).geometry().toJson()))) + " m\u00B2");
                    perimeterText.setText("Perimeter: " + String.format("%.2f", areaCalculator.calculatePerimeter(Polygon.fromJson(features.get(0).geometry().toJson()))) + " m");

                }
            }
        });
        return true;
    }


    /**
     * Set up the Layers Dialog
     */
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
                clearEntireMap();
            }
        });

        layersDialog.findViewById(R.id.satelliteCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxStyle = Style.SATELLITE_STREETS;
                onMapReady(mapboxMap);
                layersDialog.dismiss();
                clearEntireMap();
            }
        });

        layersDialog.findViewById(R.id.trafficCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxStyle = Style.TRAFFIC_DAY;
                onMapReady(mapboxMap);
                layersDialog.dismiss();
                clearEntireMap();
            }
        });

        layersDialog.findViewById(R.id.terrainCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxStyle = Style.SATELLITE;
                onMapReady(mapboxMap);
                layersDialog.dismiss();
                clearEntireMap();
            }
        });
        //#layer buttons


        layersDialog.show();

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
                .build(CalculateAreaActivity.this);
        startActivityForResult(intent, 1);
    }


}
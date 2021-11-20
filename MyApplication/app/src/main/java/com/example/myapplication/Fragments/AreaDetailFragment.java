package com.example.myapplication.Fragments;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;

        import com.example.myapplication.CalculateAreaActivity;
        import com.example.myapplication.Helpers.CalculatorHelper;
        import com.example.myapplication.MainActivity;
        import com.example.myapplication.Models.AreaModel;
        import com.example.myapplication.R;
        import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
        import com.google.android.material.textview.MaterialTextView;
        import com.mapbox.geojson.Polygon;


public class AreaDetailFragment extends BottomSheetDialogFragment {

    private AreaModel areaModel = new AreaModel();


    private MaterialTextView titleText;
    private MaterialTextView descriptionText;
    private ImageView backBtn;
    private ImageView deleteBtn;
    private ImageView editBtn;

    private TextView areaText;
    private TextView perimeterText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_marker_detail_modal, container, false);

        titleText = view.findViewById(R.id.marker_title);
        descriptionText = view.findViewById(R.id.marker_description);
        backBtn = view.findViewById(R.id.arrow_back_btn);
        deleteBtn = view.findViewById(R.id.delete_btn);
        editBtn = view.findViewById(R.id.edit_btn);
        areaText = view.findViewById(R.id.area_value);
        perimeterText = view.findViewById(R.id.perimeter_value);

        initState();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalculateAreaActivity) getContext()).areaDetailFragment.dismiss();
                ((CalculateAreaActivity) getContext()).bottomAreaListFragment.show(((CalculateAreaActivity) getContext()).getSupportFragmentManager(), "BottomList");
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
                ((CalculateAreaActivity) getContext()).areaDetailFragment.dismiss();
                ((CalculateAreaActivity) getContext()).openUpdateAreaDialog(areaModel);
            }
        });


        return view;
    }


    public void initState() {
        CalculatorHelper areaCalculator = new CalculatorHelper();
        titleText.setText(areaModel.getTitle());
        descriptionText.setText(areaModel.getDescription());
        areaText.setText("Area: " + String.format("%.2f", areaCalculator.calculateArea(Polygon.fromJson(areaModel.getCoordinates()))) + " m\u00B2");
        perimeterText.setText("Perimeter: " + String.format("%.2f", areaCalculator.calculatePerimeter(Polygon.fromJson(areaModel.getCoordinates()))) + " m");

    }

    public AreaModel getAreaModel() {
        return areaModel;
    }

    public void setAreaModel(AreaModel areaModel) {
        this.areaModel = areaModel;
    }


    private void getAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Alert");

        builder.setMessage("Are you sure want to delete?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((CalculateAreaActivity) getContext()).db.deleteArea(areaModel.getAreaId());
                ((CalculateAreaActivity) getContext()).areaDetailFragment.dismiss();
                ((CalculateAreaActivity) getContext()).bottomAreaListFragment.show(((CalculateAreaActivity) getContext()).getSupportFragmentManager(), "BottomList");

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



package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class BottomListFragment extends BottomSheetDialogFragment {

    private int typeFilter = 0;
    private String searchFilter = "";

    public BottomListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_list_modal, container, false);

        DatabaseHelper db = new DatabaseHelper(getContext());

        RecyclerView recyclerView;
        RecyclerAdapter recyclerAdapter;

        List<MarkerModel> markerList = db.getAllMarkers();



        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(markerList, getContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        MaterialButtonToggleGroup markerTypeButtonGroup = view.findViewById(R.id.marker_type_button_group);
        markerTypeButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.all_type_btn:
                            typeFilter = 0;
                            break;
                        case R.id.home_type_btn:
                            typeFilter = R.drawable.ic_home;
                            break;
                        case R.id.business_type_btn:
                            typeFilter = R.drawable.ic_business;
                            break;
                        case R.id.school_type_btn:
                            typeFilter = R.drawable.ic_school;
                            break;
                        case R.id.default_type_btn:
                            typeFilter = R.drawable.ic_location_on;
                            break;
                        default:

                            break;
                    }
                    recyclerAdapter.getFilter().filter("{\"type\":" + typeFilter + ",\"query\": \""+ searchFilter+"\" }");
                }
            }
        });


        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFilter = newText;
                recyclerAdapter.getFilter().filter("{\"type\":" + typeFilter + ",\"query\": \""+ searchFilter+"\" }");
                return false;
            }
        });


        return view;
    }
}

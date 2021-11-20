package com.example.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.AreaRecyclerAdapter;
import com.example.myapplication.Helpers.DatabaseHelper;
import com.example.myapplication.Models.AreaModel;
import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;
import java.util.Objects;

public class BottomAreaListFragment extends BottomSheetDialogFragment {

    private final Context context;

    public BottomAreaListFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_area_list_modal, container, false);

        DatabaseHelper db = new DatabaseHelper(context);

        RecyclerView recyclerView;
        AreaRecyclerAdapter recyclerAdapter;

        List<AreaModel> areaList = db.getAllAreas();




        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerAdapter = new AreaRecyclerAdapter(areaList, getContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);




        return view;
    }
}

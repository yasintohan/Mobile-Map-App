package com.example.myapplication.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.myapplication.CalculateAreaActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.navigation.NavigationView;

public class NavigationHelper implements NavigationView.OnNavigationItemSelectedListener{

    private final Context context;

    public NavigationHelper(Context context) {
        this.context = context;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.main_menu_item:
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case R.id.calculate_area_item:
                intent = new Intent(context, CalculateAreaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
        }

        return false;
    }

}

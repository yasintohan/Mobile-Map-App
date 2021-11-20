package com.example.myapplication.Models;

public class AreaModel {

    private int areaId;
    private String Title;
    private String Description;
    private String Coordinates;

    public AreaModel() {
    }

    public AreaModel(String title, String description, String coordinates) {
        Title = title;
        Description = description;
        Coordinates = coordinates;
    }


    public AreaModel(int areaId, String title, String description, String coordinates) {
        this.areaId = areaId;
        Title = title;
        Description = description;
        Coordinates = coordinates;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(String coordinates) {
        Coordinates = coordinates;
    }
}



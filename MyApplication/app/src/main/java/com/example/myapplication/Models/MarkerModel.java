package com.example.myapplication.Models;

public class MarkerModel {


    private int markerId;
    private String Title;
    private String Description;
    private double Longitude;
    private double Latidude;
    private int Icon;
    private int Color;

    public MarkerModel() {

    }

    public MarkerModel(String title, String description, double longitude, double latidude, int icon, int color) {
        Title = title;
        Description = description;
        Longitude = longitude;
        Latidude = latidude;
        Icon = icon;
        Color = color;
    }

    public MarkerModel(int id, String title, String description, double longitude, double latidude, int icon, int color) {
        markerId = id;
        Title = title;
        Description = description;
        Longitude = longitude;
        Latidude = latidude;
        Icon = icon;
        Color = color;
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

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatidude() {
        return Latidude;
    }

    public void setLatidude(double latidude) {
        Latidude = latidude;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public int getMarkerId() {
        return markerId;
    }

    public void setMarkerId(int markerId) {
        this.markerId = markerId;
    }
}

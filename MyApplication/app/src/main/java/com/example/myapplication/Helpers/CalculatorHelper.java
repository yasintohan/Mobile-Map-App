package com.example.myapplication.Helpers;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.List;

public class CalculatorHelper {

    public double calculateArea(Polygon polygon) {
        List<Point>  list = polygon.coordinates().get(0);

        return ComputeSignedArea(list);
    }


    public double calculatePerimeter(Polygon polygon) {
        List<Point>  list = polygon.coordinates().get(0);

        Point firstPoint = list.get(0);
        double distance = 0;

        for(int i = 1; i < list.size(); i++) {
            if(i == list.size()-1) {
                distance += distance(firstPoint.latitude(), list.get(i).latitude(), firstPoint.longitude(), list.get(i).longitude());
            } else {
                distance += distance(list.get(i-1).latitude(), list.get(i).latitude(), list.get(i-1).longitude(), list.get(i).longitude());
            }

        }

        return distance/1000;
    }


    private static double distance(double lat1, double lat2, double lon1, double lon2) {

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c * 1000; // convert to meters
    }




    static double EARTH_RADIUS = 6371009;

    static double ToRadians(double input)
    {
        return input / 180.0 * Math.PI;
    }

    private static double ComputeSignedArea(List<Point> path)
    {
        return ComputeSignedArea(path, EARTH_RADIUS);
    }

    private static double ComputeSignedArea(List<Point> path, double radius)
    {
        int size = path.size();
        if (size < 3) { return 0; }
        double total = 0;
        Point prev = path.get(size-1);
        double prevTanLat = Math.tan((Math.PI / 2 - ToRadians(prev.latitude())) / 2);
        double prevLng = ToRadians(prev.longitude());

        for (Point point : path)
        {
            double tanLat = Math.tan((Math.PI / 2 - ToRadians(point.latitude())) / 2);
            double lng = ToRadians(point.longitude());
            total += PolarTriangleArea(tanLat, lng, prevTanLat, prevLng);
            prevTanLat = tanLat;
            prevLng = lng;
        }
        return Math.abs(total * (radius * radius));
    }

    private static double PolarTriangleArea(double tan1, double lng1, double tan2, double lng2)
    {
        double deltaLng = lng1 - lng2;
        double t = tan1 * tan2;
        return 2 * Math.atan2(t * Math.sin(deltaLng), 1 + t * Math.cos(deltaLng));
    }




}

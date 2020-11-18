package com.leon.estimate.Utils.GIS;

import android.graphics.Color;

import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

public class MyKmlStyle implements KmlFeature.Styler {
    public static int color = 1;

    @Override
    public void onFeature(Overlay overlay, KmlFeature kmlFeature) {
    }

    @Override
    public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
    }

    @Override
    public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
    }

    @Override
    public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
        if (color == 1) {
            polygon.setStrokeColor(Color.parseColor("#4CAF50"));
            polygon.setStrokeWidth(2);
        } else if (color == 2) {
            polygon.setStrokeColor(Color.parseColor("#1E8CAB"));
            polygon.setStrokeWidth(5);
        } else if (color == 3) {
            polygon.setStrokeColor(Color.parseColor("#0C374D"));
            polygon.setStrokeWidth(10);
        } else if (color == 4) {
            polygon.setStrokeColor(Color.parseColor("#FF5722"));
            polygon.setStrokeWidth(5);
        }
    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {
        polyline.setColor(Color.parseColor("#4CAF50"));
    }
}
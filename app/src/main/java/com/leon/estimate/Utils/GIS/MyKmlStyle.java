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
        if (color == 1)
            polyline.setColor(Color.parseColor("#4CAF50"));
        else if (color == 2)
            polyline.setColor(Color.parseColor("#1E8CAB"));
        else if (color == 3)
            polyline.setColor(Color.parseColor("#E91E63"));
        else if (color == 2)
            polyline.setColor(Color.parseColor("#000000"));
    }

    @Override
    public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
//        try {
//            String styleString = kmlPlacemark.getExtendedData("style");
//            JSONObject o = new JSONObject(styleString);
//            if (o.getBoolean("stroke")) {
//                String colorHash = "#" + Integer.toHexString((int) (o.getDouble("opacity") * 255)) + o.getString("color").replace("#", "");
//                polygon.setStrokeColor(Color.parseColor(colorHash));
//                polygon.setStrokeWidth((float) o.getDouble("weight"));
//            }
//            if (o.getBoolean("fill")) {
//                String colorHash = "#" + Integer.toHexString((int) (o.getDouble("fillOpacity") * 255)) + o.getString("color").replace("#", "");
//                polygon.setFillColor(Color.parseColor(colorHash));
//            }
//        } catch (Exception e) {
//        }
        polygon.setFillColor(Color.parseColor("#E91E63"));
    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {
        polyline.setColor(Color.parseColor("#4CAF50"));
    }
}
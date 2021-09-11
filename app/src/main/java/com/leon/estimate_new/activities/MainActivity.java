package com.leon.estimate_new.activities;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.leon.estimate_new.BuildConfig;
import com.leon.estimate_new.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupMap();
    }

    void setupMap() {
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);
//        ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 32.7030911, 51.7135289, 13);
//        ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 47.495052, -121.786863, 16);
//        binding.mapView.setMap(map);
        binding.mapView.setMap(new ArcGISMap(BasemapStyle.ARCGIS_STREETS));
        binding.mapView.setViewpoint(new Viewpoint(32.7030911, 51.7135289, 72000.0));

//        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(
//                getExternalFilesDir(null) + "/Aurora_CO_shp");
        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(
                Environment.getExternalStorageDirectory() + "/Aurora_CO.shp");

        // create a feature layer to display the shapefile
        FeatureLayer shapefileFeatureLayer = new FeatureLayer(shapefileFeatureTable);
        // add the feature layer to the map
        binding.mapView.getMap().getOperationalLayers().add(shapefileFeatureLayer);
        shapefileFeatureTable.addDoneLoadingListener(() -> {
            if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                // zoom the map to the extent of the shapefile
                binding.mapView.setViewpointAsync(new Viewpoint(shapefileFeatureLayer.getFullExtent()));
            } else {
                String error = "Shapefile feature table failed to load: " + shapefileFeatureTable.getLoadError().toString();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                Log.e("reading", error);
            }
        });
    }
}
package com.leon.estimate.Activities;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.osmdroid.views.MapController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.division;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_RIGHT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textJustify;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textRadialOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textVariableAnchor;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private static final String GEOJSON_SRC_ID = "poi_source_id";
    private MapView mapView;
    private static final String POI_LABELS_LAYER_ID = "poi_labels_layer_id";
    private MapController mMapController;
    private List<Point> routeCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiaWFtYWxpcm9zdGFtaSIsImEiOiJjanhjbmptcmowMjZnM3BvdnY0YWx4ampxIn0.iv9I6s34q_-k9GqCiz2seg");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
                new Style.OnStyleLoaded() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                        GeoJsonSource source = new GeoJsonSource(GEOJSON_SRC_ID);
                        source.setUrl("asset://poi_places.geojson");

                        mapboxMap.setStyle(new Style.Builder().fromUrl(Style.LIGHT)
                                        .withSource(source)
// Adds a SymbolLayer to display POI labels
                                        .withLayer(new SymbolLayer(POI_LABELS_LAYER_ID, GEOJSON_SRC_ID)
                                                .withProperties(
                                                        textField(get("description")),
                                                        textSize(17f),
                                                        textColor(Color.RED),
                                                        textVariableAnchor(
                                                                new String[]{TEXT_ANCHOR_TOP, TEXT_ANCHOR_BOTTOM, TEXT_ANCHOR_LEFT, TEXT_ANCHOR_RIGHT}),
                                                        textJustify(TEXT_JUSTIFY_AUTO),
                                                        textRadialOffset(0.5f))),
                                new Style.OnStyleLoaded() {
                                    @Override
                                    public void onStyleLoaded(@NonNull final Style style) {
                                        Toast.makeText(MainActivity.this,
                                                getString(R.string.zoom_map_in_and_out_instruction),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addClusteredGeoJsonSource(@NonNull Style loadedMapStyle) {
        // Add a new source from the GeoJSON data and set the 'cluster' option to true.
        try {
            loadedMapStyle.addSource(
                    // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
                    // 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                    new GeoJsonSource("earthquakes",
                            new URL("https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"),
                            new GeoJsonOptions()
                                    .withCluster(true)
                                    .withClusterMaxZoom(14)
                                    .withClusterRadius(50)
                    )
            );
        } catch (MalformedURLException malformedUrlException) {
            Log.e("Check the URL %s", Objects.requireNonNull(malformedUrlException.getMessage()));
        }
        // Use the earthquakes GeoJSON source to create three layers: One layer for each cluster category.
        // Each point range gets a different fill color.
        int[][] layers = new int[][]{
                new int[]{2, getResources().getColor(R.color.mapbox_red)},
                new int[]{3, getResources().getColor(R.color.mapbox_green)},
                new int[]{4, getResources().getColor(R.color.mapbox_blue)}
        };
        //Creating a marker layer for single data points
        SymbolLayer unclustered = new SymbolLayer("unclustered-points", "earthquakes");
        unclustered.setProperties(
                iconImage("cross-icon-id"),
                iconSize(
                        division(
                                get("mag"), literal(4.0f)
                        )
                ),
                iconColor(
                        interpolate(exponential(1), get("mag"),
                                stop(2.0, rgb(0, 255, 0)),
                                stop(4.5, rgb(0, 0, 255)),
                                stop(7.0, rgb(255, 0, 0))
                        )
                )
        );
        loadedMapStyle.addLayer(unclustered);
        for (int i = 0; i < layers.length; i++) {
            //Add clusters' circles
            CircleLayer circles = new CircleLayer("cluster-" + i, "earthquakes");
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(18f)
            );
            Expression pointCount = toNumber(get("point_count"));
            // Add a filter to the cluster layer that hides the circles based on "point_count"
            circles.setFilter(
                    i == 0
                            ? all(has("point_count"),
                            gte(pointCount, literal(layers[i][0]))
                    ) : all(has("point_count"),
                            gte(pointCount, literal(layers[i][0])),
                            lt(pointCount, literal(layers[i - 1][0]))
                    )
            );
            loadedMapStyle.addLayer(circles);
        }
        //Add the count labels
        SymbolLayer count = new SymbolLayer("count", "earthquakes");
        count.setProperties(
                textField(Expression.toString(get("point_count"))),
                textSize(12f),
                textColor(Color.WHITE),
                textIgnorePlacement(true),
                textAllowOverlap(true)
        );
        loadedMapStyle.addLayer(count);
    }

    private void initRouteCoordinates() {
// Create a list to store our line coordinates.
        routeCoordinates = new ArrayList<>();
//        routeCoordinates.add(Point.fromLngLat(-118.39439114221236, 33.397676454651766));
//        routeCoordinates.add(Point.fromLngLat(-118.39421054012902, 33.39769799454838));
//        routeCoordinates.add(Point.fromLngLat(-118.39408583869053, 33.39761901490136));
//        routeCoordinates.add(Point.fromLngLat(-118.39388373635917, 33.397328225582285));
//        routeCoordinates.add(Point.fromLngLat(-118.39372033447427, 33.39728514560042));
//        routeCoordinates.add(Point.fromLngLat(-118.3930882271826, 33.39756875508861));
//        routeCoordinates.add(Point.fromLngLat(-118.3928216241072, 33.39759029501192));
//        routeCoordinates.add(Point.fromLngLat(-118.39227981785722, 33.397234885594564));
//        routeCoordinates.add(Point.fromLngLat(-118.392021814881, 33.397005125197666));
//        routeCoordinates.add(Point.fromLngLat(-118.39090810203379, 33.396814854409186));
//        routeCoordinates.add(Point.fromLngLat(-118.39040499623022, 33.39696563506828));
//        routeCoordinates.add(Point.fromLngLat(-118.39005669221234, 33.39703025527067));
//        routeCoordinates.add(Point.fromLngLat(-118.38953208616074, 33.39691896489222));
//        routeCoordinates.add(Point.fromLngLat(-118.38906338075398, 33.39695127501678));
//        routeCoordinates.add(Point.fromLngLat(-118.38891287901787, 33.39686511465794));
//        routeCoordinates.add(Point.fromLngLat(-118.38898167981154, 33.39671074380141));
//        routeCoordinates.add(Point.fromLngLat(-118.38984598978178, 33.396064537239404));
//        routeCoordinates.add(Point.fromLngLat(-118.38983738968255, 33.39582400356976));
//        routeCoordinates.add(Point.fromLngLat(-118.38955358640874, 33.3955978295119));
//        routeCoordinates.add(Point.fromLngLat(-118.389041880506, 33.39578092284221));
//        routeCoordinates.add(Point.fromLngLat(-118.38872797688494, 33.3957916930261));
//        routeCoordinates.add(Point.fromLngLat(-118.38817327048618, 33.39561218978703));
//        routeCoordinates.add(Point.fromLngLat(-118.3872530598711, 33.3956265500598));
//        routeCoordinates.add(Point.fromLngLat(-118.38653065153775, 33.39592811523983));
//        routeCoordinates.add(Point.fromLngLat(-118.38638444985126, 33.39590657490452));
//        routeCoordinates.add(Point.fromLngLat(-118.38638874990086, 33.395737842093304));
//        routeCoordinates.add(Point.fromLngLat(-118.38723155962309, 33.395027006653244));
//        routeCoordinates.add(Point.fromLngLat(-118.38734766096238, 33.394441819579285));
//        routeCoordinates.add(Point.fromLngLat(-118.38785936686516, 33.39403972556368));
//        routeCoordinates.add(Point.fromLngLat(-118.3880743693453, 33.393616088784825));
//        routeCoordinates.add(Point.fromLngLat(-118.38791956755958, 33.39331092541894));
//        routeCoordinates.add(Point.fromLngLat(-118.3874852625497, 33.39333964672257));
//        routeCoordinates.add(Point.fromLngLat(-118.38686605540683, 33.39387816940854));
//        routeCoordinates.add(Point.fromLngLat(-118.38607484627983, 33.39396792286514));
//        routeCoordinates.add(Point.fromLngLat(-118.38519763616081, 33.39346171215717));
//        routeCoordinates.add(Point.fromLngLat(-118.38523203655761, 33.393196040109466));
//        routeCoordinates.add(Point.fromLngLat(-118.3849955338295, 33.393023711860515));
//        routeCoordinates.add(Point.fromLngLat(-118.38355931726203, 33.39339708930139));
//        routeCoordinates.add(Point.fromLngLat(-118.38323251349217, 33.39305243325907));
//        routeCoordinates.add(Point.fromLngLat(-118.3832583137898, 33.39244928189641));
//        routeCoordinates.add(Point.fromLngLat(-118.3848751324406, 33.39108499551671));
//        routeCoordinates.add(Point.fromLngLat(-118.38522773650804, 33.38926830725471));
//        routeCoordinates.add(Point.fromLngLat(-118.38508153482152, 33.38916777794189));
//        routeCoordinates.add(Point.fromLngLat(-118.38390332123025, 33.39012280171983));
//        routeCoordinates.add(Point.fromLngLat(-118.38318091289693, 33.38941192035707));
//        routeCoordinates.add(Point.fromLngLat(-118.38271650753981, 33.3896129783018));
//        routeCoordinates.add(Point.fromLngLat(-118.38275090793661, 33.38902416443619));
//        routeCoordinates.add(Point.fromLngLat(-118.38226930238106, 33.3889451769069));
//        routeCoordinates.add(Point.fromLngLat(-118.38258750605169, 33.388420985121336));
//        routeCoordinates.add(Point.fromLngLat(-118.38177049662707, 33.388083490107284));
//        routeCoordinates.add(Point.fromLngLat(-118.38080728551597, 33.38836353925403));
//        routeCoordinates.add(Point.fromLngLat(-118.37928506795642, 33.38717870977523));
//        routeCoordinates.add(Point.fromLngLat(-118.37898406448423, 33.3873079646849));
//        routeCoordinates.add(Point.fromLngLat(-118.37935386875012, 33.38816247841951));
//        routeCoordinates.add(Point.fromLngLat(-118.37794345248027, 33.387810620840135));
//        routeCoordinates.add(Point.fromLngLat(-118.37546662390886, 33.38847843095069));
//        routeCoordinates.add(Point.fromLngLat(-118.37091717142867, 33.39114243958559));
        routeCoordinates.add(Point.fromLngLat(12.099, -79.045));
        routeCoordinates.add(Point.fromLngLat(32.627356, 51.616501));


    }
}

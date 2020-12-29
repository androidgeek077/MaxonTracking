package app.mustansar.maxontracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    Marker marker;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    DatabaseReference AddLocation, mDatabase, mPostReference;

    ArrayList mLocationArr;
    private FirebaseAuth mAuth;
    Float distanceCal;
    LatLng childLatLng;
    Double latDouble, langDouble;
    Double homeLat=32.10355449688785;
    Double homeLong=72.66123594968568;



    TextView mLatitude, mLongitude;
    private int NOTIFICATION_ID=001;
    private String CHANNEL_ID="customer notification";
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        setContentView(R.layout.activity_maps);
        statusCheck();
//        mLatitude=findViewById(R.id.latitude);
//        mLongitude=findViewById(R.id.longitude);

        AddLocation = FirebaseDatabase.getInstance().getReference("Registeration");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        getChildCurrentLocation();



        // Add a marker in Sydney and move the camera
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(32.078665, 72.68051999999999)).title("Marker in Child locatoin"));

//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latDouble, langDouble), 15.0f));
        CircleOptions circleOptions = new CircleOptions()

                .center(new LatLng(32.0813139865, 72.6823286561))
                .fillColor(getResources().getColor(R.color.colorPrimary))
                .radius(500); // In meters

// Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);

        CircleOptions circleOptionstwo = new CircleOptions()
                .center(new LatLng(32.10355449688785, 72.66123594968568))
                .fillColor(getResources().getColor(R.color.colorPrimary))
                .radius(500); // In meters

// Get back the mutable Circle
        Circle circletwo = mMap.addCircle(circleOptionstwo);

// Get back the mutable Polygon
    }


    @Override
    public void onLocationChanged(Location location) {


//        LatLng currentLocation=new LatLng(latDouble, langDouble);
//        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Driver position"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latDouble, langDouble), 15.0f));
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private Float getChildCurrentLocation() {
        mLocationArr = new ArrayList<>();
        AddLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                marker.remove();
                for (DataSnapshot childData:dataSnapshot.getChildren()) {
//                    childData.getValue(getLocationModel);
                    latDouble = (Double) childData.child("mLatitude").getValue();
                    langDouble = (Double) childData.child("mLongitude").getValue();
                    name =  childData.child("name").getValue().toString();
                    Toast.makeText(getApplicationContext(), ""+latDouble, Toast.LENGTH_SHORT).show();

                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latDouble, langDouble)).title(name));
                }

//                distanceCal=distance(latDouble.floatValue(), langDouble.floatValue(), homeLat.floatValue(), homeLong.floatValue());
//                int km = Math.round(distanceCal / 1000);
//                int meters = Math.round(distanceCal);
//                Toast.makeText(getApplicationContext(), "Distance in Km :" +km+"Distance in meters:"+meters, Toast.LENGTH_SHORT).show();

//                if (km >= 1) {
//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this, CHANNEL_ID)
//                            .setSmallIcon(R.drawable.ic_buying_on_smartphone)
//                            .setContentTitle("child is not in safe zone")
//                            .setContentText("Alert")
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MapsActivity.this);

// notificationId is a unique int for each notification that you must define
//                    notificationManager.notify(NOTIFICATION_ID, builder.build());

//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return distanceCal;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.logout_menu, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.logout_item) {
//
//            mAuth.signOut();
//
////            startActivity(new Intent(MapsActivity.this, LoginPageActivity.class));
//
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public float distance (float lat_a, float lng_a, float lat_b, float lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        Float abc=new Float(distance * meterConversion).floatValue();

//        Toast.makeText(getApplicationContext(), ""+abc, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), "Econd Toast", Toast.LENGTH_SHORT).show();
        return abc;
    }
}


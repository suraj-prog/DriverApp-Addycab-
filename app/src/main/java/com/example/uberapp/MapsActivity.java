package com.example.uberapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.uberapp.Model.LocationHelper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.LocationCallback;
import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements RoutingListener, OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    private RequestQueue mRequestQue;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    //GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    String TAG = "Hello";
    private static final int  REQ_PERMISSION = 101;
    LatLng LatLng;

    /*
    private ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    LatLng cLocation = new LatLng(20.008864, 73.718616);
    LatLng cLocation1 = new LatLng(20.012963, 73.804281);
    LatLng cLocation2 = new LatLng(20.006754, 73.784178);
   */
    SupportMapFragment mapFragment;
    SearchView searchView;
    TextView searchView1;
    private GoogleMap mMap;
    String cutomerid="",destination;
    private boolean isLoggingOut = false;
    private Button mRideStatus,mCancelRequest, mHistory;

    private int status = 0;
    Switch workingSwitch;

    private LatLng destinationLatLng, pickupLatLng;
    private float rideDistance;

    private LinearLayout mCustinfo;
    private ImageView mCustprofileimg,imageView;
    private TextView mCustname,mCustphone,mCustomerDestination;
    //private NavigationView navigationView;
    View hview;
    private Context context;
    private TextView hEmail;
    SharedPreferences sharedPreferencesUser;
    private static final String UName = "nameKey";

    private String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer);
        NavigationView  navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
/*
        ActionBar actionBar = getSupportActionBar();

          actionBar.setCustomView(R.layout.switch_layout);
          actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);

          //actionBar.setDisplayHomeAsUpEnabled(true);
          //actionBar.setHomeButtonEnabled(true);
          //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);*/

        hview=navigationView.getHeaderView(0);
        imageView = (ImageView)hview.findViewById(R.id.icon1);
        sharedPreferencesUser=getApplicationContext().getSharedPreferences("Users",0);
        String imguri=sharedPreferencesUser.getString("images","");
        System.out.println("Shared Preference string is"+imguri);
        Glide.with(getApplication()).load(imguri).into(imageView);
        /*Glide.with(this)
                .load("profileImageUrl")
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(imageView);*/
       // Glide.with(MapsActivity.this).load(imguri).error(R.drawable.ic_person).into(imageView);
        hEmail=hview.findViewById(R.id.hemail);
       // hEmail.setText("sk@gmail.com");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personEmail = acct.getEmail();
            hEmail.setText(personEmail);
        }

          polylines = new ArrayList<>();
          mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
          SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
           mapFragment.getMapAsync(this);

           View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
           RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
           rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
           rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
           rlp.setMargins(0, 0, 30, 30 );

           FirebaseMessaging.getInstance().subscribeToTopic("Passenger");

          mRequestQue = Volley.newRequestQueue(this);
          mCustinfo= (LinearLayout) findViewById(R.id.customerInfo);
          mCustprofileimg=findViewById(R.id.customerProfileImage);
          mCustname=findViewById(R.id.customerName);
          mCustphone=findViewById(R.id.customerPhone);
          mRideStatus = (Button) findViewById(R.id.rideStatus);
          mCancelRequest=  (Button) findViewById(R.id.cancel);
          mCustomerDestination = (TextView) findViewById(R.id.customerDestination);
          //workingSwitch = (Switch) findViewById(R.id.switchForActionBar);
          // mHistory = (Button) findViewById(R.id.history);
          mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(status){
                    case 1:
                        status=2;
                        erasePolylines();
                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
                            getRouteToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("drive completed");

                        break;
                    case 2:
                        recordRide();
                        endRide();
                        break;
                }
            }
        });

          mCancelRequest.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                      cancelBooking();
              }
          });
        /*  workingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (isChecked)
                  {
                      connectDriver();
                  }
                  else
                  {
                      disconnectingDriver();
                  }
              }
          });*/


        getCustomer();
    }

    private void cancelBooking()
    {
        mCancelRequest.setText("Cancel Request");
        erasePolylines();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(userId).child("Passenger Request");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Passenger Request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(cutomerid);
        cutomerid="";
        rideDistance = 0;

        if (pickupMarker!= null)
        {
            pickupMarker.remove();
        }
        if (pickupLocationListener != null)
        {
            databaseRefCust.removeEventListener(pickupLocationListener);
        }

        notification();
        Toast.makeText(getApplicationContext(),"Cancel Request",Toast.LENGTH_SHORT).show();


    }

    private void notification() {

        JSONObject json = new JSONObject();
        try {
            json.put("to","/topics/"+"Passenger");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title","Driver");
            notificationObj.put("body","Your Request Cancel");
            json.put("notification",notificationObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("MUR", "onResponse: ");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AIzaSyAa5JIR3iouhR0c0qjVKx0-4PVCInPK2Lw");
                    return header;
                }
            };
            mRequestQue.add(request);
        }
        catch (JSONException e)

        {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,MyFile.class);
                startActivity(intent);
            }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }*/

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.drawer_menu,menu);
         MenuItem menuItem = menu.findItem(R.id.myswitch);
         menuItem.setActionView(R.layout.switch_layout);
         workingSwitch = (Switch) menu.findItem(R.id.myswitch).getActionView().findViewById(R.id.switchForActionBar);
         workingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (isChecked)
                 {
                     connectDriver();
                 }
                 else
                 {
                     disconnectingDriver();
                 }
             }
         });

         return true;
     }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.nav_nbe:
                Intent inte = new Intent(MapsActivity.this,EventActivity.class);
                startActivity(inte);
                break;

            case R.id.nav_profile:
                Toast.makeText(getApplicationContext(), "Caleeds", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsActivity.this,MyFile.class);
                startActivity(intent);
                break;

            case R.id.history:
                Intent history = new Intent(MapsActivity.this,HistoryActivity.class);
                history.putExtra("PassengerOrDriver","Driver");
                startActivity(history);
                break;

            case R.id.btn_logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                isLoggingOut=true;
                disconnectingDriver();
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(MapsActivity.this,MainActivity.class);
                startActivity(intent1);
                break;

            case R.id.payment:
                Intent in = new Intent(MapsActivity.this,PaymentActivity.class);
                startActivity(in);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getCustomer() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(driverId).child("Passenger Request").child("PassengerRideId");
       // databaseRef.setValue(true);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    status=1;
                    cutomerid=dataSnapshot.getValue().toString();
                    getCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getCustomerInformation();

                }
                else
                {
                    endRide();
                    /*erasePolylines();
                    cutomerid="";
                    if (pickupMarker!= null)
                    {
                        pickupMarker.remove();
                    }
                    if (pickupLocationListener != null)
                    {
                        databaseRefCust.removeEventListener(pickupLocationListener);
                    }


                    mCustinfo.setVisibility(View.GONE);
                    mCustname.setText("");
                    mCustphone.setText("");
                    mCustomerDestination.setText("Destination: --");
                    mCustprofileimg.setImageResource(R.mipmap.ic_launcher_round);
*/
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    Marker pickupMarker;
    private DatabaseReference databaseRefCust;
    private ValueEventListener pickupLocationListener;
    private void getCustomerPickupLocation() {
        databaseRefCust = FirebaseDatabase.getInstance().getReference().child("Passenger Request").child(cutomerid).child("l");
        pickupLocationListener = databaseRefCust.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !cutomerid.equals(""))
                {
                    List<Object> list = (List<Object>) dataSnapshot.getValue();
                    double locationlat=0;
                    double locationlng=0;
                    if (list.get(0)!= null)
                    {
                        locationlat=Double.parseDouble(list.get(0).toString());
                    }
                    if (list.get(1) != null) {
                        locationlng = Double.parseDouble(list.get(1).toString());
                    }

                    pickupLatLng = new LatLng(locationlat,locationlng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));

                    getRouteToMarker(pickupLatLng);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getRouteToMarker(LatLng pickupLatLng) {
        if (pickupLatLng != null  && mLastLocation != null)
        {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
        }
    }
    private void  getAssignedCustomerDestination() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(driverId).child("Passenger Request");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("destination") != null) {
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else {
                        mCustomerDestination.setText("Destination:--");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if (map.get("destinationLat") != null) {
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                        System.out.println("destination1="+destinationLat);
                    }
                    if (map.get("destinationLng") != null) {
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        System.out.println("destination2="+destinationLng);
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getCustomerInformation()
    {
        mCustinfo.setVisibility(View.VISIBLE);
       DatabaseReference customerdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Passenger").child(cutomerid);
       customerdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0)
               {
                   Map<String,Object> map =(Map<String, Object>) dataSnapshot.getValue();
                   if (map.get("name")!=null)
                   {
                       mCustname.setText(map.get("name").toString());
                   }
                   if (map.get("phone")!=null)
                   {
                       mCustphone.setText(map.get("phone").toString());
                   }
                   if (map.get("profileImageUrl")!=null)
                   {
                       Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustprofileimg);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


    }

    private void endRide(){
        mRideStatus.setText("picked customer");
        erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(userId).child("Passenger Request");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Passenger Request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(cutomerid);
        cutomerid="";
        rideDistance = 0;

        if (pickupMarker!= null)
        {
            pickupMarker.remove();
        }
        if (pickupLocationListener != null)
        {
            databaseRefCust.removeEventListener(pickupLocationListener);
        }

        mCustinfo.setVisibility(View.GONE);
        mCustname.setText("");
        mCustphone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustprofileimg.setImageResource(R.mipmap.ic_launcher_round);

    }

    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Passenger").child(cutomerid).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("passenger", cutomerid);
        map.put("rating", 0);
        map.put("timestamp", getCurrentTimestamp());
        map.put("destination", destination);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("location/to/lat", destinationLatLng.latitude);
        map.put("location/to/lng", destinationLatLng.longitude);
        map.put("distance", rideDistance);
        historyRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }




    @Override
    public void onMapReady(GoogleMap googleMap)
    {   mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //mMap.setMyLocationEnabled(true);
            }
            else {
                checkLocationPermission();
            }
        }

/*
       for (int i=0;i<arrayList.size();i++)
        {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
            mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title("CAB").snippet("Available").icon(BitmapDescriptorFactory.fromResource(R.drawable.cab)));
        }*/

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations())
            {
                if(getApplicationContext()!=null)
                {

                    if(!cutomerid.equals("") && mLastLocation!=null && location != null){
                        rideDistance += mLastLocation.distanceTo(location)/1000;
                    }
                    mLastLocation = location;
                    LocationHelper helper = new LocationHelper(location.getLongitude(),location.getLatitude());
                    FirebaseDatabase.getInstance().getReference("Driver Current Location")
                            .setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(MapsActivity.this,"Location Saved",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MapsActivity.this,"Location Not  Saved",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng1);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cab));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("DriverAvailable");
                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("DriverWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    switch (cutomerid){
                        case "":
                            // geoFireWorking.removeLocation(userId);
                            GeoHash geoHash = new GeoHash(new GeoLocation(location.getLatitude(),location.getLongitude()));
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("g", geoHash.getGeoHashString());
                            updates.put("l", Arrays.asList(location.getLatitude(),location.getLongitude()));

                            refAvailable.setValue(updates,geoHash.getGeoHashString());
                            geoFireWorking.removeLocation(userId, new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));

                            break;

                        default:
                            // geoFireAvailable.removeLocation(userId);
                            geoFireAvailable.removeLocation(userId, new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });
                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }

            }

        }
    };

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      //  Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1 : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback , Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // Permission denied
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();

                }
                break;
            }
        }
    }

   /* private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }
    
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                MapsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION

        );
    }

*/
  /*  @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, (com.google.android.gms.location.LocationListener) this);

        }

    }*/





    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
       // erasePolylines();
    }

    private void erasePolylines(){
        for(Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }

    private void connectDriver()
    {
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }



    private void disconnectingDriver()
    {

      if(mFusedLocationClient != null)
      {
          mFusedLocationClient.removeLocationUpdates(locationCallback);
      }
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

    GeoFire geoFire = new GeoFire(ref);
    geoFire.removeLocation(userId);
    }


}


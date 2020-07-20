package com.example.uberapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistorySingleActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private DatabaseReference rideInformation;

    private LatLng destinationLatLng,pickupLatLng;

    private String rideId,UserId,passengerId,driveId,userDriverorPassenger;
    private TextView rideLocation;
    private TextView  rideDistance;
    private TextView dateRide;
    private TextView userName;
    private TextView userPhone;
    private ImageView userImage;

    private TextView rideCost;
    private TextView transaction;
    private Double  cost = 0.0;

    private String distance;
    private Double  Balance = 0.0;
    private Double ridePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);
        polylines = new ArrayList<>();
        mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        rideId=getIntent().getExtras().getString("rideId");
        rideLocation=(TextView) findViewById(R.id.ridelocation);
        rideDistance=(TextView)findViewById(R.id.ridedistance);
        dateRide=(TextView)findViewById(R.id.rideDate);
        userImage=(ImageView) findViewById(R.id.userimage);
        userName=(TextView)findViewById(R.id.username);
        userPhone=(TextView)findViewById(R.id.userphone);
        rideCost=(TextView)findViewById(R.id.rideprice);
        transaction=(TextView)findViewById(R.id.transaction);

        UserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rideInformation = FirebaseDatabase.getInstance().getReference().child("history").child(rideId);
        
        getRideInformation();


    }

    private void getRideInformation() {
        rideInformation.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot child :dataSnapshot.getChildren()){
                        if (child.getKey().equals("passenger"))
                        {
                            passengerId=child.getValue().toString();
                            if (!passengerId.equals(UserId))
                            {
                                userDriverorPassenger="Driver";
                                getUserInfo("Passenger",passengerId);

                            }
                        }
                       /* if (child.getKey().equals("driver"))
                        {
                            passengerId=child.getValue().toString();
                            if (!passengerId.equals(UserId))
                            {
                                userDriverorPassenger="Passenger";
                                getUserInfo("Driver",driveId);

                            }
                        }*/
                        if (child.getKey().equals("timestamp"))
                        {
                            dateRide.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }

                        if (child.getKey().equals("PassengerPaid"))
                        {
                            if (dataSnapshot.child("PassengerPaid").getValue()!=null)
                            {
                                transaction.setText("Transaction Successful");
                            }
                            else
                            {
                                transaction.setText("Transaction Unsuccessful");
                            }
                        }
                        if (child.getKey().equals("distance"))
                        {
                            distance = child.getValue().toString();
                            rideDistance.setText(distance.substring(0,Math.min(distance.length(), 5))+" Km");
                            ridePrice = Double.valueOf(distance)*0.5;
                           // Balance += ridePrice;
                            rideCost.setText("Balance:" + ridePrice);
                        }


                        if (child.getKey().equals("destination"))
                        {
                            rideLocation.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("location"))
                        {
                           // locationRide.setText(getDate(Long.valueOf(child.getValue().toString())));
                            pickupLatLng=new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()),Double.valueOf(child.child("from").child("lng").getValue().toString()));
                            destinationLatLng=new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()),Double.valueOf(child.child("to").child("lng").getValue().toString()));
                            if (destinationLatLng != new LatLng(0,0))
                            {
                                getRouteToMarker();
                            }

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo(String otheruser, String otheruserId) {
        DatabaseReference otherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otheruser).child(otheruserId);

        otherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Map<String,Object> map = (Map<String, Object>)dataSnapshot.getValue();
                    if (map.get("name")!=null)
                    {
                      userName.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null)
                    {
                        userPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("profileImageUrl")!=null)
                    {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String getDate(Long timestamp) {
        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timestamp*1000);

        String date = DateFormat.format("MM-dd-yyyy:hh:mm",calendar).toString();
        return date;
    }


    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(pickupLatLng,destinationLatLng)
                .build();
        routing.execute();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;
    }


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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int padding=(int)(width*0.2);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        mMap.animateCamera(cameraUpdate);
        mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("destination"));

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
        erasePolylines();
    }


    private void erasePolylines(){
        for(Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }
}

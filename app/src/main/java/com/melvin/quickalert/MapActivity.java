package com.melvin.quickalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    public Location myLocation;
    // public Switch locationSwitch;
    private GoogleMap gMap;
    private Location userLastKnownLocation;
    private final float DEFAULT_ZOOM = 20;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    //private ImageView alarm;
    Button btn_alert, btn_action, btn_location, btn_logout;
    Dialog alert_friends_dialog;
    Dialog share_location_dialog;


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getUid();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdataRef = firebaseDatabase.getReference().child("Emergency_Issues").child(userId);



    private static final int REQUEST_CODE = 13;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        btn_alert = findViewById(R.id.alert_friends);
        btn_location = findViewById(R.id.share_location);
        btn_action = findViewById(R.id.action1);






        //initialising alert friends dialogue
        alert_friends_dialog = new Dialog(this);
        //initialising share location dialog
        share_location_dialog = new Dialog(this);



        btn_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              getEmergencyInfo();
        }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
                alertDialog.setTitle("Share your location");
                alertDialog.setMessage("Do you want to share your live location with other users so that they can respond to your emergency alert?");
                alertDialog.setNegativeButton("No", null);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shareUserLocation();
                    }
                });
                alertDialog.show();

            }
        });
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retriveUserLocation();
            }
        });





        //Initializing the fusedLocationClient variable
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);


        //we cast our fragment into the SupportMapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);


    }

    private void logoutUser() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
        alertDialog.setTitle("Confirm Logout");
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setNegativeButton("No", null);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                startActivity(new Intent(MapActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
        alertDialog.show();




    }

    //for inflating the menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

       int id = item.getItemId();
       if (id == R.id.emergency_cases)
       {
           startActivity(new Intent(MapActivity.this, EmergencyCases.class));

       }
       else if(id == R.id.logout)
        {
            logoutUser();
        }
       else if(id == R.id.settings)
       {
           Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show();
       }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override

    public void onMapReady(@NonNull GoogleMap googleMap) {

        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);



        //requesting permission
        Dexter.withContext(MapActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //show location

                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(2000);
                        locationRequest.setFastestInterval(1000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

                        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
                        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
                        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                showlocation();

                            }
                        });


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            //location denied
                        Toast.makeText(MapActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();


                    }
                })
                .check();





    }


    @SuppressLint("MissingPermission")
    private void showlocation() {
        gMap.setMyLocationEnabled(true);

        //getting the last known location
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
        {
            @Override
            public void onComplete(@NonNull Task<Location> task)
            {
                if (task.isSuccessful())
                {
                    userLastKnownLocation= task.getResult();

                    if (userLastKnownLocation !=null) {
                        LatLng currentuserLocation = new LatLng(userLastKnownLocation.getLatitude(), userLastKnownLocation.getLongitude());
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentuserLocation, 14));

                    }

                }


            }
        });




    }
    //Fore sharing user location to the database

    @SuppressLint("MissingPermission")
    private void shareUserLocation() {
        //getting the last known location
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
        {
            @Override
            public void onComplete(@NonNull Task<Location> task)
            {
                if (task.isSuccessful())
                {
                    userLastKnownLocation= task.getResult();

                    if (userLastKnownLocation !=null) {
                        //LatLng currentuserLocation = new LatLng(userLastKnownLocation.getLatitude(), userLastKnownLocation.getLongitude());
                        //Send user location to the database
                        LocationHelpers locationHelpers = new LocationHelpers(userLastKnownLocation.getLongitude()
                                                                              ,userLastKnownLocation.getLatitude());

                        final String userID = mAuth.getUid();
                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Locations").child(userID);
                        dataRef.setValue(locationHelpers).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(MapActivity.this, "Sharing your live location...", Toast.LENGTH_SHORT).show();

                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MapActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                            }
                        });



                    }

                }


            }
        });

    }
    public void retriveUserLocation()
    {

        final String userID = mAuth.getUid();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Locations").child(userID);
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);

                    LatLng location = new LatLng(latitude,longitude);
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(location);
                    marker.title("user");
                    gMap.addMarker(marker);
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14));




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }

        });







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==13)
        {
            if (resultCode == RESULT_OK)
            {
                //user accepted allocation

                showlocation();
            }
        }
    }
//fill an emergency case im in
    private void getEmergencyInfo() {
        alert_friends_dialog.setContentView(R.layout.alert_friends_popup);
        alert_friends_dialog.getWindow().setBackgroundDrawableResource(R.color.purple_500);
        alert_friends_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //showing the popup
        alert_friends_dialog.show();
        EditText et_enterName = (EditText) alert_friends_dialog.findViewById(R.id.enter_name);
        EditText et_phoneNumber = (EditText) alert_friends_dialog.findViewById(R.id.phone);
        EditText et_natureEmergency = (EditText) alert_friends_dialog.findViewById(R.id.nature_emergency);
        Button btn_submit = (Button) alert_friends_dialog.findViewById(R.id.submit_1);




        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking if the user has left any empty spaces

                String userName = et_enterName.getText().toString().trim();
                String userNumber = et_phoneNumber.getText().toString().trim();
                String emergencyMessage = et_natureEmergency.getText().toString().trim();
                
                if (!TextUtils.isEmpty(userName)
                        && !TextUtils.isEmpty(userNumber)
                        && !TextUtils.isEmpty(emergencyMessage))
                {
                    //Uploading to firebase

                    String userId = mAuth.getUid();

                    Model model = new Model(userName,userNumber,emergencyMessage);

                    mdataRef.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                //Clear the edit texts
                                et_enterName.setText("");
                                et_phoneNumber.setText("");
                                et_natureEmergency.setText("");

                                Toast.makeText(MapActivity.this, "successfuly shared", Toast.LENGTH_SHORT).show();
                                alert_friends_dialog.dismiss();
                            }

                        }
                    });




                }

                else
                {
                    //if any field is left empty.. display the toast below

                    Toast.makeText(MapActivity.this, "Fill the above spaces for help", Toast.LENGTH_SHORT).show();


                }



            }
        });



    }



}
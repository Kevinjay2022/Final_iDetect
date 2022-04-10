package com.example.idetect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.idetect.Notify.Token;
import com.example.idetect.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentDriverMaps extends Fragment{

    LocationManager lm;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    private SearchView searchTxt;
    ImageView srcButton;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Location myLocation;
    Circle circle;
    Marker marker, startMarker;
    protected LatLng start = null;
    protected LatLng end = null;
    CameraUpdate cameraUpdate;
    boolean locationPermission = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    String shopID;

    RelativeLayout mapLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragMap = inflater.inflate(R.layout.fragment_driver_maps, container, false);
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        checkMyLocationEnabled(lm);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //request location permission.
        requestPermission();

        updateToken(FirebaseInstanceId.getInstance().getToken());
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        searchTxt = fragMap.findViewById(R.id.mapSearch);
        mapLayout = fragMap.findViewById(R.id.mapLayout);
        srcButton = fragMap.findViewById(R.id.srcButton);

        if (!gps_enabled && !network_enabled)
            buildAlertMessageNoGps();


        srcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geolocate();
            }
        });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                mMap = googleMap;
                getMyLocation();

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        String uid = marker.getSnippet();
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomDialogTheme);
                        View bottomView = LayoutInflater.from(getContext()).inflate(R.layout.visit_sheet, (LinearLayout) fragMap.findViewById(R.id.visitContainer));
                        TextView txtview = bottomView.findViewById(R.id.visitCenter);
                        TextView tvName = bottomView.findViewById(R.id.visit_name);
                        TextView tvAdd = bottomView.findViewById(R.id.visit_address);
                        TextView tvPhone = bottomView.findViewById(R.id.visit_contact);
                        ImageView profPic = bottomView.findViewById(R.id.imageProf);

                        FirebaseDatabase.getInstance().getReference("USERS").child(uid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String name = snapshot.child("name").getValue(String.class);
                                        String add = snapshot.child("address").getValue(String.class);
                                        String phone = snapshot.child("phonenum").getValue(String.class);
                                        String image = ""+ snapshot.child("image").getValue();
                                        shopID = ""+ snapshot.child("uid").getValue();
                                        tvName.setText(name);
                                        tvAdd.setText(add);
                                        tvPhone.setText(phone);
                                        if (image.isEmpty())
                                            Picasso.get().load(R.drawable.customers_logo_nav).into(profPic);
                                        else
                                            Picasso.get().load(image).into(profPic);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        txtview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent i = new Intent(getActivity(), FragmentDriverVisitShop.class);
                                i.putExtra("shopID", shopID);
                                startActivity(i);
                            }
                        });


                        bottomSheetDialog.setContentView(bottomView);
                        bottomSheetDialog.show();

                        return false;
                    }
                });

            }
        });

        requestPermission();






        return fragMap;
    }
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            locationPermission = true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
                    getMyLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()    {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.rotation(location.getBearing());
                    markerOptions.anchor((float) 0.5, (float) 0.5);
                    startMarker = mMap.addMarker(markerOptions);
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        });
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(@NonNull Location location) {
                if(circle != null)
                    circle.remove();

                if (startMarker != null)
                    startMarker.remove();

                myLocation = location;
                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(start);
                markerOptions.rotation(myLocation.getBearing());
                markerOptions.anchor((float) 0.5, (float) 0.5);
                startMarker = mMap.addMarker(markerOptions);

                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(start);
                circleOptions.radius(2000);
                circleOptions.fillColor(0x220000FF);
                circleOptions.strokeWidth(1);
                circleOptions.strokeColor(Color.RED);
                circle = mMap.addCircle(circleOptions);

                FirebaseDatabase.getInstance().getReference("USERS")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    String uid = ds.child("uid").getValue(String.class);
                                    String name = ds.child("name").getValue(String.class);
                                    String acctype = ds.child("acctype").getValue(String.class);
                                    if (acctype.equals("serve_center")) {
                                        FirebaseDatabase.getInstance().getReference("LAT_LNG").child(uid)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        double lat = Double.parseDouble("" + snapshot.child("latitude").getValue());
                                                        double lng = Double.parseDouble("" + snapshot.child("longitude").getValue());
                                                        end = new LatLng(lat, lng);

                                                        MarkerOptions markerOptions = new MarkerOptions();
                                                        markerOptions.position(end);
                                                        markerOptions.title(name +"\n"+ds.child("address").getValue(String.class));
                                                        markerOptions.snippet(uid);
                                                        mMap.addMarker(markerOptions);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Nullable
                    @Override
                    public View getInfoContents(@NonNull Marker marker) {
                        return null;
                    }

                    @Nullable
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {

                        LinearLayout info = new LinearLayout(getActivity());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getActivity());
                        title.setTextColor(Color.BLACK);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getActivity());
                        snippet.setTextColor(Color.WHITE);
                        snippet.setWidth(0);
                        snippet.setHeight(0);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
            }
        });
    }
    private void geolocate(){
        String strSearch = searchTxt.getQuery().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(strSearch, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0){
            Address address = list.get(0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 15f));
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
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
    private void checkMyLocationEnabled(LocationManager lm) {

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) { }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) { }

    }
    @Override
    public void onResume() {
        checkMyLocationEnabled(lm);
        if (gps_enabled && network_enabled) {
            mapLayout.setVisibility(View.VISIBLE);
        }else
            mapLayout.setVisibility(View.GONE);
        super.onResume();
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }
}
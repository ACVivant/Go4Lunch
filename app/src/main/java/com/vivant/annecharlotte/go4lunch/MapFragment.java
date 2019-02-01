package com.vivant.annecharlotte.go4lunch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;


    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // java.lang.RuntimeException: API key not found.  Check that <meta-data android:name="com.google.android.geo.API_KEY" android:value="your API key"/> is in the <application> element of AndroidManifest.xml
        mMapView = (MapView) mView.findViewById(R.id.map);
       /* if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }*/
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247, -74.044502)).title("Statue de la liberté").snippet("Victoire et boule de gomme"));

        CameraPosition Liberty = CameraPosition.builder().target(new LatLng(40.689247, -74.044502)).zoom(16).zoom(16).bearing(0).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
    }

}

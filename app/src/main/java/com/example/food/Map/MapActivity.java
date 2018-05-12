package com.example.food.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.example.food.R;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mgoogleMap; //儲存地圖資訊
    private LocationManager mlocationManager;//LocationManager物件
    //    private String provider;
    // private Location location;
    private Marker Store_Marker;
    private RecyclerView recyclerView;
    private List<StoreInfo> storeInfoList;
    private double latitude, longitude;
    private GoogleApiClient mgoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_map);
            initMap();
        }
        handViews();
        Toolbar mtoolbar = findViewById(R.id.map_toolbar);
        mtoolbar.setTitle(getString(R.string.textMap));
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void handViews() {
        recyclerView = findViewById(R.id.map_recycleView);
        recyclerView.setLayoutManager(new
                StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        storeInfoList = getStoreInfoList();
        recyclerView.setAdapter(new storeInfoAdapter(this,storeInfoList));
        /* 不處理捲動事件所以監聽器設為null */
        recyclerView.setOnFlingListener(null);
        /* 如果希望一次滑動一頁資料，要加上PagerSnapHelper物件 */
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

    }

    //CardView implement
    private class storeInfoAdapter extends RecyclerView.Adapter<storeInfoAdapter.mViewHolder> {
        private Context context;
        private List<StoreInfo> storeInfoList;

        storeInfoAdapter(Context context,List<StoreInfo> storeInfoList){
            this.context = context;
            this.storeInfoList = storeInfoList;
        }

        class mViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvName, tvinfo;
            mViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.map_cardview_storeImg);
                tvName = itemView.findViewById(R.id.map_cardView_storename);
                tvinfo = itemView.findViewById(R.id.map_cardView_tvstoreinfo);
            }
        }

        @NonNull
        @Override
        public mViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.map_cardview,viewGroup,false);
            return new mViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
            final StoreInfo storeInfo = storeInfoList.get(position);
            holder.imageView.setImageResource(storeInfo.getStore_img());
            holder.tvName.setText(storeInfo.getStrore_Name());
            holder.tvinfo.setText(storeInfo.getStore_simpinfo()+", id is :"+storeInfo.getStor_id());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MapActivity.this,"Click",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return storeInfoList.size();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmMap);
        mapFragment.getMapAsync(MapActivity.this);
    }

    //check googleServicesAvailable
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(MapActivity.this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            //map request可以正常使用
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "地圖請求無法使用", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    //get User DeviceLocation
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Location currentlocation = (Location) task.getResult();
                        setLocation(currentlocation.getLatitude(),currentlocation.getLongitude());
                    } else {
                        Toast.makeText(MapActivity.this,"unable to get currentLocation",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("DeviceLoc","DeviceLocation:"+e.getMessage());
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mgoogleMap = googleMap;
        mlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //判斷GPS或WIFI是否開啟
        boolean isGPSEnable = mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnable = mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGPSEnable && !isNetworkEnable) {
            Toast.makeText(this,"No GPS or WIFI Detected!",Toast.LENGTH_LONG).show();
            new AlertDialog().show(getSupportFragmentManager(),"Warm");
        }
        //check Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mgoogleMap.setMyLocationEnabled(true);
        getDeviceLocation();
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        makeMakers();
        //MakerClick(CardView ScrollToPosition)
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final LatLng markerPosition = marker.getPosition();
                int selectMarker = -1;
                for(int i =0;i< storeInfoList.size();i++) {
                    if(markerPosition.latitude == storeInfoList.get(i).getLatitude() && markerPosition.longitude == storeInfoList.get(i).getLongitude()) {
                        selectMarker = i;
                    }
                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))
                        .zoom(14)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                recyclerView.smoothScrollToPosition(selectMarker);
                return false;
            }
        });
    }

    //設定位置
    private void setLocation(double lat, double lon) {
        latitude = lat;
        longitude = lon;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(14)
                .build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mgoogleMap.animateCamera(update);
    }

    LocationRequest mLocationRequest;
    //LocationRequest
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        //Ask Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null) {
            Toast.makeText(this,"get current location failed",Toast.LENGTH_LONG).show();
        } else {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            setLocation(latitude,longitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Gps和Network失效 Dialog
    public static class AlertDialog extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.text_warm)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setMessage("Please Open GPS or WIFI")
                    .setPositiveButton("Yes",this)
                    .setNegativeButton("NO",this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    getActivity().finish();
                    break;
                default:
                    dialog.cancel();
                    break;
            }
        }
    }


    private void makeMakers() {
        List<StoreInfo> markers = getStoreInfoList();
        int height = 50;
        int width = 50;
        //Custom Marker size
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b,width,height,false);
        //MapMarker
        for(int i=0;i<markers.size();i++) {
            Store_Marker = mgoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(markers.get(i).getLatitude(),markers.get(i).getLongitude()))
                                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }

    }

    //StoreInfoList
    public List<StoreInfo> getStoreInfoList() {
        List<StoreInfo> StoreInfoList = new ArrayList<>();
        StoreInfoList.add(new StoreInfo(1,R.drawable.drinks_and_desserts,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.042685,121.539954));
        StoreInfoList.add(new StoreInfo(2,R.drawable.ice_cream,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.040197,121.535736));
        StoreInfoList.add(new StoreInfo(3,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.041201,121.531548));
        return StoreInfoList;
    }

}

package com.example.food.Map;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.food.R;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mgoogleMap; //儲存地圖資訊
    private LocationManager mlocationManager;//LocationManager物件
    private Marker Store_Marker,lastClicked,defaultMarker;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private List<StoreInfo> storeInfoList;
    private double latitude, longitude;
    private GoogleApiClient mgoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ProgressDialog mprogressDialog;
    private int overallXScrol = 0;
    private int scrollPosition = 0;
    private LatLng markerPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_map);
            initMap();
            handViews();
            Toolbar mtoolbar = findViewById(R.id.map_toolbar);
            mtoolbar.setTitle(getString(R.string.textMap));
            setSupportActionBar(mtoolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void handViews() {
        recyclerView = findViewById(R.id.map_recycleView);
        recyclerView.setLayoutManager(new
                StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        storeInfoList = getStoreInfoList();
        recyclerView.setAdapter(new storeInfoAdapter(this,storeInfoList));
        /* 不處理捲動事件所以監聽器設為null */
        recyclerView.setOnFlingListener(null);
        /* 一次滑動一頁資料*/
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE ) {
                    recyclePosition(scrollPosition,defaultMarker);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScrol = recyclerView.computeHorizontalScrollExtent();
                Log.d("overallXScrol",""+overallXScrol);
                Log.d("ScrollExtent",""+recyclerView.computeHorizontalScrollExtent());
                Log.d("ScrollOffset",""+recyclerView.computeHorizontalScrollOffset());
                Log.d("ScrollRange",""+recyclerView.computeHorizontalScrollRange());
                scrollPosition = recyclerView.computeHorizontalScrollOffset()/overallXScrol;
            }
        });

    }

    //RecyclerView implement
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
        public void onBindViewHolder(@NonNull final mViewHolder holder, final int position) {
            final StoreInfo storeInfo = storeInfoList.get(position);
            holder.imageView.setImageResource(storeInfo.getStore_img());
            holder.tvName.setText(storeInfo.getStrore_Name());
            holder.tvinfo.setText(storeInfo.getStore_simpinfo()+", id is :"+storeInfo.getStor_id());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MapActivity.this,"ID:"+position,Toast.LENGTH_SHORT).show();
                    /*
                                            send Data to 店家評價頁面
                                      */
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
      //  Toast.makeText(MapActivity.this,"Map Ready!",Toast.LENGTH_SHORT).show();
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
                    if(task.isSuccessful() && location != null) {
                        Location currentlocation = (Location) task.getResult();
                        if(currentlocation != null) {
                            setLocation(currentlocation.getLatitude(),currentlocation.getLongitude());
                            //User為圓心半徑50M
                            mgoogleMap.addCircle(new CircleOptions()
                                    .center(new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude()))
                                    .radius(50) // In meters
                                    .strokeColor(0x800000FF) //ARGB，
                                    .strokeWidth(2f)
                                    .fillColor(0x200000FF));
                        } else {
                            new AlertDialog().show(getSupportFragmentManager(),"Warm");
                        }

                    } else {
                        Toast.makeText(MapActivity.this,"unable to get currentLocation",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("DeviceLoc","DeviceLocation:"+e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DeviceLoc","DeviceLocation Latln:"+e.getMessage());
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
        //Init Markers
        makeMarkers();
        //MakerClick(CardView ScrollToPosition)
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerPosition = marker.getPosition();
                int selectMarker = -1;
                for(int i =0;i< storeInfoList.size();i++) {
                    if(markerPosition.latitude == storeInfoList.get(i).getLatitude() && markerPosition.longitude == storeInfoList.get(i).getLongitude()) {
                        selectMarker = i;
                    }
                }
                recyclerView.smoothScrollToPosition(selectMarker);
                recyclePosition(selectMarker,defaultMarker);
                return true;
            }
        });
    }

    //設定位置
    private void setLocation(double lat, double lon) {
        latitude = lat;
        longitude = lon;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
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
                    .setIcon(R.drawable.warn_icon)
                    .setMessage("Please check GPS or WIFI")
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
    //MapMarkers
    private void makeMarkers() {
        List<StoreInfo> markers = getStoreInfoList();
        Bitmap smallMarker = smallMarker();
        //MapMarker
        for(int i=0;i<markers.size();i++) {
                Store_Marker = mgoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(markers.get(i).getLatitude(),markers.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
        defaultMarker = mgoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(markers.get(0).getLatitude(),markers.get(0).getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(SelectsmallMarker())));
    }
    //Custom Marker
    private Bitmap smallMarker() {
        int height = 50;
        int width = 50;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b,width,height,false);
        return smallMarker;
    }
    //Custom Select Marker
    private Bitmap SelectsmallMarker() {
        int height = 60;
        int width = 60;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_maker_item);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap SelectsmallMarker = Bitmap.createScaledBitmap(b,width,height,false);
        return SelectsmallMarker;
    }
    //顯示Select Markers
    private void recyclePosition(int position,Marker marker){
         defaultMarker = marker;
         scrollPosition = position;
         markerPosition  = new LatLng(storeInfoList.get(scrollPosition).getLatitude(),storeInfoList.get(scrollPosition).getLongitude());
         defaultMarker.setPosition(markerPosition);
         defaultMarker.setIcon(BitmapDescriptorFactory.fromBitmap(SelectsmallMarker()));
         if(lastClicked != null) {
             lastClicked.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker()));
         }
         defaultMarker.setIcon(BitmapDescriptorFactory.fromBitmap(SelectsmallMarker()));
         lastClicked = defaultMarker;
    }

    //StoreInfoList
    public List<StoreInfo> getStoreInfoList() {
        List<StoreInfo> StoreInfoList = new ArrayList<>();
        StoreInfoList.add(new StoreInfo(0,R.drawable.drinks_and_desserts,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.042685,121.539954));
        StoreInfoList.add(new StoreInfo(1,R.drawable.ice_cream,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.040197,121.535736));
        StoreInfoList.add(new StoreInfo(2,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.041201,121.531548));
        StoreInfoList.add(new StoreInfo(3,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.04380758,121.53599093));
        StoreInfoList.add(new StoreInfo(4,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.04417695,121.53433869));
        StoreInfoList.add(new StoreInfo(5,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.04061933,121.53543303));
        StoreInfoList.add(new StoreInfo(6,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.04281612,121.53886626));
        StoreInfoList.add(new StoreInfo(7,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.04629592,121.53508562));
        StoreInfoList.add(new StoreInfo(8,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.04240787,121.53021473));
        StoreInfoList.add(new StoreInfo(9,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.04011386,121.536463));
        StoreInfoList.add(new StoreInfo(10,R.drawable.korea_food,"XXX甜點","dsfdfdsfdjfoie wfjhnvujdhhencvjvkjnjsbkjdbvghkj hvofj o",25.03844193,121.53856585));
        return StoreInfoList;
    }
}

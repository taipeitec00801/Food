package com.example.food.Map;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.Comment.CommentActivity;
import com.example.food.R;
import com.example.food.Search.SearchActivity;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mgoogleMap; //儲存地圖資訊
    private Marker lastClicked;
    private Marker defaultMarker;
    private RecyclerView recyclerView;
    private List<StoreInfo> storeInfoList;
    private List<LatLng> locationList;
    private double latitude, longitude;
    private GoogleApiClient mgoogleApiClient;
    private int overallXScrol = 0;
    private int scrollPosition = 0;
    private LatLng markerPosition;
    private IconGenerator iconFactory;
    private TextView listid;
    private ImageView selectMarker;
    private Thread t1;
    private CustomProgressDialog progressDialog;
    private Toolbar mtoolbar;
    private boolean dialogShow = false;

    @Override //Create Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_map);
            initProgressDialog();
//            t1=new Thread(r1);
//            t1.start();
            new initMarkers().execute();
            /*  接收Search結果 並執行
                        locationToMarker(String StoreAddress)*/
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       // new initMarkers().execute();
    }


    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    class initMarkers extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("runOnUiThread = " , String.valueOf(Thread.currentThread().getId()));
                    // Skipped 36 frames!  The application may be doing too much work on its main thread.
                    if(dialogShow)
                        initMap();
                }
            });
            mtoolbar = findViewById(R.id.map_toolbar);
            initToolBar();
            handViews();
            getLocationList();
            getDeviceLocation();

//            HandlerThread mThread = new HandlerThread("aa");
//            mThread.start();
//            Handler mThreadHandler = new Handler(mThread.getLooper());
//            mThreadHandler.post(r1);
            //新增Thread處理其他事件
            return null;
        }


    }

//    private Runnable r1 = new Runnable() {
//        public void run() {
//            Log.d("Thread", String.valueOf(t1.getState()));
//            //這裡放執行緒要執行的程式。
//            Log.d("r1 = " , String.valueOf(Thread.currentThread().getId()));
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("runOnUiThread = " , String.valueOf(Thread.currentThread().getId()));
//                   // Skipped 36 frames!  The application may be doing too much work on its main thread.
//                    initMap();
//                }
//            });
//            mtoolbar = findViewById(R.id.map_toolbar);
//            initToolBar();
//            handViews();
//            getLocationList();
//            getDeviceLocation();
//        }
//    };

    private void initProgressDialog() {
        progressDialog = CustomProgressDialog.createDialog(this);
        progressDialog.getWindow()
                .setLayout(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        dialogShow = true;
    }

    private void initToolBar() {
        mtoolbar.setTitle(getString(R.string.textMap));
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //SearchIcon點擊跳至搜尋頁
        mtoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent();
                switch (item.getItemId()) {
                    case R.id.mapSearchIcon:
                        intent.setClass(MapActivity.this, SearchActivity.class);
                        startActivity(intent);
                    case android.R.id.home:
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        MapActivity.this.finish();
                }
                return false;
            }
        });
    }

    @Override
    public void onMapLoaded() {
        recyclerView = findViewById(R.id.map_recycleView);
        makeMarkers();
        recyclerView.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
       // Log.d("makeMarkers = " , String.valueOf(Thread.currentThread().getId()));
      //  Log.d("Thread", String.valueOf(t1.getState()));
    }

    //點擊店家跳至店家頁面的animate
    public void animateIntent() {
        Intent intent = new Intent(MapActivity.this,CommentActivity.class);
        String transitionName = getString(R.string.map_transition_string);
        String transitionName2 = getString(R.string.map_storeInfo_name);
        View viewStart = findViewById(R.id.map_cardview_storeImg);
        View viewStart2 = findViewById(R.id.storeInfo);

        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View,String> (viewStart,transitionName);
        pairs[1] = new Pair<View,String> (viewStart2,transitionName2);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(MapActivity.this,pairs);

        startActivity(intent,options.toBundle());

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
            TextView tvName, tvAddress,businessHours,tel;
            mViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.map_cardview_storeImg);
                tvName = itemView.findViewById(R.id.map_cardView_storename);
                tvAddress = itemView.findViewById(R.id.map_cardView_storeAddress);
                businessHours = itemView.findViewById(R.id.map_cardView_businessHours);
                tel = itemView.findViewById(R.id.map_cardView_tel);
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
            holder.imageView.setImageResource(storeInfo.getStoreImg());
            holder.tvName.setText("店名: "+storeInfo.getStoreName());
            holder.tvAddress.setText("地址: "+storeInfo.getStoreAddress());
            holder.businessHours.setText("今日營業: "+storeInfo.getBusinessHours());
            holder.tel.setText("電話: "+storeInfo.getTel());
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  send Data to 店家評價頁面
                    animateIntent();
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
        mapFragment.setMenuVisibility(true);
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
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
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
        mgoogleMap.setOnMapLoadedCallback(this);
        LocationManager mlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //MakerClick(CardView ScrollToPosition)
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("Marker ID:",""+marker.getId());
                markerPosition = marker.getPosition();
                String markerId = marker.getId();
                String pos[] = markerId.split("m");
                int markert = Integer.parseInt(pos[1]);
              //  int selectMarker = -1;
              //  LatLng changeposition = null;
                if(markert >= storeInfoList.size()){
                    // Do Nothing
                } else {
                    recyclerView.smoothScrollToPosition(markert);
                    recyclePosition(markert,defaultMarker);
                }

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
        //mgoogleMap.animateCamera(update);
        mgoogleMap.moveCamera(update);
    }

    LocationRequest mLocationRequest;
    //LocationRequest
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        //Ask Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapActivity.this,"請准許使用位置",Toast.LENGTH_SHORT).show();
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
        iconFactory = new IconGenerator(MapActivity.this);
        List<StoreInfo> markers = getStoreInfoList();
        // inflate custom_marker
        LayoutInflater inflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View markerview = inflater.inflate(R.layout.custom_marker,null);
        listid = markerview.findViewById(R.id.listid);
        selectMarker = markerview.findViewById(R.id.MarkerIcon);

        for(int i=0;i<markers.size();i++) {
            String id = String.valueOf(markers.get(i).getStoreID()+1); //店家ID
            listid.setText(id);
            iconFactory.setContentView(markerview);
            iconFactory.setBackground(null);
            Marker store_Marker = mgoogleMap.addMarker(new MarkerOptions()
                    .position(locationList.get(i))
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon())));
        }
        String id = String.valueOf(markers.get(0).getStoreID()+1); //店家ID
        listid.setText(id);
        //selectMarker = markerview.findViewById(R.id.MarkerIcon);
        selectMarker.setImageResource(R.drawable.map_maker_item);
        iconFactory.setContentView(markerview);
        //default_marker
        defaultMarker = mgoogleMap.addMarker(new MarkerOptions()
                //.position(new LatLng(markers.get(0).getLatitude(),markers.get(0).getLongitude()))
                .position(locationList.get(0))
                .zIndex(1.0f)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon())));
    }

    //Custom Marker
    private Bitmap smallMarker() {
        int height = 60;
        int width = 60;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b,width,height,false);
        return smallMarker;
    }

    //Custom Select Marker
//    private Bitmap SelectsmallMarker() {
//        int height = 80;
//        int width = 80;
//        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_maker_item);
//        Bitmap b = bitmapdraw.getBitmap();
//        Bitmap SelectsmallMarker = Bitmap.createScaledBitmap(b,width,height,false);
//        return SelectsmallMarker;
//    }

    //顯示Select Markers
    private void recyclePosition(int position,Marker marker){
         iconFactory = new IconGenerator(MapActivity.this);
         LayoutInflater inflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View markerview = inflater.inflate(R.layout.custom_marker,null);
         listid = markerview.findViewById(R.id.listid);
         selectMarker = markerview.findViewById(R.id.MarkerIcon);
         String id = String.valueOf(position+1); //店家ID
         listid.setText(id);
         defaultMarker = marker;
         scrollPosition = position;
         selectMarker.setImageResource(R.drawable.map_maker_item);
         markerPosition = storeAddresstoLatlng(storeInfoList.get(scrollPosition).getStoreAddress());
         defaultMarker.setPosition(markerPosition);
         iconFactory.setContentView(markerview);
         iconFactory.setBackground(null);
         defaultMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
          if(lastClicked != null) {
             lastClicked.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker()));
         }
         defaultMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
         lastClicked = defaultMarker;
    }

    //地址轉換成座標
    private LatLng storeAddresstoLatlng(String StoreAddress) {
        Geocoder geocoder = new Geocoder(this);
        int maxResult = 1;
        LatLng position = null;
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocationName(StoreAddress,maxResult);
            Address address = addressList.get(0);
            position = new LatLng(address.getLatitude(),address.getLongitude());
        } catch (IOException e) {
            Log.e("storeAddress",e.toString());
        }
        return position;
    }

    //接收Search Result並顯示Marker
    private  void locationToMarker(String StoreAddress) {
        mgoogleMap.clear();
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        int maxResults = 1;
        try {
            addressList = geocoder.getFromLocationName(StoreAddress,maxResults);
        } catch (IOException e) {
            Log.e("locationToMarker",e.toString());
        }
        //如果addressList不為NULL 鏡頭移動到該地點
        if(addressList == null || addressList.isEmpty()) {
            Toast.makeText(this,"addressList Error",Toast.LENGTH_SHORT).show();
        }else {
            Address address = addressList.get(0);
            LatLng position = new LatLng(address.getLatitude(),address.getLongitude());
            mgoogleMap.addMarker(new MarkerOptions().position(position));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(position).zoom(15).build();
            mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public List<LatLng> getLocationList() {
        locationList = new ArrayList<>();
        storeInfoList = getStoreInfoList();
        String Address;
        for(int i=0;i<storeInfoList.size();i++) {
            Address = storeInfoList.get(i).getStoreAddress();
           // Log.d("Address","Address:"+i+"||"+storeAddresstoLatlng(Address));
            locationList.add(storeAddresstoLatlng(Address));
            locationList.add(i,storeAddresstoLatlng(Address));
           // Log.d("locationListSize","locationList:"+locationList.size());
        }
        return locationList;
    }

    //StoreInfoList
    public List<StoreInfo> getStoreInfoList() {
        List<StoreInfo> StoreInfoList = new ArrayList<>();
        StoreInfoList.add(new StoreInfo(0, R.drawable.food,"忠貞小館","台北市大安區新生南路一段103-2號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(1, R.drawable.food,"忠貞小館","台北市中正區新生南路一段12巷","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(2, R.drawable.food,"忠貞小館","台北市中正區新生南路一段12巷2號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(3, R.drawable.food,"忠貞小館","台北市中山區新生南路一段15號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(4, R.drawable.food,"忠貞小館","台北市八德路二段36號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(5, R.drawable.food,"忠貞小館","台北市大安區市民大道三段178號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(6, R.drawable.food,"忠貞小館","台北市大安區市民大道三段150巷16號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(7, R.drawable.food,"忠貞小館","台北市大安區建國南路一段65巷7號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(8, R.drawable.food,"忠貞小館","台北市大安區濟南路三段67號","11:00-22:00","02-23389881"));
        StoreInfoList.add(new StoreInfo(9, R.drawable.food,"忠貞小館","台北市大安區濟南路三段66號","11:00-22:00","02-23389881"));
//        StoreInfoList.add(new StoreInfo(10, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(11, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(12, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(13, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(14, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(15, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(16, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(17, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(18, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(19, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(20, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(21, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(22, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(23, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(24, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(25, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(26, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(27, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(28, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(29, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
//        StoreInfoList.add(new StoreInfo(30, R.drawable.drinks_and_desserts,"忠貞小館","台北市大安區濟南路三段25號"));
        return StoreInfoList;
    }
}

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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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

import com.example.food.AppModel.Store;
import com.example.food.Comment.CommentActivity;
import com.example.food.DAO.StoreDAO;
import com.example.food.DAO.task.Common;
import com.example.food.DAO.task.ImageTaskOIB;
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
import com.google.android.gms.maps.GoogleMapOptions;
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
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mgoogleMap; //儲存地圖資訊
    private Marker lastClicked;
    private Marker defaultMarker;
    private RecyclerView recyclerView;
    private List<StoreInfo> storeInfoList;
    private List<Store> storeList;
    private List<LatLng> locationList;
    private ImageTaskOIB storeImgTask;
    private double latitude, longitude;
    private Location currentlocation;
    private GoogleApiClient mgoogleApiClient;
    private int overallXScrol = 0;
    private int scrollPosition = 0;
    private LatLng markerPosition;
    private IconGenerator iconFactory;
    private TextView listid;
    private ImageView selectMarker;
    private Thread t1;
    private SupportMapFragment mapFragment;
    private CustomProgressDialog progressDialog;
    private Toolbar mtoolbar;
    private boolean dialogShow = false;
    private boolean getList = false;
    private boolean mapReady = false;

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
            initMap();
            t1=new Thread(r1);
            t1.start();
            /*  接收Search結果 並執行
                        locationToMarker(String StoreAddress)*/
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        //Detach mapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmMap);
        GoogleMapOptions options = new GoogleMapOptions();
        options.useViewLifecycleInFragment(true);
        mapFragment = SupportMapFragment.newInstance(options);
        //Check mapFragment is Null
        if(mapFragment.isVisible())
            Log.d("googleMapStatus","mapFragment isVisible()");
        else
            Log.d("googleMapStatus","mapFragment IS Null");

        MapActivity.this.finish();
        super.onDestroy();
    }

    private Runnable r1 = new Runnable() {
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getDeviceLocation();
            Log.d("Thread", String.valueOf(t1.getState()));
            //這裡放執行緒要執行的程式。
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("runOnUiThread = " , String.valueOf(Thread.currentThread().getId()));
                    mtoolbar = findViewById(R.id.map_toolbar);
                    initToolBar();
                }
            });
        }
    };

    private void initProgressDialog() {
        progressDialog = CustomProgressDialog.createDialog(this);
        progressDialog.getWindow()
                .setLayout(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
      //  dialogShow = true;
    }

    private void initToolBar() {
        mtoolbar.setTitle(getString(R.string.textMap));
        setSupportActionBar(mtoolbar);
       // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
                }
                return true;
            }
        });
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("home","click");
                mapFragment.onDetach();
                MapActivity.this.finish();
            }
        });
    }

    @Override
    public void onMapLoaded() {
        recyclerView = findViewById(R.id.map_recycleView);
        if (getList) {
            makeMarkers();
            recyclerView.setVisibility(View.VISIBLE);
        }
        progressDialog.dismiss();
    }

    //點擊店家跳至店家頁面的animate
    public void animateIntent(int storeNum) {
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

        //將資料送至商店頁
        Bundle bundle = new Bundle();
        bundle.putSerializable("store",storeList.get(storeNum));
        bundle.putAll(options.toBundle());
        intent.putExtras(bundle);
        intent.putExtras(options.toBundle());
        startActivity(intent,options.toBundle());

    }

    private void handViews() {
        recyclerView = findViewById(R.id.map_recycleView);
        recyclerView.setLayoutManager(new
                StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(new storeInfoAdapter(this,storeList));
       // Log.d("hadViews storeList",""+storeList.size());
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
        private List<Store> storeList;

        storeInfoAdapter(Context context,List<Store> storeList){
            this.context = context;
            this.storeList = storeList;
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
           // final StoreInfo storeInfo = storeInfoList.get(position);
            final Store store = storeList.get(position);
            String url = Common.URL+"/appGetImages";
            int id = store.getStoreId();
            storeImgTask = new ImageTaskOIB(url, id, holder.imageView);
            storeImgTask.execute();

           // holder.imageView.setImageResource(R.drawable.food);
            holder.tvName.setText("店名: "+store.getStoreName());
            holder.tvAddress.setText("地址: "+store.getStoreAddress());
            holder.businessHours.setText("今日營業: "+store.getServiceHours());
            holder.tel.setText("電話: "+store.getStorePhone());
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                  send Data to 店家評價頁面
                    animateIntent(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return storeList.size();
        }
    }

    private void initMap() {
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmMap);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmMap);
        mapFragment.getMapAsync(MapActivity.this);

        //mapFragment.setMenuVisibility(true);
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
                        currentlocation = (Location) task.getResult();
                        if(currentlocation != null) {
                            setLocation(currentlocation.getLatitude(),currentlocation.getLongitude());
                            getStoreList(currentlocation); //將位置傳入並和StoreList比對
                           // Log.d("currentlocation",""+currentlocation.getLatitude()+"|"+currentlocation.getLongitude());
                            //User為圓心半徑50M
                            try {
                                mgoogleMap.addCircle(new CircleOptions()
                                        .center(new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude()))
                                        .radius(10) // In meters
                                        .strokeColor(0x800000FF) //ARGB，
                                        .strokeWidth(2f)
                                        .fillColor(0x200000FF));

                            }catch (NullPointerException e) {
                                System.out.print("addCircle:"+e.getStackTrace().toString());
                            }

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
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
       // mgoogleMap.setOnMapLoadedCallback(this);
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
               // Log.d("Marker ID:",""+marker.getId());
                markerPosition = marker.getPosition();
                String markerId = marker.getId();
                String pos[] = markerId.split("m");
                int markert = Integer.parseInt(pos[1]);
              //  int selectMarker = -1;
              //  LatLng changeposition = null;
                if(markert >= storeList.size()){
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
                .zoom(17)
                .build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        try {
            mgoogleMap.animateCamera(update);
           // mgoogleMap.moveCamera(update);
        } catch(NullPointerException e) {
            System.out.print("animateCamera: "+e.getStackTrace());
        }

        //mgoogleMap.moveCamera(update);
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
       // Log.d("makeMarkers",""+storeList.size());
        iconFactory = new IconGenerator(MapActivity.this);
        //List<StoreInfo> markers = getStoreInfoList();
        // inflate custom_marker
        LayoutInflater inflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View markerview = inflater.inflate(R.layout.custom_marker,null);
        listid = markerview.findViewById(R.id.listid);
        selectMarker = markerview.findViewById(R.id.MarkerIcon);

        for(int i=0;i<storeList.size();i++) {
            String id = String.valueOf(i+1); //店家ID
            listid.setText(id);
            iconFactory.setContentView(markerview);
            iconFactory.setBackground(null);

            Marker store_Marker = mgoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(storeList.get(i).getLatitude(),storeList.get(i).getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon())));
        }
        String id = String.valueOf(1); //店家ID
        listid.setText(id);
        //selectMarker = markerview.findViewById(R.id.MarkerIcon);
        selectMarker.setImageResource(R.drawable.map_maker_item);
        iconFactory.setContentView(markerview);
        //default_marker
        defaultMarker = mgoogleMap.addMarker(new MarkerOptions()
                //.position(new LatLng(markers.get(0).getLatitude(),markers.get(0).getLongitude()))
                .position(new LatLng(storeList.get(0).getLatitude(),storeList.get(0).getLongitude()))
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
         markerPosition = new LatLng(storeList.get(scrollPosition).getLatitude(),storeList.get(scrollPosition).getLongitude());
        // markerPosition = storeAddresstoLatlng(storeInfoList.get(scrollPosition).getStoreAddress());
         defaultMarker.setPosition(markerPosition);
         iconFactory.setContentView(markerview);
         iconFactory.setBackground(null);
         defaultMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
          if(lastClicked != null) {
             lastClicked.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker()));
         }
         defaultMarker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
         lastClicked = defaultMarker;
         double lat = storeList.get(position).getLatitude();
         double lon = storeList.get(position).getLongitude();
         setLocation(lat,lon);
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
                                            .target(position).zoom(17).build();
            mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    //getStoreFromDatabase
    private void getStoreList(Location uselocation) {
        System.out.println("getStoreList() Start!!!");
        currentlocation = uselocation;
       // Log.d("currentlocation2",""+currentlocation.getLatitude()+"|"+currentlocation.getLongitude());
        float[] results = new float[1]; //接收比對距離後結果，單位：公尺
        double userlat,userlon,storelat,storelon;

        if(currentlocation != null) {
            userlat = currentlocation.getLatitude(); //UserLatitude
            userlon = currentlocation.getLongitude(); //UserLongitude
            StoreDAO storeDAO = new StoreDAO(MapActivity.this);
            storeList = storeDAO.getStoreByDistance();

            if(storeList == null) {
                progressDialog.dismiss();
                setContentView(R.layout.map_fail);
                TextView errorText = findViewById(R.id.mapError);
                mtoolbar = findViewById(R.id.mapfail_toolbar);
                initToolBar();
                errorText.setText("連線不穩，請重新嘗試。");
              //  progressDialog.dismiss();
            } else {
              //  Log.d("storeList","||"+storeList.size());
                //比對店家位置和User位置並放入storeList
                for(int i=0;i<storeList.size();i++) {
                    storelat = storeList.get(i).getLatitude();
                    storelon = storeList.get(i).getLongitude();
                    //Log.d("storeLocation",""+storelat+"|"+storelon);
                    //計算距離 小於1KM就放進storeList
                    Location.distanceBetween(userlat,userlon,storelat,storelon,results);
                  //  Log.d("distance",""+results[0]);
                    int distance = (int) results[0];
                    if(distance > 1000) {
                        storeList.remove(i);
                        i--;
                    }
                }
               // Log.d("storeList","|| 塞選後"+storeList.size());
                String[] imgRes = storeList.get(0).getStorePicture().split(",");
                String storeImg = "\\data\\images\\Store_img\\"+imgRes[0]+".jpg";
                Log.d("imgRes",""+storeImg);
                //拿到List後makeMarkers
                handViews();
                getList = true;
                mgoogleMap.setOnMapLoadedCallback(this);
            }

        } else {

        }
    }

}

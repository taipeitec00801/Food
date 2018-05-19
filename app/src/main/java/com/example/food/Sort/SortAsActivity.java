package com.example.food.Sort;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.R;
import com.example.food.Sort.task.CommonTask;
import com.example.food.Sort.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SortAsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "SortAsActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CommonTask spotGetAllTask;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_as);
        initContent();
        setupNavigationDrawerMenu();

        recyclerView = findViewById(R.id.sortAs_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(SortAsActivity.this));

        //將上一頁送來的資料打開，找到餐廳編號後，丟入尋找餐廳的方法。
        Bundle bundle = getIntent().getExtras();
        sortRestaurant(bundle.getInt("SortRes"));
    }

    private void sortRestaurant(int sortNumber){
        if (Common.networkConnected(SortAsActivity.this)) {
            //建立商店物件的List，以便接收資料庫回傳的資料。
            List<SortAs> sortAsList = null;
            //建立Gson物件，以便將資料轉成Json格式。
            Gson gson = new Gson();
            //將收到的餐廳編號轉成字串。
            String sortNumbers = String.valueOf(sortNumber);
            //透過IP和資料庫名稱找到資料庫。
            String url = Common.URL + "/ssServlet";
            //建立JsonObject
            JsonObject jsonObject = new JsonObject();
            //jsonObject新增屬性action其值為findSortByRes
            jsonObject.addProperty("action", "findSortByRes");
            //jsonObject新增屬性sortNumber其值為sortNumbers
            jsonObject.addProperty("sortNumber",sortNumbers);
            //將jsonObject轉成json格式的字串。
            String jsonOut = jsonObject.toString();
            spotGetAllTask = new CommonTask(url, jsonOut);
            //CommonTask會將傳入的jsonOut字串送給伺服器
            //而伺服器判斷字串對應的方法後，對資料庫做出方法內的動作。
            //伺服器會找到在伺服器內部的findSortByRes方法，傳入參數sortNumbers，
            //以下為寫在伺服器的方法
            //public List<Sort> findSortByRes(int sortNumbers) {
            //		String sql = "SELECT SORTNAME,LIKENUMBER FROM RESTAURANT WHERE SORTRES=?;";
            //		Connection conn = null;
            //		PreparedStatement ps = null;
            //		List<Sort> sorts = new ArrayList<Sort>();
            //		try {
            //			conn = DriverManager.getConnection(Common.URL, Common.USER,
            //					Common.PASSWORD);
            //			ps = conn.prepareStatement(sql);
            //			ps.setInt(1, sortNumbers);
            //			ResultSet rs = ps.executeQuery();
            //			if (rs.next()) {
            //       /*********將查詢結果取出後寫入Sort物件在加進List<Sort>中回傳。********/
            //				String sortName = rs.getString(1);
            //				int likeNumber = rs.getInt(2);
            //				Sort sort = new Sort(sortNumbers,sortName,likeNumber);
            //				sorts.add(sort);
            //			}
            //		} catch (SQLException e) {
            //			e.printStackTrace();
            //		} finally {
            //			try {
            //				if (ps != null) {
            //					ps.close();
            //				}
            //				if (conn != null) {
            //					conn.close();
            //				}
            //			} catch (SQLException e) {
            //				e.printStackTrace();
            //			}
            //		}
            //		return sorts;
            //	}
            //}
            try {
                //用字串儲存伺服器回應的json格式字串。
                String jsonIn = spotGetAllTask.execute().get();
                //利用TypeToken指定資料型態為List<SortAs>
                Type listType = new TypeToken<List<SortAs>>() {}.getType();
                //利用Gson把json字串轉成Type指定的型態(List<SortAs>)後放入sortAsList(List<SortAs>)。
                sortAsList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (sortAsList == null || sortAsList.isEmpty()) {
                //當伺服器回傳空的List時顯示給使用者"查無資料"。
                Common.showToast(SortAsActivity.this, "查無資料");
            } else {
                //利用setAdapter把sortAsList寫上itemview。
                recyclerView.setAdapter(new sortAdapter(SortAsActivity.this, sortAsList));
            }
        } else {
            Common.showToast(SortAsActivity.this, "no network connection available");
        }
    }
    private void initContent() {
        Bundle bundle = getIntent().getExtras();
        String a = bundle.getString("Sort");
        toolbar = findViewById(R.id.sortAs_toolbar);
        toolbar.setTitle(a);
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.sortAs_navigationView);
        drawerLayout = findViewById(R.id.sortAs_drawerLayout);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        return true;
    }

    // Close the Drawer
    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    // Open the Drawer
    private void showDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
    }

    private class sortAdapter extends
            RecyclerView.Adapter<sortAdapter.SortViewHolder> {
        private Context context;
        private List<SortAs> sortList;
        private int imageSize;

        sortAdapter(Context context, List<SortAs> sortList) {
            this.context = context;
            this.sortList = sortList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 8;
        }

        class SortViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView ,likeView;
            TextView tvName,tvLike;
            SortViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.sortAs_item_iv);
                tvName =  itemView.findViewById(R.id.sortAs_item_tv);
                tvLike =  itemView.findViewById(R.id.sortAs_item_like_tv);
                likeView = itemView.findViewById(R.id.sortAs_item_like_iv);
            }
        }

        @Override
        public int getItemCount() {
            return sortList.size();
        }

        @Override
        public SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.sort_as_item, viewGroup, false);
            return new SortViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SortViewHolder viewHolder, int position) {
            SortAs sort = sortList.get(position);
            viewHolder.likeView.setImageResource(R.drawable.like);
            viewHolder.imageView.setImageResource(R.drawable.p01);
            viewHolder.tvName.setText(String.valueOf(sort.getName()));
            viewHolder.tvLike.setText(String.valueOf(sort.getNumber()));

        }
    }

}



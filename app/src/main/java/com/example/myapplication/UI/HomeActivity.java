package com.example.myapplication.UI;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.myapplication.Adapter.MyArrayAdapter;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.model.User;
import com.example.myapplication.R;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
public class HomeActivity extends AppCompatActivity{
    private TextView errorMessage;
    private TextView fullName;

    private User user;

    private GridView gv;
    private ArrayList<BaiDang> listBD;
    private MyArrayAdapter myAdapter;
    private ArrayList<NguyenLieu> nguyenLieu;

    Button btnFavorite;
    Button btnRecommend;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        String fullname = user.getFullname();
        user.setFullname(fullname);
        fullName = findViewById(R.id.textFullName);
        fullName.setText(fullname);

        gv = findViewById(R.id.gridFoods);
        listBD = new ArrayList<>();

        btnFavorite = findViewById(R.id.btnFavorite);
        btnRecommend = findViewById(R.id.btnRecommend);
        takeBD();
        myAdapter = new MyArrayAdapter(HomeActivity.this, R.layout.layout_item, listBD);
        gv.setAdapter(myAdapter);

        btnFavorite.setOnClickListener(v -> {
            takeFAV();
            myAdapter = new MyArrayAdapter(HomeActivity.this, R.layout.layout_item, listBD);
            gv.setAdapter(myAdapter);
        });
        btnRecommend.setOnClickListener(v -> {
            takeBD();
            myAdapter = new MyArrayAdapter(HomeActivity.this, R.layout.layout_item, listBD);
            gv.setAdapter(myAdapter);
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentBD = new Intent(HomeActivity.this, DetailFoodActivity.class);
                BaiDang chonBD = listBD.get(position);
                Toast.makeText(HomeActivity.this, "Bạn đã chọn món " + chonBD.getTenMon(), Toast.LENGTH_SHORT).show();
                intentBD.putExtra("data", chonBD);
                intentBD.putExtra("user", user);
                startActivity(intentBD);
            }
        });


        BottomNavigationView botNav = findViewById(R.id.bottomNavView);
        botNav.setSelectedItemId(R.id.menuHome);

        botNav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menuHome) {
                Intent intent2 = new Intent(HomeActivity.this, HomeActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            } else if (id == R.id.menuProfile){
                Intent intent2 = new Intent(HomeActivity.this, ProfileeActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            } else if (id == R.id.menuSearch){
                Intent intent2 = new Intent(HomeActivity.this, SearchActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            }else if(id == R.id.menuAdd){
                Intent intent2 = new Intent(HomeActivity.this, AddFoodPostActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            }
            return false;
        });

    }
    private void takeBD(){
        String url = "https://mobilenodejs.onrender.com/api/baidang";
        RequestQueue queue = Volley.newRequestQueue(this);

        try{
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        try {
                            listBD.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                BaiDang baiDang = new BaiDang();
                                baiDang.setId(obj.getString("_id"));
                                baiDang.setTenMon(obj.getString("tenMon"));
                                baiDang.setCachLam(obj.getString("cachLam"));
                                baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                                baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                                baiDang.setLuotThich(obj.optInt("luotThich", 0));
                                baiDang.setImage(obj.optString("image", ""));

                                JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                                nguyenLieu = new ArrayList<>();
                                for(int j = 0; j < nlArray.length(); j++){
                                    nguyenLieu.add(new NguyenLieu(nlArray.getJSONObject(j).getString("_id"), nlArray.getJSONObject(j).getString("ten")));
                                }
                                baiDang.setNguyenLieu(nguyenLieu);

                                listBD.add(baiDang);
                            }
                            myAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            Toast.makeText(this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show()
            );
            queue.add(jsonArrayRequest);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo request", Toast.LENGTH_SHORT).show();
        }
    }

    private void takeFAV(){
        String url = "https://mobilenodejs.onrender.com/api/nguoidung/fav/" + user.getId();
        RequestQueue queue = Volley.newRequestQueue(this);

        try{
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        try {
                            listBD.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                BaiDang baiDang = new BaiDang();
                                baiDang.setId(obj.getString("_id"));
                                baiDang.setTenMon(obj.getString("tenMon"));
                                baiDang.setCachLam(obj.getString("cachLam"));
                                baiDang.setNguyenLieuDinhLuong(obj.optString("nguyenLieuDinhLuong", ""));
                                baiDang.setLinkYtb(obj.optString("linkYtb", ""));
                                baiDang.setLuotThich(obj.optInt("luotThich", 0));
                                baiDang.setImage(obj.optString("image", ""));

                                JSONArray nlArray = obj.getJSONArray("nguyenLieu");
                                nguyenLieu = new ArrayList<>();
                                for(int j = 0; j < nlArray.length(); j++){
                                    nguyenLieu.add(new NguyenLieu(nlArray.getJSONObject(j).getString("_id"), nlArray.getJSONObject(j).getString("ten")));
                                }
                                baiDang.setNguyenLieu(nguyenLieu);

                                listBD.add(baiDang);
                            }
                            myAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            Toast.makeText(this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show()
            );
            queue.add(jsonArrayRequest);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo request", Toast.LENGTH_SHORT).show();
        }
    }
}


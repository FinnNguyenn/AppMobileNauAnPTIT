package com.example.myapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.Adapter.ArrayNLAdapter;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class AddFoodPostActivity extends AppCompatActivity {
    private User user;
    private ArrayList<NguyenLieu> al;
    private Button btnCong;
    private Button btnTru;
    private Button btnSave;
    private EditText edtTenMon;
    private EditText edtCachLam;
    private EditText edtLinkYtb;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_food_post);
        Intent intent = getIntent();

        user = (User) intent.getSerializableExtra("user");
        al = new ArrayList<>();
        tableLayout = findViewById(R.id.tablet);
        btnCong = findViewById(R.id.buttonCo);
        btnTru = findViewById(R.id.buttonTru);
        btnSave = findViewById(R.id.btnSave);
        edtTenMon = findViewById(R.id.edtTenMon);
        edtCachLam = findViewById(R.id.edtCachLam);
        edtLinkYtb = findViewById(R.id.edtLinkYtb);


        btnCong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                TableRow newRow = (TableRow) LayoutInflater.from(AddFoodPostActivity.this)
                        .inflate(R.layout.table_layout, tableLayout, false);

                EditText tv = newRow.findViewById(R.id.textTen);
                EditText et = newRow.findViewById(R.id.editDinhLuong);

                tv.setText("");
                et.setText("");

                tableLayout.addView(newRow);
            }
        });
        btnTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                if(rowCount > 1) {
                    tableLayout.removeViewAt(rowCount - 1);
                }
                else {
                    Toast.makeText(AddFoodPostActivity.this, "Không thể xóa hết các dòng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int rowCount = tableLayout.getChildCount();
                al.clear();
                TableRow firstOne = (TableRow) tableLayout.getChildAt(0);
                EditText ten = firstOne.findViewById(R.id.editTen1);
                NguyenLieu tmp = new NguyenLieu();
                if(ten.getText().toString().isEmpty()) {
                    Toast.makeText(AddFoodPostActivity.this, "Tên nguyên liệu không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                tmp.setTen(ten.getText().toString());
                al.add(tmp);
                for(int i = 1; i < rowCount; i++){
                    TableRow hehe = (TableRow) tableLayout.getChildAt(i);
                    EditText tv = hehe.findViewById(R.id.textTen);

                    if (!tv.getText().toString().isEmpty()) {
                        NguyenLieu tmp1 = new NguyenLieu();
                        tmp1.setTen(tv.getText().toString());
                        al.add(tmp1);
                    }
                }
                BaiDang bd = new BaiDang();
                bd.setTenMon(edtTenMon.getText().toString());
                bd.setCachLam(edtCachLam.getText().toString());
                bd.setNguyenLieu(al);
                bd.setNguyenLieuDinhLuong(al.toString());
                bd.setLinkYtb(edtLinkYtb.getText().toString());
                bd.setLuotThich(0);
                bd.setImage("");
                addfoodpost(bd);
            }
        });

        BottomNavigationView botNav = findViewById(R.id.bottomNavView);
        botNav.setSelectedItemId(R.id.menuAdd);


        botNav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menuHome) {
                Intent intent2 = new Intent(AddFoodPostActivity.this, HomeActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;

            } else if (id == R.id.menuProfile){
                Intent intent2 = new Intent(AddFoodPostActivity.this, ProfileeActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            } else if (id == R.id.menuSearch){
                Intent intent2 = new Intent(AddFoodPostActivity.this, SearchActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            }
            else if(id == R.id.menuAdd){
                Intent intent2 = new Intent(AddFoodPostActivity.this, AddFoodPostActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            }
            return false;
        });


    }
    private void addfoodpost(BaiDang bd){
        String url = "https://mobilenodejs.onrender.com/api/nguoidung/add/" + user.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try{
            jsonBody.put("tenMon", bd.getTenMon());
            jsonBody.put("cachLam", bd.getCachLam());
            jsonBody.put("nguyenLieuDinhLuong", bd.getNguyenLieuDinhLuong());
            jsonBody.put("linkYtb", bd.getLinkYtb());
            jsonBody.put("image", bd.getImage());
            jsonBody.put("luotThich", bd.getLuotThich());

            JSONArray nguyenLieuArray = new JSONArray();
            for (NguyenLieu nl : bd.getNguyenLieu()) {
                JSONObject nguyenLieuObject = new JSONObject();
                nguyenLieuObject.put("ten", nl.getTen());
                nguyenLieuArray.put(nguyenLieuObject);
            }

            jsonBody.put("nguyenlieu",nguyenLieuArray);

        } catch(Exception e) {
            Toast.makeText(this, "Lỗi khi tạo dữ liệu JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(AddFoodPostActivity.this, "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddFoodPostActivity.this, DetailFoodActivity.class);
                            intent.putExtra("data", bd);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AddFoodPostActivity.this, "Lỗi thông tin đăng kí", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AddFoodPostActivity.this, "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AddFoodPostActivity.this, "Lỗi kết nối: " + error.toString(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonObjectRequest);

    }
}
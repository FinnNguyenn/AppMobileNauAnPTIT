package com.example.myapplication.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.myapplication.model.BaiDang;
import com.example.myapplication.model.NguyenLieu;
import com.example.myapplication.Adapter.ArrayNLAdapter;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;


import java.util.List;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

public class DetailFoodActivity extends AppCompatActivity {
    private BaiDang baiDang;
    private List<NguyenLieu> nlList;
    private ArrayNLAdapter adapterNL;
    private ListView lvNL;
    private ImageView imgBD;
    private TextView txtBD;
    private TextView tvNguyenLieu;
    private TextView tvCachLam;
    private User user;

    private YouTubePlayerView youtubePlayerView;

    TabHost tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_detail);
        addControl();

        baiDang = (BaiDang) getIntent().getSerializableExtra("data");
        user = (User) getIntent().getSerializableExtra("user");
        if (baiDang == null) {
            Toast.makeText(this, "Không có dữ liệu bài đăng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imgBD = findViewById(R.id.imgFood);
        Glide.with(this)
                .load(baiDang.getImage())
                .placeholder(R.drawable.logo_app)
                .error(R.drawable.ic_launcher_background)
                .into(imgBD);

        txtBD = findViewById(R.id.txtFoodName);
        txtBD.setText(baiDang.getTenMon());
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        // Test bang string

//        tvNguyenLieu = findViewById(R.id.tvNguyenLieu);
//        StringBuilder nlBuilder = new StringBuilder();
//        for (NguyenLieu nl : baiDang.getNguyenLieu()) {
//            nlBuilder.append("- ").append(nl.getTen()).append("\n");
//        }
//        tvNguyenLieu.setText(nlBuilder.toString());

        // Test LítView
        View layoutNL = findViewById(R.id.layoutNL);
        lvNL = layoutNL.findViewById(R.id.listNL);
        nlList = baiDang.getNguyenLieu();
        adapterNL = new ArrayNLAdapter(DetailFoodActivity.this, R.layout.layout_itemnl, nlList);
        lvNL.setAdapter(adapterNL);

        tvCachLam = findViewById(R.id.tvCachLam);
        tvCachLam.setText(baiDang.getCachLam());

        getLifecycle().addObserver(youtubePlayerView);
        String linkYtb = baiDang.getLinkYtb();
        String videoId = extractYoutubeVideoId(linkYtb);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer.unMute();
                youTubePlayer.setVolume(100);
            }
        });


        BottomNavigationView botNav = findViewById(R.id.bottomNavView);
        botNav.setSelectedItemId(R.id.menuHome);

        botNav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menuHome) {
                Intent intent2 = new Intent(DetailFoodActivity.this, HomeActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;

            } else if (id == R.id.menuProfile){
                Intent intent2 = new Intent(DetailFoodActivity.this, ProfileeActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            } else if (id == R.id.menuSearch){
                Intent intent2 = new Intent(DetailFoodActivity.this, SearchActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            }else if(id == R.id.menuAdd){
                Intent intent2 = new Intent(DetailFoodActivity.this, AddFoodPostActivity.class);
                intent2.putExtra("user", user);
                startActivity(intent2);
                return true;
            }
            return false;
        });

    }

    private void addControl(){
        tabHost = findViewById(R.id.tabBD);
        tabHost.setup();
        TabHost.TabSpec tab1, tab2;
        tab1 = tabHost.newTabSpec("t1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("Nguyên liệu");
        tabHost.addTab(tab1);
        tab2 = tabHost.newTabSpec("t2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("Hướng dẫn");
        tabHost.addTab(tab2);

        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tabhost_bg_slt);
        TextView df = tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tabhost_bg_unslt);
        TextView df1 = tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        df.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        df1.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        tabHost.setOnTabChangedListener(tabId -> {
            for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                View tabView = tabHost.getTabWidget().getChildAt(i);
                TextView tv = tabView.findViewById(android.R.id.title);

                if (i == tabHost.getCurrentTab()) {
                    tv.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    tabView.setBackgroundResource(R.drawable.tabhost_bg_slt);
                } else {
                    tv.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    tabView.setBackgroundResource(R.drawable.tabhost_bg_unslt);
                }
            }
        });

    }

    private String extractYoutubeVideoId(String url) {
        if (url == null || url.isEmpty()) return "";

        if (url.contains("v=")) {
            String id = url.substring(url.indexOf("v=") + 2);
            int ampIndex = id.indexOf("&");
            if (ampIndex != -1) {
                id = id.substring(0, ampIndex);
            }
            return id;
        }


        if (url.contains("youtu.be/")) {
            String id = url.substring(url.indexOf("youtu.be/") + 9);
            int qmIndex = id.indexOf("?");
            if (qmIndex != -1) {
                id = id.substring(0, qmIndex);
            }
            return id;
        }

        return url;
    }
}
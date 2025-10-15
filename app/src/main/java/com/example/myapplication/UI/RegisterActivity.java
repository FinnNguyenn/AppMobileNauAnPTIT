package com.example.myapplication.UI;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.model.User;
import com.example.myapplication.R;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.*;

public class RegisterActivity extends AppCompatActivity {
    private TextView errorMessage;
    private EditText username;
    private EditText password;
    private EditText fullname;
    private EditText address;
    private EditText email;
    private EditText tel;
    private EditText confirmPassword;
    private Button btnRegister;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);
        username = findViewById(R.id.editRegisterUsername);
        password = findViewById(R.id.editRegisterPassword);
        fullname = findViewById(R.id.editRegisterName);
        address = findViewById(R.id.editRegisterAddress);
        email = findViewById(R.id.editRegisterEmail);
        tel = findViewById(R.id.editRegisterPhone);
        btnRegister = findViewById(R.id.button2);
        confirmPassword = findViewById(R.id.editConfirmPassword);
        btnBack = findViewById(R.id.buttonBack);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString();
                String passWord = password.getText().toString();
                String fullName = fullname.getText().toString();
                String Address = address.getText().toString();
                String Email = email.getText().toString();
                String Tel = tel.getText().toString();
                String confirmpassword = confirmPassword.getText().toString();
                if(userName.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
                }
                else if(passWord.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                }
                else if(!passWord.equals(confirmpassword)){
                    Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
                else if(fullName.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Fullname is required", Toast.LENGTH_SHORT).show();
                }
                else if(Address.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Address is required", Toast.LENGTH_SHORT).show();
                }
                else if(Email.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                }
                else if(Tel.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Tel is required", Toast.LENGTH_SHORT).show();
                }
                else{
                    User user = new User(userName,passWord,fullName,Address,Email,Tel);
                    RegisterWithAPI(user);
                }


            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void RegisterWithAPI(User user){
        String url = "https://mobilenodejs.onrender.com/api/nguoidung/register";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HashMap<String,String> params = new HashMap<>();
        params.put("username",user.getUsername());
        params.put("password",user.getPassword());
        params.put("name",user.getFullname());
        params.put("address",user.getAddress());
        params.put("email",user.getEmail());
        params.put("phone",user.getTel());

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        String name = response.getString("name");
                        if (!name.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Lỗi parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && (error.networkResponse.statusCode == 409 || error.networkResponse.statusCode == 400)) {
                        Toast.makeText(RegisterActivity.this, "Username này đã tồn tại, vui lòng chọn tên khác.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối hoặc server có vấn đề: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }


}
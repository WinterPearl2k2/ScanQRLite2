package com.example.scanqrlite2.Create.item_create;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.scanqrlite2.FullScreen;
import com.example.scanqrlite2.R;

public class URL extends AppCompatActivity {
    FullScreen screen;
    LinearLayout btnBack;
    private EditText edtURL;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        screen = new FullScreen(URL.this);
        screen.changeFullScreen(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        ORM();
        createWifi();
        backLayout();
    }

    private void createWifi() {if(edtURL.getText().toString().trim().length() == 0) {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtURL.setError("Please enter your link");
            }
        });
    }

        edtURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().length() == 0) {
                    btnCreate.setTextColor(getResources().getColor(R.color.primaryTextColor));
                    btnCreate.setBackgroundResource(R.drawable.cus_create);
                    btnCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            edtURL.setError("Please enter your content");
                        }
                    });
                } else {
                    btnCreate.setTextColor(getResources().getColor(R.color.white));
                    btnCreate.setBackgroundResource(R.drawable.cus_create_allow);
                }
            }
        });
    }

    private void backLayout() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ORM() {
        btnBack = findViewById(R.id.btn_back);
        edtURL = findViewById(R.id.edt_url);
        btnCreate = findViewById(R.id.btn_create);
    }
}
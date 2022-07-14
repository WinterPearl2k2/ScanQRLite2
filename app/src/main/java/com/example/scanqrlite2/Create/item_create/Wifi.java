package com.example.scanqrlite2.Create.item_create;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.scanqrlite2.FullScreen;
import com.example.scanqrlite2.R;

public class Wifi extends AppCompatActivity {
    FullScreen screen;
    LinearLayout btnBack;
    EditText edtNetworkName, edtNetworkPass;
    Button btnCreate;
    RadioGroup rdgSecurity;
    RadioButton rdbWPA, rdbWEP, rdbNone;

    String ssid, password, setcurity, value;
    boolean isNone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        screen = new FullScreen(Wifi.this);
        screen.changeFullScreen(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        ORM();
        backLayout();
        createWifi();
    }

    private void createWifi() {
        ssid = edtNetworkName.getText().toString().trim();
        password = edtNetworkPass.getText().toString().trim();
        setcurity = getSecurity();

        rdgSecurity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rdb_wep:
                        isNone = true;
                        edtNetworkPass.setEnabled(true);
                        break;
                    case R.id.rdb_wpa:
                        isNone = true;
                        edtNetworkPass.setEnabled(true);
                        break;
                    case R.id.rdb_none:
                        isNone = false;
                        edtNetworkPass.setEnabled(false);
                        break;
                }
                setcurity = getSecurity();
                handleCreate(isNone);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyError(checkIsEmpty());
            }
        });

        TextWatcher textWatcher = getTextWacher();
        edtNetworkName.addTextChangedListener(textWatcher);
        edtNetworkPass.addTextChangedListener(textWatcher);
    }

    private TextWatcher getTextWacher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ssid = edtNetworkName.getText().toString().trim();
                password = edtNetworkPass.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        return textWatcher;
    }

    private void handleCreate(boolean isNone) {
        if(isNone) {
            value = "WIFI:T:" + setcurity + ";S:" + ssid + ";P:" + password + ";H:false";
            if(checkIsEmpty() != 0) {
                btnCreate.setTextColor(getResources().getColor(R.color.primaryTextColor));
                btnCreate.setBackgroundResource(R.drawable.cus_create);
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyError(checkIsEmpty());
                    }
                });
            } else {
                btnCreate.setTextColor(getResources().getColor(R.color.white));
                btnCreate.setBackgroundResource(R.drawable.cus_create_allow);
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Wifi.this, "Intent to Reuslt", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if(!isNone){
            value = "WIFI:T:" + setcurity + ";S:" + ssid + ";P:;H:false";
            edtNetworkPass.setError(null);
            if(ssid.length() < 1) {
                btnCreate.setTextColor(getResources().getColor(R.color.primaryTextColor));
                btnCreate.setBackgroundResource(R.drawable.cus_create);
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyError(checkIsEmpty());
                    }
                });
            } else {
                btnCreate.setTextColor(getResources().getColor(R.color.white));
                btnCreate.setBackgroundResource(R.drawable.cus_create_allow);
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Wifi.this, "Intent to Reuslt", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void notifyError(int checkIsEmpty) {
        if(checkIsEmpty == 3) {
            edtNetworkName.setError("Account not be blank");
            edtNetworkPass.setError("Password please enter more than 8 character");
        } else if(checkIsEmpty == 2) {
            edtNetworkName.setError("Account not be blank");
        } else if (checkIsEmpty == 1) {
            edtNetworkPass.setError("Password please enter more than 8 character");
        }
    }

    private int checkIsEmpty() {
        if(ssid.length() < 1 && password.length() < 8)
            return 3;
        else if(ssid.length() < 1)
            return 2;
        else if(password.length() < 8)
            return 1;
        return 0;
    }

    private String getSecurity() {
        if(rdbWPA.isChecked())
            return "WPA";
        else if(rdbWEP.isChecked())
            return "WEP";
        else
            return "nopass";
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
        edtNetworkName = findViewById(R.id.edt_network_name);
        edtNetworkPass = findViewById(R.id.edt_network_pass);
        btnCreate = findViewById(R.id.btn_create);
        rdgSecurity = findViewById(R.id.rdg_container);
        rdbWPA = findViewById(R.id.rdb_wpa);
        rdbWEP = findViewById(R.id.rdb_wep);
        rdbNone = findViewById(R.id.rdb_none);
    }
}
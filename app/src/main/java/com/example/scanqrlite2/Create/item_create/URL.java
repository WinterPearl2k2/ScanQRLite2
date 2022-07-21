package com.example.scanqrlite2.Create.item_create;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.scanqrlite2.FullScreen;
import com.example.scanqrlite2.HideKeyboard;
import com.example.scanqrlite2.History.History_Menu.HistoryCreateItem;
import com.example.scanqrlite2.History.History_Menu.database.CreateDatabase;
import com.example.scanqrlite2.Language;
import com.example.scanqrlite2.R;
import com.example.scanqrlite2.Result;

public class URL extends AppCompatActivity {
    FullScreen screen;
    LinearLayout btnBack;
    private EditText edtURL;
    private Button btnCreate;
    private Language language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        language = new Language(this);
        language.Language();
        getSupportActionBar().hide();
        screen = new FullScreen(URL.this);
        screen.changeFullScreen(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        ORM();
        createURL();
        backLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            HideKeyboard hideKeyboard = new HideKeyboard(this);
            hideKeyboard.closeKeyboard();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void createURL() {
        language.Language();
        if(edtURL.getText().toString().trim().length() == 0) {
            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edtURL.setError(getText(R.string.please_enter_your_link));
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
                    btnCreate.setTextColor(getResources().getColor(R.color.brown));
                    btnCreate.setBackgroundResource(R.drawable.cus_create);
                    btnCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            edtURL.setError(getText(R.string.please_enter_your_link));
                        }
                    });
                } else {
                    btnCreate.setTextColor(getResources().getColor(R.color.au_white));
                    btnCreate.setBackgroundResource(R.drawable.cus_create_allow);
                    btnCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent result = new Intent(URL.this, Result.class);
                            result.putExtra("value", editable.toString().trim());
                            result.putExtra("type", "URL");
                            result.putExtra("type_qr", "QRCode");
                            HistoryCreateItem createItem = new HistoryCreateItem("URL", editable.toString().trim(),
                                    editable.toString().trim());
                            CreateDatabase.getInstance(URL.this).createItemDAO().insertItem(createItem);
                            startActivity(result);
                        }
                    });
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
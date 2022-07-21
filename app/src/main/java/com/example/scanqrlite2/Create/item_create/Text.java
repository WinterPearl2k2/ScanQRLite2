package com.example.scanqrlite2.Create.item_create;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.content.Context;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class Text extends AppCompatActivity {
    private FullScreen screen;
    private LinearLayout btnBack;
    private EditText edtText;
    private Button btnCreate;
    private Language language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        language = new Language(this);
        language.Language();
        getSupportActionBar().hide();
        screen = new FullScreen(Text.this);
        screen.changeFullScreen(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        ORM();
        createText();
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

    private void createText() {
        language.Language();
        if(edtText.getText().toString().trim().length() == 0) {
            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edtText.setError(getString(R.string.please_enter_your_content));
                }
            });
        }

        edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                language.Language();
                if(editable.toString().trim().length() < 1) {
                    btnCreate.setTextColor(getResources().getColor(R.color.brown));
                    btnCreate.setBackgroundResource(R.drawable.cus_create);
                    btnCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            edtText.setError(getString(R.string.please_enter_your_content));
                        }
                    });
                } else {
                    btnCreate.setTextColor(getResources().getColor(R.color.au_white));
                    btnCreate.setBackgroundResource(R.drawable.cus_create_allow);
                    btnCreate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent result = new Intent(Text.this, Result.class);
                            result.putExtra("value", editable.toString().trim());
                            result.putExtra("type", "Text");
                            result.putExtra("type_qr", "QRCode");
                            HistoryCreateItem createItem = new HistoryCreateItem("Text", editable.toString().trim(),
                                    editable.toString().trim());
                            CreateDatabase.getInstance(Text.this).createItemDAO().insertItem(createItem);
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
        edtText = findViewById(R.id.edt_text);
        btnCreate = findViewById(R.id.btn_create);
    }
}
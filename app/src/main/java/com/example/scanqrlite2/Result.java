package com.example.scanqrlite2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public class Result extends AppCompatActivity {
    FullScreen screen;
    LinearLayout btnBack, btnToURL, btnConnectWifi, btnSearchProduct, btnShare;
    LinearLayout resultText, resultWifi, resultURL, resultProduct;
    CardView btnCoppy, btnSearch, btnSaveImg;
    ImageView imgResult, imgQR;
    TextView txtTitleResult;
    TextView txtContentText, txtContentURL, txtContentProduct, txtSSID, txtPass, txtSecurity;
    Bitmap bitmap;

    Intent result;
    String ssid, password, security, content, type, typeQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        screen = new FullScreen(Result.this);
        screen.changeFullScreen(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_generator);
        ORM();
        backLayout();
        showResult();
        showQR();
        connectWifi();
        toURL();
        coppyToClipboard();
        searchProduct();
        searchGoogle();
        saveImage();
        shareContent();
    }

    private void shareContent() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, content);
                startActivity(share);
            }
        });
    }

    private void saveImage() {
        btnSaveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Result.this, "lolo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchGoogle() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Result.this, "koko", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void coppyToClipboard() {
        btnCoppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Result.this, "hoho", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchProduct() {
        btnSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Result.this, "kiki", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectWifi() {
        btnConnectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Result.this, "huhu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toURL() {
        btnToURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Result.this, "hihi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQR() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        try {
            imgQR.setImageBitmap(CreateQR(content));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap CreateQR(String content) throws WriterException {
        int sizeWidth = 600;
        int sizeHeight = 264;

        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix matrix = null;
        switch (typeQR) {
            case "QRCode":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, sizeWidth, sizeWidth, hints);
                break;
            case "Code_128":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, sizeWidth, sizeHeight, hints);
                break;
            case "Code_39":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_39, sizeWidth, sizeHeight, hints);
                break;
            case "Code_ITF":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.ITF, sizeWidth, sizeHeight, hints);
                break;
            case "EAN_13":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.EAN_13, sizeWidth, sizeHeight, hints);
                break;
            case "EAN_8":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.EAN_8, sizeWidth, sizeHeight, hints);
                break;
            case "UPC_A":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.UPC_A, sizeWidth, sizeHeight, hints);
                break;
            case "UPC_E":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.UPC_E, sizeWidth, sizeHeight, hints);
                break;

        }

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int pixel[] = new int[width * height];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(matrix.get(j, i))
                    pixel[i * width + j] = 0xff000000;
                else
                    pixel[i * width + j] = 0xffffffff;
            }
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixel, 0, width, 0, 0, width, height);
        return bitmap;
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
        btnToURL = findViewById(R.id.btn_goto_url);
        btnConnectWifi = findViewById(R.id.btn_connect_wifi);
        btnSearchProduct = findViewById(R.id.btn_search_product);
        btnCoppy = findViewById(R.id.btn_copy);
        btnSearch = findViewById(R.id.btn_search);
        btnSaveImg = findViewById(R.id.btn_download);
        btnShare = findViewById(R.id.btn_share);

        imgResult = findViewById(R.id.img_result);
        imgQR = findViewById(R.id.image_qr);

        txtTitleResult = findViewById(R.id.txt_title_result);
        resultText = findViewById(R.id.frame_content_text);
        resultWifi = findViewById(R.id.frame_content_wifi);
        resultURL = findViewById(R.id.frame_content_url);
        resultProduct = findViewById(R.id.frame_content_product);

        txtContentText = findViewById(R.id.content_text);
        txtContentURL = findViewById(R.id.content_url);
        txtSSID = findViewById(R.id.content_name);
        txtPass = findViewById(R.id.content_pass);
        txtSecurity = findViewById(R.id.content_security);
        txtContentProduct = findViewById(R.id.content_product);
    }

    private void showResult() {
        getResult();
        switch (typeQR) {
            case "QRCode":
                switch (type) {
                    case "Text":
                        txtTitleResult.setText("Text");
                        imgResult.setImageResource(R.drawable.logo_text);
                        getShowResult(type);
                        break;
                    case "URL":
                        txtTitleResult.setText("URL");
                        imgResult.setImageResource(R.drawable.logo_url);
                        getShowResult(type);
                        break;
                    case "Wifi":
                        txtTitleResult.setText("Wifi");
                        imgResult.setImageResource(R.drawable.logo_wifi);
                        getShowResult(type);
                        break;
                }
                break;
            case "Code_128":
            case "Code_39":
            case "Code_ITF":
                txtTitleResult.setText("Text");
                imgResult.setImageResource(R.drawable.logo_text);
                getShowResult(type);
                break;
            case "EAN_13":
            case "EAN_8":
            case "UPC_A":
            case "UPC_E":
                txtTitleResult.setText("Product");
                imgResult.setImageResource(R.drawable.ic_logo_product);
                getShowResult(type);
                break;
        }
    }

    private void getShowResult(String type) {
        if(type.equals("Text")) {
            resultText.setVisibility(View.VISIBLE);
            txtContentText.setText(content != null ? content : "");
        } else if(type.equals("URL")) {
            resultURL.setVisibility(View.VISIBLE);
            handleURL();
            txtContentURL.setText(content != null ? content : "");
            btnToURL.setVisibility(View.VISIBLE);
        } else if(type.equals("Wifi")) {
            resultWifi.setVisibility(View.VISIBLE);
            txtSSID.setText(ssid != null ? ssid : "");
            txtPass.setText(password != null ? password : "");
            txtSecurity.setText(security);
            btnConnectWifi.setVisibility(View.VISIBLE);
        } else if(type.equals("Product")) {
            resultProduct.setVisibility(View.VISIBLE);
            txtContentProduct.setText(content != null ? content : "");
            btnSearchProduct.setVisibility(View.VISIBLE);
        }
    }

    private void handleURL() {
        if(content.toLowerCase().startsWith("https://")) {
            content = content.substring(8);
            content = "https://" + content;
        } else if(content.toLowerCase().startsWith("http://")) {
            content = content.substring(7);
            content = "http://" + content;
        } else if(!content.startsWith("http://") || !content.startsWith("https://"))
            content = "https://" + content;
    }

    private void getResult() {
        result = getIntent();
        type = result.getStringExtra("type");
        typeQR = result.getStringExtra("type_qr");
        ssid = result.getStringExtra("ssid");
        password = result.getStringExtra("password");
        security = getSecurity();
        content = result.getStringExtra("value");
    }

    private String getSecurity() {
        int typeSecurity = result.getIntExtra("security", -1);
        if(typeSecurity == 2)
            return "WPA";
        else if (typeSecurity == 3)
            return "WEP";
        else
            return "None";
    }
}
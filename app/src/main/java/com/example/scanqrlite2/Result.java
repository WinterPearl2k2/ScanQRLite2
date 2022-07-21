package com.example.scanqrlite2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.scanqrlite2.Language;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Result extends AppCompatActivity {
    private final int SHARE = 200, SEARCH = 201, SEARCH_PRODUCT = 202, TO_URL = 203, CONNECT_WIFI = 204;
    private WifiManager wifiManager;

    FullScreen screen;
    LinearLayout btnBack, btnToURL, btnConnectWifi, btnSearchProduct, btnShare;
    LinearLayout resultText, resultWifi, resultURL, resultProduct;
    CardView btnCoppy, btnSearch, btnSaveImg;
    ImageView imgResult, imgQR;
    TextView txtTitleResult;
    TextView txtContentText, txtContentURL, txtContentProduct, txtSSID, txtPass, txtSecurity;
    Bitmap bitmap;
    AdView adView;
    AdRequest request;

    Intent result;
    Language language;
    String ssid, password, security, content, type, typeQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        language = new Language(this);
        language.Language();
        getSupportActionBar().hide();
        screen = new FullScreen(Result.this);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
            screen.changeFullScreen(1);
        else
            screen.changeFullScreen(0);
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
        search();
        saveImage();
        shareContent();
        showAds();
    }

    private void showAds() {
        request = new AdRequest.Builder().build();
        adView.loadAd(request);
    }

    @Override
    protected void onStart() {
        if(adView != null)
            adView.pause();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adView != null)
            adView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adView != null)
            adView.destroy();
    }

    private void shareContent() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, content);
                startActivityForResult(share, SHARE);
                btnShare.setEnabled(false);
            }
        });
    }

    private void saveImage() {
        btnSaveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(Result.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                    if(getFromPer(Result.this, "ALLOWED")) {
                        Intent save = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("pakage", getPackageName(), null);
                        save.setData(uri);
                        startActivityForResult(save, 104);
                    } else {
                        ActivityCompat.requestPermissions(Result.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 103);
                    }
                } else {
                    handleSaving();
                }
            }
        });
    }

    private void handleSaving() {
        Random generator = new Random();
        int n = 1000000;
        n = generator.nextInt(n);
        String mName = "Image-" + n + ".jpg";
        OutputStream fos;
        ContentResolver resolver = Result.this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, mName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            fos = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Objects.requireNonNull(fos);
            Toast.makeText(Result.this, R.string.save_success, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean getFromPer(Result result, String allowed) {
        SharedPreferences preferences = getSharedPreferences("save_img", MODE_PRIVATE);
        return preferences.getBoolean("save_img", false);
    }

    public static void saveToPres(Context context, String key, Boolean allowed) {
        SharedPreferences preferences = context.getSharedPreferences("save_img", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, allowed);
        editor.commit();
    }

    private void search() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(Intent.ACTION_VIEW);
                handleSearch(search);
                startActivityForResult(search, SEARCH);
                btnSearch.setEnabled(false);
            }
        });
    }

    private void handleSearch(Intent search) {
        SharedPreferences prefSearch = getSharedPreferences("search", MODE_PRIVATE);
        String typeSearch = prefSearch.getString("search", "Google");
        switch (typeSearch) {
            case "Google":
                search.setData(Uri.parse("https://www.google.com/search?q=" + content));
                break;
            case "Yandex":
                search.setData(Uri.parse("https://yandex.com/search/?text=" + content));
                break;
            case "CocCoc":
                search.setData(Uri.parse("https://coccoc.com/search?query=" + content));
                break;
            case "Yahoo":
                search.setData(Uri.parse("https://www.search.yahoo.com/search?p=" + content));
                break;
            case "Bing":
                search.setData(Uri.parse("https://www.bing.com/search?q=" + content));
                break;
            case "DuckDuckGo":
                search.setData(Uri.parse("https://duckduckgo.com/?q=" + content));
                break;
        }
    }

    private void coppyToClipboard() {
        btnCoppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", content);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(Result.this, R.string.copy_success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchProduct() {
        btnSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchProduct = new Intent(Intent.ACTION_VIEW);
                handleSearch(searchProduct);
                startActivityForResult(searchProduct, SEARCH_PRODUCT);
                btnSearchProduct.setEnabled(false);
            }
        });
    }

    private void connectWifi() {
        Intent wifi = new Intent(Settings.ACTION_WIFI_SETTINGS);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        btnConnectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + ssid  + "\"";
                    conf.status = WifiConfiguration.Status.DISABLED;
                    conf.priority = 40;
                    if(security.equals("None")) {
                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        conf.allowedAuthAlgorithms.clear();
                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    } else if (security.equals("WEP")) {
                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                        conf.wepKeys[0] = "\"".concat(password).concat("\"");
                    } else if (security.equals("WPA")) {
                        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                        conf.preSharedKey = "\"" + password + "\"";
                    }

                    int networkID = wifiManager.addNetwork(conf);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(networkID, true);
                    wifiManager.reconnect();
                    startActivityForResult(wifi, CONNECT_WIFI);

                    btnConnectWifi.setEnabled(false);
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                    if(security.equals("None")) {
                        startActivity(new Intent("android.settings.panel.action.INTERNET_CONNECTIVITY"));
                    }else {
                        if (password.length() != 0) {
                            WifiNetworkSuggestion.Builder builder = new WifiNetworkSuggestion.Builder();
                            builder.setSsid(ssid);
                            builder.setWpa2Passphrase(password);
                            WifiNetworkSuggestion suggestion = builder.build();
                            List<WifiNetworkSuggestion> list = new ArrayList<WifiNetworkSuggestion>();
                            list.add(suggestion);
                            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            wifiManager.removeNetworkSuggestions(new ArrayList<WifiNetworkSuggestion>());
                            wifiManager.addNetworkSuggestions(list);
                        }
                    }
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Result.this);
                    alertDialog.setTitle("Các thiết bị Android 10 cần kết nối Wifi một cách thủ công")
                            .setMessage("Chuyển hướng đến mạng wifi \n Mật khẩu của bạn đã được sao chép, ấn OK để chuyển hướng đến Wifi")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    coppyToClipboard();
                                    startActivity(new Intent("android.settings.panel.action.INTERNET_CONNECTIVITY"));
                                }
                            }).setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }
        });
    }

    private void toURL() {
        btnToURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toURL = new Intent(Intent.ACTION_VIEW);
                toURL.setData(Uri.parse(content));
                startActivityForResult(toURL, TO_URL);
                btnToURL.setEnabled(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case SHARE:
                btnShare.setEnabled(true);
                break;
            case SEARCH:
                btnSearch.setEnabled(true);
                break;
            case SEARCH_PRODUCT:
                btnSearchProduct.setEnabled(true);
                break;
            case TO_URL:
                btnToURL.setEnabled(true);
                break;
            case CONNECT_WIFI:
                btnConnectWifi.setEnabled(true);
                break;
            case 104:
                if(ActivityCompat.checkSelfPermission(Result.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    handleSaving();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 103:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleSaving();
                    break;
                }
                String perStorage = permissions[0];
                if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    boolean per = ActivityCompat.shouldShowRequestPermissionRationale(Result.this, perStorage);
                    if(!per)
                        saveToPres(Result.this, "ALLOWED", true);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        int sizeQR = 500;
        int sizeWidth = 664;
        int sizeHeight = 204;

        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix matrix = null;
        switch (typeQR) {
            case "QRCode":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, sizeQR, sizeQR, hints);
                break;
            case "AZTEC":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.AZTEC, sizeQR, sizeQR, hints);
                break;
            case "DATA_MATRIX":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.DATA_MATRIX, sizeQR, sizeQR, hints);
                break;
            case "CODABAR":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODABAR, sizeWidth, sizeHeight, hints);
                break;
            case "PDF417":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.PDF_417, sizeWidth, sizeHeight, hints);
                break;
            case "Code_128":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, sizeWidth, sizeHeight, hints);
                break;
            case "Code_39":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_39, sizeWidth, sizeHeight, hints);
                break;
            case "Code_93":
                matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_93, sizeWidth, sizeHeight, hints);
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

        adView = findViewById(R.id.ads_result);
    }

    private void showResult() {
        getResult();
        switch (typeQR) {
            case "QRCode":
                switch (type) {
                    case "Text":
                        txtTitleResult.setText(R.string.text);
                        imgResult.setImageResource(R.drawable.logo_text);
                        getShowResult(type);
                        break;
                    case "URL":
                        txtTitleResult.setText(R.string.url);
                        imgResult.setImageResource(R.drawable.logo_url);
                        getShowResult(type);
                        break;
                    case "Wifi":
                        txtTitleResult.setText(R.string.wifi);
                        ssid = result.getStringExtra("ssid");
                        password = result.getStringExtra("password");
                        if(result.getStringExtra("security").equals("nopass"))
                            security = "None";
                        else
                            security = result.getStringExtra("security");
                        imgResult.setImageResource(R.drawable.logo_wifi);
                        getShowResult(type);
                        break;
                }
                break;
            case "Code_128":
            case "Code_39":
            case "Code_93":
            case "Code_ITF":
            case "AZTEC":
            case "DATA_MATRIX":
            case "CODABAR":
            case "PDF417":
                txtTitleResult.setText(R.string.text);
                imgResult.setImageResource(R.drawable.logo_text);
                getShowResult(type);
                break;
            case "EAN_13":
            case "EAN_8":
            case "UPC_A":
            case "UPC_E":
                txtTitleResult.setText(R.string.search_product);
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
            ssid = result.getStringExtra("ssid");
            password = result.getStringExtra("password");
            if(result.getStringExtra("security").equals("nopass"))
                security = "None";
            else
                security = result.getStringExtra("security");
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
            content = "https://" + content.trim();
        } else if(content.toLowerCase().startsWith("http://")) {
            content = content.substring(7);
            content = "http://" + content.trim();
        } else if(!content.startsWith("http://") || !content.startsWith("https://"))
            content = "https://" + content.trim();
    }

    private void getResult() {
        result = getIntent();
        type = result.getStringExtra("type");
        typeQR = result.getStringExtra("type_qr");
        content = result.getStringExtra("value");
    }
}
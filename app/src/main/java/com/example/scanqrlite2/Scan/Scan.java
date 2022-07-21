package com.example.scanqrlite2.Scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import com.example.scanqrlite2.History.History_Menu.HistoryScanItem;
import com.example.scanqrlite2.History.History_Menu.database.ScanDatabase;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Vibrator;
import android.provider.Settings;
import com.example.scanqrlite2.Language;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanqrlite2.R;
import com.example.scanqrlite2.Result;
import com.example.scanqrlite2.Setting.Setting;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.example.scanqrlite2.History.History_Menu.HistoryScanItem;
import com.example.scanqrlite2.History.History_Menu.database.ScanDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scan extends Fragment{
    TextView btnGallery;
    CheckBox btnFlash;
    PreviewView previewView;
    ExecutorService cameraExecutor;
    ListenableFuture<ProcessCameraProvider> listenableFuture;
    ImageAnalysis.Analyzer analyzer;
    private boolean checkWifi = false;
    Language language;
    AdView adView;
    AdRequest request;

    private FrameLayout layoutScan;
    private RelativeLayout layoutPermisson;
    private Button btnPermisson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       language = new Language(getContext());
        language.Language();
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ORM(view);
        scanByGallery();
        handleImage();
        openCamera();
        checkPermission();
        showAds();
        return view;
    }

    private void showAds() {
        request = new AdRequest.Builder().build();
        adView.loadAd(request);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adView != null)
            adView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adView != null)
            adView.destroy();
    }

    private void checkPermission() {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            onPause();
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 101);
        }
        btnPermisson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePermisson();
            }
        });
    }

    private void handlePermisson() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if(getFromPerf(getActivity(), "ALLOW_KEY")) {
                    showSetting();
                } else if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, 101);
                }
            } else {
                changeLayout(1);
            }
        } else {
            changeLayout(1);
        }
    }

    private void showSetting() {
        Intent accessSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        accessSetting.setData(uri);
        startActivityForResult(accessSetting, 103);
    }

    private static Boolean getFromPerf(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("ALLOW_KEY", Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    private static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences preferences = context.getSharedPreferences("ALLOW_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ALLOW_KEY", allowed);
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        language.Language();
        switch (requestCode) {
            case 101:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeLayout(1);
                    break;
                } else {
                    changeLayout(2);
                }

                for(int i = 0, len = permissions.length; i < len; i++) {
                    String permisson = permissions[i];
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRotinable = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permisson);
                        if(!showRotinable) {
                            saveToPreferences(getActivity(), "ALLOW_KEY", true);
                        }
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handleImage() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        listenableFuture = ProcessCameraProvider.getInstance(requireActivity());
        analyzer = new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                scanBarCode(image);
            }
        };
    }

    private void openCamera() {
        listenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 101);
                    else {
                        ProcessCameraProvider processCameraProvider = listenableFuture.get();
                        bindpreview(processCameraProvider);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void bindpreview(ProcessCameraProvider processCameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.unbindAll();

        ImageCapture imageCapture = new ImageCapture.Builder().build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer( cameraExecutor, analyzer);

        Camera camera = processCameraProvider.bindToLifecycle(getActivity(), cameraSelector, preview,
                imageCapture, imageAnalysis);

        flashSwitch(camera);
    }

    private void flashSwitch(Camera camera) {
        camera.getCameraControl().enableTorch(false);
        btnFlash.setText(R.string.flash_off);
        language.Language();
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnFlash.isChecked()) {
                    camera.getCameraControl().enableTorch(true);
                    btnFlash.setText(R.string.flash_on);
                } else {
                    camera.getCameraControl().enableTorch(false);
                    btnFlash.setText(R.string.flash_off);
                }
            }
        });
    }

    private void scanBarCode(ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
        assert image1 != null;
        InputImage inputImage = InputImage.fromMediaImage(image1, image.getImageInfo().getRotationDegrees());
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC, Barcode.FORMAT_DATA_MATRIX,
                        Barcode.FORMAT_CODE_128, Barcode.FORMAT_PDF417,
                        Barcode.FORMAT_CODE_93,Barcode.FORMAT_EAN_8,Barcode.FORMAT_EAN_13
                        ,Barcode.FORMAT_CODE_39,Barcode.FORMAT_ALL_FORMATS
                        ,Barcode.FORMAT_CODABAR,Barcode.FORMAT_ITF,Barcode.FORMAT_UPC_A,Barcode.FORMAT_UPC_E).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> result = scanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        readerBarcodeData(barcodes);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Barcode>> task) {
                        image.close();
                    }
                });
    }

    String content;
    String ssid, password, type, security;

    @SuppressLint("NewApi")
    private boolean readerBarcodeData(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String value = barcode.getRawValue();
            int valueType = barcode.getValueType();
            int barcodeType = barcode.getFormat();
            String typeQR;
            if(barcodeType == barcode.FORMAT_QR_CODE) {
                typeQR = "QRCode";
                switch (valueType) {
                    case Barcode.TYPE_TEXT:
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.TYPE_URL:
                        content = value;
                        type = "URL";
                        break;
                    case Barcode.TYPE_WIFI:
                        content = barcode.getWifi().getSsid();
                        ssid = barcode.getWifi().getSsid();
                        password = barcode.getWifi().getPassword();
                        security = getSecurity(barcode.getWifi().getEncryptionType());
                        type = "Wifi";
                        checkWifi=true;
                        break;
                    default:
                        return false;
                }
            } else {
                switch (barcodeType) {
                    case Barcode.FORMAT_CODE_128:
                        typeQR = "Code_128";
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.FORMAT_CODE_39:
                        typeQR = "Code_39";
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.FORMAT_ITF:
                        typeQR = "Code_ITF";
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.FORMAT_CODE_93:
                        typeQR = "Code_93";
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.FORMAT_EAN_13:
                        typeQR = "EAN_13";
                        content = value;
                        type = "Product";
                        break;
                    case Barcode.FORMAT_EAN_8:
                        typeQR = "EAN_8";
                        content = value;
                        type = "Product";
                        break;
                    case Barcode.FORMAT_UPC_A:
                        typeQR = "UPC_A";
                        content = value;
                        type = "Product";
                        break;
                    case Barcode.FORMAT_UPC_E:
                        typeQR = "UPC_E";
                        content = value;
                        type = "Product";
                        break;
                    case Barcode.FORMAT_AZTEC:
                        typeQR = "AZTEC";
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.FORMAT_DATA_MATRIX:
                        typeQR = "DATA_MATRIX";
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.FORMAT_CODABAR:
                        typeQR = "CODABAR";
                        content = value;
                        type = "Text";
                        break;
                    case Barcode.FORMAT_PDF417:
                        typeQR = "PDF417";
                        content = value;
                        type = "Text";
                        break;
                    default:
                        return false;
                }
            }
            toResult(type, value, typeQR);
            if(doCopy())
                copyTextToClipboard(value);
            HistoryScanItem historyScanItem = new HistoryScanItem(type, content, value);
            historyScanItem.setTypeScan(typeQR);
            if(checkWifi) {
                historyScanItem.setSecurity(security);
                historyScanItem.setPassword(password);
            }
            ScanDatabase.getInstance(getContext()).scanItemDAO().insertItem(historyScanItem);
            Vibrate();
            Beep();
            onPause();
            break;
        }
        return true;
    }

    private void toResult(String mType, String mContent, String mTypeQR) {
        Intent result = new Intent(getContext(), Result.class);

        if(mType.equals("Product")) {
            result.putExtra("value", mContent);
        } else if(mType.equals("Text")) {
            result.putExtra("value", mContent);
        } else if (mType.equals("URL")) {
            result.putExtra("value", mContent);
        } else if (mType.equals("Wifi")) {
            result.putExtra("value", mContent);
            result.putExtra("ssid", ssid);
            result.putExtra("password", password);
            result.putExtra("security", security);
        }
        result.putExtra("type", mType);
        result.putExtra("type_qr", mTypeQR);
        startActivity(result);
    }

    private String month(int numMonth) {
        switch (numMonth) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }
    }

    private String getSecurity(int encryptionType) {
        if(encryptionType == 2)
            return "WPA";
        else if (encryptionType == 3)
            return "WEP";
        else
            return "nopass";
    }

    private void scanByGallery() {
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                btnGallery.setEnabled(false);
                btnFlash.setChecked(false);
                startActivityForResult(intent, 102);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 102:
                btnGallery.setEnabled(true);
                if(data == null || data.getData() == null) {
                    return;
                }

                Uri uri = data.getData();
                InputStream inputStream = null;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
                scanBarcodeGallery(inputImage);

                break;
            case 103:
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    changeLayout(1);
                break;

        }
    }

    private void scanBarcodeGallery(InputImage inputImage) {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC, Barcode.FORMAT_DATA_MATRIX,
                        Barcode.FORMAT_CODE_128, Barcode.FORMAT_PDF417,
                        Barcode.FORMAT_CODE_93,Barcode.FORMAT_EAN_8,Barcode.FORMAT_EAN_13
                        ,Barcode.FORMAT_CODE_39,Barcode.FORMAT_ALL_FORMATS
                        ,Barcode.FORMAT_CODABAR,Barcode.FORMAT_ITF,Barcode.FORMAT_UPC_A,Barcode.FORMAT_UPC_E).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> result = scanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        language.Language();
                        if(barcodes.isEmpty()) {
                            Toast.makeText(getActivity(), R.string.qr_barcode_not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            if(!readerBarcodeData(barcodes))
                                Toast.makeText(getActivity(), R.string.qr_barcode_not_found, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changeLayout(int num) {
        if(num == 1) {
            layoutScan.setVisibility(View.VISIBLE);
            layoutPermisson.setVisibility(View.GONE);
        } else if(num == 2){
            onPause();
            layoutScan.setVisibility(View.GONE);
            layoutPermisson.setVisibility(View.VISIBLE);
        }
    }

    ProcessCameraProvider processCameraProvider = null;

    public boolean doCopy(){
        SharedPreferences coppy ;
        coppy = getActivity().getSharedPreferences("clipboard",0 );
        boolean check = coppy.getBoolean("clipboard",false);
        if(check){
            return true;
        }
        return false;
    }

    private void copyTextToClipboard(String content) {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", content);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), R.string.copy_success, Toast.LENGTH_SHORT).show();
    }

    public void Beep(){
        SharedPreferences beep;
        beep = getContext().getSharedPreferences("beep",0);
        boolean check = beep.getBoolean("beep",false);
        if(check ==true){
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 500);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
        }

    }
    public void Vibrate(){
        SharedPreferences vibrate;
        vibrate = getContext().getSharedPreferences("vibrate",0);
        boolean check = vibrate.getBoolean("vibrate", false);
        if(check == true){
            Viber(getContext(),"on");
        }
        else
            Viber(getContext(),"off");
    }

    @JavascriptInterface
    public void Viber(Context ct, String value){
        if(value.equals("on")) {
            Vibrator v = (Vibrator) ct.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(300);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            processCameraProvider = listenableFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processCameraProvider.unbindAll();
        btnFlash.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(adView != null)
            adView.resume();

        try {
            processCameraProvider = listenableFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bindpreview(processCameraProvider);
    }

    private void ORM(View view) {
        btnGallery = view.findViewById(R.id.btn_gallery);
        btnFlash = view.findViewById(R.id.btn_fash);
        previewView = view.findViewById(R.id.cameraPreviewView);
        layoutScan = view.findViewById(R.id.layout_scan);
        layoutPermisson = view.findViewById(R.id.layout_permisson);
        btnPermisson = view.findViewById(R.id.btn_permisson);
        adView = view.findViewById(R.id.ads_scan);
    }
}
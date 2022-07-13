package com.example.scanqrlite2.Scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
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
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanqrlite2.R;
import com.example.scanqrlite2.Setting.Setting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scan extends Fragment {
    TextView btnGallery;
    CheckBox btnFlash;
    PreviewView previewView;
    ExecutorService cameraExecutor;
    ListenableFuture<ProcessCameraProvider> listenableFuture;
    ImageAnalysis.Analyzer analyzer;

    private FrameLayout layoutScan;
    private RelativeLayout layoutPermisson;
    private Button btnPermisson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ORM(view);
        scanByGallery();
        handleImage();
        openCamera();
        checkPermission();
        return view;
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

    @Override
    public void onStart() {
        super.onStart();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED)
//                changeLayout(2);
//            else
//                changeLayout(1);
//        }else
//            changeLayout(1);
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
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnFlash.isChecked()) {
                    camera.getCameraControl().enableTorch(true);
                    btnFlash.setText("Flash on");
                } else {
                    camera.getCameraControl().enableTorch(false);
                    btnFlash.setText("Flash off");
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
                        Barcode.FORMAT_AZTEC,
                        Barcode.FORMAT_CODE_128,
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

    @SuppressLint("NewApi")
    private void readerBarcodeData(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String value = barcode.getRawValue();
            int valueType = barcode.getValueType();
            int barcodeType = barcode.getFormat();

            if(barcodeType == barcode.FORMAT_QR_CODE) {
                switch (valueType) {
                    case Barcode.TYPE_TEXT:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_URL:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_WIFI:
                        Toast.makeText(getActivity(), barcode.getWifi().getSsid() + "\n" + barcode.getWifi().getPassword() + "\n"
                                + barcode.getWifi().getEncryptionType(), Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_PHONE:
                        Toast.makeText(getActivity(), barcode.getPhone().getNumber(), Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_EMAIL:
                        Toast.makeText(getActivity(), barcode.getEmail().getAddress()
                                + "\n" + barcode.getEmail().getSubject()
                                + "\n" + barcode.getEmail().getBody(), Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_SMS:
                        Toast.makeText(getActivity(), barcode.getSms().getPhoneNumber() + "\n"
                                + barcode.getSms().getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_CALENDAR_EVENT:
                        Toast.makeText(getActivity(), barcode.getCalendarEvent().getSummary()
                                + "\n" + barcode.getCalendarEvent().getStart().getHours() + ":"
                                 + barcode.getCalendarEvent().getStart().getMinutes() + ", "
                                + barcode.getCalendarEvent().getStart().getDay() + " / "
                                + month(barcode.getCalendarEvent().getStart().getMonth()) + " / "
                                 + barcode.getCalendarEvent().getStart().getYear()
                                + "\n" + barcode.getCalendarEvent().getEnd().getHours() + ":"
                                + barcode.getCalendarEvent().getEnd().getMinutes() + ", "
                                 + barcode.getCalendarEvent().getEnd().getDay() + " / "
                                 + month(barcode.getCalendarEvent().getEnd().getMonth()) + " / "
                                 + barcode.getCalendarEvent().getEnd().getYear()
                                + "\n" + barcode.getCalendarEvent().getLocation()
                                + "\n" + barcode.getCalendarEvent().getDescription(), Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_CONTACT_INFO:
                        Toast.makeText(getActivity(), barcode.getContactInfo().getName().getFormattedName()
                                + "\n" + barcode.getContactInfo().getPhones().get(1).getNumber()
                                + "\n" + barcode.getContactInfo().getPhones().get(0).getNumber()
                                + "\n" + barcode.getContactInfo().getPhones().get(2).getNumber()
                                + "\n" + barcode.getContactInfo().getEmails().get(0).getAddress()
                                + "\n" + barcode.getContactInfo().getUrls().get(0)
                                + "\n" + barcode.getContactInfo().getTitle()
                                + "\n" + Arrays.toString(barcode.getContactInfo().getAddresses().get(0).getAddressLines().clone()), Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.TYPE_GEO:
                        Toast.makeText(getActivity(), barcode.getGeoPoint().getLat() + "\n"
                                + barcode.getGeoPoint().getLng(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return;
                }
            } else {
                switch (barcodeType) {
                    case Barcode.FORMAT_CODE_128:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.FORMAT_CODE_39:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.FORMAT_EAN_13:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.FORMAT_EAN_8:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.FORMAT_ITF:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.FORMAT_UPC_A:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    case Barcode.FORMAT_UPC_E:
                        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return;
                }
            }
            onPause();
            break;
        }
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
                break;
            case 103:
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    changeLayout(1);
                break;

        }
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
    }
}
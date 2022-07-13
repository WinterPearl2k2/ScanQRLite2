package com.example.scanqrlite2.Scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.scanqrlite2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scan extends Fragment {
    TextView btnGallery;
    CheckBox btnFlash;
    PreviewView previewView;
    ExecutorService cameraExecutor;
    ListenableFuture<ProcessCameraProvider> listenableFuture;
    ImageAnalysis.Analyzer analyzer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ORM(view);
        scanByGallery();
        openFlash();
        cameraExecutor = Executors.newSingleThreadExecutor();
        listenableFuture = ProcessCameraProvider.getInstance(requireActivity());
        analyzer = new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                scanBarCode(image);
            }
        };
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
        return view;
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
    }

    private void scanBarCode(ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
        assert image1 != null;
        InputImage inputImage = InputImage.fromMediaImage(image1, image.getImageInfo().getRotationDegrees());
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC).build();
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

    private void readerBarcodeData(List<Barcode> barcodes) {
    }

    private void openFlash() {
        if(btnFlash.isChecked())
            btnFlash.setText("Flash on");
        else
            btnFlash.setText("Flash off");
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnFlash.isChecked())
                    btnFlash.setText("Flash on");
                else
                    btnFlash.setText("Flash off");
            }
        });
    }

    private void scanByGallery() {
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                btnGallery.setEnabled(false);
                startActivityForResult(intent, 102);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102) {
            btnGallery.setEnabled(true);
        }
    }

    private void ORM(View view) {
        btnGallery = view.findViewById(R.id.btn_gallery);
        btnFlash = view.findViewById(R.id.btn_fash);
        previewView = view.findViewById(R.id.cameraPreviewView);
    }
}
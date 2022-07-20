package com.example.scanqrlite2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanqrlite2.History.History_Menu.HistoryScanItem;
import com.example.scanqrlite2.R;
import com.example.scanqrlite2.Result;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;
import java.util.List;

public class HistoryScanAdapter extends RecyclerView.Adapter <HistoryScanAdapter.ScanViewHolder> {

    List<HistoryScanItem> scanItemList;
    Bitmap bitmap;
    Context context;

    public HistoryScanAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<HistoryScanItem> data) {
        this.scanItemList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        return new ScanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanViewHolder holder, int position) {

//        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_scale);
//        holder.itemView.startAnimation(animation);

        HistoryScanItem historyScanItem = scanItemList.get(position);
        if (historyScanItem == null) {
            return;
        }

        holder.txtTitle.setText(historyScanItem.getTitle());
        holder.txtContent.setText(historyScanItem.getContent());

        try {
            holder.imgQr.setImageBitmap(CreateImage(historyScanItem.getResult(), historyScanItem.getTypeScan()));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Result.class);
                String valueType = historyScanItem.getTypeScan();
                switch (valueType) {
                    case "QRCode":
                        intent.putExtra("value", historyScanItem.getResult());
                        if(historyScanItem.getTitle().equals("Wifi")) {
                            intent.putExtra("ssid", historyScanItem.getContent());
                            intent.putExtra("password", historyScanItem.getPassword());
                            intent.putExtra("security", historyScanItem.getSecurity());
                        }
                        intent.putExtra("type", "QRCode");
                        intent.putExtra("type_qr", historyScanItem.getTypeScan());
                        break;
                    case "Code_128":
                    case "Code_39":
                    case "EAN_13":
                    case "EAN_8":
                    case "Code_ITF":
                    case "UPC_A":
                    case "UPC_E":
                        intent.putExtra("value", historyScanItem.getResult());
                        intent.putExtra("type", "Barcode");
                        intent.putExtra("type_qr", historyScanItem.getTypeScan());
                        break;
                }

                context.startActivity(intent);
            }
        });
    }

    private Bitmap CreateImage(String result, String content) throws WriterException {
        int sizeWidth = 660;
        int sizeHeight = 264;


        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = null;
        String type = content;
        switch (type) {
            case "QRCode":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.QR_CODE, sizeWidth, sizeWidth, hints);
                break;
            case "EAN_13":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.EAN_13, sizeWidth, sizeHeight, hints);
                break;
            case "EAN_8":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.EAN_8, sizeWidth, sizeHeight, hints);
                break;
            case "UPC_A":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.UPC_A, sizeWidth, sizeHeight, hints);
                break;
            case "UPC_E":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.UPC_E, sizeWidth, sizeHeight, hints);
                break;
            case "Code_128":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.CODE_128, sizeWidth, sizeHeight, hints);
                break;
            case "Code_ITF":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.ITF, sizeWidth, sizeHeight, hints);
                break;
            case "Code_39":
                matrix = new MultiFormatWriter().encode(result, BarcodeFormat.CODE_39, sizeWidth, sizeHeight, hints);
                break;
        }

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixel = new int[width * height];
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if (matrix.get(j, i))
                    pixel[i * width + j] = 0xff000000;
                else
                    pixel[i * width + j] = 0xffffffff;
            }
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixel, 0, width, 0 , 0, width, height);

        return bitmap;
    }

    @Override
    public int getItemCount() {
        return scanItemList == null ? 0 : scanItemList.size();
    }

    public class ScanViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutItem;
        private TextView txtTitle, txtContent;
        private ImageView imgQr;

        public ScanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title_history_item);
            txtContent = itemView.findViewById(R.id.infor_history_item);
            imgQr = itemView.findViewById(R.id.qr_image_history_item);
            layoutItem = itemView.findViewById(R.id.history_recycleview_item);
        }
    }
}

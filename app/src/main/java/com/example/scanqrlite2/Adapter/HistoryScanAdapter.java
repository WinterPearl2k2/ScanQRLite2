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
        holder.imgQr.setImageResource(CreateImage(holder.txtTitle.getText().toString()));

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
                        intent.putExtra("type", holder.txtTitle.getText().toString());
                        intent.putExtra("type_qr", "QRCode");
                        break;
                    case "Code_128":
                    case "Code_39":
                    case "EAN_13":
                    case "EAN_8":
                    case "Code_ITF":
                    case "UPC_A":
                    case "UPC_E":
                    case "AZTEC":
                    case "DATA_MATRIX":
                    case "CODABAR":
                    case "PDF417":
                        intent.putExtra("value", historyScanItem.getResult());
                        intent.putExtra("type", holder.txtTitle.getText().toString());
                        intent.putExtra("type_qr", historyScanItem.getTypeScan());
                        break;
                }

                context.startActivity(intent);
            }
        });
    }

    private int CreateImage(String content){
        switch (content){
            case "Wifi":
                return R.drawable.ic_img_wifi_circle;
            case "URL":
                return R.drawable.ic_img_url_circle;
            case "Text":
                return R.drawable.ic_img_text_circle;
            case "Product":
                return R.drawable.ic_img_product_circle;
            default:
                return R.drawable.ic_img_text_circle;
        }
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

package com.example.scanqrlite2.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scanqrlite2.R;

import com.example.scanqrlite2.History.History_Menu.HistoryCreateItem;
import com.example.scanqrlite2.Result;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.skydoves.powermenu.CircularEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import java.util.List;

public class HistoryCreateAdaper extends RecyclerView.Adapter<HistoryCreateAdaper.CreateViewHolder> {

    List<HistoryCreateItem> createItemList;
    Bitmap bitmap;
    Context context;

    public HistoryCreateAdaper(Context context){ this.context = context;}
    public void setData(List<HistoryCreateItem> data) {
        this.createItemList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        return new CreateViewHolder(view);
    }

    public  void onBindViewHolder (@NonNull CreateViewHolder holder, int position){

//        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_scale);
//        holder.itemView.startAnimation(animation);

        HistoryCreateItem historyCreateItem = createItemList.get(position);
        if(historyCreateItem == null) {
            return;
        }

        holder.txtTitle.setText(historyCreateItem.getTitle());
        holder.txtContent.setText(historyCreateItem.getContent());
        try {
            holder.imgQr.setImageBitmap(CreateImage(historyCreateItem.getResult()));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Result.class);
                intent.putExtra("value", historyCreateItem.getResult());
                if(historyCreateItem.getTitle().equals("Wifi")) {
                    intent.putExtra("ssid", historyCreateItem.getContent());
                    intent.putExtra("password", historyCreateItem.getPassword());
                    intent.putExtra("security", historyCreateItem.getSecurity());
                }
                intent.putExtra("type", "QRCode");
                intent.putExtra("type_barcode", "QRCode");
                context.startActivity(intent);
            }
        });

    }


    private Bitmap CreateImage(String content) throws WriterException {
        int sizeWidth = 660;
        int sizeHeight = 264;

        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, sizeWidth, sizeWidth, hints);
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
        return createItemList == null ? 0 : createItemList.size();
    }

    public class CreateViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutItem;
        private TextView txtTitle, txtContent;
        private ImageView imgQr;

        public CreateViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title_history_item);
            txtContent = itemView.findViewById(R.id.infor_history_item);
            imgQr = itemView.findViewById(R.id.qr_image_history_item);
            layoutItem = itemView.findViewById(R.id.history_recycleview_item);
        }
    }

}

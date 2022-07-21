package com.example.scanqrlite2.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scanqrlite2.R;

import com.example.scanqrlite2.History.History_Menu.HistoryCreateItem;
import com.example.scanqrlite2.Result;
import com.google.zxing.WriterException;

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

        HistoryCreateItem historyCreateItem = createItemList.get(position);
        if(historyCreateItem == null) {
            return;
        }

        holder.txtTitle.setText(historyCreateItem.getTitle());
        holder.txtContent.setText(historyCreateItem.getContent());
        holder.imgQr.setImageResource(CreateImage(holder.txtTitle.getText().toString()));

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
                intent.putExtra("type", holder.txtTitle.getText().toString());
                intent.putExtra("type_qr", "QRCode");
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
            default:
                return R.drawable.ic_img_text_circle;
        }
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

package com.jiny.liftlink.inquiry.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jiny.liftlink.R;
import com.jiny.liftlink.inquiry.data.Inquiry;

import java.text.SimpleDateFormat;
import java.util.List;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.InquiryViewHolder> {
    // timestamp는 적절한 형식으로 표시
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Inquiry> inquiryList;

    public InquiryAdapter(List<Inquiry> inquiryList) {
        this.inquiryList = inquiryList;
    }

    @NonNull
    @Override
    public InquiryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_inquiry.xml 레이아웃을 인플레이트하여 ViewHolder에 전달
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inquiry, parent, false);
        return new InquiryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InquiryViewHolder holder, int position) {
        Inquiry inquiry = inquiryList.get(position);

        // 각 필드를 View에 바인딩
        holder.textDuration.setText(inquiry.getDuration());
        holder.textHeight.setText(inquiry.getHeight());
        holder.textRegion.setText(inquiry.getRegion());

        holder.textTimestamp.setText(sdf.format(inquiry.getTimestamp()));

        holder.textVehicle.setText(inquiry.getVehicle());
        holder.textWork.setText(inquiry.getWork());
    }

    @Override
    public int getItemCount() {
        return inquiryList.size();
    }

    public static class InquiryViewHolder extends RecyclerView.ViewHolder {
        TextView textDuration, textHeight, textRegion, textTimestamp, textVehicle, textWork;

        public InquiryViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_inquiry.xml에서 뷰를 연결
            textDuration = itemView.findViewById(R.id.text_duration);
            textHeight = itemView.findViewById(R.id.text_height);
            textRegion = itemView.findViewById(R.id.text_region);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            textVehicle = itemView.findViewById(R.id.text_vehicle);
            textWork = itemView.findViewById(R.id.text_work);
        }
    }
}


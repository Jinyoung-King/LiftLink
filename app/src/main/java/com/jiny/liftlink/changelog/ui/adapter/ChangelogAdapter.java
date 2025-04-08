package com.jiny.liftlink.changelog.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jiny.liftlink.R;
import com.jiny.liftlink.changelog.data.model.Changelog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChangelogAdapter extends RecyclerView.Adapter<ChangelogAdapter.ChangelogViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Changelog changelog);
    }

    private final OnItemClickListener listener;
    private List<Changelog> changelogList = new ArrayList<>();

    public ChangelogAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setChangelogList(List<Changelog> list) {
        this.changelogList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChangelogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_changelog, parent, false);
        return new ChangelogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangelogViewHolder holder, int position) {
        Changelog changelog = changelogList.get(position);
        holder.bind(changelog, listener);
    }

    @Override
    public int getItemCount() {
        return changelogList.size();
    }

    static class ChangelogViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView tagTextView;
        private final TextView dateTextView;

        public ChangelogViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textTitle);
            tagTextView = itemView.findViewById(R.id.textTag);
            dateTextView = itemView.findViewById(R.id.textDate);
        }

        public void bind(Changelog changelog, OnItemClickListener listener) {
            titleTextView.setText(changelog.getTitle());
            tagTextView.setText("#" + changelog.getTag());

            // timestamp -> formatted date
            String formattedDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                    .format(new Date(changelog.getTimestamp()));
            dateTextView.setText(formattedDate);

            itemView.setOnClickListener(v -> listener.onItemClick(changelog));
        }
    }
}

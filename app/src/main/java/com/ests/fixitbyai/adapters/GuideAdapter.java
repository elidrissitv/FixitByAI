package com.ests.fixitbyai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ests.fixitbyai.R;
import com.ests.fixitbyai.database.Guide;

public class GuideAdapter extends ListAdapter<Guide, GuideAdapter.GuideViewHolder> {
    private final OnGuideClickListener listener;

    public interface OnGuideClickListener {
        void onGuideClick(Guide guide);
    }

    public GuideAdapter(OnGuideClickListener listener) {
        super(new GuideDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guide, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        Guide guide = getItem(position);
        holder.bind(guide, listener);
    }

    static class GuideViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView descriptionView;
        private final TextView categoryView;

        GuideViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.guide_title);
            descriptionView = itemView.findViewById(R.id.guide_description);
            categoryView = itemView.findViewById(R.id.guide_category);
        }

        void bind(Guide guide, OnGuideClickListener listener) {
            titleView.setText(guide.getTitle());
            descriptionView.setText(guide.getDescription());
            categoryView.setText(guide.getCategory());
            itemView.setOnClickListener(v -> listener.onGuideClick(guide));
        }
    }

    static class GuideDiffCallback extends DiffUtil.ItemCallback<Guide> {
        @Override
        public boolean areItemsTheSame(@NonNull Guide oldItem, @NonNull Guide newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Guide oldItem, @NonNull Guide newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                   oldItem.getDescription().equals(newItem.getDescription()) &&
                   oldItem.getCategory().equals(newItem.getCategory());
        }
    }
} 
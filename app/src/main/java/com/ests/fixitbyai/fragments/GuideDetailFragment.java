package com.ests.fixitbyai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.ests.fixitbyai.R;
import com.ests.fixitbyai.database.Guide;
import com.ests.fixitbyai.viewmodels.GuideViewModel;

public class GuideDetailFragment extends Fragment {
    private GuideViewModel viewModel;
    private ImageView imageView;
    private TextView titleView;
    private TextView descriptionView;
    private TextView stepsView;
    private TextView difficultyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_detail, container, false);

        imageView = view.findViewById(R.id.guide_image);
        titleView = view.findViewById(R.id.guide_title);
        descriptionView = view.findViewById(R.id.guide_description);
        stepsView = view.findViewById(R.id.guide_steps);
        difficultyView = view.findViewById(R.id.guide_difficulty);

        int guideId = getArguments() != null ? getArguments().getInt("guide_id", -1) : -1;
        if (guideId != -1) {
            setupViewModel(guideId);
        }

        return view;
    }

    private void setupViewModel(int guideId) {
        viewModel = new ViewModelProvider(this).get(GuideViewModel.class);
        viewModel.getGuideById(guideId).observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(Guide guide) {
        if (guide != null) {
            titleView.setText(guide.getTitle());
            descriptionView.setText(guide.getDescription());
            stepsView.setText(guide.getSteps());
            difficultyView.setText(getString(R.string.difficulty_format, guide.getDifficulty()));

            if (guide.getImageUrl() != null && !guide.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(guide.getImageUrl())
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
} 
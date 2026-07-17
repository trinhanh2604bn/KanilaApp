package com.example.frontend.feature.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import ui.account.BeautyProfileActivity;
import java.util.ArrayList;
import java.util.List;

public class SkinMatchScoreFragment extends Fragment {
    private static final String ARG_SCORE = "score";
    private static final String ARG_LEVEL = "level";
    private static final String ARG_CHIPS = "chips";

    public static SkinMatchScoreFragment newInstance(int score, String level, List<String> chips) {
        SkinMatchScoreFragment fragment = new SkinMatchScoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putString(ARG_LEVEL, level);
        args.putStringArrayList(ARG_CHIPS, new ArrayList<>(chips));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skin_match_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        view.findViewById(R.id.btnTopBarBack).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
        });
        
        TextView tvTitle = view.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText("Skin Match Score");
        
        if (getArguments() != null) {
            int score = getArguments().getInt(ARG_SCORE);
            String level = getArguments().getString(ARG_LEVEL);
            List<String> chips = getArguments().getStringArrayList(ARG_CHIPS);
            
            TextView tvScore = view.findViewById(R.id.tvScoreLarge);
            TextView tvLevel = view.findViewById(R.id.tvScoreLevel);
            ChipGroup cgChips = view.findViewById(R.id.cgProfileChips);
            
            if (tvScore != null) tvScore.setText(score + "%");
            if (tvLevel != null) tvLevel.setText(level);
            
            if (cgChips != null && chips != null) {
                for (String chipText : chips) {
                    Chip chip = new Chip(getContext());
                    chip.setText(chipText);
                    chip.setChipBackgroundColorResource(R.color.background_main);
                    chip.setChipStrokeColorResource(R.color.border_divider);
                    chip.setChipStrokeWidth(1f);
                    cgChips.addView(chip);
                }
            }

            RecyclerView rvReasons = view.findViewById(R.id.rvReasons);
            if (rvReasons != null) {
                // TODO: Set adapter for reasons
                rvReasons.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            }
        }

        View btnEdit = view.findViewById(R.id.tvEditSkinProfile);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), BeautyProfileActivity.class));
            });
        }

        View btnUpdate = view.findViewById(R.id.tvUpdateProfileLink);
        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), BeautyProfileActivity.class));
            });
        }
    }
}

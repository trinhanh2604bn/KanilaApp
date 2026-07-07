package com.example.frontend.feature.product;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProductInfoDetailFragment extends Fragment {
    public enum InfoMode {
        DESCRIPTION, INGREDIENTS, USAGE
    }

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_MODE = "mode";

    public static ProductInfoDetailFragment newInstance(String title, String content, InfoMode mode) {
        ProductInfoDetailFragment fragment = new ProductInfoDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductInfoDetailFragment newInstance(String title, String content) {
        return newInstance(title, content, InfoMode.DESCRIPTION);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_info_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTopBar(view);

        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
            String content = getArguments().getString(ARG_CONTENT);
            InfoMode mode = (InfoMode) getArguments().getSerializable(ARG_MODE);
            if (mode == null) mode = InfoMode.DESCRIPTION;

            renderHeader(view, title, mode);
            renderContent(view, content, mode);
        }
    }

    private void setupTopBar(View view) {
        View btnBack = view.findViewById(R.id.btnTopBarBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }

        View btnSearch = view.findViewById(R.id.btnTopBarSearch);
        if (btnSearch != null) {
            btnSearch.setVisibility(View.GONE);
        }
    }

    private void renderHeader(View view, String title, InfoMode mode) {
        TextView tvBarTitle = view.findViewById(R.id.tvTopBarTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvInfoSubtitle);
        ImageView ivIcon = view.findViewById(R.id.ivInfoIcon);

        if (tvBarTitle != null) tvBarTitle.setText(title);

        if (tvSubtitle != null) {
            switch (mode) {
                case DESCRIPTION:
                    tvSubtitle.setText("Tìm hiểu chi tiết hơn về công dụng và đặc điểm của sản phẩm");
                    if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_list);
                    break;
                case INGREDIENTS:
                    tvSubtitle.setText("Xem nhanh các thành phần nổi bật trong công thức");
                    if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_drops_filled);
                    break;
                case USAGE:
                    tvSubtitle.setText("Cách dùng đúng để đạt hiệu quả tối ưu");
                    if (ivIcon != null) ivIcon.setImageResource(R.drawable.ic_routine);
                    break;
            }
        }
    }

    private void renderContent(View view, String content, InfoMode mode) {
        View layoutDescription = view.findViewById(R.id.layoutDescriptionMode);
        RecyclerView rvIngredients = view.findViewById(R.id.rvIngredients);
        RecyclerView rvUsage = view.findViewById(R.id.rvUsageSteps);
        View layoutEmpty = view.findViewById(R.id.layoutEmpty);
        View cardUsageNote = view.findViewById(R.id.cardUsageNote);

        // Reset visibility
        layoutDescription.setVisibility(View.GONE);
        rvIngredients.setVisibility(View.GONE);
        rvUsage.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        cardUsageNote.setVisibility(View.GONE);

        if (TextUtils.isEmpty(content)) {
            showEmptyState(view, mode);
            return;
        }

        switch (mode) {
            case DESCRIPTION:
                layoutDescription.setVisibility(View.VISIBLE);
                TextView tvDescription = view.findViewById(R.id.tvDescriptionContent);
                if (tvDescription != null) tvDescription.setText(content);
                break;

            case INGREDIENTS:
                rvIngredients.setVisibility(View.VISIBLE);
                setupIngredientsList(rvIngredients, content);
                break;

            case USAGE:
                rvUsage.setVisibility(View.VISIBLE);
                cardUsageNote.setVisibility(View.VISIBLE);
                setupUsageSteps(rvUsage, content);
                break;
        }
    }

    private void showEmptyState(View view, InfoMode mode) {
        View layoutEmpty = view.findViewById(R.id.layoutEmpty);
        if (layoutEmpty == null) return;
        layoutEmpty.setVisibility(View.VISIBLE);

        TextView tvEmptyDesc = layoutEmpty.findViewById(R.id.tvEmptyStateDescription);
        TextView tvEmptyTitle = layoutEmpty.findViewById(R.id.tvEmptyStateTitle);
        
        if (tvEmptyTitle != null) tvEmptyTitle.setText("Chưa có thông tin");

        if (tvEmptyDesc != null) {
            switch (mode) {
                case DESCRIPTION:
                    tvEmptyDesc.setText("Thông tin mô tả đang được cập nhật");
                    break;
                case INGREDIENTS:
                    tvEmptyDesc.setText("Thông tin thành phần đang được cập nhật");
                    break;
                case USAGE:
                    tvEmptyDesc.setText("Hướng dẫn sử dụng đang được cập nhật");
                    break;
            }
        }
    }

    private void setupIngredientsList(RecyclerView rv, String content) {
        List<String> ingredients = Arrays.stream(content.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (ingredients.isEmpty()) {
            rv.setVisibility(View.GONE);
            showEmptyState((View) rv.getParent(), InfoMode.INGREDIENTS);
            return;
        }

        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(new RecyclerView.Adapter<IngredientViewHolder>() {
            @NonNull
            @Override
            public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_chip, parent, false);
                return new IngredientViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
                holder.tvName.setText(ingredients.get(position));
            }

            @Override
            public int getItemCount() {
                return ingredients.size();
            }
        });
    }

    private void setupUsageSteps(RecyclerView rv, String content) {
        // Simple parsing by newline or "Bước"
        String[] lines = content.split("\n|(?=Bước)");
        List<String> steps = Arrays.stream(lines)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (steps.isEmpty()) {
            rv.setVisibility(View.GONE);
            showEmptyState((View) rv.getParent(), InfoMode.USAGE);
            return;
        }

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new RecyclerView.Adapter<UsageStepViewHolder>() {
            @NonNull
            @Override
            public UsageStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usage_step, parent, false);
                return new UsageStepViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull UsageStepViewHolder holder, int position) {
                holder.tvNumber.setText(String.valueOf(position + 1));
                holder.tvDesc.setText(steps.get(position));
            }

            @Override
            public int getItemCount() {
                return steps.size();
            }
        });
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        IngredientViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
        }
    }

    static class UsageStepViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvDesc;
        UsageStepViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvStepNumber);
            tvDesc = itemView.findViewById(R.id.tvStepDescription);
        }
    }
}

package com.example.frontend.feature.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.feature.product.adapter.ReviewAdapter;
import java.util.ArrayList;

public class ReviewListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.rvReviews);
        ReviewAdapter adapter = new ReviewAdapter();
        rv.setAdapter(adapter);
        
        ArrayList<Object> dummy = new ArrayList<>();
        dummy.add(new Object());
        dummy.add(new Object());
        dummy.add(new Object());
        adapter.setReviews(dummy);
    }
}

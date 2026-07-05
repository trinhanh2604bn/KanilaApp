package ui.commerce;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddressPickerBottomSheet extends BottomSheetDialog {

    public interface OnItemSelectedListener {
        void onItemSelected(String item);
    }

    private String title;
    private List<String> allItems;
    private List<String> filteredItems;
    private OnItemSelectedListener listener;
    private PickerAdapter adapter;

    public AddressPickerBottomSheet(@NonNull Context context, String title, List<String> items, OnItemSelectedListener listener) {
        super(context);
        this.title = title;
        this.allItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_address_picker);

        TextView tvTitle = findViewById(R.id.tvPickerTitle);
        if (tvTitle != null) tvTitle.setText(title);

        View ivClose = findViewById(R.id.ivClosePicker);
        if (ivClose != null) ivClose.setOnClickListener(v -> dismiss());

        EditText edtSearch = findViewById(R.id.edtPickerSearch);
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        RecyclerView rvList = findViewById(R.id.rvPickerList);
        if (rvList != null) {
            rvList.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PickerAdapter();
            rvList.setAdapter(adapter);
        }
    }

    private void filter(String query) {
        filteredItems.clear();
        if (query.isEmpty()) {
            filteredItems.addAll(allItems);
        } else {
            String lowerQuery = query.toLowerCase();
            for (String item : allItems) {
                if (item.toLowerCase().contains(lowerQuery)) {
                    filteredItems.add(item);
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private class PickerAdapter extends RecyclerView.Adapter<PickerAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_picker_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = filteredItems.get(position);
            holder.tvName.setText(item);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemSelected(item);
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return filteredItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvPickerItemName);
            }
        }
    }
}

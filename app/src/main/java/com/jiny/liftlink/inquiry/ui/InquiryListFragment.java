package com.jiny.liftlink.inquiry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jiny.liftlink.R;
import com.jiny.liftlink.inquiry.data.Inquiry;
import com.jiny.liftlink.inquiry.ui.adapter.InquiryAdapter;

import java.util.ArrayList;
import java.util.List;

public class InquiryListFragment extends Fragment {
    private RecyclerView recyclerView;
    private InquiryAdapter adapter;
    private List<Inquiry> inquiryList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inquiry_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InquiryAdapter(inquiryList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadInquiries();

        return view;
    }

    private void loadInquiries() {
        db.collection("inquiries")
            .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                inquiryList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Inquiry inquiry = document.toObject(Inquiry.class);
                    inquiryList.add(inquiry);
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show());
    }
}

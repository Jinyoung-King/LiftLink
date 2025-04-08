package com.jiny.liftlink.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiny.liftlink.R;
import com.jiny.liftlink.databinding.FragmentHomeBinding;
import com.jiny.liftlink.inquiry.ui.NewInquiryActivity;
import com.jiny.liftlink.inquiry.ui.Step1VehicleActivity;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private static final String LOG_TAG = "HomeFragment";

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "On Resume");
        super.onResume();

        // binding 객체가 null인 경우
        if (binding == null) {
            Log.e(LOG_TAG, "binding is null in onResume");
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "On Create View");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "On Created View");

        if (binding == null) {
            Log.e(LOG_TAG, "binding is null in onViewCreated");
            return;
        }

        // Firebase 사용자 정보 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            fetchUserData(user.getUid());
        } else {
            // 로그인되지 않은 경우 처리
            Log.e(LOG_TAG, "User is not logged in");
        }

        // 예약 정보 설정 (예제 데이터)
        int ongoingReservations = 2; // 실제 데이터 가져오기
        binding.tvReservationStatus.setText(getString(R.string.reservation_status, ongoingReservations));

        // 버튼 클릭 리스너 설정
        binding.btnNewInquiry.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewInquiryActivity.class);
            startActivity(intent);
        });

        binding.btnMyTest.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Step1VehicleActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserData(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String userName = document.getString("name");
                    Log.d(LOG_TAG, "binding: " + binding);
                    // Fragment가 여전히 액티브한 상태인지 확인
                    if (isAdded() && binding != null && binding.tvWelcomeMessage != null) {
                        if (userName != null) {
                            binding.tvWelcomeMessage.setText(getString(R.string.welcome_message, userName));
                        } else {
                            Log.e(LOG_TAG, "User name is null");
                        }
                    } else {
                        Log.i(LOG_TAG, "Fragment is not added or binding is null");
                    }
                } else {
                    Log.e(LOG_TAG, "User document does not exist");
                }
            } else {
                Log.e(LOG_TAG, "Failed to fetch user data", task.getException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "On Destroy View");
        super.onDestroyView();
        binding = null;
    }
}

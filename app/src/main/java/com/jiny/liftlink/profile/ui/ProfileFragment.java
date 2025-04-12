package com.jiny.liftlink.profile.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiny.liftlink.R;
import com.jiny.liftlink.auth.ui.LoginActivity;
import com.jiny.liftlink.changelog.ui.screen.ChangelogListFragment;
import com.jiny.liftlink.databinding.FragmentProfileBinding;
import com.jiny.liftlink.ui.ThemeSelectionActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private ActivityResultLauncher<Intent> themeSelectionLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getContext(), "테마가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        requireActivity().recreate(); // 액티비티 재시작으로 테마 반영
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false); // 무조건 제일 먼저

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 기타 클릭 리스너 등 설정
        binding.btnLogout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    auth.signOut();
                    Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("아니오", null)
                .show());

        binding.textChangelog.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new ChangelogListFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.btnChangeTheme.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("liftlink", Context.MODE_PRIVATE);
            prefs.edit().putString("lastFragment", "ProfileFragment").apply();

            Intent intent = new Intent(requireContext(), ThemeSelectionActivity.class);
            themeSelectionLauncher.launch(intent);
        });

        return binding.getRoot();
    }

    private void loadUserProfile(String userId) {
        Log.d("ProfileFragment", "Start to load user profile of 'users' collection by document path: " + userId);
        if (binding == null || getActivity() == null) return;

        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (auth.getCurrentUser() == null) {
                Log.e("ProfileFragment", "유저 정보가 없습니다.");
                Toast.makeText(getActivity(), "사용자 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            if (documentSnapshot.exists()) {
                binding.textViewName.setText(documentSnapshot.getString("name"));
                binding.textViewEmail.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail());
                binding.textViewPhone.setText(documentSnapshot.getString("phone"));
                binding.textViewAddress.setText(documentSnapshot.getString("address"));

                String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this).load(profileImageUrl).into(binding.imageViewProfile);
                } else {
                    binding.imageViewProfile.setImageResource(R.drawable.default_profile);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileFragment", "유저 정보 불러오기 실패", e);
            Toast.makeText(getActivity(), "사용자 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(getTag(), "On View Created");
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = binding.toolbar;
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() == null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        }

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("프로필");
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // 살짝 딜레이를 주는 방법도 있어
            new Handler(Looper.getMainLooper()).post(() -> loadUserProfile(user.getUid()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}

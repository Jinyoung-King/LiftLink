package com.jiny.liftlink.chat.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.jiny.liftlink.R;
import com.jiny.liftlink.chat.data.Message;
import com.jiny.liftlink.chat.ui.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private FirebaseFirestore db;

    private ListenerRegistration firestoreListenerRegistration;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener; //전역 변수로 선언

    private static final String LOG_TAG = "ChatFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToLastMessage();
            }
        });

        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                requireView().getWindowVisibleDisplayFrame(r);
                int screenHeight = requireView().getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    scrollToLastMessage();
                }
            }
        };
        requireView().getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "On Resume");
        super.onResume();

    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "On Pause");
        super.onPause();
        requireView().getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_chat);
        editTextMessage = view.findViewById(R.id.editText_message);
        buttonSend = view.findViewById(R.id.button_send);

        db = FirebaseFirestore.getInstance();
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        // Firestore에서 메시지 가져오기
        loadMessages();

        // 메시지 전송 버튼 클릭 리스너
        buttonSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트가 파괴될 때 Firestore 리스너 제거
        if (firestoreListenerRegistration != null) {
            firestoreListenerRegistration.remove();
            firestoreListenerRegistration = null;
        }
    }

    private void loadMessages() {
        // 기존 리스너가 있다면 제거
        if (firestoreListenerRegistration != null) {
            firestoreListenerRegistration.remove();
        }
        firestoreListenerRegistration = db.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(LOG_TAG, "Firestore listen failed.", error);
                        return;
                    }

                    messageList.clear();
                    assert value != null;
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Message message = doc.toObject(Message.class);
                        messageList.add(message);
                    }
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);  // 최신 메시지로 스크롤 이동
                });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d(LOG_TAG, "Current User ID: " + currentUser.getUid());
            Log.d(LOG_TAG, "Current User: " + currentUser.getDisplayName());
            Log.d(LOG_TAG, "Current User Email: " + currentUser.getEmail());
            db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String senderName = currentUser.getEmail();
                    Message message = new Message(messageText, senderName, new Timestamp(new Date()));

                    db.collection("chats").add(message)
                            .addOnSuccessListener(documentReference -> editTextMessage.setText(""))
                            .addOnFailureListener(e -> Log.e("ChatFragment", "메시지 전송 실패", e));
                }
            });
        }
    }


    private void scrollToLastMessage() {
        recyclerView.postDelayed(() ->
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1), 100);
    }

}

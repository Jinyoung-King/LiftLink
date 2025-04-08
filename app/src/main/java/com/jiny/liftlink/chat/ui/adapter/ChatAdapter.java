package com.jiny.liftlink.chat.ui.adapter;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiny.liftlink.R;
import com.jiny.liftlink.chat.data.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final String LOG_TAG = "ChatAdapter";
    private final List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
//        Log.d(LOG_TAG, "On Bind View Holder");

        Message message = messageList.get(position);
        String sender = message.getSender();

        holder.textViewMessage.setText(message.getMessage());
        holder.textViewSender.setText(sender);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        Log.d(LOG_TAG, "메시지 송신자: " + sender);
        if (sender == null) {
            Log.w(LOG_TAG, "메시지 송신자가 설정되지 않은 메시지입니다.");
            holder.textViewSender.setText("알 수 없는 사용자");
            // 상대방 메시지는 왼쪽 정렬
            holder.textViewMessage.setBackgroundResource(R.drawable.bubble_other);
            holder.textViewMessage.setGravity(Gravity.START);
            return;
        }

        if (currentUser != null && sender.equals(currentUser.getEmail())) {
            // 내 메시지 - 오른쪽 정렬 & 초록색 말풍선
            holder.textViewSender.setVisibility(View.GONE);
            holder.messageContainer.setBackgroundResource(R.drawable.bubble_mine);
            holder.layoutParent.setGravity(Gravity.END);
        } else {
            // Firestore에서 sender의 사용자 이름 가져오기
            fetchUserName(message.getSender(), holder.textViewSender);
            // 상대방 메시지 - 왼쪽 정렬 & 회색 말풍선
            holder.messageContainer.setBackgroundResource(R.drawable.bubble_other);
            holder.layoutParent.setGravity(Gravity.START);
        }
    }

    // Firestore에서 이메일에 해당하는 사용자 이름을 가져오는 함수
    private void fetchUserName(String email, TextView textViewSender) {
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", email)  // 이메일로 사용자 찾기
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(LOG_TAG,"Document: " + queryDocumentSnapshots);
                    Log.d(LOG_TAG,"Documents: " + queryDocumentSnapshots.getDocuments());
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String userName = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                        textViewSender.setText(userName);  // sender 이름 표시
                    } else {
                        textViewSender.setText(email);  // 이름 없으면 이메일 표시
                    }
                })
                .addOnFailureListener(e -> textViewSender.setText(email));  // 오류 시 이메일 표시
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewSender;
        LinearLayout layoutParent, messageContainer;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textView_message);
            textViewSender = itemView.findViewById(R.id.textView_sender);
            layoutParent = (LinearLayout) itemView; // 최상위 LinearLayout
            messageContainer = itemView.findViewById(R.id.messageContainer); // 메시지 내용 감싸는 레이아웃
        }
    }
}

package com.jiny.liftlink.changelog.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jiny.liftlink.changelog.data.model.Changelog;

import java.util.ArrayList;
import java.util.List;

public class ChangelogRepository {

    private final FirebaseFirestore db;
    private final CollectionReference changelogsRef;

    public ChangelogRepository() {
        db = FirebaseFirestore.getInstance();
        changelogsRef = db.collection("changelogs");
    }

    /**
     * 변경사항 목록 불러오기
     */
    public LiveData<List<Changelog>> getAllChangelogs() {
        MutableLiveData<List<Changelog>> changelogsLiveData = new MutableLiveData<>();

        changelogsRef.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Changelog> changelogs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Changelog changelog = doc.toObject(Changelog.class);
                        changelog.setId(doc.getId());
                        changelogs.add(changelog);
                    }
                    changelogsLiveData.setValue(changelogs);
                })
                .addOnFailureListener(e -> changelogsLiveData.setValue(new ArrayList<>()));

        return changelogsLiveData;
    }

    /**
     * 특정 변경사항 상세 조회
     */
    public LiveData<Changelog> getChangelogById(String id) {
        MutableLiveData<Changelog> changelogLiveData = new MutableLiveData<>();

        changelogsRef.document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Changelog changelog = documentSnapshot.toObject(Changelog.class);
                        changelog.setId(documentSnapshot.getId());
                        changelogLiveData.setValue(changelog);
                    } else {
                        changelogLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> changelogLiveData.setValue(null));

        return changelogLiveData;
    }

    /**
     * 변경사항 등록
     */
    public void addChangelog(Changelog changelog, OnChangelogOperationListener listener) {
        changelog.setTimestamp(System.currentTimeMillis());

        changelogsRef.add(changelog)
                .addOnSuccessListener(documentReference -> {
                    changelog.setId(documentReference.getId());
                    listener.onSuccess(changelog);
                })
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * 변경사항 삭제
     */
    public void deleteChangelog(String id, OnChangelogOperationListener listener) {
        changelogsRef.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * 콜백 인터페이스
     */
    public interface OnChangelogOperationListener {
        void onSuccess(Changelog changelog);
        void onFailure(Exception e);
    }
}

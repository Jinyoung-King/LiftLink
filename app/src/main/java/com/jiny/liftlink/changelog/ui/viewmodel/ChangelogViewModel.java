package com.jiny.liftlink.changelog.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jiny.liftlink.changelog.data.model.Changelog;

import java.util.ArrayList;
import java.util.List;

public class ChangelogViewModel extends ViewModel {

    private final MutableLiveData<List<Changelog>> changelogList = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Changelog>> getChangelogs() {
        return changelogList;
    }

    public void fetchChangelogs() {
        db.collection("changelogs")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Changelog> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Changelog changelog = doc.toObject(Changelog.class);
                        changelog.setId(doc.getId()); // 문서 ID를 객체에 저장
                        list.add(changelog);
                    }
                    changelogList.setValue(list);
                })
                .addOnFailureListener(e -> {
                    // 실패 시 빈 리스트로 초기화
                    changelogList.setValue(new ArrayList<>());
                });
    }
}

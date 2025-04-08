package com.jiny.liftlink.changelog.ui.screen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jiny.liftlink.R;
import com.jiny.liftlink.changelog.data.model.Changelog;
import com.jiny.liftlink.changelog.ui.adapter.ChangelogAdapter;
import com.jiny.liftlink.changelog.ui.viewmodel.ChangelogViewModel;

import java.util.List;

public class ChangelogListFragment extends Fragment implements ChangelogAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ChangelogAdapter adapter;
    private ChangelogViewModel viewModel;

    public ChangelogListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_changelog_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewChangelogs);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChangelogAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChangelogViewModel.class);

        observeChangelogs();
    }

    private void observeChangelogs() {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getChangelogs().observe(getViewLifecycleOwner(), changelogs -> {
            progressBar.setVisibility(View.GONE);
            if (changelogs == null || changelogs.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setChangelogList(changelogs);
            }
        });
    }

    @Override
    public void onItemClick(Changelog changelog) {
        Intent intent = new Intent(getContext(), ChangelogDetailActivity.class);
        intent.putExtra("changelog_id", changelog.getId());
        startActivity(intent);
    }
}

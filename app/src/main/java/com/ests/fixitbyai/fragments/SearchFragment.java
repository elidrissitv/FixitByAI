package com.ests.fixitbyai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ests.fixitbyai.R;
import com.ests.fixitbyai.adapters.GuideAdapter;
import com.ests.fixitbyai.viewmodels.GuideViewModel;
import com.ests.fixitbyai.GeminiHelper;
import com.google.android.material.button.MaterialButton;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private GuideAdapter adapter;
    private GuideViewModel viewModel;
    private TextView iaResponseText;
    private MaterialButton sendButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        iaResponseText = view.findViewById(R.id.ia_response_text);
        sendButton = view.findViewById(R.id.send_button);
        
        setupRecyclerView();
        setupSearchView();
        setupViewModel();
        setupSendButton();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new GuideAdapter(guide -> {
            // Navigation vers le détail du guide
            Bundle args = new Bundle();
            args.putInt("guide_id", guide.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, GuideDetailFragment.class, args)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Ne rien faire ici, on attend le clic sur le bouton d'envoi
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Ne rien faire ici, on attend le clic sur le bouton d'envoi
                return true;
            }
        });
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            if (!query.isEmpty()) {
                iaResponseText.setVisibility(View.VISIBLE);
                iaResponseText.setText(getString(R.string.loading));
                new Thread(() -> {
                    try {
                        String iaResponse = GeminiHelper.getGeminiResponse(query);
                        requireActivity().runOnUiThread(() -> iaResponseText.setText(iaResponse));
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> iaResponseText.setText("Erreur Gemini : " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(GuideViewModel.class);
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), guides -> {
            if (guides == null || guides.isEmpty()) {
                iaResponseText.setVisibility(View.VISIBLE);
                iaResponseText.setText(getString(R.string.loading));
                String question = searchView.getQuery().toString();
                new Thread(() -> {
                    try {
                        String iaResponse = GeminiHelper.getGeminiResponse(question);
                        requireActivity().runOnUiThread(() -> iaResponseText.setText(iaResponse));
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> iaResponseText.setText("Erreur Gemini : " + e.getMessage()));
                    }
                }).start();
                adapter.submitList(null); // Vide la liste
            } else {
                iaResponseText.setVisibility(View.GONE);
                adapter.submitList(guides);
            }
        });
    }
} 
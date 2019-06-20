package com.khadejaclarke.taskswithsearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ReadTasksFragment extends Fragment {
    private static final String TAG = "ReadTasksFragment";
    Activity activity;
    View view;
    ArrayList<Task> tasks;
    RecyclerView rv_read;
    TasksRecyclerViewAdapter adapter;

    public static ReadTasksFragment newInstance() {
        return new ReadTasksFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_read, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasks = new TaskFetchr().fetchTasks();

        rv_read = view.findViewById(R.id.recyclerView);
        rv_read.setLayoutManager(new LinearLayoutManager(activity));

        adapter = new TasksRecyclerViewAdapter(activity, tasks);
        rv_read.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);

        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                searchView.clearFocus();
                QueryPreferences.setStoredQuery(activity, query);
                updateItems();
                updateSuggestions();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.d(TAG, "QueryTextChange: " + query);
                QueryPreferences.setStoredQuery(activity, query);
                updateItems();
                return true;
            }
        });

        searchView.setOnSearchClickListener(v -> {
            String query = QueryPreferences.getStoredQuery(activity);
            searchView.setQuery(query, false);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_clear) {
            QueryPreferences.setStoredQuery(activity, null);
            updateItems();
            deleteSuggestions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(activity);
        adapter.getFilter().filter(query);
    }

    private void updateSuggestions() {
        Intent intent  = activity.getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(activity,
                    TasksSuggestionProvider.AUTHORITY, TasksSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }

    private void deleteSuggestions() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(activity,
                TasksSuggestionProvider.AUTHORITY, TasksSuggestionProvider.MODE);

        new AlertDialog.Builder(activity)
                .setTitle("Deletion Confirmation")
                .setMessage("Are you sure you want to clear your search history?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> suggestions.clearHistory())
                .setNegativeButton(android.R.string.no, (dialog, which) -> {})
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


}

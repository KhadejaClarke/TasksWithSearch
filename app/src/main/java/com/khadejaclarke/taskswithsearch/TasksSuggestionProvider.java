package com.khadejaclarke.taskswithsearch;

import android.content.SearchRecentSuggestionsProvider;


public class TasksSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = TasksSuggestionProvider.class.getSimpleName();
    public final static int MODE = DATABASE_MODE_QUERIES;

    public TasksSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
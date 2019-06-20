package com.khadejaclarke.taskswithsearch;

import android.support.v4.app.Fragment;

public class TasksActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ReadTasksFragment.newInstance();
    }
}


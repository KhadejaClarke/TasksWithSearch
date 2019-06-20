package com.khadejaclarke.taskswithsearch;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;


public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter.TaskViewHolder> implements Filterable {
    private Activity activity;
    private ArrayList<Task> tasks;
    private ArrayList<Task> tasks_filtered;

    public TasksRecyclerViewAdapter(Activity activity, ArrayList<Task> tasks) {
        this.activity = activity;
        this.tasks = tasks;
        this.tasks_filtered = tasks;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView task_id;
        TextView task_title;
        TextView task_description;
        TextView task_done;

        TaskViewHolder(View view) {
            super(view);

            task_id = view.findViewById(R.id.task_id);
            task_title = view.findViewById(R.id.task_title);
            task_description = view.findViewById(R.id.task_description);
            task_done = view.findViewById(R.id.task_done);

        }

        void bind(Task task) {
            task_id.setText(new StringBuilder().append("Task ").append(task.getID()));
            task_title.setText(new StringBuilder().append(":  ").append(task.getTitle()));
            task_description.setText(new StringBuilder().append("Description: ").append(task.getDescription()));
            task_done.setText(new StringBuilder().append("Done: ").append(task.getDone().toString()));
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.rv_row, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    tasks_filtered = tasks;
                } else {
                    ArrayList<Task> filteredList = new ArrayList<>();
                    for (Task task : tasks) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (task.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(task);
                        }
                    }

                    tasks_filtered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tasks_filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tasks_filtered = (ArrayList<Task>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

}

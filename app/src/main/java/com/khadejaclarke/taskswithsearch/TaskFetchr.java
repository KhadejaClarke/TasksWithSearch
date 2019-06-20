package com.khadejaclarke.taskswithsearch;

import android.util.Log;

import com.khadejaclarke.taskswithsearch.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import volley.Config_URL;


public class TaskFetchr {
    private static final String TAG = "TaskFetchr";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<Task> fetchTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        try {
            String url = Config_URL.URL_READ;
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonTask = new JSONObject(jsonString);
            parseTasks(tasks, jsonTask);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch tasks", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return tasks;
    }

    private void parseTasks(List<Task> Tasks, JSONObject jsonTask) throws JSONException {

        JSONObject tasksJsonObject = jsonTask.getJSONObject("tasks");
        JSONArray taskJsonArray = tasksJsonObject.getJSONArray("task");

        for (int i = 0; i < taskJsonArray.length(); i++) {
            JSONObject object = taskJsonArray.getJSONObject(i);

            Task task = new Task();
            task.setID(object.getInt("id"));
            task.setTitle(object.getString("title"));
            task.setDescription(object.getString("description"));
            task.setDone(object.getBoolean("done"));
            Tasks.add(task);
        }
    }
}


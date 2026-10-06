package com.example.focusfuel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity {
    private ArrayList<Task> taskList;
    private ActivityResultLauncher<Intent> addTaskLauncher;
    private TaskAdapter adapter;

    private static final String TASKS_FILE = "tasks.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_do_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadTasksFromFile();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);

        adapter = new TaskAdapter(this, taskList, this::saveTasksToFile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String json = result.getData().getStringExtra("newTask");
                    Task task = new Gson().fromJson(json, Task.class);
                    adapter.addTask(task);
                    saveTasksToFile();
                }
            }
        );

        Button homeButton = findViewById(R.id.buttonHome);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                v.getContext().startActivity(intent);
            }
        });

        Button addButton = findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskActivity.class);
            addTaskLauncher.launch(intent);
        });
    }

    // Save tasks to file
    private void saveTasksToFile() {
        try {
            FileOutputStream fos = openFileOutput(TASKS_FILE, Context.MODE_PRIVATE);
            String json = new Gson().toJson(taskList);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromFile() {
        try {
            FileInputStream fis = openFileInput(TASKS_FILE);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            Type listType = new TypeToken<ArrayList<Task>>() {}.getType();
            taskList = new Gson().fromJson(sb.toString(), listType);
        } catch (IOException e) {
            taskList = new ArrayList<>();
        }
    }
}
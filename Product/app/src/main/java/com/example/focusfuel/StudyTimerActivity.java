package com.example.focusfuel;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudyTimerActivity extends AppCompatActivity {

    private static final String TASKS_FILE = "tasks.json";

    private Spinner taskSpinner;
    private NumberPicker minutePicker, secondPicker;
    private TextView timerDisplay;
    private Button startButton, resetButton, cancelButton;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isRunning = false;

    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_timer);

        timerDisplay = findViewById(R.id.textViewTimer);
        taskSpinner = findViewById(R.id.spinnerTasks);
        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);
        startButton = findViewById(R.id.buttonStart);
        resetButton = findViewById(R.id.buttonReset);
        cancelButton = findViewById(R.id.buttonCancel);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        loadTaskListFromFile();
        setupSpinner();

        taskSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    minutePicker.setVisibility(View.VISIBLE);
                    secondPicker.setVisibility(View.VISIBLE);
                    return;
                }

                Task selectedTask = taskList.get(position - 1);
                if (selectedTask.getDueDate() != null && selectedTask.getDueTime() != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        Date dueDateTime = sdf.parse(selectedTask.getDueDate() + " " + selectedTask.getDueTime());
                        Date now = Calendar.getInstance().getTime();
                        long diff = dueDateTime.getTime() - now.getTime();

                        if (diff > 0) {
                            timeLeftInMillis = diff;
                            updateTimerDisplay();

                            minutePicker.setVisibility(View.GONE);
                            secondPicker.setVisibility(View.GONE);
                        } else {
                            timeLeftInMillis = 0;
                            timerDisplay.setText("Task is overdue");
                            minutePicker.setVisibility(View.VISIBLE);
                            secondPicker.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    minutePicker.setVisibility(View.VISIBLE);
                    secondPicker.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                if (minutePicker.getVisibility() == View.VISIBLE && secondPicker.getVisibility() == View.VISIBLE) {
                    int minutes = minutePicker.getValue();
                    int seconds = secondPicker.getValue();
                    timeLeftInMillis = (minutes * 60 + seconds) * 1000L;
                }
                startTimer();
            }
        });

        resetButton.setOnClickListener(v -> resetTimer());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadTaskListFromFile() {
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
        } catch (Exception e) {
            taskList = new ArrayList<>();
        }
    }

    private void setupSpinner() {
        ArrayList<String> names = new ArrayList<>();
        names.add(0, ""); // Empty item as default
        for (Task task : taskList) {
            names.add(task.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskSpinner.setAdapter(adapter);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                startButton.setEnabled(true);
                minutePicker.setEnabled(true);
                secondPicker.setEnabled(true);
                taskSpinner.setEnabled(true);
            }
        }.start();

        isRunning = true;
        startButton.setEnabled(false);
        minutePicker.setEnabled(false);
        secondPicker.setEnabled(false);
        taskSpinner.setEnabled(false);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;
        startButton.setEnabled(true);
        minutePicker.setEnabled(true);
        secondPicker.setEnabled(true);
        taskSpinner.setEnabled(true);
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerDisplay.setText(timeFormatted);
    }
}

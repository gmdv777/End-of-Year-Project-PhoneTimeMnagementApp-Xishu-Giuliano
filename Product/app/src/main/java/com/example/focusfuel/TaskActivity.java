package com.example.focusfuel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;


public class TaskActivity extends AppCompatActivity {

    EditText nameEditText, descEditText, dateEditText, timeEditText;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        nameEditText = findViewById(R.id.editTextName);
        descEditText = findViewById(R.id.editTextDescription);
        dateEditText = findViewById(R.id.editTextDueDate);
        timeEditText = findViewById(R.id.editTextDueTime);
        saveButton = findViewById(R.id.buttonSave);
        cancelButton = findViewById(R.id.buttonCancel);

        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String desc = descEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();

            Task newTask = new Task(name, desc, date, time);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("newTask", new Gson().toJson(newTask));
            setResult(RESULT_OK, resultIntent);
            finish(); // return to ToDoList
        });

        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                    dateEditText.setText(formattedDate);
                }, year, month, day);
        dialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, h, m) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", h, m);
                    timeEditText.setText(formattedTime);
                }, hour, minute, true);
        dialog.show();
    }
}


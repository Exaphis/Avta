package com.exaphis.avta.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.exaphis.avta.Event;
import com.exaphis.avta.R;
import com.exaphis.avta.SetEvent;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class AddSetEventActivity extends AppCompatActivity {
    private DateTimeFormatter dateFormat, timeFormat;
    private EditText startDateInput, startTimeInput, endDateInput, endTimeInput;
    private LocalDateTime startDate, endDate, currentDate;
    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set_event);

        events = getIntent().getParcelableArrayListExtra("events");

        startTimeInput = findViewById(R.id.startTimeInput);
        startDateInput = findViewById(R.id.startDateInput);
        endTimeInput = findViewById(R.id.endTimeInput);
        endDateInput = findViewById(R.id.endDateInput);

        // Set date/time formatters
        Locale locale = getResources().getConfiguration().getLocales().get(0);
        dateFormat = DateTimeFormatter.ofPattern("EEE, d MMM YYYY", locale);
        timeFormat = DateTimeFormatter.ofPattern("hh:mm a", locale);

        // Set start and end date
        currentDate = LocalDateTime.now().withNano(0).withSecond(0);
        startDate = currentDate;
        endDate = startDate.plusMinutes(30);

        // Change title text
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Set Event");
        }

        updateDateTimeDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_set_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete_button) {
            // Hide keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String name = ((EditText) findViewById(R.id.nameInput)).getText().toString().trim();
            // Check that event name is not empty
            if (name.length() == 0) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Event name can't be empty", Snackbar.LENGTH_LONG);
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            // Check that event doesn't end before it starts
            if (endDate.isBefore(startDate)) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Event can't end before it starts", Snackbar.LENGTH_LONG);
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            // Check that event doesn't end when it starts
            if (endDate.isEqual(startDate)) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Event duration can't be 0", Snackbar.LENGTH_LONG);
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            // Check that event doesn't start during another
            for (Event e : events) {
                if (e instanceof SetEvent &&
                        startDate.isBefore(e.getEnd()) && e.getStart().isBefore(endDate)) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Time overlaps with " + e.getEventName(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return super.onOptionsItemSelected(item);
                }
            }

            SetEvent e = new SetEvent(name,
                    ((EditText) findViewById(R.id.descInput)).getText().toString().trim(),
                    startDate, endDate);

            Intent intent = new Intent();
            intent.putExtra("event", e);
            setResult(RESULT_OK, intent);

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDateTimeDisplay() {
        startDateInput.setText(dateFormat.format(startDate));
        startTimeInput.setText(timeFormat.format(startDate));
        endDateInput.setText(dateFormat.format(endDate));
        endTimeInput.setText(timeFormat.format(endDate));
    }

    public void selectStartDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate = startDate.withYear(year)
                                .withMonth(month + 1)
                                .withDayOfMonth(dayOfMonth);

                        if (startDate.isAfter(endDate)) {
                            endDate = startDate.plusMinutes(30);
                        }

                        updateDateTimeDisplay();
                    }
                }, startDate.getYear(),
                startDate.getMonthValue() - 1,
                startDate.getDayOfMonth());

        datePickerDialog.getDatePicker()
                .setMinDate(currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        datePickerDialog.show();
    }

    public void selectStartTime(View view) {
        new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startDate = startDate.withHour(hourOfDay).withMinute(minute);
                updateDateTimeDisplay();
            }
        }, startDate.getHour(), startDate.getMinute(), false).show();
    }

    public void selectEndDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate = endDate.withYear(year)
                                .withMonth(month + 1)
                                .withDayOfMonth(dayOfMonth);

                        updateDateTimeDisplay();
                    }
                }, endDate.getYear(), endDate.getMonthValue() - 1, endDate.getDayOfMonth());

        datePickerDialog.getDatePicker()
                .setMinDate(startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        datePickerDialog.show();
    }

    public void selectEndTime(View view) {
        new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endDate = endDate.withHour(hourOfDay).withMinute(minute);
                updateDateTimeDisplay();
            }
        }, endDate.getHour(), endDate.getMinute(), false).show();
    }
}

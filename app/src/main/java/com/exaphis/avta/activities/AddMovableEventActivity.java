package com.exaphis.avta.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.exaphis.avta.MovableEvent;
import com.exaphis.avta.fragments.PickerDialogFragment;
import com.exaphis.avta.R;
import com.exaphis.avta.TimeDurationPickerCallback;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class AddMovableEventActivity extends AppCompatActivity implements TimeDurationPickerCallback {
    private SeekBar seekBar;
    private TextView textView;
    private EditText durationInput, dueDateInput;
    private long duration;
    private LocalDateTime dueDate, currentDate;
    private DateTimeFormatter dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: add due date selector
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movable_event);

        // Set up event duration selector
        durationInput = findViewById(R.id.durationInput);
        updateDuration(30 * 60 * 1000);

        // Set up seek bar for enjoyment value
        seekBar = findViewById(R.id.seek_Bar);
        textView = findViewById(R.id.enjoymentValue);

        textView.setText(getApplicationContext().getString(R.string.enjoyment_selector_text,
                0, seekBar.getMax()));
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView.setText(getApplicationContext().getString(
                                R.string.enjoyment_selector_text,
                                progress, seekBar.getMax()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                }
        );

        // Change title text
        ActionBar actionBar = getSupportActionBar();
        System.out.println(actionBar == null);
        if (actionBar != null) {
            actionBar.setTitle("Add Movable Event");
        }

        // Due date
        Locale locale = getResources().getConfiguration().getLocales().get(0);
        dateFormat = DateTimeFormatter.ofPattern("EEE, d MMM YYYY", locale);
        dueDateInput = findViewById(R.id.dueDateInput);

        currentDate = LocalDateTime.now().withNano(0).withSecond(0);
        dueDate = currentDate;

        updateDueDate();
    }

    public void updateDueDate() {
        dueDateInput.setText(dateFormat.format(dueDate));
    }

    public void updateDuration(long duration) {
        this.duration = duration;
        long minutes = (duration / 1000) / 60;
        long hours = minutes / 60;
        minutes %= 60;
        durationInput.setText(getApplicationContext().getString(R.string.select_duration, hours,
                minutes));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_movable_event, menu);
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

            // Check that duration is not 0
            if (duration == 0) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Duration can't be 0", Snackbar.LENGTH_LONG);
                snackbar.show();
                return super.onOptionsItemSelected(item);
            }

            MovableEvent e = new MovableEvent(name, duration / 1000 / 60,
                    ((EditText) findViewById(R.id.subjectInput)).getText().toString().trim(),
                    dueDate, seekBar.getProgress());

            Intent intent = new Intent();
            intent.putExtra("event", e);
            setResult(RESULT_OK, intent);

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void enterApproxTime(View view) {
        new PickerDialogFragment().show(getFragmentManager(), "dialog");
    }

    public void selectDueDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dueDate = dueDate.withYear(year)
                                .withMonth(month + 1)
                                .withDayOfMonth(dayOfMonth);

                        updateDueDate();
                    }
                }, dueDate.getYear(), dueDate.getMonthValue() - 1, dueDate.getDayOfMonth());

        datePickerDialog.getDatePicker()
                .setMinDate(currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        datePickerDialog.show();
    }
}

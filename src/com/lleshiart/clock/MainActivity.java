package com.lleshiart.clock;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private BroadcastReceiver mMinuteReceiver;
	private TextView mHourText;
	private TextView mMinuteText;
	private TextView mAmPmText;
	private TextView mDate;
	private ImageButton mSettings;
	
	private String mSeperator = ":"; // Seperator for the clock
	private boolean mClockFormatIs24 = true; // defines if the clock-format is 24-hours or 12-hours
	private int mOptionSelected = 1; // saves the option checked in the clock settings
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		// Assign our variables
		mHourText = (TextView) findViewById(R.id.hourText);
		mMinuteText = (TextView) findViewById(R.id.minuteText);
		mAmPmText = (TextView) findViewById(R.id.amPmText);
		mDate = (TextView) findViewById(R.id.date);
		mSettings = (ImageButton) findViewById(R.id.settings);
		
		// Set the time to the current system time
		updateTime();
		updateDate();
		
		// When the settings button is clicked
		mSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSettings();
			}
		});
		
		// Our BroadcastReceiver to handle the time change
		mMinuteReceiver = new BroadcastReceiver() {
			@Override
			// This method is called when a broadcast is received
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
					// If the system time is updated, update the app's time and date
					updateTime();
					updateDate();
				}
			}
		};
		
		// Registering the Broadcast receiver in order to receive a time change
		registerReceiver(mMinuteReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		
	}

	@Override
	public void onStop() {
		super.onStop();
		// unregister the broadcast receiver if the app is stopped
		if (mMinuteReceiver != null) {
			unregisterReceiver(mMinuteReceiver);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Register the Broadcast receiver again when the app is resumed
		registerReceiver(mMinuteReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		// Update the time and date
		updateTime();
		updateDate();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Private methods
	@SuppressLint("SimpleDateFormat")
	public void updateTime() {
		// Create a new Date variable
		Date today = new Date();
		// Using SimpleDateFormat to convert the system time into a readable string
		SimpleDateFormat hour24 = new SimpleDateFormat("H");
		SimpleDateFormat hour12 = new SimpleDateFormat("h");
		SimpleDateFormat minutes = new SimpleDateFormat("m");
		SimpleDateFormat amPm = new SimpleDateFormat("a");
		
		// Format the date and save it into a String
		String currentHour24 = hour24.format(today);
		String currentHour12 = hour12.format(today);
		String currentMinutes = minutes.format(today);
		String currentAmPm = amPm.format(today);
		
		// Update the corresponding TextViews
		if (currentMinutes.length() == 1) {
			currentMinutes = "0" + currentMinutes;
		}
		
		// If the current Clock format is 24-hour clock
		if (mClockFormatIs24) {
			mHourText.setText(currentHour24);
		}
		else {
			if (currentHour12.length() == 1) {
				currentHour12 = "0" + currentHour12;
			}
			mHourText.setText(currentHour12);
		}
		mMinuteText.setText(mSeperator + currentMinutes);
		mAmPmText.setText(currentAmPm);
	}
	
	@SuppressLint("SimpleDateFormat")
	public void updateDate() {
		Date today = new Date();
		// Using SimpleDateFormat to convert the system date into a readable string
		SimpleDateFormat day = new SimpleDateFormat("E");
		SimpleDateFormat month = new SimpleDateFormat("MMM");
		SimpleDateFormat dayInMonth = new SimpleDateFormat("d");
		SimpleDateFormat year = new SimpleDateFormat("y");
		
		String currentDay = day.format(today);
		String currentMonth = month.format(today);
		String currentdayInMonth = dayInMonth.format(today);
		String currentYear = year.format(today);
		
		mDate.setText(currentDay + ", " + currentMonth + " " + currentdayInMonth + ", " + currentYear);
	}
	
	public void showSettings() {
		AlertDialog.Builder settingsDialogBuilder = new AlertDialog.Builder(this);
		 
		// Set the Dialog Title
		settingsDialogBuilder.setTitle(R.string.settings_title);
		
		// Set the choices
		settingsDialogBuilder.setSingleChoiceItems(R.array.settings_dialog, mOptionSelected, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					mClockFormatIs24 = false;
				}
				else {
					mClockFormatIs24 = true;
				}
			}
		});
		// The OK button
		settingsDialogBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK, so update the time
                updateTime();
                // Update the option selected variable
                if (!mClockFormatIs24) {
                	mOptionSelected = 0;
                }
                else {
                	mOptionSelected = 1;
                }
            }
        });
		// The Cancel Button
		settingsDialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	// cancel the Dialog
                dialog.cancel();
            }
        });

		// create the alert dialog
		AlertDialog settingsDialog = settingsDialogBuilder.create();

		// show the dialog
		settingsDialog.show();
	}
	
}
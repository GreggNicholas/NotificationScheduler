package com.example.greggnicholas.notificationscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int JOB_ID = 0;
    private Button scheduleJobButton;
    private Button cancelJob;
    private Switch deviceIdleSwitch;
    private Switch deviceChargingSwitch;
    private SeekBar seekBar;
    private JobScheduler jobScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadioGroup networkOptions = findViewById(R.id.networkOptions_radio);
        deviceIdleSwitch = findViewById(R.id.idleSwitch);
        deviceChargingSwitch = findViewById(R.id.chargingSwitch);
        seekBar = findViewById(R.id.seekBar);

        final TextView seekBarProgress = findViewById(R.id.seekBarProgress);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 0) {
                    seekBarProgress.setText( progress + getString(R.string.seekbarS));
                } else {
                    seekBarProgress.setText(getString(R.string.notset));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void schedulejob(View view) {
        int seekBarInteger = seekBar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;

        RadioGroup networkOptionsRadio = findViewById(R.id.networkOptions_radio);
        int selectedNetworkID = networkOptionsRadio.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        switch (selectedNetworkID) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
        builder.setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(deviceIdleSwitch.isChecked())
                .setRequiresCharging(deviceChargingSwitch.isChecked());

        if (seekBarSet) {
            builder.setOverrideDeadline(seekBarInteger * 1000);
        }
        boolean constraintSet = (selectedNetworkOption
                != JobInfo.NETWORK_TYPE_NONE)
                || deviceChargingSwitch.isChecked()
                || deviceIdleSwitch.isChecked()
                || seekBarSet;

        if (constraintSet) {
            JobInfo myJobInfo = builder.build();
            jobScheduler.schedule(myJobInfo);

            Toast.makeText(this, "Job Scheduled, job will run when "
                    + "the constraints are met", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please set at least one constraint", Toast.LENGTH_SHORT).show();
        }


    }

    public void cancelJobs(View view) {
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
            jobScheduler = null;
            Toast.makeText(this, "Jobs canceled"
                    , Toast.LENGTH_SHORT).show();
        }

    }
}

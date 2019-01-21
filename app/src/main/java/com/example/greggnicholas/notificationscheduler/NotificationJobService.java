package com.example.greggnicholas.notificationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;


public class NotificationJobService extends JobService {
    NotificationManager notifier;

    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";


    @Override
    public boolean onStartJob(JobParameters params) {

        createNotificationChannel();

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this,
                        MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.jobservice))
                .setContentText(getString(R.string.jobrunning))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        notifier.notify(0,builder.build());
        return false;

    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public void createNotificationChannel() {
        notifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1) {

            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Job Service Scheduler",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.TRANSPARENT);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notification from Job Service");

            notifier.createNotificationChannel(notificationChannel);
        }
    }
}

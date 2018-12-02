package com.vt.disposisibandung.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.Token;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.UploadFileHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.EService;

import java.io.File;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by irvan on 7/7/15.
 */
@EService
public class UploadService extends Service {

    private static final int NOTIFICATION_ID_UPLOAD = 13510;

    private NotificationCompat.Builder notificationBuilder;
    private int count;
    private int uploaded;
    private boolean uploading;

    @Override
    public void onCreate() {
        super.onCreate();

        uploading = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!uploading) startUpload();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startUpload() {
        uploading = true;

        count = UploadFileHelper.getInstance().uploadQueueCount(this);
        if (count > 0) {
            Token token = DatabaseHelper.getInstance().getRealm().where(Token.class).findFirst();
            if (token != null) {
                WebServiceHelper.getInstance().setAccessToken(token);

                notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_notification)
                        .setColor(getResources().getColor(R.color.surat_panel))
                        .setContentTitle("Upload file")
                        .setProgress(10, 0, false)
                        .setOngoing(true);
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID_UPLOAD, notificationBuilder.build());

                uploaded = 0;
                performUploadFile();
            }
        } else {
            stopSelf();
        }
    }

    private void performUploadFile() {
        notificationBuilder.setProgress(count, uploaded, false).setContentText("Upload file " + uploaded + " dari " + count);
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID_UPLOAD, notificationBuilder.build());

        int suratType = UploadFileHelper.getInstance().peekUploadQueueType(this);
        Map<Long, File> fileInfo = UploadFileHelper.getInstance().peekUploadQueue(this);
        long suratId = fileInfo.entrySet().iterator().next().getKey();

        if (suratType == Surat.TYPE_MASUK) {
            WebServiceHelper.getInstance().getServices().uploadFileSuratMasuk(suratId, new TypedFile("multipart/form-data", fileInfo.get(suratId)), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    ++uploaded;
                    UploadFileHelper.getInstance().popUploadQueue(UploadService.this);

                    if (uploaded < count) {
                        performUploadFile();
                    } else {
                        NotificationManagerCompat.from(UploadService.this).cancel(NOTIFICATION_ID_UPLOAD);
                        stopSelf();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    NotificationManagerCompat.from(UploadService.this).cancel(NOTIFICATION_ID_UPLOAD);
                    stopSelf();
                }
            });
        } else if (suratType == Surat.TYPE_KELUAR) {
            WebServiceHelper.getInstance().getServices().uploadFileSuratKeluar(suratId, new TypedFile("multipart/form-data", fileInfo.get(suratId)), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    ++uploaded;
                    UploadFileHelper.getInstance().popUploadQueue(UploadService.this);

                    if (uploaded < count) {
                        performUploadFile();
                    } else {
                        NotificationManagerCompat.from(UploadService.this).cancel(NOTIFICATION_ID_UPLOAD);
                        stopSelf();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    NotificationManagerCompat.from(UploadService.this).cancel(NOTIFICATION_ID_UPLOAD);
                    stopSelf();
                }
            });
        }
    }
}

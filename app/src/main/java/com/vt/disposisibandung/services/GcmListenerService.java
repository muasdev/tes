package com.vt.disposisibandung.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.vt.disposisibandung.DetailSuratActivity_;
import com.vt.disposisibandung.MainActivity_;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.Token;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

/**
 * Created by irvan on 7/4/15.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    public static final int NOTIF_ID = 10016;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        displayNotification(data.getString("title"), data.getString("message"), Long.parseLong(data.getString("id")), data.getString("jenis"));
    }

    private void displayNotification(String title, String message, long id, String jenis) {
        Token token = DatabaseHelper.getInstance().getRealm().where(Token.class).findFirst();

        if (token != null) {
            WebServiceHelper.getInstance().setAccessToken(token);

            PendingIntent pendingIntent = null;
            switch (jenis) {
                case "surat_baru":
                case "kepada": {
                    pendingIntent = TaskStackBuilder.create(this)
                            .addNextIntent(MainActivity_.intent(this).get())
                            .addNextIntent(DetailSuratActivity_.intent(this).suratId(id).suratType(Surat.TYPE_MASUK).get().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            .getPendingIntent(NOTIF_ID, PendingIntent.FLAG_CANCEL_CURRENT);
                    break;
                }
                case "aksi": {
                    pendingIntent = TaskStackBuilder.create(this)
                            .addNextIntent(MainActivity_.intent(this).initSuratType(Surat.TYPE_KELUAR).get())
                            .addNextIntent(DetailSuratActivity_.intent(this).suratId(id).suratType(Surat.TYPE_KELUAR).get().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            .getPendingIntent(NOTIF_ID, PendingIntent.FLAG_CANCEL_CURRENT);
                    break;
                }
                case "disposisi": {
                    pendingIntent = TaskStackBuilder.create(this)
                            .addNextIntent(MainActivity_.intent(this).initSuratType(Surat.TYPE_DISPOSISI_MASUK).get())
                            .addNextIntent(DetailSuratActivity_.intent(this).suratId(id).suratType(Surat.TYPE_DISPOSISI_MASUK).get().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            .getPendingIntent(NOTIF_ID, PendingIntent.FLAG_CANCEL_CURRENT);
                    break;
                }
                case "suratmasuk_laporan": {
                    pendingIntent = TaskStackBuilder.create(this)
                            .addNextIntent(MainActivity_.intent(this).initSuratType(Surat.TYPE_MASUK).get())
                            .addNextIntent(DetailSuratActivity_.intent(this).suratId(id).suratType(Surat.TYPE_MASUK).isKomentar(true).get().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            .getPendingIntent(NOTIF_ID, PendingIntent.FLAG_CANCEL_CURRENT);
                    break;
                }
                case "suratkeluar_laporan": {
                    pendingIntent = TaskStackBuilder.create(this)
                            .addNextIntent(MainActivity_.intent(this).initSuratType(Surat.TYPE_KELUAR).get())
                            .addNextIntent(DetailSuratActivity_.intent(this).suratId(id).suratType(Surat.TYPE_KELUAR).isKomentar(true).get().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            .getPendingIntent(NOTIF_ID, PendingIntent.FLAG_CANCEL_CURRENT);
                    break;
                }
                case "update_selesai": {
                    pendingIntent = TaskStackBuilder.create(this)
                            .addNextIntent(MainActivity_.intent(this).initSuratType(Surat.TYPE_DISPOSISI_MASUK).get())
                            .addNextIntent(DetailSuratActivity_.intent(this).suratId(id).suratType(Surat.TYPE_DISPOSISI_MASUK).isKomentar(true).get().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            .getPendingIntent(NOTIF_ID, PendingIntent.FLAG_CANCEL_CURRENT);
                    break;
                }
            }

            if (pendingIntent != null) {
                NotificationManagerCompat.from(this).notify(NOTIF_ID, new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setColor(getResources().getColor(R.color.surat_panel))
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .build());
            }
        }
    }
}

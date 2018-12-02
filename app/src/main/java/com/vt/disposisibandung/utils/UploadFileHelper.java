package com.vt.disposisibandung.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by irvan on 7/8/15.
 */
public class UploadFileHelper {

    private static final String DIR_PREVIEW_TEMP = "preview";
    private static final String DIR_UPLOAD_QUEUE = "uploads";

    public static UploadFileHelper instance;

    private UploadFileHelper() {
    }

    public static UploadFileHelper getInstance() {
        if (instance == null) instance = new UploadFileHelper();
        return instance;
    }

    public boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public File getPreviewTempDirectory(Context context) {
        File previewDir = new File(context.getExternalFilesDir(null), DIR_PREVIEW_TEMP);
        if (!previewDir.exists()) previewDir.mkdir();
        return previewDir;
    }

    public void clearPreviewTempDirectory(Context context) {
        File previewDir = getPreviewTempDirectory(context);
        for (File file : previewDir.listFiles()) {
            file.delete();
        }
    }

    public void addToUploadQueue(Context context, int suratType, long suratId, List<String> files) {
        File queueDir = new File(context.getExternalFilesDir(null), DIR_UPLOAD_QUEUE);
        if (!queueDir.exists()) queueDir.mkdir();

        File suratDir = new File(queueDir, String.valueOf(suratId));
        if (!suratDir.exists()) suratDir.mkdir();

        File suratTypeDir = new File(suratDir, String.valueOf(suratType));
        if (!suratTypeDir.exists()) suratTypeDir.mkdir();

        for (String filename : files) {
            File file = new File(filename);
            file.renameTo(new File(suratTypeDir, file.getName()));
        }
    }

    public int uploadQueueCount(Context context) {
        File queueDir = new File(context.getExternalFilesDir(null), DIR_UPLOAD_QUEUE);
        if (!queueDir.exists()) return 0;

        int count = 0;
        for (File suratDir : queueDir.listFiles()) {
            for (File suratTypeDir : suratDir.listFiles()) {
                count += suratTypeDir.list().length;
            }
        }

        return count;
    }

    public int peekUploadQueueType(Context context) {
        File queueDir = new File(context.getExternalFilesDir(null), DIR_UPLOAD_QUEUE);
        File suratDir = queueDir.listFiles()[0];
        String suratTypeDir = suratDir.list()[0];
        return Integer.valueOf(suratTypeDir);
    }

    public Map<Long, File> peekUploadQueue(Context context) {
        File queueDir = new File(context.getExternalFilesDir(null), DIR_UPLOAD_QUEUE);
        File suratDir = queueDir.listFiles()[0];
        File suratTypeDir = new File(suratDir, suratDir.list()[0]);
        File file = suratTypeDir.listFiles()[0];

        Map<Long, File> fileInfo = new HashMap<>();
        try {
            fileInfo.put(Long.parseLong(suratDir.getName()), file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileInfo;
    }

    public Map<Long, File> popUploadQueue(Context context) {
        int suratType = peekUploadQueueType(context);
        Map<Long, File> fileInfo = peekUploadQueue(context);
        long suratId = fileInfo.entrySet().iterator().next().getKey();
        fileInfo.get(suratId).delete();

        File queueDir = new File(context.getExternalFilesDir(null), DIR_UPLOAD_QUEUE);
        File suratDir = new File(queueDir, String.valueOf(suratId));
        File suratTypeDir = new File(suratDir, String.valueOf(suratType));
        if (suratTypeDir.list().length == 0) suratTypeDir.delete();
        if (suratDir.list().length == 0) suratDir.delete();

        return fileInfo;
    }

}

package com.example.questify.data.export;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class StatisticsExporter {

    private final Context context;

    @Inject
    public StatisticsExporter(@ApplicationContext Context context) {
        this.context = context;
    }

    public boolean saveBitmapToGallery(Bitmap bitmap, String name) {
        OutputStream out = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".png");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Questify");
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    return false;
                }
                out = context.getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else {
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Questify");
                if (!dir.exists() && !dir.mkdirs()) {
                    return false;
                }
                File file = new File(dir, name + ".png");
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }

    public File saveBitmapToCache(Bitmap bitmap, String name) {
        File dir = new File(context.getCacheDir(), "shared");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        File file = new File(dir, name + ".png");
        try (OutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean saveJsonToDownloads(String json, String name) {
        OutputStream out = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, name + ".json");
                values.put(MediaStore.Downloads.MIME_TYPE, "application/json");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Questify");
                Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    return false;
                }
                out = context.getContentResolver().openOutputStream(uri);
                out.write(json.getBytes());
            } else {
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Questify");
                if (!dir.exists() && !dir.mkdirs()) {
                    return false;
                }
                File file = new File(dir, name + ".json");
                out = new FileOutputStream(file);
                out.write(json.getBytes());
            }
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }
}

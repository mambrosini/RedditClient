package com.example.maxi.redditclient.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.maxi.redditclient.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Maxi on 19/9/2016.
 */
public class StorageHelper {

    private static final String REDDIT_IMAGES_FOLDER = "/Reddit Images";
    private static final String REDDIT_IMAGE_PREFIX = "reddit_IMG_";
    private static final String REDDIT_IMAGES_EXTENSION = ".jpg";

    public static void writeBitmapToStorage(Context context, Bitmap bitmap){
        File file = getFileForPicture();
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
                Toast.makeText(context, context.getResources().getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanMedia(context, file.toString());
    }

    public static File getFileForPicture (){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + REDDIT_IMAGES_FOLDER);
        myDir.mkdirs();

        String fileNameFull = REDDIT_IMAGE_PREFIX + new Date().getTime() + REDDIT_IMAGES_EXTENSION;
        File file = new File(myDir, fileNameFull);

        return file;
    }

    public static void scanMedia(Context context, String fileString){
        MediaScannerConnection.scanFile(context, new String[]{fileString}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }
}

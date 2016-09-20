package com.example.maxi.redditclient.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.maxi.redditclient.R;
import com.example.maxi.redditclient.helpers.ImageHelper;
import com.example.maxi.redditclient.helpers.StorageHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class ImageActivity extends AppCompatActivity {

    private static ProgressDialog progressDialog;
    private static Bitmap downloadedBitmap;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public static final String URL = "URL";
    public static final String THUMB_URL = "THUMB_URL";
    private static String thumbUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        String imageUrl = getIntent().getExtras().getString(URL);
        thumbUrl = getIntent().getExtras().getString(THUMB_URL);
        if (URLUtil.isHttpUrl(imageUrl) || URLUtil.isHttpsUrl(imageUrl)) {
            progressDialog = new ProgressDialog(ImageActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.downloading_image));
            progressDialog.show();
            new ThumbnailTask(getApplicationContext(), imageView, imageUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.save_to_phone:{
                if (downloadedBitmap != null){
                    saveImageToExternalStorage(ImageActivity.this, downloadedBitmap);
                    return true;
                }}
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ThumbnailTask extends AsyncTask<Bitmap, String, Bitmap> {
        ImageView imageView;
        private String mUrl;
        Context context;

        public ThumbnailTask(Context context, ImageView imageView, String url) {
            this.imageView = imageView;
            this.context = context;
            mUrl = url;
        }

        @Override
        protected void onProgressUpdate(String... item) {
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            return getBitmapFromURL(context, mUrl);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressDialog.dismiss();
            downloadedBitmap = bitmap;
            imageView.setImageBitmap(bitmap);
        }
    }

    public static Bitmap getBitmapFromURL(Context context, String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Bitmap resizedBitmap = ImageHelper.getResizedBitmapKeepingAspectRatio(myBitmap, 1280, 1280);
            return resizedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof FileNotFoundException){
                return getBitmapFromURL(context, thumbUrl);
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.downloading_image_error), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public static void saveImageToExternalStorage(Context context, Bitmap finalBitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((ImageActivity)context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                StorageHelper.writeBitmapToStorage(context, downloadedBitmap);
            }
        } else {
            StorageHelper.writeBitmapToStorage(context, downloadedBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StorageHelper.writeBitmapToStorage(getApplicationContext(), downloadedBitmap);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.write_to_storage_permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


}

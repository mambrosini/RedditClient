package com.example.maxi.redditclient.helpers;

import android.graphics.Bitmap;

/**
 * Created by Maxi on 19/9/2016.
 */
public class ImageHelper {
    public static Bitmap getResizedBitmapKeepingAspectRatio(Bitmap bitmap, int requiredWidth, int requiredHeight) {
        if(requiredWidth >= bitmap.getWidth() && requiredHeight >= bitmap.getHeight()){
            return bitmap;
        }else{
            Bitmap resizedBitmap;
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            int newWidth = -1;
            int newHeight = -1;
            float multFactor;
            if(originalHeight > originalWidth) {
                newHeight = requiredHeight;
                multFactor = (float) originalWidth/(float) originalHeight;
                newWidth = (int) (newHeight*multFactor);
            } else if(originalWidth > originalHeight) {
                newWidth = requiredWidth;
                multFactor = (float) originalHeight/ (float)originalWidth;
                newHeight = (int) (newWidth*multFactor);
            } else if(originalHeight == originalWidth) {
                newHeight = requiredHeight;
                newWidth = requiredWidth;
            }
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
            return resizedBitmap;
        }
    }
}

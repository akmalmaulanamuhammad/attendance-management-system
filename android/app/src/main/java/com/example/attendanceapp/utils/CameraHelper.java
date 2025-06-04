package com.example.attendanceapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraHelper {
    private final Context context;
    private File photoFile;
    private Uri photoUri;

    public CameraHelper(Context context) {
        this.context = context;
    }

    public Intent getCameraIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                return takePictureIntent;
            }
        }
        return null;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "ATTENDANCE_" + timeStamp;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public File processImage() {
        if (photoFile == null) return null;

        try {
            // Compress and resize the image
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
            
            // Check if we need to rotate the image
            ExifInterface ei = new ExifInterface(photoFile.getPath());
            int orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    rotatedBitmap = bitmap;
            }

            // Scale down the image if it's too large
            Bitmap scaledBitmap = scaleBitmap(rotatedBitmap, Constants.IMAGE_MAX_SIZE);

            // Create a new file for the processed image
            File processedFile = new File(photoFile.getParent(), "processed_" + photoFile.getName());
            FileOutputStream fos = new FileOutputStream(processedFile);
            
            // Compress and save the image
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY, fos);
            fos.flush();
            fos.close();

            // Delete the original file
            photoFile.delete();
            photoFile = processedFile;

            return processedFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = originalWidth;
        int resizedHeight = originalHeight;

        if (originalHeight > maxDimension || originalWidth > maxDimension) {
            if (originalWidth > originalHeight) {
                resizedWidth = maxDimension;
                resizedHeight = (int) (originalHeight * ((float) maxDimension / originalWidth));
            } else {
                resizedHeight = maxDimension;
                resizedWidth = (int) (originalWidth * ((float) maxDimension / originalHeight));
            }
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, true);
    }

    public void clearCachedPhotos() {
        if (photoFile != null && photoFile.exists()) {
            photoFile.delete();
        }
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && storageDir.exists()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("ATTENDANCE_")) {
                        file.delete();
                    }
                }
            }
        }
    }
}

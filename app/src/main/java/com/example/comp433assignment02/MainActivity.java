package com.example.comp433assignment02;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {

    /**
     *
     */
    int currentPhotoIndex = -1;

    File[] appPhotos;

    final String photoFileExtension = ".png";

    SensorManager sm = null;
    Sensor s = null;

    float shakeThreshold = 12.5F; // Adjust this value as needed
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F; // Example threshold

    private float[] prevAcceleration; // Initialize to null or a known value


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Update the images in the ImageView objects upon opening the app
        updateAppPhotoList();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // For "this" to work, we had to add "implements SensorEventListener" to this class
        // definition
        sm.registerListener(this, s, 1000000);
    }

    /**
     * Sorts the specified File array in descending order by last modified date and then name.
     * @param files
     */
    private void sortFiles(File[] files) {
        Arrays.sort(files, (f1, f2) -> {

            if (f1 == null && f2 == null) {
                return 0;
            }

            if (f1 == null) {
                return -1;
            }

            // compare by date in descending order
            int comparison = Long.compare(f2.lastModified(), f1.lastModified());
            if (comparison != 0) {
                return comparison;
            }

            // If there are equal last modified dates, then compare by name
            return f2.getName().compareToIgnoreCase(f1.getName());
        });
    }

    private File[] getFileList() {
        // 1. Get the picture directory
        File pictureDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // If this directory is valid, add all of the items to the arraylist in descending order.
        if (pictureDirectory == null || !pictureDirectory.isDirectory()) {
            return new File[0];
        }

        File[] files = pictureDirectory.listFiles();
        if (files == null || files.length == 0) {
            return new File[0];
        }

        // Sort the files in descending order by date then name
        sortFiles(files);

        return files;
    }

    /**
     * How do you see the images in Android Studio?
     * on macOS, in Android Studio, go to View --> Tool Windows --> Device Manager.
     * Navigate to the data --> data folder and find the folder for your application.
     * This app's folder would be com.example.comp433assignment02. Look in the "files"
     * folder within this folder for the actual files.
     */
    private void updateAppPhotoList() {
        // make sure that the list of photos is up to date
        File[] allPhotos = getFileList();

        int numPhotos = allPhotos.length;

        Log.v("updateAppPhotoList", "# of photos: " + numPhotos);

        ImageView[] imageViews = {findViewById(R.id.iv1), findViewById(R.id.iv2), findViewById(R.id.iv3)};

        for (int i = 0; i < imageViews.length; i++) {
            // Set the default background to a dark gray for the ImageView
            imageViews[i].setBackgroundColor(Color.rgb(169, 169, 169));
            imageViews[i].setImageBitmap(null);

            // If there are no more photos, then skip to the next ImageView (if applicable)
            if (i + 1 > numPhotos) {
                continue;
            }

            File currentImage = allPhotos[i];

            // Update the imageview with the appropriate image
            imageViews[i].setBackgroundColor(Color.rgb(255, 255, 255));
            Bitmap image = BitmapFactory.decodeFile(currentImage.getAbsolutePath());
            imageViews[i].setImageBitmap(image);
        }

        // Clear the canvas
        onClickClear(null);

        // Update the
        this.appPhotos = allPhotos;
    }



    /**
     * Returns a File object for saving a full-size photo from the current canvas.
     * @return File
     * @throws IOException
     * <a href="https://developer.android.com/media/camera/camera-deprecated/photobasics#TaskPath">...</a>
     */
    private File createImageFile() throws IOException {
        // Create the filename first
        // The Locale.US is optional, sets the timezone for the date
        String timeStamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.US
        ).format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        // Seems like you have to create a File object for the parent directory of the photo
        // that will be returned from the camera
        // This points to this folder in the file system:
        // /storage/emulated/0/Android/data/com.example.comp433assignment02
        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                this.photoFileExtension,         /* suffix */
                imageDir      /* directory */
        );
    }

    public void onClickClear(View view) {
        MyDrawingArea v = findViewById(R.id.drawingarea);
        v.clearDrawing();
    }

    public void onClickSave(View view) {

        try {
            MyDrawingArea v = findViewById(R.id.drawingarea);
            Bitmap b = v.getBitmap(); //we wrote this function inside custom view
            File f = createImageFile();
            FileOutputStream fos = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Log.v("onClickSave()", f.getAbsolutePath());

            // Update the ImageViews with the latest photos
            updateAppPhotoList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public void onClickShake(View view) {

        MyDrawingArea v = findViewById(R.id.drawingarea);
        v.getCreateCircles();
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     *
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     *
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     *
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (prevAcceleration == null) {
            prevAcceleration = new float[3];
            System.arraycopy(event.values, 0, prevAcceleration, 0, 3);
            return;
        }

        float[] currentAcceleration = event.values;
        float deltaX = currentAcceleration[0] - prevAcceleration[0];
        float deltaY = currentAcceleration[1] - prevAcceleration[1];
        float deltaZ = currentAcceleration[2] - prevAcceleration[2];

        prevAcceleration[0] = currentAcceleration[0];
        prevAcceleration[1] = currentAcceleration[1];
        prevAcceleration[2] = currentAcceleration[2];

        // Check for a shake event based on the acceleration change
        if (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) > shakeThreshold) {
            // Device is shaking! Trigger your shake action here.

            MyDrawingArea v = findViewById(R.id.drawingarea);
            v.getCreateCircles();
        }


    }
}
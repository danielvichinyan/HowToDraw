package com.introtoandroid.howtodraw;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * A class which is using AsyncTask
 * Used for saving a drawing into the internal storage of a device
 * @author Daniel Vichinyan
 *
 */
public class SaveAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private Bitmap bitmap;
    private File directory;

    public SaveAsyncTask(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        this.directory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ContextWrapper cw = new ContextWrapper(context);
        String fileName = "HowToDraw" + System.currentTimeMillis();
        File myPath = new File(directory, fileName + ".jpg");

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast message = Toast.makeText(context, "Image saved! " + directory.getAbsolutePath(), Toast.LENGTH_LONG);
        message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
        message.show();
    }
}

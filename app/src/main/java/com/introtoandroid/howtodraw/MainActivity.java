package com.introtoandroid.howtodraw;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;
    private DrawView drawView;
    private AlertDialog.Builder alertDialog;
    private ImageView lineSizeImageView;
    private AlertDialog dialogLineSize;
    private AlertDialog colorDialog;

    private SeekBar seekBarAlphaColor;
    private SeekBar seekBarRedColor;
    private SeekBar seekBarGreenColor;
    private SeekBar seekBarBlueColor;
    private View colorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = findViewById(R.id.view);
        requestForPermission(getApplicationContext());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // creating case for each click of a button (a total of 4)
        switch (item.getItemId()) {
            // when the user clicks the save button
            case R.id.saveButtonId:
                drawView.saveDrawing();
                break;
            // when the user clicks the color button
            case R.id.buttonColorId:
                colorDialog();
                break;
            // when the user clicks the line size button
            case R.id.buttonLineWidth:
                lineSizeDialog();
                break;
            // when the user clicks the clear/erase button
            case R.id.eraseButtonId:
                drawView.clearPaint();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void colorDialog() {
        alertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);
        seekBarAlphaColor = view.findViewById(R.id.alphaSeekBar);
        seekBarRedColor = view.findViewById(R.id.redSeekBar);
        seekBarGreenColor = view.findViewById(R.id.greenSeekBar);
        seekBarBlueColor = view.findViewById(R.id.blueSeekBar);
        colorView = view.findViewById(R.id.colorView);

        // create event listeners on all seek bars for colors
        seekBarAlphaColor.setOnSeekBarChangeListener(changeColorSeekBar);
        seekBarRedColor.setOnSeekBarChangeListener(changeColorSeekBar);
        seekBarGreenColor.setOnSeekBarChangeListener(changeColorSeekBar);
        seekBarBlueColor.setOnSeekBarChangeListener(changeColorSeekBar);

        int color = drawView.getDrawingColor();
        seekBarAlphaColor.setProgress(Color.alpha(color));
        seekBarRedColor.setProgress(Color.red(color));
        seekBarGreenColor.setProgress(Color.green(color));
        seekBarBlueColor.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setDrawingColor(Color.argb
                        (seekBarAlphaColor.getProgress(),
                                seekBarRedColor.getProgress(),
                                seekBarGreenColor.getProgress(),
                                seekBarBlueColor.getProgress()
                        ));

                colorDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.setTitle("Create color:");
        colorDialog = alertDialog.create();
        colorDialog.show();
    }

    void lineSizeDialog() {
        alertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.linesize_dialog, null);
        final SeekBar sizeSeekBar = view.findViewById(R.id.sizeSeekBar);
        Button setLineSizeButton = view.findViewById(R.id.buttonLineSize);
        lineSizeImageView = view.findViewById(R.id.imageViewId);
        setLineSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setLineWidth(sizeSeekBar.getProgress());
                dialogLineSize.dismiss();
                alertDialog = null;
            }
        });

        sizeSeekBar.setOnSeekBarChangeListener(lineSizeSeekBar);
        sizeSeekBar.setProgress(drawView.getLineWidth()); // displays a default size

        alertDialog.setView(view);
        dialogLineSize = alertDialog.create();
        dialogLineSize.setTitle("Set Line Size:");
        dialogLineSize.show();
    }

    /**
     * A method which is creating and updating color
     * according to the user's input
     */
    private SeekBar.OnSeekBarChangeListener changeColorSeekBar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            drawView.setBackgroundColor(Color.argb(seekBarAlphaColor.getProgress(), seekBarRedColor.getProgress(), seekBarGreenColor.getProgress(), seekBarBlueColor.getProgress()));

            //display the current color we just made
            colorView.setBackgroundColor(Color.argb(seekBarAlphaColor.getProgress(), seekBarRedColor.getProgress(), seekBarGreenColor.getProgress(), seekBarBlueColor.getProgress()));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * A method which is creating and updating the line size
     * according to the user's input
     */
    private SeekBar.OnSeekBarChangeListener lineSizeSeekBar = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint p = new Paint();
            p.setColor(drawView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);
            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30, 50, 370, 50, p);
            lineSizeImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Dynamically ask for permission
     *
     */
    public boolean requestForPermission(Context context) {

        boolean hasPermission = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd(context)) {
                hasPermission = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return hasPermission;
    }

    public boolean canAccessExternalSd(Context context) {
        return (hasPermission(context));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(Context context) {
        return (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));

    }

}

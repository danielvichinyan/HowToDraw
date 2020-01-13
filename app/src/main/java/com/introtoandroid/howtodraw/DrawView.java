package com.introtoandroid.howtodraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

public class DrawView extends View {

    public static final float TOUCH_TOLERANCE = 10;
    private Paint drawingLine;
    private Paint paint;
    private Bitmap bitmap;
    private HashMap<Integer, Point> pointMap;
    private HashMap<Integer, Path> pathMap;
    private Canvas canvasBitmap;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        paint = new Paint();

        drawingLine = new Paint();
        drawingLine.setAntiAlias(true);
        drawingLine.setColor(Color.BLACK);
        drawingLine.setStyle(Paint.Style.STROKE);
        drawingLine.setStrokeWidth(5);
        drawingLine.setStrokeCap(Paint.Cap.ROUND);
        this.bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        this.canvasBitmap = new Canvas();
        this.pathMap = new HashMap<>();
        this.pointMap = new HashMap<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvasBitmap = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paint);

        for (Path path :
                pathMap.values()) {
            canvas.drawPath(path, drawingLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // type of event we are getting, is it up or is it down
        int actionIndex = event.getActionIndex(); // the pointer ( in this case is finger )*/

        // action is down which means that the finger is down
        // so we know that the touch has started
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            // when that happens, we are going to get the X coordinate as well as the Y coordinate of the finger
            // and we also need to get the ID of that particular spot
            touchScreen(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {

            endedFingerTouch(event.getPointerId(actionIndex));
        } else {
            moveFinger(event);
        }

        invalidate(); // it just redraws the screen
        return true;
    }

    private void moveFinger(MotionEvent event) {

        // as long as we get points on the screen, this means that we are still moving our finger
        for (int i = 0; i < event.getPointerCount(); i++) {
            // for each iteration we are getting a pointer id
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            // we are moving our finger, so each time we are going to get new X and Y values
            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = pointMap.get(pointerId);

                // How far have we moved since the last update ( new X and Y values )
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is long enough to be considered a movement
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // we are moving the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

                    // store our new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }

    }

    public void setDrawingColor(int color) {
        drawingLine.setColor(color);
    }

    public int getDrawingColor() {
        return drawingLine.getColor();
    }

    public void setLineWidth(int width) {
        drawingLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) drawingLine.getStrokeWidth();
    }

    public void clearPaint() {
        pathMap.clear(); // removes all containing paths
        pointMap.clear(); // also removes all points
        bitmap.eraseColor(Color.WHITE); // background color
        invalidate();
    }

    private void endedFingerTouch(int pointerId) {
        Path path = pathMap.get(pointerId); // we are getting the current path
        canvasBitmap.drawPath(path, drawingLine); // we are drawing the path to a bitmap canvas object
        path.reset();
    }

    private void touchScreen(float x, float y, int pointerId) {
        Path path; // for each touch that we make, we are storing the path
        Point point; // we are storing the last point in the path


        if (pathMap.containsKey(pointerId)) {
            path = pathMap.get(pointerId);
            point = pointMap.get(pointerId);
        } else {
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            pointMap.put(pointerId, point);
        }

        // we have to move over to the coordinates of the touch
        path.moveTo(x, y);
        // assigning x and y to whatever values we get from our floats
        point.x = (int) x;
        point.y = (int) y;
    }

    public void saveDrawing() {
        SaveAsyncTask task = new SaveAsyncTask(getContext(), bitmap);
        task.execute();
    }
}

package com.example.comp433assignment02;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDrawingArea extends View {

    Path path = new Path();
    Bitmap bmp;
    Paint p;

    Paint circlePaint;

    int circleRadius = 20;

    int circleSpacing = 60;

    /**
     * These arraylists are used for the coordinates for where to draw the circles
     */
    ArrayList<Circle> circles = new ArrayList<>(100);

    private void configurePaint() {
        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5f);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);

        setBackgroundColor(Color.rgb(169, 169, 169));
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public MyDrawingArea(Context context) {
        super(context);

        configurePaint();
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public MyDrawingArea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        configurePaint();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @see #View(Context, AttributeSet)
     */
    public MyDrawingArea(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        configurePaint();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute or style resource. This constructor of View allows
     * subclasses to use their own base style when they are inflating.
     * <p>
     * When determining the final value of a particular attribute, there are
     * four inputs that come into play:
     * <ol>
     * <li>Any attribute values in the given AttributeSet.
     * <li>The style resource specified in the AttributeSet (named "style").
     * <li>The default style specified by <var>defStyleAttr</var>.
     * <li>The default style specified by <var>defStyleRes</var>.
     * <li>The base values in this theme.
     * </ol>
     * <p>
     * Each of these inputs is considered in-order, with the first listed taking
     * precedence over the following ones. In other words, if in the
     * AttributeSet you have supplied <code>&lt;Button * textColor="#ff000000"&gt;</code>
     * , then the button's text will <em>always</em> be black, regardless of
     * what is specified in any of the styles.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the view, used only if
     *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
     *                     to not look for defaults.
     * @see #View(Context, AttributeSet, int)
     */
    public MyDrawingArea(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        configurePaint();
    }

    /**
     * This function came straight from the PDF explaining how to complete class work 02.
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        canvas.drawPath(path, p);
        invalidate();

        for (Circle circle : circles) {
            canvas.drawCircle(circle.getX(), circle.getY(), circle.radius, circlePaint);

            // move the circle
            circle.move();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // defines two variables on the same line
        float x = event.getX(), y = event.getY();
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN){
            path.moveTo(x, y); //path is global. Same thing that onDraw uses.
            return true;
        }

        if (action == MotionEvent.ACTION_MOVE){
            path.lineTo(x, y);
            return true;
        }

        return false;
    }

    public Path getPath() {
        return path;
    }

    public void getCreateCircles() {

        // clear any existing circles
        circles.clear();

        PathMeasure pm = new PathMeasure(path, false);
        float[] pos = new float[2];

        // Iterate all contours (a Path can have multiple subpaths)
        do {

            float len = pm.getLength();
            if (len <= 0) {
                continue;
            }

            float d = 0f;
            while (d <= len) {
                if (pm.getPosTan(d, pos, null)) {
                    Circle c = new Circle(
                            pos[0],
                            pos[1],
                            circleRadius,
                            getHeight(),
                            0.01F,
                            30
                    );
                    circles.add(c);
                }

                d += circleSpacing;


            }

        } while (pm.nextContour());


    }

    /**
     * Clear the path.
     */
    public void clearDrawing() {
        path.reset();
        circles.clear();
    }

    /**
     * Retrieve a bitmap from the canvas that is the drawing area.
     * @return
     */
    public Bitmap getBitmap()
    {
        bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawPath(path, p); //path is global. The very same thing that onDraw uses.
        return bmp;
    }
}

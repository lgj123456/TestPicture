package lgj.example.com.testpicture;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by yhdj on 2017/6/4.
 */

public class MyView extends ImageView {
    private Context mContext;
    private Matrix currentMatrix, savedMatrix;

    private float startX, startY;
    private PointF startF = new PointF();
    private PointF midF;
    private float secondeDis = 1f;
    private static final int MODE_NONE = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;
    private int mode = MODE_NONE;
    private float saveRoate = 0;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;


        init();
    }

    private void init() {

        currentMatrix = new Matrix();
        savedMatrix = new Matrix();


//        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(outMetrics);
//        int Screenwidth = outMetrics.widthPixels;
//        int Screenheight = outMetrics.heightPixels;


//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
     //  Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.a);
//
////        bitmap = Bitmap.createScaledBitmap(bitmap, Screenwidth, Screenheight, true);
//        setImageBitmap(bitmap);
     //  setImageBitmap(bitmap1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(currentMatrix);
                startF.set(event.getX(), event.getY());
                mode = MODE_DRAG;


                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                secondeDis = calDis(event);
                if (secondeDis > 10) {
                    savedMatrix.set(currentMatrix);
                    midF = calMidPoint(event);
                    mode = MODE_ZOOM;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_DRAG) {
                    currentMatrix.set(savedMatrix);
                    float dx = event.getX() - startF.x;
                    float dy = event.getY() - startF.y;
                    currentMatrix.postTranslate(dx, dy);
                } else if (mode == MODE_ZOOM && event.getPointerCount() == 2) {
                    float firstDis = calDis(event);
                    float rotate = calRotation(event);
                    currentMatrix.set(savedMatrix);
                    if (firstDis > 10) {
                        float scale = firstDis / secondeDis;
                        currentMatrix.postScale(scale, scale, midF.x, midF.y);
                    }
                    if (Math.abs(rotate - saveRoate) > 10f) {
                        currentMatrix.postRotate(rotate - saveRoate, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
                mode = MODE_NONE;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                savedMatrix.set(currentMatrix);
                if (event.getActionIndex() == 0) {
                    startF.set(event.getX(1), event.getY(1));

                } else if (event.getActionIndex() == 1) {
                    startF.set(event.getX(0), event.getY(0));
                    mode = MODE_DRAG;
                }
                break;
        }

        setImageMatrix(currentMatrix);
        return true;
    }

    private float calRotation(MotionEvent event) {
        double deltaX = event.getX(0) - event.getX(1);
        double deltaY = event.getY(0) - event.getY(1);
        double radius = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radius);
    }

    private PointF calMidPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    private float calDis(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
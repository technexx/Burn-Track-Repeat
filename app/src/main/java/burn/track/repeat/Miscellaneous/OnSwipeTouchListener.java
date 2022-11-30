package burn.track.repeat.Miscellaneous;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class OnSwipeTouchListener implements View.OnTouchListener {

    Context mContext;
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        this.mContext = context;
        gestureDetector = new GestureDetector(mContext, new GestureListener());
    }

    public void onSwipeLeft() {
        Toast.makeText(mContext, "Swiping left!", Toast.LENGTH_SHORT).show();
    }

    public void onSwipeRight() {
        Toast.makeText(mContext, "Swiping right!", Toast.LENGTH_SHORT).show();
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 65;
        private static final int SWIPE_VELOCITY_THRESHOLD = 65;

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}
package dream.use_doublescrolllayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 最后一个 child 拥有全部空间
 * 支持内外连续卷动，需要提供接口实现
 * 增加下拉刷新，也需要提供接口实现
 */
public class DoubleScrollLayout extends LinearLayout {

    private static final float REFRESH_HEAD_MOVE_SCALE = -0.36F;

    private final int touchSlop;
    private final int minimumVelocity;
    private final int maximumVelocity;

    private VelocityTracker velocityTracker;
    private Scroller scroller;

    private int maxScroll;
    private float lastY;
    private float error;

    private ScrollViewController scrollViewController;
    private RefreshHeadController refreshHeadController;

    public DoubleScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获得能够进行手势滑动的距离
        touchSlop = configuration.getScaledTouchSlop();
        // 获得允许执行一个fling手势动作的最小速度值
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        // 获得允许执行一个fling手势动作的最大速度值
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        scroller = new Scroller(context);
        setOrientation(VERTICAL);
        scrollViewController = new ScrollViewController() {
            @Override
            public boolean isScrollTop() { return true; }
            @Override
            public void scrollBy(int deltaY) { }
        };
        refreshHeadController = new RefreshHeadController() {
            @Override
            public void modifyHeight(float deltaY) { }

            @Override
            public void back() { }

            @Override
            public boolean isMin() { return true; }
        };
    }

    public void setScrollViewController(ScrollViewController scrollViewController) {
        this.scrollViewController = scrollViewController;
    }

    public void setRefreshHeadController(RefreshHeadController refreshHeadController) {
        this.refreshHeadController = refreshHeadController;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        if (childCount > 1) {
            View lastView = getChildAt(childCount - 1);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            maxScroll = height - lastView.getMeasuredHeight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            lastView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                lastY = event.getY();
                error = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getY() - lastY) > touchSlop) {
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                move(event.getY(), true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
                lastY = -getScrollY();
                error = 0;
                velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
                int initialYVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialYVelocity) > minimumVelocity) {
                    fling(-initialYVelocity);
                }
                releaseVelocityTracker();
                if (!refreshHeadController.isMin()) {
                    refreshHeadController.back();
                }
                break;
        }
        return true;
    }

    private void move(float y, boolean enableRefresh) {
        int deltaY = (int) (lastY + error - y);
        error = lastY + error - y - deltaY;
        lastY = y;
        int scrollY = getScrollY();
        if (scrollY < maxScroll) {
            if (deltaY < 0) {
                if (enableRefresh && scrollY == 0) {
                    refreshHeadController.modifyHeight(deltaY * REFRESH_HEAD_MOVE_SCALE);
                    return;
                } else if (deltaY + scrollY < 0) {
                    if (enableRefresh) {
                        refreshHeadController.modifyHeight((deltaY + scrollY) * REFRESH_HEAD_MOVE_SCALE);
                    }
                    deltaY = -scrollY;
                }
            } else if (deltaY > 0) {
                if (enableRefresh && scrollY == 0 && !refreshHeadController.isMin()) {
                    refreshHeadController.modifyHeight(deltaY * REFRESH_HEAD_MOVE_SCALE);
                    return;
                } else if (deltaY + scrollY > maxScroll) {
                    scrollViewController.scrollBy(deltaY + scrollY - maxScroll);
                    deltaY = maxScroll - scrollY;
                }
            }
            scrollBy(0, deltaY);
        } else {
            if (deltaY < 0 && scrollViewController.isScrollTop()) {
                scrollBy(0, deltaY);
            } else {
                scrollViewController.scrollBy(deltaY);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            move(-scroller.getCurrY(), false);
            invalidate();
        }
    }

    public void fling(int velocityY) {
        scroller.fling(0, getScrollY(), 0, velocityY, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        awakenScrollBars(scroller.getDuration());
        invalidate();
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }
}

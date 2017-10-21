package dream.use_doublescrolllayout.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 状态
 * 正常状态
 * 拖拽-近
 * 拖拽-远
 * 返回-远
 * 返回-近
 * 子View高度
 *
 * 只用一个child view
 */
public class RefreshHeadLayout extends ViewGroup implements RefreshHeadController {

	public interface OnShowStateChangedListener {

		int SHOW_STATE_NORMAL = 0;
		int SHOW_STATE_SCALE_INSIDE = 1;
		int SHOW_STATE_SCALE_OUTSIDE = 2;
		int SHOW_STATE_BACK_OUTSIDE = 3;
		int SHOW_STATE_BACK_INSIDE = 4;
		int SHOW_STATE_CHILD_HEIGHT = 5;

		void onInitView(RefreshHeadLayout layout);
		void onShowStateChangedEvent(int state);
	}

	private static final int BACK_IGNORE_MIN_VALUE = 4;
	private static final int MSG_DELAY = 17;
	private static final float BACK_HEIGHT_SCALE = 0.5f;

	private int currentHeight;
	private int state;
	private OnShowStateChangedListener listener;

	private float error;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case OnShowStateChangedListener.SHOW_STATE_BACK_OUTSIDE:
					backAnimation(getChildAt(0).getMeasuredHeight(), OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT);
					break;
				case OnShowStateChangedListener.SHOW_STATE_BACK_INSIDE:
					backAnimation(0, OnShowStateChangedListener.SHOW_STATE_NORMAL);
					break;
				default:
					break;
			}
		}

		private void backAnimation(int endHeight, int endState) {
			int nextHeight = currentHeight - (int)((currentHeight - endHeight) * BACK_HEIGHT_SCALE);
			if (nextHeight <= endHeight + BACK_IGNORE_MIN_VALUE) {
				adjustHeight(endHeight);
				state = endState;
				notifyStateChanged();
			} else {
				adjustHeight(nextHeight);
				handler.sendEmptyMessageDelayed(state, MSG_DELAY);
			}
		}
	};

	public RefreshHeadLayout(Context context) { super(context); }

	public RefreshHeadLayout(Context context, AttributeSet attrs) { super(context, attrs); }

	public void setOnShowStateChangedListener(OnShowStateChangedListener listener) {
		this.listener = listener;
		if (listener != null) {
			listener.onInitView(this);
		}
	}

	@Override
	public void modifyHeight(float deltaY) {
		if (state == OnShowStateChangedListener.SHOW_STATE_BACK_OUTSIDE
				|| state == OnShowStateChangedListener.SHOW_STATE_BACK_INSIDE
				|| state == OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT) {
			handler.removeMessages(OnShowStateChangedListener.SHOW_STATE_BACK_OUTSIDE);
			handler.removeMessages(OnShowStateChangedListener.SHOW_STATE_BACK_INSIDE);
		}
		int height = (int) (currentHeight + deltaY + error);
		error = currentHeight + deltaY + error - height;
		if (height < 0) {
			height = 0;
		}
		if (height == 0) {
			if (state != OnShowStateChangedListener.SHOW_STATE_NORMAL) {
				adjustHeight(height);
				state = OnShowStateChangedListener.SHOW_STATE_NORMAL;
				notifyStateChanged();
			}
		} else if (height > 0) {
			adjustHeight(height);
			if (height > getChildAt(0).getMeasuredHeight()) {
				if (state != OnShowStateChangedListener.SHOW_STATE_SCALE_OUTSIDE) {
					state = OnShowStateChangedListener.SHOW_STATE_SCALE_OUTSIDE;
					notifyStateChanged();
				}
			} else {
				if (state != OnShowStateChangedListener.SHOW_STATE_SCALE_INSIDE) {
					state = OnShowStateChangedListener.SHOW_STATE_SCALE_INSIDE;
					notifyStateChanged();
				}
			}
		}
	}

	private void adjustHeight(int height) {
		if (currentHeight != height) {
			currentHeight = height;
			requestLayout();
		}
	}

	private void notifyStateChanged() {
		if (listener != null) {
			listener.onShowStateChangedEvent(state);
		}
	}

	public void back() {
		if (state == OnShowStateChangedListener.SHOW_STATE_SCALE_OUTSIDE) {
			state = OnShowStateChangedListener.SHOW_STATE_BACK_OUTSIDE;
			handler.sendEmptyMessage(state);
			notifyStateChanged();
		} else if (state == OnShowStateChangedListener.SHOW_STATE_SCALE_INSIDE || state == OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT) {
			state = OnShowStateChangedListener.SHOW_STATE_BACK_INSIDE;
			handler.sendEmptyMessage(state);
			notifyStateChanged();
		}
	}

	@Override
	public boolean isMin() {
		return currentHeight <= 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		View childView = getChildAt(0);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		childView.measure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, currentHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View childView = getChildAt(0);
		int bottom = b - t;
		int top = bottom - childView.getMeasuredHeight();
		childView.layout(0, top, childView.getMeasuredWidth(), bottom);
	}
}

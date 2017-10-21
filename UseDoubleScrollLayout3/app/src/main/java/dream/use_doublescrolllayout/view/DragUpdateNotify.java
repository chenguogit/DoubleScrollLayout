package dream.use_doublescrolllayout.view;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import dream.use_doublescrolllayout.R;

/**
 * 管理刷新状态的显示
 */
public class DragUpdateNotify implements RefreshHeadLayout.OnShowStateChangedListener {

	public interface OnStartRefreshListener {
		void onStartRefresh();
	}

	private ImageView iconView;
	private TextView titleView;

	private Animation refreshingAnimation;
	private Animation preparingAnimation;
	private Animation preparedAnimation;
	private int lastState;

	private OnStartRefreshListener listener;

	public DragUpdateNotify(OnStartRefreshListener listener) {
		this.listener = listener;
	}

	@Override
	public void onInitView(RefreshHeadLayout view) {
		iconView = (ImageView)view.findViewById(R.id.icon);
		titleView = (TextView)view.findViewById(R.id.title);

		refreshingAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.unlimited_rotate);

		final float startAngle = -180f;
		final float center = 0.5f;
		final int duration = 500;
		preparingAnimation = new RotateAnimation(startAngle, 0, Animation.RELATIVE_TO_SELF, center
				, Animation.RELATIVE_TO_SELF, center);
		preparingAnimation.setDuration(duration);
		preparingAnimation.setFillEnabled(true);
		preparingAnimation.setFillAfter(true);

		preparedAnimation = new RotateAnimation(-startAngle, 0, Animation.RELATIVE_TO_SELF, center
				, Animation.RELATIVE_TO_SELF, center);
		preparedAnimation.setDuration(duration);
		preparedAnimation.setFillEnabled(true);
		preparedAnimation.setFillAfter(true);
	}

	@Override
	public void onShowStateChangedEvent(int state) {
		switch (state) {
			case SHOW_STATE_SCALE_INSIDE:
				if (lastState == SHOW_STATE_SCALE_OUTSIDE) {
					setArrowDownWithAnimation();
				} else {
					setArrowDown();
				}
				break;
			case SHOW_STATE_SCALE_OUTSIDE:
				setArrowUpWithAnimation();
				break;
			case SHOW_STATE_BACK_OUTSIDE:
				setRefreshAnimation();
				if (listener != null) {
					listener.onStartRefresh();
				}
				break;
			default:
				break;
		}
		lastState = state;
	}

	public DragUpdateNotify setTitle(String title) {
		titleView.setText(title);
		return this;
	}

	public void setArrowDown() {
		iconView.clearAnimation();
		iconView.setImageResource(R.drawable.refresh_down_arrow);
	}

	public void setArrowDownWithAnimation() {
		iconView.clearAnimation();
		iconView.setImageResource(R.drawable.refresh_down_arrow);
		iconView.startAnimation(preparedAnimation);
	}

	public void setArrowUpWithAnimation() {
		iconView.clearAnimation();
		iconView.setImageResource(R.drawable.refresh_up_arrow);
		iconView.startAnimation(preparedAnimation);
	}

	public void setRefreshAnimation() {
		iconView.clearAnimation();
		iconView.setImageResource(R.drawable.refresh);
		iconView.startAnimation(refreshingAnimation);
	}
}

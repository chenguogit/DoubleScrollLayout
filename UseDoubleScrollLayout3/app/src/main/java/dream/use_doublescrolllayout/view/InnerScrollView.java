package dream.use_doublescrolllayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class InnerScrollView extends ScrollView implements ScrollViewController {

    public InnerScrollView(Context context) {
        super(context);
    }

    public InnerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isScrollTop() {
        return getScrollY() == 0;
    }

    @Override
    public void scrollBy(int deltaY) {
        scrollBy(0, deltaY);
    }
}

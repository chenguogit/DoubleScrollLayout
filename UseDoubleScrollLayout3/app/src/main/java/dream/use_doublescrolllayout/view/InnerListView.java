package dream.use_doublescrolllayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class InnerListView extends ListView implements ScrollViewController {

    public InnerListView(Context context) {
        super(context);
    }

    public InnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isScrollTop() {
        return getChildCount() == 0 || (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0);
    }

    @Override
    public void scrollBy(int deltaY) {
        scrollListBy(deltaY);
    }
}

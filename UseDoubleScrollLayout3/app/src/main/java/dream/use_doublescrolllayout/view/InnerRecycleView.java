package dream.use_doublescrolllayout.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class InnerRecycleView extends RecyclerView implements ScrollViewController {
    public InnerRecycleView(Context context) {
        super(context);
    }

    public InnerRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isScrollTop() {
        return !canScrollVertically(-1);
    }

    @Override
    public void scrollBy(int deltaY) {
        scrollBy(0, deltaY);
    }
}

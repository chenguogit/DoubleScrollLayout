package dream.use_doublescrolllayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import dream.use_doublescrolllayout.view.DoubleScrollLayout;
import dream.use_doublescrolllayout.view.DragUpdateNotify;
import dream.use_doublescrolllayout.view.InnerScrollView;
import dream.use_doublescrolllayout.view.RefreshHeadLayout;

public class TestScrollViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_scrollview);

        DoubleScrollLayout doubleScrollLayout = (DoubleScrollLayout) findViewById(R.id.double_scroll);
        doubleScrollLayout.setScrollViewController((InnerScrollView) findViewById(R.id.scroll_view));
        final RefreshHeadLayout refreshHeadLayout = (RefreshHeadLayout) findViewById(R.id.refresh_head);
        doubleScrollLayout.setRefreshHeadController(refreshHeadLayout);
        refreshHeadLayout.setOnShowStateChangedListener(new DragUpdateNotify(new DragUpdateNotify.OnStartRefreshListener() {
            Handler handler = new Handler();
            @Override
            public void onStartRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshHeadLayout.back();
                    }
                }, 500);
            }
        }));
    }
}

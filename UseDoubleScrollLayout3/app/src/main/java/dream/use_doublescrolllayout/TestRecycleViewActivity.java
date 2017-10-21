package dream.use_doublescrolllayout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dream.use_doublescrolllayout.view.DoubleScrollLayout;
import dream.use_doublescrolllayout.view.DragUpdateNotify;
import dream.use_doublescrolllayout.view.InnerListView;
import dream.use_doublescrolllayout.view.InnerRecycleView;
import dream.use_doublescrolllayout.view.RefreshHeadLayout;

public class TestRecycleViewActivity extends AppCompatActivity {

    LayoutInflater inflater;
    float density;
    MyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = LayoutInflater.from(this);
        density = getResources().getDisplayMetrics().density;
        adapter = new MyAdapter();

        setContentView(R.layout.test_recycleview);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        InnerRecycleView recyclerView = (InnerRecycleView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(true));

        DoubleScrollLayout doubleScrollLayout = (DoubleScrollLayout) findViewById(R.id.double_scroll);
        doubleScrollLayout.setScrollViewController(recyclerView);
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class Data {
        String info;
        int size;

        public Data(String info) {
            this.info = info;
            size = (int)(Math.random() * 40 * density + density * 40);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        List<Data> dataList = new ArrayList<>();

        public MyAdapter() {
            for (int i = 0; i < 100; i++) {
                dataList.add(new Data("" + i));
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(inflater.inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(dataList.get(position).info);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = dataList.get(position).size;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    class MyDecoration extends RecyclerView.ItemDecoration {

        Paint paint = new Paint();
        boolean drawLine;

        public MyDecoration(boolean drawLine) {
            paint.setColor(0xff00ff00);
            this.drawLine = drawLine;
        }

        // 绘制Item项之前，执行该方法
        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = parent.getChildAt(i);
                if (drawLine) {
                    int left = view.getLeft();
                    int right = view.getRight();
                    int top = view.getTop();
                    int bottom = view.getBottom();
                    c.drawLine(left, bottom, right, bottom, paint);
                    c.drawLine(right, top, right, bottom, paint);
                }
            }
        }

        // 绘制Item项之后，执行该方法
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
        }

        // 类似于ViewGroup的padding值的影响
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(1, 1, 1, 1); // 如果这里不设置，在onDraw方法里绘制的内容将被item项完全覆盖
        }
    }
}

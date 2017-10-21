package dream.use_doublescrolllayout;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    private static final String [] show_data = {
            "测试 ListView",
            "测试 ScrollView",
            "测试 RecycleView"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, show_data));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, TestListViewActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, TestScrollViewActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, TestRecycleViewActivity.class));
                break;
        }
    }
}

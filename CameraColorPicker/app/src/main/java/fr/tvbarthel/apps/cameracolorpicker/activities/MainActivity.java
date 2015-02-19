package fr.tvbarthel.apps.cameracolorpicker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import fr.tvbarthel.apps.cameracolorpicker.R;
import fr.tvbarthel.apps.cameracolorpicker.adapters.ColorAdapter;
import fr.tvbarthel.apps.cameracolorpicker.data.ColorItem;
import fr.tvbarthel.apps.cameracolorpicker.data.ColorItems;
import fr.tvbarthel.apps.cameracolorpicker.utils.ClipDatas;

/**
 * An {@link android.support.v7.app.ActionBarActivity} that shows the list of the colors that the user saved.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    /**
     * A {@link fr.tvbarthel.apps.cameracolorpicker.adapters.ColorAdapter} used for adapting the {@link fr.tvbarthel.apps.cameracolorpicker.data.ColorItem}s.
     */
    protected ColorAdapter mColorAdapter;

    /**
     * The user-visible label for the clip {@link fr.tvbarthel.apps.cameracolorpicker.data.ColorItem}.
     */
    protected String mClipColorItemLabel;

    /**
     * A reference to the current {@link android.widget.Toast}.
     * <p/>
     * Used for hiding the current {@link android.widget.Toast} before showing a new one or the activity is paused.
     * {@link }
     */
    protected Toast mToast;

    protected ListView mListView;

    protected ColorItems.OnColorItemChangeListener mOnColorItemChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClipColorItemLabel = getString(R.string.color_clip_color_label_hex);

        mColorAdapter = new ColorAdapter(this);
        mColorAdapter.addAll(ColorItems.getSavedColorItems(this));
        final View emptyView = findViewById(R.id.activity_main_empty_view);
        mListView = (ListView) findViewById(R.id.activity_main_list_view);
        mListView.setAdapter(mColorAdapter);
        mListView.setEmptyView(emptyView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ColorItem colorItem = mColorAdapter.getItem(position);
                ColorDetailActivity.startWithColorItem(MainActivity.this, colorItem, view.findViewById(R.id.row_color_item_preview));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ColorItem colorItem = mColorAdapter.getItem(position);
                ClipDatas.clipPainText(MainActivity.this, mClipColorItemLabel, colorItem.getHexString());
                showToast(R.string.color_clip_success_copy_message);
                return true;
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_main_fab);
        fab.attachToListView(mListView);
        fab.setOnClickListener(this);

        mOnColorItemChangeListener = new ColorItems.OnColorItemChangeListener() {
            @Override
            public void onColorItemChanged(List<ColorItem> colorItems) {
                mColorAdapter.clear();
                mColorAdapter.addAll(colorItems);
                mColorAdapter.notifyDataSetChanged();
            }
        };

        ColorItems.registerListener(this, mOnColorItemChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideToast();
    }

    @Override
    protected void onDestroy() {
        ColorItems.unregisterListener(this, mOnColorItemChangeListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.menu_main_action_licenses) {
            final Intent intent = new Intent(this, LicenseActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();

        switch (viewId) {
            case R.id.activity_main_fab:
                final Intent intent = new Intent(this, ColorPickerActivity.class);
                startActivity(intent);
                break;

            default:
                throw new IllegalArgumentException("View clicked unsupported. Found " + v);
        }
    }

    /**
     * Hide the current {@link android.widget.Toast}.
     */
    protected void hideToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    /**
     * Show a toast text message.
     *
     * @param resId The resource id of the string resource to use.
     */
    protected void showToast(int resId) {
        hideToast();
        mToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        mToast.show();
    }
}

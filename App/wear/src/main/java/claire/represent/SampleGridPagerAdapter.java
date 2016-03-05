package claire.represent;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;

import android.widget.TextView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.GridViewPager;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by clairelee on 3/3/16.
 */

public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private List<Row> mRows;
    String zipCode;

    public SampleGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;

        mRows = new ArrayList<SampleGridPagerAdapter.Row>();

        if (zipCode != null) {
            mRows.add(new Row(cardFragment(R.string.a, R.string.b),
                    cardFragment(R.string.c, R.string.d),
                    cardFragment(R.string.e, R.string.f)));
            if (zipCode.equals("94704")) {
                mRows.add(new Row(cardFragment(R.string.g2, R.string.h)));
            } else {
                mRows.add(new Row(cardFragment(R.string.g, R.string.h)));
            }
        }
    }


    private Fragment cardFragment(int titleRes, int textRes) {
        Resources res = mContext.getResources();
        CardFragment fragment =
                CardFragment.create(res.getText(titleRes), res.getText(textRes));
        return fragment;
    }

    /** A convenient container for a row of fragments. */
    private class Row {
        final List<Fragment> columns = new ArrayList<Fragment>();
        public Row(Fragment... fragments) {
            for (Fragment f : fragments) {
                add(f);
            }
        }
        public void add(Fragment f) {
            columns.add(f);
        }
        Fragment getColumn(int i) {
            return columns.get(i);
        }
        public int getColumnCount() {
            return columns.size();
        }

    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row adapterRow = mRows.get(row);
        return adapterRow.getColumn(col);
    }

    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return mRows.size();
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return mRows.get(rowNum).getColumnCount();
    }


}

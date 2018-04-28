package com.support.widget;

import com.support.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class MyListView extends ListView implements OnClickListener {
    View mFooter;
    ProgressBar pb;
    TextView tv;
    onLoadMoreListener l;
    private boolean isLoading;
    private boolean mEnable = true;
    OnScrollListener mLis;

    @Override
    public void setOnScrollListener(OnScrollListener l) {
            mLis = l;
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        mFooter = inflater.inflate(R.layout.mlv_footer, null);
//        setBackgroundColor(context.getResources().getColor(R.color.white));
        pb = (ProgressBar) mFooter.findViewById(R.id.pb_footer);
        tv = (TextView) mFooter.findViewById(R.id.tv_footer);
        mFooter.setOnClickListener(this);
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mLis != null)
                    mLis.onScrollStateChanged(view, scrollState);
            }

            int last;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                // 鍒ゆ柇婊氬姩鍒板簳閮�
                if (last != (view.getCount() - 1)
                        && getFooterViewsCount() > 0
                        && mEnable
                        && view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    if (!isLoading) {
                        last = view.getCount() - 1;
                        setIsLoading(true);
                        l.onLoadMore();
                    }
                }
                if (mLis != null)
                    mLis.onScroll(view, firstVisibleItem, visibleItemCount,
                            totalItemCount);
            }
        });
//        addFooterView(mFooter);
        setFooterDividersEnabled(false);
    }

    public void setIsLoading(boolean b) {
        setIsLoading(b, "加载更多");
    }

    public void setIsLoading(boolean b, String text) {
        isLoading = b;
        if (isLoading) {
            pb.setVisibility(View.VISIBLE);
            tv.setVisibility(View.INVISIBLE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
        }
    }

    public boolean getIsLoading() {
        return isLoading;
    }

    public void setFooterEnable(boolean enable) {
        mEnable = enable;
    }

    public void setFooterVisible(boolean enable) {
        if (enable) {
            if (getFooterViewsCount() == 0)
                addFooterView(mFooter);
        } else {
            if (getFooterViewsCount() > 0)
                removeFooterView(mFooter);
        }
//    	mFooter.setVisibility(enable?View.VISIBLE:View.GONE);
//    	mFooter.setEnabled(enable);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        removeFooterView(mFooter);
        setFooterVisible(true);
        super.setAdapter(adapter);
        setFooterVisible(false);
    }

    @Override
    public void onClick(View v) {
        if (!isLoading) {
            if (l != null) {
                setIsLoading(true);
                l.onLoadMore();
            }
        }
    }

    public void setOnLoadMoreListener(onLoadMoreListener lis) {
        l = lis;
    }

    public interface onLoadMoreListener {
        void onLoadMore();
    }
}

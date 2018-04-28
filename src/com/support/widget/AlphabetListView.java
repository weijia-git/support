package com.support.widget;

import com.support.util.ConvertUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlphabetListView extends LinearLayout {

	private char[] alphabets;
	
	private char lastChar;
	
	private int selectBgColor;
	private int defaultBgColor;
	private AlphabetPositionListener listener;

	public AlphabetListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AlphabetListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AlphabetListView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setOrientation(VERTICAL);
		defaultBgColor = Color.WHITE;
		selectBgColor = Color.WHITE;//Color.parseColor("#50000000");
		setBackgroundColor(defaultBgColor);
		setGravity(Gravity.CENTER);
		
		initChildView(context);
	}
	
	private void initChildView(Context context) {
		if (alphabets==null) {
			alphabets = new char[27];
			for (char c = 'A'; c <= 'Z'; c++) {
				alphabets[c - 'A'] = c;
			}
			alphabets[26] = '#';
		}
		TextView textView = null;
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1);
		params.leftMargin = ConvertUtil.dip2px(context, 2);
		params.rightMargin = ConvertUtil.dip2px(context, 2);
		for (int i = 0; i < alphabets.length; i++) {
			textView = new TextView(context);
			textView.setLayoutParams(params);
			textView.setGravity(Gravity.CENTER);
			textView.setText(alphabets[i] + "");
			textView.setTextColor(Color.parseColor("#28C4C5"));
			addView(textView);
		}
	}

	public void setAlphabetPositionListener(AlphabetPositionListener listener) {
		this.listener = listener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int count = alphabets.length;
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 设置字母索引背景
			// alphabetLayout.setBackgroundColor(Color.parseColor("#FF0000"));
			setBackgroundColor(selectBgColor);
			float len = (float) (count == 0 ? 0.1 : (float)count);
			int l = (int) ((ev.getY() +getHeight() / count / 2) / (getHeight() / count))
					- 1;
			if (l >= count)
				l = count - 1;
			else if (l < 0)
				l = 0;
			lastChar = alphabets[l];
			if (listener!=null) {
				listener.onActionDown(alphabets[l]);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			l = (int) ((ev.getY() +getHeight() / count / 2) / (getHeight() / count))
					- 1;
			if (l >= count)
				l = count - 1;
			else if (l < 0)
				l = 0;
			if (lastChar != alphabets[l]) {
				lastChar = alphabets[l];
				if (listener!=null) {
					listener.onPositionChange(alphabets[l]);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			setBackgroundColor(defaultBgColor);
			lastChar = 0;
			listener.onAcitonUp();
			break;
		}
		return true;
	}
	
	public interface AlphabetPositionListener {
		void onPositionChange(char alphabet);
		void onActionDown(char alphabet);
		void onAcitonUp();
    }

}

package com.support.widget;

import com.support.R;
import com.support.util.ConvertUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 核心方法 setTitles、setIndex、setOnTabClickListener 最简单的初始使用流程：1.setTitles
 * 2.setOnTabClickListener
 * 
 * @author xj
 * 
 */
public class TabView extends View {
	public int getForeColor() {
		return foreColor;
	}

	public void setForeColor(int foreColor) {
		this.foreColor = foreColor;
		invalidate();
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
		invalidate();
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
		invalidate();
	}

	public int getPressColor() {
		return pressColor;
	}

	public void setPressColor(int pressColor) {
		this.pressColor = pressColor;
		invalidate();
	}

	private int vH, vW, foreColor = 0xff4fc3FF, bgColor = 0xffffffff,
			borderColor = 0xff4fc3FF, pressColor = 0xffeeeeee;
	private RectF vR;
	private RectF[] trs, lines;
	private String[] titles = { "title0", "title1", "title2" };
	private Paint vpFore, vpBorder, tp0, tp1, p_txt0, p_txt1;
	private Context c;
	private int index = 0, touchIndex = -1;
	OnTabClickListener lis;
	Path path;
	float[] radii;
	float[] radiiLeft;
	float[] radiiRight;
	float round;

	public void setOnTabClickListener(OnTabClickListener l) {
		this.lis = l;
	}

	public interface OnTabClickListener {
		void OnTabClick(int index);
	}

	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.TabView);
		round = ta.getDimension(R.styleable.TabView_round, 10f);
		radii = new float[] { round, round, round, round, round, round, round,
				round };
		radiiLeft = new float[] { round, round, 0f, 0f, 0f, 0f, round, round };
		radiiRight = new float[] { 0f, 0f, round, round, round, round, 0f, 0f };
		CharSequence[] temp = ta.getTextArray(R.styleable.TabView_titles);
		if (temp != null) {
			titles = new String[temp.length];
			for (int i = 0; i < temp.length; i++) {
				titles[i] = temp[i].toString();
			}
		}

		bgColor = ta.getInt(R.styleable.TabView_bgColor, bgColor);
		borderColor = ta.getInt(R.styleable.TabView_borderColor,
				borderColor);
		foreColor = ta.getInt(R.styleable.TabView_foreColor, foreColor);
		pressColor = ta.getInt(R.styleable.TabView_pressColor, pressColor);

		ta.recycle();

		c = context;
		tp0 = new Paint();
		tp0.setAntiAlias(true);
		tp0.setColor(0x00000000);

		setClickable(true);
		p_txt0 = new Paint();
		p_txt0.setTextSize(ConvertUtil.sp2px(c,12));
		p_txt0.setAntiAlias(true);
		p_txt1 = new Paint();
		p_txt1.setTextSize(ConvertUtil.sp2px(c,12));
		p_txt1.setColor(0xffffffff);
		p_txt1.setAntiAlias(true);

	}

	/**
	 * 更改tab项
	 * 
	 * @param arr
	 *            标题
	 * @param c
	 *            颜色
	 */
	public void setTitles(String[] arr, int c) {
		foreColor = c;
		titles = arr;
		invalidate();
	}

	public void setColor(int c) {
		foreColor = c;
		invalidate();
	}

	public void setTitles(String[] arr) {
		titles = arr;
		invalidate();
	}

	public void setIndex(int index) {
		this.index = index;
		invalidate();
	}

	float x, y;
	int selection = -1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = event.getX();
		y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			selection = (int) x / (int) tW + (x % tW > 0 ? 1 : 0) - 1;
			touchIndex = selection;
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (y < 0 || y > vH || x < selection * tW
					|| x > (selection + 1) * tW) {
				touchIndex = -1;
				selection = -1;
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			touchIndex = -1;
			invalidate();
			if (y < 0 || y > vH || x < 0 || x > vW) {
				break;
			}
			if (selection == ((int) x / (int) tW + (x % tW > 0 ? 1 : 0) - 1)) {
				if (lis != null) {
					lis.OnTabClick(selection);
				}
				setIndex(selection);
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private void reInit() {
		int lineW = ConvertUtil.dip2px(c,1.2f) / 2;
		vpFore = new Paint();
		vpFore.setAntiAlias(true);
		vpFore.setStyle(Style.STROKE);
		vpFore.setStrokeWidth(ConvertUtil.dip2px(c,1.2f));
		vpFore.setColor(foreColor);
		vpBorder = new Paint();
		vpBorder.setAntiAlias(true);
		vpBorder.setStyle(Style.STROKE);
		vpBorder.setStrokeWidth(ConvertUtil.dip2px(c,1.2f));
		vpBorder.setColor(borderColor);
		tp1 = new Paint();
		tp1.setAntiAlias(true);
		tp1.setColor(foreColor);
		vR = new RectF(lineW, lineW, vW - lineW, vH - lineW);
		tW = vW / titles.length;
		trs = new RectF[titles.length];
		lines = new RectF[titles.length - 1];
		for (int i = 0; i < titles.length; i++) {
			trs[i] = new RectF(i * tW, 0, i * tW + tW, vH);
			if (i > 0) {
				lines[i - 1] = new RectF(i * tW - lineW, 0, i * tW + lineW, vH);
			}
		}
	}

	float tW;

	@Override
	protected void onDraw(Canvas canvas) {
		if (vR == null) {
			vH = getHeight();
			vW = getWidth();
			reInit();
		}
		p_txt0.setColor(foreColor);
		float[] rad;
		for (int i = 0; i < trs.length; i++) {
			if (i == 0)
				rad = radiiLeft;
			else if (i == trs.length - 1) {
				rad = radiiRight;
			} else {
				rad = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			}
			float f = getFontHeight(p_txt1);
			if (index == i) {
				path = new Path();
				path.addRoundRect(trs[i], rad, Path.Direction.CW);
				canvas.drawPath(path, tp1);
				canvas.drawText(titles[i],
						i * tW + tW / 2 - p_txt1.measureText(titles[i]) / 2, vH
								/ 2 + f / 3, p_txt1);
			} else {
				if (i == touchIndex) {
					tp0.setColor(pressColor);
				} else {
					tp0.setColor(bgColor);
				}
				path = new Path();
				path.addRoundRect(trs[i], rad, Path.Direction.CW);
				canvas.drawPath(path, tp0);
				canvas.drawText(titles[i],
						i * tW + tW / 2 - p_txt0.measureText(titles[i]) / 2, vH
								/ 2 + f / 3, p_txt0);
			}
			if (i > 0) {
				canvas.drawRect(lines[i - 1], tp1);
			}
		}
		path = new Path();
		path.addRoundRect(vR, radii, Path.Direction.CW);
		canvas.drawPath(path, vpBorder);
	}

	/**
	 * @return 返回指定笔的文字高度
	 */
	private float getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.descent - fm.ascent;
	}

}

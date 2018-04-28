package com.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.support.R;

public class TextRoundImageView extends RoundImageView {

	private String mHeadText = "";
	private int mHeadTextColor = Color.BLUE;
	private int mHeadBgColor = Color.BLUE;
	private int mStrokeColor = Color.BLUE;
	private float mStrokeWidth = 0f;
	private float mTextSize = 16f;
	private boolean mIsStroke = true;

	private int mWidth, mHeight;

	// 标识是否显示
	private boolean mShowTextHead = false;

	public TextRoundImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TextRoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initAttr(context, attrs);
	}

	@Override
	public void setImageBitmap(android.graphics.Bitmap bm) {
		this.mShowTextHead = false;
		super.setImageBitmap(bm);
	}

	@Override
	public void setImageResource(int resId) {
		this.mShowTextHead = false;
		super.setImageResource(resId);
	}

	@Override
	public void setImageDrawable(android.graphics.drawable.Drawable drawable) {
		this.mShowTextHead = false;
		super.setImageDrawable(drawable);
	}

	public void setTextBitmap(String text) {
		this.mHeadText = text;
		this.mShowTextHead = true;
		invalidate();
	}

	private void initAttr(Context context, AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CirclePageIndicator);
		this.mStrokeColor = a.getColor(
				R.styleable.CirclePageIndicator_strokeColor, this.mStrokeColor);
		this.mStrokeWidth = a
				.getDimension(R.styleable.CirclePageIndicator_cStrokeWidth,
						this.mStrokeWidth);
		this.mHeadBgColor = a.getColor(
				R.styleable.CirclePageIndicator_cBackground, this.mHeadBgColor);
		this.mHeadTextColor = a
				.getColor(R.styleable.CirclePageIndicator_cTextColor,
						this.mHeadTextColor);
		this.mIsStroke = a.getBoolean(
				R.styleable.CirclePageIndicator_cIsStroke, this.mIsStroke);
		this.mTextSize = a.getDimension(
				R.styleable.CirclePageIndicator_cTextSize, this.mTextSize);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();

		if (this.mShowTextHead) {
			int coordinateX = this.mWidth >> 1, coordinateY = this.mHeight >> 1;
			int radius = Math.min(coordinateX, coordinateY);

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			// 先画背景
			paint.setColor(this.mHeadBgColor);
			paint.setStyle(this.mIsStroke ? Style.STROKE
					: Style.FILL_AND_STROKE);
			if (this.mStrokeWidth > 0) {
				paint.setStrokeWidth(this.mStrokeWidth);
				radius -= this.mStrokeWidth;
			}
			// 获取圆心
			canvas.drawCircle(coordinateX, coordinateY, radius - 1, paint);
			// 画字
			paint.setColor(this.mHeadTextColor);
			paint.setTextSize(this.mTextSize);
			paint.setStyle(Style.FILL);
			paint.setStrokeWidth(0);
			// paint.setFakeBoldText(true);

//			Rect txtBound = new Rect();
//			paint.getTextBounds(this.mHeadText, 0, this.mHeadText.length(),
//					txtBound);
//
//			int txtX = coordinateX - (txtBound.width() >> 1), txtY = coordinateY
//					+ (txtBound.height() >> 1);
//			canvas.drawText(this.mHeadText, txtX, txtY, paint);

			FontMetricsInt fontMetrics = paint.getFontMetricsInt();
			Rect targetRect = new Rect(0, 0, mWidth, mHeight);
			int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
			// 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(this.mHeadText, targetRect.centerX(), baseline, paint);
			
		} else {
			super.onDraw(canvas);
		}
	}
}

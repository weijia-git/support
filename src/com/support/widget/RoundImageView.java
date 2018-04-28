package com.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.support.R;

/**
 * Բ��ImageView
 * 
 * @author skg
 * 
 */
public class RoundImageView extends ImageView {

	// point if is circle
	private boolean mIsCircle = false;

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CirclePageIndicator);
		rect_adius = a.getDimension(R.styleable.CirclePageIndicator_radius,
				rect_adius);
		a.recycle();

		// pointer if is circle
		mIsCircle = rect_adius == 90;

		init();
	}

	public RoundImageView(Context context) {
		this(context, null);
	}

	private final RectF roundRect = new RectF();
	private float rect_adius = 90;
	private final Paint maskPaint = new Paint();
	private final Paint zonePaint = new Paint();

	private void init() {
		maskPaint.setAntiAlias(true);
		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//
		zonePaint.setAntiAlias(true);
		zonePaint.setColor(Color.WHITE);
		//
		float density = getResources().getDisplayMetrics().density;
		rect_adius = rect_adius * density;
	}

	public void setRectAdius(float adius) {
		rect_adius = adius;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int w = getWidth();
		int h = getHeight();
		roundRect.set(0, 0, w, h);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
		if (this.mIsCircle) {// draw the circle
			int coordinateX = (int) roundRect.width(), coordinateY = (int) roundRect
					.height();

			coordinateX >>= 1;
			coordinateY >>= 1;

			int radius = coordinateX > coordinateY ? coordinateY : coordinateX;
			canvas.drawCircle(coordinateX, coordinateY, radius, zonePaint);
		} else {// draw round rect
			canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
		}
		//
		canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
		super.draw(canvas);
		canvas.restore();
	}
}

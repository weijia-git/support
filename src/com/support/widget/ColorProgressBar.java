package com.support.widget;


import com.support.R;
import com.support.util.ConvertUtil;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 最大进度progress为100， 核心方法是setProgress、setColor、setRound、setText
 * 
 * @author xj
 * 
 */
public class ColorProgressBar extends View {
	private int color = 0xcc4fc3FF;
	private int borderColor = 0xff4fc3FF;
	private RectF border;
	private int height, width, right;
	private int progress = -1;
	public int getProgress() {
		return progress;
	}
	private float round = 10;
	private String text;
	private Context c;
	Paint p, p2, pText,pBg;
	
	public void setProgressShowAnim(int p){
		new ObjectAnimator().ofInt(this, "progress", progress,p).setDuration(300).start();
		progress=p;
	}
	
	public void setRound(float r) {
		round = r;
		invalidate();
	}

	public void setProgress(int p) {
		progress = p;
		if (border == null)
			return;
		right = (width) * progress / 100;
		invalidate();
	}

	public void setText(String str, int color, int size) {
		text=str;
		pText.setColor(color);
		if(size>0)
		pText.setTextSize(size);
		invalidate();
	}
	
	public void setColor(int c,boolean hasBorder) {
		if(hasBorder){
			borderColor = c & 0x00ffffff;
			color = borderColor | 0xff000000;
			borderColor = borderColor | 0xaa000000;
		}else{
			borderColor = c & 0x00ffffff;
			color = borderColor | 0xcc000000;
			borderColor = borderColor | 0xcc000000;
		}
		if (p != null) {
			p.setColor(color);
			p2.setColor(borderColor);
		}
		invalidate();
	}
	public void setColor(int c) {
		borderColor = c & 0x00ffffff;
		color = borderColor | 0xff000000;
		borderColor = borderColor | 0xaa000000;
		if (p != null) {
			p.setColor(color);
			p2.setColor(borderColor);
		}
		invalidate();
	}

	public ColorProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.ColorProgressBar);
		progress=ta.getInt(R.styleable.ColorProgressBar_progress, progress);
		color=ta.getInt(R.styleable.ColorProgressBar_color, color);
		round=ta.getFloat(R.styleable.ColorProgressBar_round, round);
		text=ta.getString(R.styleable.ColorProgressBar_text);
		
		p = new Paint();
		p.setStyle(Style.STROKE);
		p2 = new Paint();
		p.setColor(color);
		p2.setColor(color);
		wide = ConvertUtil.dip2px(context, 0.9f);
		w2 = wide / 2;
		p.setStrokeWidth(wide);
		p.setAntiAlias(true);
		p2.setAntiAlias(true);

		pText = new Paint();
		pText.setTextSize(ConvertUtil.sp2px(context,12));
		pText.setColor(Color.GRAY);
		pText.setAntiAlias(true);
		
		pBg=new Paint();
		pBg.setColor(0x00000000);
		pBg.setAntiAlias(true);
	}
	OnClickListener l;
	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
		this.l=l;
		if(l==null){
			pBg.setColor(0x00000000);
			invalidate();
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(l!=null&&isEnabled()){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				pBg.setColor(0xffdddddd);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				if(event.getX()<0||event.getY()<0||event.getX()>getWidth()||event.getY()>getHeight()){
					pBg.setColor(0x00000000);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				pBg.setColor(0x00000000);
				invalidate();
				break;
			}
		}
		return super.onTouchEvent(event);
	}
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		if (border == null) {
			height = getHeight();
			width = getWidth();
			border = new RectF(w2, w2, width - w2, height - w2);
			if (progress != -1)
				right = (width) * progress / 100;
		}
		canvas.drawRoundRect(border, round, round, pBg);
		canvas.save();
		canvas.clipRect(0, 0, right, height - w2);
		canvas.drawRoundRect(border, round, round, p2);
		canvas.restore();

		canvas.drawRoundRect(border, round, round, p);
		if (text != null && text.trim().length() != 0)
			canvas.drawText(text, width / 2 - pText.measureText(text) / 2,
					height / 2 + getFontHeight(pText)/3, pText);
	}

	int wide, w2;

	/**
	 * @return 返回指定笔的文字高度
	 */
	private float getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.descent - fm.ascent;
	}

}

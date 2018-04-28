package com.support.widget;

import com.support.util.ConvertUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
/**
 * 核心方法是
 * setDotCount、setSelection、moveOval
 * @author xj
 *
 */
public class DotView extends View {

	private int mDotH;
	private int mDotW;
	private int mCount = 1;
	private int mIndex = 0;
	private Context c;
	private Paint mp0, mp1,mp2,mp3,mp4;
	private RectF oval;
	public DotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		mDotH = ConvertUtil.dip2px(c, 10)/2*2;
		mDotW = mDotH;
		mp0 = new Paint();
		mp0.setAntiAlias(true);
		mp0.setColor(Color.WHITE);
		mp1 = new Paint();
		mp1.setAntiAlias(true);
		mp1.setColor(Color.GRAY);
		
		mp2=new Paint();
		mp2.setStyle(Paint.Style.STROKE);
		mp2.setColor(Color.YELLOW);
		mp2.setStrokeWidth(2);
		mp2.setAntiAlias(true);	
	}
	int height,width,start,fw;
	float x,y;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		height = getHeight();
		width = getWidth();
		x = width/mCount/2;
		y = height / 2;
		if(oval==null){
			fw=mDotW+4;
			int fleft=width/mCount*mIndex+(width/mCount-fw)/2;
			int ftop=(height-fw)/2;
			oval=new RectF(
					fleft, 
					ftop, 
					fleft+fw, 
					ftop+fw);
		}
		for (int i = 0; i < mCount; i++) {
			if (mIndex == i){
				canvas.drawCircle(x, y, mDotH / 2, mp0);
				if(showOval)
				canvas.drawArc(oval, start=start>360?0:start+3, 300, false, mp2);
			}
			else
				canvas.drawCircle(x, y, mDotH / 2, mp1);
			x+=width/mCount;
		}
		if(showOval)
		invalidate();
	}

	public void setDotCount(int count) {
		mCount = count<1?1:count;
		LayoutParams params = getLayoutParams();
		params.width=mDotW*mCount+(mCount-1)*mDotW*2+mDotW*2;
		setLayoutParams(params);
		oval=null;
	}

	public void setSelection(int index) {
		mIndex = index;
		if(!showOval){
			invalidate();
		}
	}
	int oleft;
	public void moveOval(int arg0,float arg1){
		if(oval==null)
			return;
		oleft=arg0*width/mCount+(int)(width/mCount*arg1)+(width/mCount-fw)/2;
		oval.left=oleft;
		oval.right=oleft+fw;
		if(!showOval){
			invalidate();
		}
	}
	boolean showOval=true;
	public void setShowOval(boolean b){
		showOval=b;
		invalidate();
	}

}

package com.support.widget;


import com.support.util.ConvertUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * ��ĸ����������Ҫ������ϵ���б�ҳ��������ĸ������б�ҳ
 * ���ķ�����setOnIndexSelectedListener
 * @author xj
 *
 */
public class IndexBar extends View {
	Context context;
	public interface OnIndexSelectedListener{
		void indexChange(char c);
		void indexSelected(char c);
	}
	public void setOnIndexSelectedListener(OnIndexSelectedListener l){
		osi=l;
	}
	private OnIndexSelectedListener osi;
	private char[] indexs = new char[] { '#', 'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	Paint paint=new Paint();
	public IndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		setClickable(true);
	}
	int selected=-1;
	int sigleHeight;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight()-ConvertUtil.dip2px(context,5);
		int width = getWidth();
		sigleHeight = height / indexs.length; // ������ĸռ�ĸ߶�
		for (int i = 0; i < indexs.length; i++) {
			paint.setColor(Color.GRAY);
			paint.setTextSize(ConvertUtil.sp2px(context,14));
			paint.setAntiAlias(true);
			// �滭��λ��
			float xPos = width / 2 - paint.measureText(indexs[i]+"") / 2;
			float yPos = sigleHeight * i + sigleHeight;
			canvas.drawText(indexs[i]+"", xPos, yPos, paint);
			paint.reset();
		}
	}
	char currentIndex=' ';
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y=event.getY();
		int i=(int) Math.ceil(y/sigleHeight);
		if(i<1){
			i=1;
		}else if(i>indexs.length){
			i=indexs.length;
		}
		char index=indexs[i-1];
		if(currentIndex!=index){
			currentIndex=index;
			if(osi!=null){
				osi.indexChange(currentIndex);
			}
		}
		if(event.getAction()==MotionEvent.ACTION_UP){
			if(osi!=null){
				osi.indexSelected(index);
			}
			currentIndex=' ';
		}
		return super.onTouchEvent(event);
	}
}

package com.support.widget;

import com.support.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * ���ķ����ǣ�SetChecked(),setOnChangeListener()
 * @author copy��
 */
public class SwitchButton extends View implements
		android.view.View.OnClickListener {
	private Bitmap mSwitchBottom, mSwitchThumb, mSwitchPressed, mSwitchNormal,
			mSwitchFrame, mSwitchMask;
	private float mCurrentX = 0;
	private boolean mSwitchOn = true;// ����Ĭ���ǿ��ŵ�

	public void setChecked(boolean b) {
		mSwitchOn = b;
		invalidate();
	}

	private int mMoveLength;// ����ƶ�����
	private float mLastX = 0;// ��һ�ΰ��µ���Ч����

	private Rect mDest = null;// ���Ƶ�Ŀ�������С
	private Rect mSrc = null;// ��ȡԴͼƬ�Ĵ�С
	private int mDeltX = 0;// �ƶ���ƫ����
	private Paint mPaint = null;
	private OnChangeListener mListener = null;
	private boolean mFlag = false;

	public SwitchButton(Context context) {
		this(context, null);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * ��ʼ�������Դ
	 */
	public void init() {
		mSwitchBottom = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_bottom);
		mSwitchPressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_btn_pressed);
		mSwitchNormal = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_btn_normal);
		mSwitchFrame = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_frame);
		mSwitchMask = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_mask);
		mSwitchThumb = mSwitchNormal;
		setOnClickListener(this);
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		mMoveLength = mSwitchBottom.getWidth() - mSwitchFrame.getWidth();
		mDest = new Rect(0, 0, mSwitchFrame.getWidth(),
				mSwitchFrame.getHeight());
		mSrc = new Rect();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setAlpha(255);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(mSwitchFrame.getWidth(), mSwitchFrame.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (mDeltX > 0 || mDeltX == 0 && mSwitchOn) {
			if (mSrc != null) {
				mSrc.set(mMoveLength - mDeltX, 0, mSwitchBottom.getWidth()
						- mDeltX, mSwitchFrame.getHeight());
			}
		} else if (mDeltX < 0 || mDeltX == 0 && !mSwitchOn) {
			if (mSrc != null) {
				mSrc.set(-mDeltX, 0, mSwitchFrame.getWidth() - mDeltX,
						mSwitchFrame.getHeight());
			}
		}
		int count = canvas.saveLayer(new RectF(mDest), null,
				Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
						| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
						| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
						| Canvas.CLIP_TO_LAYER_SAVE_FLAG);

		canvas.drawBitmap(mSwitchBottom, mSrc, mDest, null);
		canvas.drawBitmap(mSwitchThumb, mSrc, mDest, null);
		canvas.drawBitmap(mSwitchFrame, 0, 0, null);
		canvas.drawBitmap(mSwitchMask, 0, 0, mPaint);
		canvas.restoreToCount(count);
	}

	private long time;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = event.getX();
			mSwitchThumb = mSwitchPressed;
			break;
		case MotionEvent.ACTION_MOVE:
			if (System.currentTimeMillis() - time < 15)
				return false;
			mCurrentX = event.getX();
			mDeltX = (int) (mCurrentX - mLastX);
			// ���ؿ������󻬶������߿��ع������һ�������ʱ���ǲ���Ҫ����ģ�
			if ((mSwitchOn && mDeltX < 0) || (!mSwitchOn && mDeltX > 0)) {
				mFlag = true;
				mDeltX = 0;
			}

			if (Math.abs(mDeltX) > mMoveLength) {
				mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
			}
			time = System.currentTimeMillis();
			invalidate();
			return true;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mSwitchThumb = mSwitchNormal;
			invalidate();
			if (Math.abs(mDeltX) > 0 && Math.abs(mDeltX) < mMoveLength / 2) {
				mDeltX = 0;
				invalidate();
				return true;
			} else if (Math.abs(mDeltX) > mMoveLength / 2
					&& Math.abs(mDeltX) <= mMoveLength) {
				mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
				mSwitchOn = !mSwitchOn;
				if (mListener != null) {
					mListener.onChange(this, mSwitchOn);
				}
				invalidate();
				mDeltX = 0;
				return true;
			} else if (mDeltX == 0 && mFlag) {
				// ��ʱ��õ����ǲ���Ҫ���д���ģ���Ϊ�Ѿ�move����
				mDeltX = 0;
				mFlag = false;
				return true;
			}
			return super.onTouchEvent(event);
		default:
			break;
		}
		invalidate();
		return super.onTouchEvent(event);
	}

	public void setOnChangeListener(OnChangeListener listener) {
		mListener = listener;
	}

	public interface OnChangeListener {
		void onChange(SwitchButton sb, boolean state);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mDeltX = mSwitchOn ? mMoveLength : -mMoveLength;
		mSwitchOn = !mSwitchOn;
		if (mListener != null) {
			mListener.onChange(this, mSwitchOn);
		}
		invalidate();
		mDeltX = 0;
	}
}

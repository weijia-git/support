package com.support.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.support.R;
import com.support.util.NetUtils;

/**
 * 自定义异常view
 */
public class ExceptionView extends RelativeLayout {

	private Context context;
	private LinearLayout llException;// 异常页面
	private ImageView ivErrorImage; // 异常图片
	private TextView tvHint; // 异常文案提示
	private ProgressBar pb;
	private OnClickReloadListener reloadListener; // 点击异常页面监听器

	public ExceptionView(Context context) {
		super(context);

		this.context = context;
		inflateResource(context);
		initView();
	}

	public ExceptionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		inflateResource(context);
		initView();
	}

	public ExceptionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		inflateResource(context);
		initView();
	}

	private void inflateResource(Context context) {
		LayoutInflater.from(context).inflate(R.layout.ev_exception_view, this);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		pb = (ProgressBar) this.findViewById(R.id.ev_progressbar);
		llException = (LinearLayout) this.findViewById(R.id.exceptionLayout);
		ivErrorImage = (ImageView) this.findViewById(R.id.exception_img);
		tvHint = (TextView) this.findViewById(R.id.excetion_hint);
		llException.setOnClickListener(clickListener);
		setWhiteMode();
	}

	/**
	 * 加载数据
	 * 
	 * @param hideback
	 *            是否显示菊花
	 */
	public void loading() {
		this.setVisibility(View.VISIBLE);
		llException.setVisibility(View.GONE);
		pb.setVisibility(View.VISIBLE);
	}

	/**
	 * 数据加载完成，一般在onSuccess和onFailure调用
	 */
	public void loaded() {
		this.setVisibility(View.GONE);
	}

	/**
	 * 异常时点击重新做加载�?要用到的监听器，必须手动set进来
	 * 
	 * @param reLoadListener
	 */
	public void setClickReLoadListener(OnClickReloadListener reLoadListener) {
		this.reloadListener = reLoadListener;
	}

	/**
	 * 取消异常页面点击监听，因为有成功正确加载数据但无数据特殊异常页面�?
	 */
	public void offClickReLoadListener() {
		llException.setEnabled(false);
	}

	/**
	 * 显示没有访问到相关数据的异常�?
	 */
	public void showNoDataExceptionView() {
		setNoDataDefaultHit();
		offClickReLoadListener();
		llException.setVisibility(View.VISIBLE);
	}

	/**
	 * 自定义异常提�?
	 * 
	 * @param content
	 */
	public void setCustomHit(String content) {
		if (tvHint != null) {
			tvHint.setText(content);
			tvHint.setVisibility(View.VISIBLE);
			if (ivErrorImage != null)
				ivErrorImage.setVisibility(View.VISIBLE);
			this.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 正常加载，下载不到数据时调用此方�?
	 */
	public void setNoDataDefaultHit() {
		
		// 不显示加载进度
		pb.setVisibility(View.GONE);
		
		if (tvHint != null) {
			tvHint.setText(context.getString(R.string.hint_no_data));
			tvHint.setVisibility(View.VISIBLE);
			if (ivErrorImage != null)
				ivErrorImage.setVisibility(View.VISIBLE);
			llException.setVisibility(View.VISIBLE);
			this.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 加载失败
	 */
	public void loadFaile() {
		try {
			if (!NetUtils.isConnected(context)) {
				setNetworkException();
			} else {
				setLoadFaileException();
			}
		} catch (Exception e) {
			setNetworkException();
			e.printStackTrace();
		}
		setVisible(false, true);
	}

	/**
	 * 设置加载失败�? 如：json解析失败
	 */
	public void setLoadFaileException() {
		if (tvHint != null) {
			tvHint.setText(context.getString(R.string.hint_load_data_failure));
			tvHint.setVisibility(View.VISIBLE);
			if (ivErrorImage != null)
				ivErrorImage.setVisibility(View.VISIBLE);
			this.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置网络异常�? 如网络未连接
	 */
	public void setNetworkException() {
		llException.setEnabled(true);
		if (tvHint != null) {
			tvHint.setText(context.getString(R.string.hint_network_failure));
			tvHint.setVisibility(View.VISIBLE);
			if (ivErrorImage != null)
				ivErrorImage.setVisibility(View.VISIBLE);
			this.setVisibility(View.VISIBLE);
		}
	}

	private void setViewVisible(final View v, final boolean visible) {
		if (null != v) {
			if (visible) {
				v.setVisibility(View.VISIBLE);
			} else {
				v.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * �?要显示组件与�?
	 * 
	 * @param progressVisible
	 * @param exceptionVisible
	 */
	public void setVisible(final boolean progressVisible,
			final boolean exceptionVisible) {
		setViewVisible(pb, progressVisible);
		setViewVisible(llException, exceptionVisible);
		if (null != context) {
			if (!progressVisible && !exceptionVisible) {
				this.setVisibility(View.GONE);
			} else {
				this.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 设置异常页面背景,文字,图片
	 */
	public void setWhiteMode() {
		tvHint.setTextColor(getResources().getColor(R.color.cus_text));
	}

	/**
	 * 重新加载监听�?
	 */
	public interface OnClickReloadListener {
		void reLoad();
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.exceptionLayout) {
				if (reloadListener != null) {
					loading();
					reloadListener.reLoad();
				}
			}
		}
	};
}

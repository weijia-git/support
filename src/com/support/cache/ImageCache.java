package com.support.cache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import com.support.cache.DiskLruCache.Editor;
import com.support.cache.DiskLruCache.Snapshot;
import com.support.util.ConvertUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


/**
 * ��ʼ����</br>��onCreate�е���getInstance��������onPause�е���fluchCache��������onDestroy�е���cancelAllTasks����������˳��������Ҫ�������أ�
 * </br></br>
 * 
 * �µĺ��ķ�����</br>
 * downBitmapToDiskCache�����ط�����getBitmapFrom2Cache����ȡ������
 * ��������getBitmapFrom2Cache�����Ϊnull�ٵ���downBitmapToDiskCache��
 * </br></br>
 * �ɵĺ��ķ���:</br>
 * loadBitmaps�������ڴ桢Ӳ�̡����磨���ȼ����Ⱥ������������Ϊ ʳ�������
 * ���룬��˳����л�ȡ��Դ��������䵽ָ����ImageView�ϡ�
 * ����setOnLoadBitmapListener����ÿ�������ɺ�����OnLoadBitmap������
 * </br></br>
 * ˵���������õ���2���ڴ���?���࣬LruCache���ڴ滺�湤���࣬DiskLruCache��Ӳ�̻��湤���࣬
 * ����LruCache��󻺴��ڴ�ռ��Ϊ��������ڴ��8��֮1��
 * ��DiskLruCache�����洢�ռ�Ϊ10MB���������޸ĵ�76�У��������ǿռ䲻��ʱ�������ͷŵ�����ʹ�û��������ʹ�õ���Դ��
 * 
 * @author xj
 */
public class ImageCache {
	/**
	 * �ļ��е�����
	 */
	private final String mFolderName="ImageCache";
	DiskLruCache mRomCache, mRomPreview;
	LruCache<String, Bitmap> mRamCache;
	Context mContext;
	ImageView mIv;
	OnLoadBitmapListener mLis;
	private final String TAG_PREVIEW="_preview";
	/**
	 * ��¼�����������ػ�ȴ����ص����񡣵��˳�ʱֹͣ�������������
	 */
	private Set<BitmapWorkerTask> mTaskCollection;

	public interface OnLoadBitmapListener {
		void onLoadBitmapFinish();
	}

	public void setOnLoadBitmapListener(OnLoadBitmapListener lis) {
		this.mLis = lis;
	}

	private static ImageCache instance;

	public static ImageCache getInstance(Context c) {
		if (instance == null) {
			instance = new ImageCache(c);
		}
		return instance;
	}

	private ImageCache(Context c) {
		mContext = c;
		mTaskCollection = new HashSet<BitmapWorkerTask>();
		// ��ȡӦ�ó����������ڴ�
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// ����ͼƬ�����СΪ�����������ڴ��1/8
		mRamCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		try {
			// ��ȡͼƬ����·��
			File cacheDir = getDiskCacheDir(mContext, mFolderName);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// ����DiskLruCacheʵ���ʼ���������
			mRomCache = DiskLruCache.open(cacheDir, getAppVersion(mContext), 1,
					30 * 1024 * 1024);// ���������õ�10MB���洢�ռ�

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��һ��ͼƬ�洢��LruCache�С�
	 * 
	 * @param key
	 *            LruCache�ļ����ﴫ��ͼƬ��URL��ַ��
	 * @param bitmap
	 *            LruCache�ļ����ﴫ������������ص�Bitmap����
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mRamCache.put(key, bitmap);
		}
	}

	/**
	 * ��LruCache�л�ȡһ��ͼƬ�������ھͷ���null��
	 * 
	 * @param key
	 *            LruCache�ļ����ﴫ��ͼƬ��URL��ַ��
	 * @return ��Ӧ������Bitmap���󣬻���null��
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mRamCache.get(key);
	}

	/**
	 * ����Bitmap���󡣴˷�������LruCache�м��������Ļ�пɼ��ImageView��Bitmap����
	 * ������κ�һ��ImageView��Bitmap�����ڻ����У��ͻῪ���첽�߳�ȥ����ͼƬ��
	 */
	public void loadBitmaps(ImageView imageView, String imageUrl) {
		// mIv = imageView;
		try {
			Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask();
				mTaskCollection.add(task);
				task.execute(imageUrl);
			} else {
				if (imageView != null && bitmap != null) {
					if (imageView != null)
						imageView.setImageBitmap(bitmap);
					mLis.onLoadBitmapFinish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * ȡ�������������ػ�ȴ����ص�����
	 */
	public void cancelAllTasks() {
		if (mTaskCollection != null) {
			for (BitmapWorkerTask task : mTaskCollection) {
				task.cancel(false);
			}
		}
	}

	/**
	 * ��ݴ����uniqueName��ȡӲ�̻����·����ַ��
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * ��ȡ��ǰӦ�ó���İ汾�š�
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * ʹ��MD5�㷨�Դ����key���м��ܲ����ء�
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * �������¼ͬ����journal�ļ��С�
	 */
	public void fluchCache() {
		if (mRomCache != null) {
			try {
				mRomCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public interface OnDownBitmapListener {
		void onDownFinish(Bitmap bmp);
	}
	public void downBitmapToDiskCache(String imgUrl,
			final OnDownBitmapListener lis) {
		new AsyncTask<String, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(String... params) {
				String imageUrl = params[0];
				if (imageUrl == null || imageUrl.length() == 0) {
					return null;
				}
				FileDescriptor fileDescriptor = null;
				FileInputStream fileInputStream = null;
				Snapshot snapShot = null;
				try {
					final String key = hashKeyForDisk(imageUrl);
					snapShot = mRomCache.get(key);
					if (snapShot == null) {
						DiskLruCache.Editor editor = mRomCache.edit(key);
						if (editor != null) {
							OutputStream outputStream = editor
									.newOutputStream(0);
							if (downloadUrlToStream(imageUrl, outputStream)) {
								editor.commit();
							} else {
								editor.abort();
							}
						}
						// ���汻д����ٴβ���key��Ӧ�Ļ���
						snapShot = mRomCache.get(key);
					}
					if (snapShot != null) {
						fileInputStream = (FileInputStream) snapShot
								.getInputStream(0);
						fileDescriptor = fileInputStream.getFD();
					}
					// ��������ݽ�����Bitmap����
					Bitmap bitmap = null;
					if (fileDescriptor != null) {
						bitmap = BitmapFactory
								.decodeFileDescriptor(fileDescriptor);
					}
					if (bitmap != null) {
						int px= ConvertUtil.dip2px(mContext, 60);
						Bitmap temp=ThumbnailUtils.extractThumbnail(bitmap,px, px); 
						addBitmapToMemoryCache(params[0]+TAG_PREVIEW, temp);
						DiskLruCache.Editor editor = mRomCache.edit(key+TAG_PREVIEW);
						if (editor != null) {
							OutputStream outputStream = editor.newOutputStream(0);
							InputStream is=ConvertUtil.Bitmap2InputStream(temp, 100);
							if (isToOs(is, outputStream)) {
								editor.commit();
							} else {
								editor.abort();
							}
						}
						// ��Bitmap������ӵ��ڴ滺�浱��
						addBitmapToMemoryCache(params[0], bitmap);
					}
					return bitmap;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fileDescriptor == null && fileInputStream != null) {
						try {
							fileInputStream.close();
						} catch (IOException e) {
						}
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if (lis != null)
					lis.onDownFinish(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgUrl);
	}
	
	public boolean isToOs(InputStream is,OutputStream os){
		int len=0;
		byte[] buf=new byte[1024];
		try {
			while((len=is.read(buf))>-1){
				os.write(buf);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (Exception e) {
			}
			try {
			os.close();
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	public Bitmap getBitmapFrom2Cache(String imgUrl){
		return getBitmapFrom2Cache(imgUrl, false);
	}
	public Bitmap getBitmapFrom2Cache(String imgUrl,boolean isPreview) {
		Bitmap bmp = null;
		if (imgUrl == null || imgUrl.length() == 0)
			return null;
		bmp = getBitmapFromMemoryCache(isPreview?imgUrl+TAG_PREVIEW:imgUrl);
		if (bmp == null) {
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			try {
				String key = hashKeyForDisk(imgUrl);
				if(isPreview){
					key+="preview";
				}
				Snapshot snapShot = mRomCache.get(key);
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				if (fileDescriptor != null) {
					bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bmp != null) {
					// ��Bitmap������ӵ��ڴ滺�浱��
					addBitmapToMemoryCache(imgUrl+TAG_PREVIEW, bmp);
				}
			} catch (Exception e) {
			}
		}
		return bmp;
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		public BitmapWorkerTask() {
		}

		/**
		 * ͼƬ��URL��ַ
		 */
		private String imageUrl;

		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapShot = null;
			try {
				// ���ͼƬURL��Ӧ��key
				final String key = hashKeyForDisk(imageUrl);
				// ����key��Ӧ�Ļ���
				snapShot = mRomCache.get(key);
				if (snapShot == null) {
					// ���û���ҵ���Ӧ�Ļ��棬��׼����������������ݣ���д�뻺��
					DiskLruCache.Editor editor = mRomCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// ���汻д����ٴβ���key��Ӧ�Ļ���
					snapShot = mRomCache.get(key);
				}
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// ��������ݽ�����Bitmap����
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
					// ��Bitmap������ӵ��ڴ滺�浱��
					addBitmapToMemoryCache(params[0], bitmap);
				}
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// ���Tag�ҵ���Ӧ��ImageView�ؼ��������غõ�ͼƬ��ʾ������
			if (mIv != null && bitmap != null) {
				mIv.setImageBitmap(bitmap);
				if (mLis != null)
					mLis.onLoadBitmapFinish();
			}
			mTaskCollection.remove(this);
		}

	}

	/**
	 * ����HTTP���󣬲���ȡBitmap����
	 * 
	 * @param imageUrl
	 *            ͼƬ��URL��ַ
	 * @return �������Bitmap����
	 */
	private boolean downloadUrlToStream(String urlString,
			OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setReadTimeout(10000);
			urlConnection.setConnectTimeout(30000);
			in = urlConnection.getInputStream();
			int len;
			byte[] buffer = new byte[1024 * 2];

			while ((len = in.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
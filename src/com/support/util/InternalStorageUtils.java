package com.support.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.support.httputil.okhttp.HttpUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * 内部存储文件读写 数据预载
 *
 */
public class InternalStorageUtils {
	private static final String TAG = "InternalStorageUtils";
	private static ThreadPoolExecutor threadPool;
	private static int BUFFER_SIZE = 4096;
	
	private static class SingleHolder{
        private static final ThreadPoolExecutor fixedThreadPool=(ThreadPoolExecutor)Executors.newFixedThreadPool(1);
    }
	
	/**
	 * 下载并缓存
	 * @param context
	 * @param url
	 * @param fileName
	 * @param isRefresh
	 * @param downloadListener
	 */
	public static void downloadWithCache(final Context context,final String url,final String fileName,
			final boolean isRefresh,final DownloadListener downloadListener){
		threadPool = SingleHolder.fixedThreadPool;
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				InputStream inputStream = null;
				ByteArrayOutputStream baos = null;
				byte[] buffer = new byte[BUFFER_SIZE];
		        byte[] outbuffer = null;
		        int len = 0;
				try {
					String content = null;
					//读取本地数据
					if(!isRefresh){
						if(fileName!=null && !"".equals(fileName)){
							content = getContent(context, fileName);
						}
					}
					//下载最新数据并做本地文件存储
					if(content==null || "".equals(content) || isRefresh){
						content  = HttpUtils.getInstance().get(url, null, null, null).body().string();
					    inputStream = HttpUtils.getInstance().get(url, null, null, null).body().byteStream();
						if(null!=inputStream && null!=buffer){
							baos = new ByteArrayOutputStream();
							while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {                     
								baos.write(buffer, 0, len);
								baos.flush();
							}
							outbuffer =  baos.toByteArray();
							if(fileName!=null && !"".equals(fileName)){
								saveFile(context,FileUtil.byteToInputSteram(outbuffer),fileName);
							}
						}
					}
					
					if(downloadListener!=null){
						downloadListener.onSuccess(content);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(inputStream!=null){
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(baos!=null){
						try {
							baos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	/**
	 * 读取内部存储数据  优先读取内部存储读不到再从Assets下的读数据
	 * @param context
	 * @param fileName
	 * @param asyncResonseHandler
	 */
	public static void asynReadInternalFile(final Context context,final String fileName,final AsyncResonseHandler asyncResonseHandler){
		threadPool = SingleHolder.fixedThreadPool;
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				String content = getContent(context, fileName);
				asyncResonseHandler.sendResponseMessage(content);
			}
		});
	}
	
	/**
	 * 数据预载 存储到内部存储
	 * @param context
	 * @param url
	 * @param fileName
	 */
	public static void preload(Context context,String url,String fileName){
		downloadWithCache(context,url,fileName,true,null);
	}
	
	
	/**
	 * 存储到手机内部存储
	 * @param context
	 * @param is
	 * @param fileName
	 */
	private static void saveFile(Context context,InputStream is,String fileName){
		if (context != null) {
			try {
	            File file = new File(context.getFilesDir().getPath().toString() + File.separator + fileName);
	            if (!file.exists()) {
					file.createNewFile();
	            }
	            if (is != null) {
	            	FileUtil.writeFile(file, is);
	            }
            } catch (IOException e) {
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}finally{
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
        }
	}
	
	/**
	 * 获取内部存储数据
	 * @param context
	 * @param fileName
	 * @return
	 */
	private static String getContent(Context context,String fileName){
		String content = null;
		try {
			if(context!=null){
				File file = new File(context.getFilesDir().getPath().toString() + File.separator + fileName);
				if (file != null && file.length() > 0) {
					content = FileUtil.readTextFromFile(file);
					LogUtil.v(TAG, "read internal storage data success!");
				}
				if(content==null || "".equals(content)){									
					content = FileUtil.readTextInputStream(context.getResources().getAssets().open(fileName));
					LogUtil.v(TAG, "read assets data success!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}
	
	public interface DownloadListener {
		void onSuccess(String content);
	}
	
	public class AsyncResonseHandler {
		protected static final int SUCCESS_MESSAGE = 0;
		private Handler handler;
		
		public AsyncResonseHandler(){
			if(Looper.myLooper() != null) {
	            handler = new Handler(){
	                public void handleMessage(Message msg){
	                	AsyncResonseHandler.this.handleMessage(msg);
	                }
	            };
	        }
		}
		
		protected Message obtainMessage(int responseMessage, Object response) {
	        Message msg = null;
	        if(handler != null){
	            msg = this.handler.obtainMessage(responseMessage, response);
	        }else{
	            msg = new Message();
	            msg.what = responseMessage;
	            msg.obj = response;
	        }
	        return msg;
		}
		
		
		 protected void sendMessage(Message msg) {
	        if(handler != null){
	            handler.sendMessage(msg);
	        } else {
	            handleMessage(msg);
	        }
		 }
		
		protected void handleMessage(Message msg){
			onSuccess((String) msg.obj);
	    }
		
		void sendResponseMessage(String content){
			sendMessage(obtainMessage(SUCCESS_MESSAGE, content));
		}
		
		protected void onSuccess(String content){
			
		}
		
	}
}
	

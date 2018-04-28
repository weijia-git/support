package com.support.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;

/**
 * 
 * @author itboy
 *
 */
public class FileUtil {

	private final static String TAG = "FileUtil";
	private final static int BUFFER = 8192;
	private final static long MILLIS_ONE_DAY = 24 * 60 * 60 * 1000;// 一天毫秒数
	public final static String PIC_FILE_PATH = "bitmaps";

	/**
	 * 获取可用的存储空间
	 * 
	 * @param dir
	 * @return
	 */
	public static long getAvailableStorageSize(File dir) {
		long size = -1;
		if (dir != null && dir.exists() && dir.isDirectory()) {
			try {
				StatFs stat = new StatFs(dir.getPath());
				size = (long) stat.getBlockSize() * stat.getAvailableBlocks();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return size;
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		long size = 0;
		if (null != dir && dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (null != files && files.length > 0) {
				for (File file : files) {
					if (file.isFile()) {
						size += file.length();
					} else {
						size += getDirSize(file);
					}
				}
			}
		}
		return size;
	}

	/**
	 * 复制文件和目录
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copy(File sourceFile, File targetFile) throws IOException {
		if (null != sourceFile && !sourceFile.exists()) {
			LogUtil.i(TAG, "the source file is not exists: " + sourceFile.getAbsolutePath());
		} else {
			if (sourceFile.isFile()) {
				copyFile(sourceFile, targetFile);
			} else {
				copyDirectory(sourceFile, targetFile);
			}
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		if (null != sourceFile && null != targetFile) {
			try {
				inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
				outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
				byte[] buffer = new byte[BUFFER];
				int length;
				while ((length = inBuff.read(buffer)) != -1) {
					outBuff.write(buffer, 0, length);
				}
				outBuff.flush();
			} finally {
				if (inBuff != null) {
					inBuff.close();
				}
				if (outBuff != null) {
					outBuff.close();
				}
			}
		}
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
		// 新建目标目录
		targetDir.mkdirs();
		if (null != sourceDir) {
			// 遍历源目录下所有文件或目录
			File[] file = sourceDir.listFiles();
			for (int i = 0; i < file.length; i++) {
				if (file[i].isFile()) {
					File sourceFile = file[i];
					File targetFile = new File(targetDir.getAbsolutePath() + File.separator + file[i].getName());
					copyFile(sourceFile, targetFile);
				} else if (file[i].isDirectory()) {
					File dir1 = new File(sourceDir, file[i].getName());
					File dir2 = new File(targetDir, file[i].getName());
					copyDirectory(dir1, dir2);
				}
			}
		}
	}

	/**
	 * 删除文件或目录
	 * 
	 * @param file
	 * @return
	 */
	public static boolean delete(File file) {
		if (file.isDirectory()) {
			// 删除目录
			boolean success = true;
			File[] subFiles = file.listFiles();
			for (int i = 0; i < subFiles.length; i++) {
				if (!delete(subFiles[i])) {
					success = false;
				}
			}
			if (!file.delete()) {
				success = false;
			}
			return success;
		} else {
			// 删除文件
			return file.delete();
		}
	}

	public static void move(File src, File dest) throws IOException {
		copy(src, dest);
		delete(src);
	}

	/**
	 * 从输入流读取文件内容
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readTextInputStream(InputStream is) throws IOException {
		if (null == is)
			return null;
		StringBuffer strbuffer = new StringBuffer();
		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is));
			while ((line = reader.readLine()) != null) {
				strbuffer.append(line).append("\r\n");
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return strbuffer.toString();
	}

	/**
	 * 从文件读取文本内容
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readTextFromFile(File file) throws IOException {
		String text = null;
		InputStream is = null;
		if (null != file) {
			try {
				is = new FileInputStream(file);
				text = readTextInputStream(is);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		return text;
	}

	/**
	 * 将文本内容写入文件
	 * 
	 * @param file
	 * @param str
	 * @throws IOException
	 */
	public static void writeToFile(File file, String str) throws IOException {
		writeToFile(file, new String[] { str }, false);
	}

	/**
	 * 将文本内容追加到文件末尾
	 * 
	 * @param file
	 * @param strs
	 * @throws IOException
	 */
	public static void appendToFile(File file, String str) throws IOException {
		writeToFile(file, new String[] { str }, true);
	}

	/**
	 * 将一系列字符串写入文件
	 * 
	 * @param file
	 * @param strs
	 * @throws IOException
	 */
	public static void writeToFile(File file, String[] strs) throws IOException {
		writeToFile(file, strs, false);
	}

	/**
	 * 将一系列字符串追加到文件末尾
	 * 
	 * @param file
	 * @param strs
	 * @throws IOException
	 */
	public static void appendToFile(File file, String[] strs) throws IOException {
		writeToFile(file, strs, true);
	}

	// 合并多个文本文件的内容到一个文件
	public static void combineTextFile(File[] sFiles, File dFile) throws IOException {
		BufferedReader in = null;
		BufferedWriter out = null;
		if (null != dFile && null != sFiles) {
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dFile)));

				for (int i = 0; i < sFiles.length; i++) {
					in = new BufferedReader(new InputStreamReader(new FileInputStream(sFiles[i])));
					String oldLine = in.readLine();
					String newLine = null;
					while ((newLine = in.readLine()) != null) {
						out.write(oldLine);
						out.newLine();
						oldLine = newLine;
					}
					out.write(oldLine);

					if (i != sFiles.length - 1)
						out.newLine();

					out.flush();
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
		}
	}

	/**
	 * SD卡是否存在
	 *
	 * @return
	 */
	public static boolean isSDCardAvaliable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取外置SD卡图片路径
	 *
	 * @param context
	 * @param paramPath
	 * @return
	 */
	public static String getBitmapCachePath(Context context, String paramPath) {
		String filepath = "";
		filepath = context.getCacheDir().getPath() + paramPath;
		if (isSDCardAvaliable()) {
			if (android.os.Build.VERSION.SDK_INT < 8) {
				filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + paramPath;
			} else {
				if (context != null && context.getExternalCacheDir() != null) {
					filepath = context.getExternalCacheDir().getAbsolutePath() + File.separatorChar + paramPath;
				}
			}
		}
		return filepath;
	}

	// 写入数据到文件
	public static void writeFile(File file, byte[] data) throws Exception {
		DataOutputStream out = null;
		if (null != file && null != data) {
			try {
				out = new DataOutputStream(new FileOutputStream(file));
				out.write(data);
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}

	// 将输入流中的数据写入文件
	public static int writeFile(File file, InputStream inStream) throws IOException {
		long dataSize = 0;
		DataInputStream in = null;
		DataOutputStream out = null;
		if (null != inStream && null != file) {
			try {
				byte buffer[] = new byte[BUFFER];
				out = new DataOutputStream(new FileOutputStream(file));
				in = new DataInputStream(inStream);

				int nbyteread;
				while ((nbyteread = in.read(buffer)) != -1) {
					out.write(buffer, 0, nbyteread);
					dataSize += nbyteread;
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
		}

		return (int) (dataSize / 1024);
	}

	/***
	 * 读取文件到byte数组
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileToByte(File file) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				bos.close();
			}
		}
	}

	/**
	 * 获取内部存储路径
	 *
	 * @param context
	 * @param paramPath
	 * @return
	 */
	public static String getFileCachePath(Context context, String fileName) {
		return context.getCacheDir().getPath() + "/" + fileName;
	}

	public static String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}

	/**
	 * 字节数组转成流
	 * 
	 * @param data
	 * @return
	 */
	public static InputStream byteToInputSteram(byte[] data) {
		InputStream is = null;
		if (null != data && data.length > 0) {
			is = new ByteArrayInputStream(data);
		}
		return is;
	}

	/**
	 * ���ݴ����uniqueName��ȡӲ�̻����·����ַ��
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) && context.getExternalCacheDir() != null) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		File file = new File(cachePath + File.separator + uniqueName);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 判断文件是否为图片<br>
	 * <br>
	 * 
	 * @param pInput
	 *            文件名<br>
	 * @param pImgeFlag
	 *            判断具体文件类型<br>
	 * @return 检查后的结果<br>
	 * @throws Exception
	 */
	public static boolean isPicture(String pInput, String pImgeFlag) throws Exception {
		// 文件名称为空的场合
		if (pInput == null) {
			// 返回不和合法
			return false;
		}
		// 获得文件后缀名
		String tmpName = pInput.substring(pInput.lastIndexOf(".") + 1, pInput.length());
		// 声明图片后缀名数组
		String imgeArray[][] = { { "bmp", "0" }, { "dib", "1" }, { "gif", "2" }, { "jfif", "3" }, { "jpe", "4" },
				{ "jpeg", "5" }, { "jpg", "6" }, { "png", "7" }, { "tif", "8" }, { "tiff", "9" }, { "ico", "10" } };
		// 遍历名称数组
		for (int i = 0; i < imgeArray.length; i++) {
			// 判断单个类型文件的场合
			if (pImgeFlag != null && imgeArray[i][0].equals(tmpName.toLowerCase())
					&& imgeArray[i][1].equals(pImgeFlag)) {
				return true;
			}
			// 判断符合全部类型的场合
			if (pImgeFlag == null && imgeArray[i][0].equals(tmpName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * д�ı��ļ� ��Androidϵͳ�У��ļ������� /data/data/PACKAGE_NAME/files Ŀ¼��
	 * 
	 * @param context
	 * @param msg
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null)
			content = "";

		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(content.getBytes());

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�ı��ļ�
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String readInStream(InputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			LogUtil.i("FileTest", e.getMessage());
		}
		return null;
	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	public static void createFile(String folderPath, InputStream inps) {
		File file = new File(folderPath);
		FileOutputStream out = null;
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		try {
			while ((rc = inps.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] in2b = swapStream.toByteArray();

		try {
			out = new FileOutputStream(file);
			out.write(in2b);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * ���ֻ�дͼƬ
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeFile(byte[] buffer, String folder, String fileName) {
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist) {
			folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
		} else {
			writeSucc = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writeSucc;
	}

	/**
	 * �����ļ��ľ���·����ȡ�ļ�������������չ��
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
	}

	/**
	 * ��ȡ�ļ���չ��
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * ��ȡ�ļ���С
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * ��ȡ�ļ���С
	 * 
	 * @param size
	 *            �ֽ�
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * ת���ļ���С
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * ��ȡĿ¼�ļ�����
	 * 
	 * @param emojiFragment
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// �ݹ�
				count--;
			}
		}
		return count;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * ����ļ��Ƿ����
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;
	}

	public static boolean exists(String name) {
		// TODO Auto-generated method stub
		boolean status;
		if (!name.equals("")) {

			File newPath = new File(name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;
	}

	/**
	 * ���·���Ƿ����
	 * 
	 * @param path
	 * @return
	 */
	public static boolean checkFilePathExists(String path) {
		return new File(path).exists();
	}

	/**
	 * ����SD����ʣ��ռ�
	 * 
	 * @return ����-1��˵��û�а�װsd��
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * �½�Ŀ¼
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * ����Ƿ�װSD��
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		status = sDCardStatus.equals(Environment.MEDIA_MOUNTED);
		return status;
	}

	/**
	 * ����Ƿ�װ���õ�SD��
	 * 
	 * @return
	 */
	public static boolean checkExternalSDExists() {

		Map<String, String> evn = System.getenv();
		return evn.containsKey("SECONDARY_STORAGE");
	}

	/**
	 * ɾ��Ŀ¼(������Ŀ¼��������ļ�)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/" + listfile[i]);
						deletedFile.delete();
					}
					newPath.delete();
					LogUtil.i("DirectoryManager deleteDirectory", fileName);
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					LogUtil.i("DirectoryManager deleteFile", fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					se.printStackTrace();
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * ɾ����Ŀ¼
	 * 
	 * ���� 0����ɹ� ,1 ����û��ɾ��Ȩ��, 2�����ǿ�Ŀ¼,3 ����δ֪����
	 * 
	 * @return
	 */
	public static int deleteBlankPath(String path) {
		File f = new File(path);
		if (!f.canWrite()) {
			return 1;
		}
		if (f.list() != null && f.list().length > 0) {
			return 2;
		}
		if (f.delete()) {
			return 0;
		}
		return 3;
	}

	/**
	 * ������
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean reNamePath(String oldName, String newName) {
		File f = new File(oldName);
		return f.renameTo(new File(newName));
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filePath
	 */
	public static boolean deleteFileWithPath(String filePath) {
		SecurityManager checker = new SecurityManager();
		File f = new File(filePath);
		checker.checkDelete(filePath);
		if (f.isFile()) {
			LogUtil.i("DirectoryManager deleteFile", filePath);
			f.delete();
			return true;
		}
		return false;
	}

	/**
	 * ���һ���ļ���
	 * 
	 * @param files
	 */
	public static void clearFileWithPath(String filePath) {
		List<File> files = FileUtil.listPathFiles(filePath);
		if (files.isEmpty()) {
			return;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				clearFileWithPath(f.getAbsolutePath());
			} else {
				f.delete();
			}
		}
	}

	/**
	 * ��ȡSD���ĸ�Ŀ¼
	 * 
	 * @return
	 */
	public static String getSDRoot() {

		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * ��ȡ�ֻ�����SD���ĸ�Ŀ¼
	 * 
	 * @return
	 */
	public static String getExternalSDRoot() {

		Map<String, String> evn = System.getenv();

		return evn.get("SECONDARY_STORAGE");
	}

	/**
	 * �г�rootĿ¼��������Ŀ¼
	 * 
	 * @param path
	 * @return ����·��
	 */
	public static List<String> listPath(String root) {
		List<String> allDir = new ArrayList<String>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		// ���˵���.��ʼ���ļ���
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory() && !f.getName().startsWith(".")) {
					allDir.add(f.getAbsolutePath());
				}
			}
		}
		return allDir;
	}

	/**
	 * ��ȡһ���ļ����µ������ļ�
	 * 
	 * @param root
	 * @return
	 */
	public static List<File> listPathFiles(String root) {
		List<File> allDir = new ArrayList<File>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		File[] files = path.listFiles();
		for (File f : files) {
			if (f.isFile())
				allDir.add(f);
			else
				listPath(f.getAbsolutePath());
		}
		return allDir;
	}

	public enum PathStatus {
		SUCCESS, EXITS, ERROR
	}

	/**
	 * ����Ŀ¼
	 * 
	 * @param path
	 */
	public static PathStatus createPath(String newPath) {
		File path = new File(newPath);
		if (path.exists()) {
			return PathStatus.EXITS;
		}
		if (path.mkdir()) {
			return PathStatus.SUCCESS;
		} else {
			return PathStatus.ERROR;
		}
	}

	/**
	 * ��ȡ·����
	 * 
	 * @return
	 */
	public static String getPathName(String absolutePath) {
		int start = absolutePath.lastIndexOf(File.separator) + 1;
		int end = absolutePath.length();
		return absolutePath.substring(start, end);
	}

	/**
	 * ��ȡӦ�ó��򻺴��ļ����µ�ָ��Ŀ¼
	 * 
	 * @param context
	 * @param dir
	 * @return
	 */
	public static String getAppCache(Context context, String dir) {
		String savePath = context.getCacheDir().getAbsolutePath() + "/" + dir + "/";
		File savedir = new File(savePath);
		if (!savedir.exists()) {
			savedir.mkdirs();
		}
		savedir = null;
		return savePath;
	}

	public static Drawable getImageDrawable(String imgKey) {
		// TODO Auto-generated method stub
		FileInputStream fis;
		try {
			fis = new FileInputStream(imgKey);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);

			Drawable drawable = new BitmapDrawable(bitmap);
			return drawable;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/*******************/
	public final static int IMG = 0;
	public final static int SOUND = 1;
	public final static int APK = 2;
	public final static int PPT = 3;
	public final static int XLS = 4;
	public final static int DOC = 5;
	public final static int PDF = 6;
	public final static int CHM = 7;
	public final static int TXT = 8;
	public final static int MOVIE = 9;

	public static boolean changeFile(String oldFilePath, String newFilePath) {
		return saveFileByBase64(getBase64StringFromFile(oldFilePath), newFilePath);
	}

	public static String getBase64StringFromFile(String imageFile) {
		// ��ͼƬ�ļ�ת��Ϊ�ֽ������ַ��������������Base64���봦��
		InputStream in = null;
		byte[] data = null;
		// ��ȡͼƬ�ֽ�����
		try {
			in = new FileInputStream(imageFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ���ֽ�����Base64����
		return Base64.encodeToString(data, Base64.DEFAULT);// ����Base64��������ֽ������ַ�
	}

	/**
	 * @param fileString
	 *            base64
	 * @param filePath
	 *            ����·��,��������
	 * @return
	 */
	public static boolean saveFileByBase64(String fileString, String filePath) {
		// ���ֽ������ַ�������Base64���벢����ͼ�d
		if (fileString == null) // ͼ������Ϊ��
			return false;
		byte[] data = Base64.decode(fileString, Base64.DEFAULT);
		saveFileByBytes(data, filePath);
		// MyApplication.getInstance().sendBroadcast(
		// new
		// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+filePath)));
		return true;
	}

	// /**
	// * @param bitmap bitmap
	// * @param filePath ����·��,��������
	// * @return
	// */
	// public static boolean saveFileByBitmap(Bitmap bitmap, String filePath) {
	// // ���ֽ������ַ�������Base64���벢����ͼ�d
	// if (bitmap == null) // ͼ������Ϊ��
	// return false;
	// byte[] data = ImageUtil.Bitmap2Bytes(bitmap);
	// saveFileByBytes(data, filePath);
	// return true;
	// }
	//
	//
	/**
	 * @param fileString
	 *            bytes[]
	 * @param filePath
	 *            ����·��,��������
	 * @return
	 */
	public static boolean saveFileByBytes(byte[] data, String filePath) {
		// ���ֽ������ַ�������Base64���벢����ͼ�d
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				File file2 = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1));
				file2.mkdirs();
			}
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(data);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * imputStream ����out
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static boolean saveFileByInputStream(InputStream inputStream, String filePath) {
		File file = null;
		file = new File(filePath);
		if (!file.exists()) {
			File file2 = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1));
			file2.mkdirs();
		}
		FileOutputStream outputStream = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;

		try {
			bis = new BufferedInputStream(inputStream);
			outputStream = new FileOutputStream(file);
			bos = new BufferedOutputStream(outputStream);
			int byte_ = 0;
			while ((byte_ = bis.read()) != -1)
				bos.write(byte_);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				bos.flush();
				bos.close();
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * ����url�õ�����
	 * 
	 * @param url
	 * @return
	 */
	/*
	 * public static String getFileName(String url) { String fileName = ""; if
	 * (url != null) { fileName = url.substring(url.lastIndexOf("/") + 1); }
	 * return fileName; }
	 */

	public static boolean renameFile(String path, String oldName, String newName) {
		try {
			File file = null;
			file = new File(path + "/" + oldName);
			if (file.exists()) {
				file.renameTo(new File(path + "/" + newName));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ����������ɾ���ļ�
	 * 
	 * @param path
	 * @param context
	 * @param imageName
	 */
	private void delFile(String path, Context context, String imageName) {
		File file = null;
		String real_path = "";
		try {
			if (Util.getInstance().hasSDCard()) {
				real_path = (path != null && path.startsWith("/") ? path : "/" + path);
			} else {
				real_path = Util.getInstance().getPackagePath(context)
						+ (path != null && path.startsWith("/") ? path : "/" + path);
			}
			file = new File(real_path, imageName);
			if (file != null)
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �ݹ�ɾ���ļ��м��ļ�
	public static void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	/**
	 * ����·�������ƶ�����
	 * 
	 * @param filePath
	 * @return
	 */
	public static int getType(String filePath) {
		if (filePath == null) {
			return -1;
		}
		String end;
		if (filePath.contains("/")) {
			File file = new File(filePath);
			if (!file.exists())
				return -1;
			/* ȡ����չ�� */
			end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
		} else {
			end = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).toLowerCase();
		}

		end = end.trim().toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
				|| end.equals("wav") || end.equals("amr")) {
			return SOUND;
		} else if (end.equals("3gp") || end.equals("mp4")) {
			return MOVIE;
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp")) {
			return IMG;
		} else if (end.equals("apk")) {
			return APK;
		} else if (end.equals("ppt")) {
			return PPT;
		} else if (end.equals("xls")) {
			return XLS;
		} else if (end.equals("doc")) {
			return DOC;
		} else if (end.equals("pdf")) {
			return PDF;
		} else if (end.equals("chm")) {
			return CHM;
		} else if (end.equals("txt")) {
			return TXT;
		} else {
			return -1;
		}
	}

	public static Intent openFile(String filePath) {
		if (filePath == null) {
			return null;
		}
		File file = new File(filePath);
		if (!file.exists())
			return null;
		/* ȡ����չ�� */
		String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length())
				.toLowerCase();
		end = end.trim().toLowerCase();
		/* ����չ�������;���MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
				|| end.equals("wav") || end.equals("amr")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("3gp") || end.equals("mp4")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp")) {
			return getImageFileIntent(filePath);
		} else if (end.equals("apk")) {
			return getApkFileIntent(filePath);
		} else if (end.equals("ppt")) {
			return getPptFileIntent(filePath);
		} else if (end.equals("xls")) {
			return getExcelFileIntent(filePath);
		} else if (end.equals("doc")) {
			return getWordFileIntent(filePath);
		} else if (end.equals("pdf")) {
			return getPdfFileIntent(filePath);
		} else if (end.equals("chm")) {
			return getChmFileIntent(filePath);
		} else if (end.equals("txt")) {
			return getTextFileIntent(filePath, false);
		} else {
			return getAllIntent(filePath);
		}
	}

	// Android��ȡһ�����ڴ�APK�ļ���intent
	public static Intent getAllIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "*/*");
		return intent;
	}

	// Android��ȡһ�����ڴ�APK�ļ���intent
	public static Intent getApkFileIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

	// Android��ȡһ�����ڴ�VIDEO�ļ���intent
	public static Intent getVideoFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// Android��ȡһ�����ڴ�AUDIO�ļ���intent
	public static Intent getAudioFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// Android��ȡһ�����ڴ�Html�ļ���intent
	public static Intent getHtmlFileIntent(String param) {

		Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
				.encodedPath(param).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	// Android��ȡһ�����ڴ�ͼƬ�ļ���intent
	public static Intent getImageFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// Android��ȡһ�����ڴ�PPT�ļ���intent
	public static Intent getPptFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// Android��ȡһ�����ڴ�Excel�ļ���intent
	public static Intent getExcelFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// Android��ȡһ�����ڴ�Word�ļ���intent
	public static Intent getWordFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// Android��ȡһ�����ڴ�CHM�ļ���intent
	public static Intent getChmFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// Android��ȡһ�����ڴ��ı��ļ���intent
	public static Intent getTextFileIntent(String param, boolean paramBoolean) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	// Android��ȡһ�����ڴ�PDF�ļ���intent
	public static Intent getPdfFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	/**
	 * 打开文件
	 * 
	 * @param context
	 * @param file
	 */
	public static void openFile(Context context, File file) {

		// Uri uri = Uri.parse("file://"+file.getAbsolutePath());

		Intent intent = new Intent();

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// ����intent��Action����

		intent.setAction(Intent.ACTION_VIEW);

		// ��ȡ�ļ�file��MIME����

		String type = getMIMEType(file);
		// ����intent��data��Type���ԡ�
		intent.setDataAndType(/* uri */Uri.fromFile(file), type);
		context.startActivity(intent);
	}

	private static final String[][] MIME_MapTable = { { ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" }, { ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" }, { ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" },
			{ ".c", "text/plain" }, { ".class", "application/octet-stream" }, { ".conf", "text/plain" },
			{ ".cpp", "text/plain" }, { ".doc", "application/msword" }, { ".docx", "application/msword" },
			{ ".xls", "application/msword" }, { ".exe", "application/octet-stream" }, { ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" }, { ".h", "text/plain" },
			{ ".htm", "text/html" }, { ".html", "text/html" }, { ".jar", "application/java-archive" },
			{ ".java", "text/plain" }, { ".jpeg", "image/jpeg" }, { ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" }, { ".log", "text/plain" }, { ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" }, { ".m4b", "audio/mp4a-latm" }, { ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" }, { ".m4v", "video/x-m4v" }, { ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" }, { ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" }, { ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" }, { ".mpg4", "video/mp4" }, { ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" }, { ".pdf", "application/pdf" },
			{ ".png", "image/png" }, { ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" }, { ".prop", "text/plain" },
			{ ".rar", "application/x-rar-compressed" }, { ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" },
			{ ".rtf", "application/rtf" }, { ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" }, { ".wav", "audio/x-wav" },
			{ ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv" }, { ".wps", "application/vnd.ms-works" },
			{ ".xml", "text/plain" }, { ".z", "application/x-compress" }, { ".zip", "application/zip" },
			{ "", "*/*" } };

	/**
	 * 
	 * �����ļ���׺����ö�Ӧ��MIME���͡�
	 * 
	 * @param file
	 */

	private static String getMIMEType(File file)

	{

		String type = "*/*";

		String fName = file.getName();

		// ��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á�

		int dotIndex = fName.lastIndexOf(".");

		if (dotIndex < 0) {

			return type;

		}

		/* ��ȡ�ļ��ĺ�׺�� */

		String end = fName.substring(dotIndex, fName.length()).toLowerCase();

		if (end == "")
			return type;

		// ��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡�

		for (int i = 0; i < MIME_MapTable.length; i++) {

			if (end.equals(MIME_MapTable[i][0]))

				type = MIME_MapTable[i][1];

		}

		return type;

	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// Base Methods

	/**
	 * 向文件写入字符串列表
	 * 
	 * @param file
	 * @param strs
	 * @param append
	 * @throws IOException
	 */
	private static void writeToFile(File file, String[] strs, boolean append) throws IOException {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file, append));
			for (int i = 0; i < strs.length - 1; i++) {
				bos.write((strs[i] + "\r\n").getBytes());
			}
			bos.write(strs[strs.length - 1].getBytes());
		} catch (IOException e) {
			throw e;
		} finally {
			if (bos != null) {
				bos.flush();
				bos.close();
			}
		}
	}

	/**
	 * 从输入流中读取所有字节
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] getBytes(InputStream inStream) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = inStream.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			return null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	public static List<String> readLines(String fname, boolean trim) {
		return readLines(fname, trim, 0, Integer.MAX_VALUE);
	}

	public static List<String> readLines(String fname, boolean trim, int minlen) {
		return readLines(fname, trim, minlen, Integer.MAX_VALUE);
	}

	public static List<String> readLines(String fname, boolean trim, int minlen, int maxlen) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(fname)));

			List<String> lines = new ArrayList<>();
			String line = null;

			while ((line = br.readLine()) != null) {
				if (trim) {
					line.trim();
				}
				if (line.length() >= minlen && line.length() <= maxlen) {
					lines.add(line);
				}
			}
			return lines;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
package com.ac.ict.activityrecorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class FileUtils {
	private String SDPATH = null;
	private String dirName = null;
	private String timeFileName = null;
	private String typeFileName = null;
	private String TAG = "FileUtils";

	public FileUtils(String dirName, String timeFileName, String typeFileName)
			throws IOException {
		// ��ȡSD��Ŀ¼
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		this.dirName = SDPATH + dirName + "/";
		this.timeFileName = this.dirName + timeFileName;
		this.typeFileName = this.dirName + typeFileName;
		createSDDir(this.dirName);
		createSDFile(this.timeFileName);
		createSDFile(this.typeFileName);
	}

	/**
	 * �����ļ�
	 */
	private File createSDFile(String fName) throws IOException {
		File file = new File(fName);
		file.createNewFile();
		return file;
	}

	/**
	 * ����Ŀ¼
	 */
	private File createSDDir(String dir) {
		File file = new File(dir);
		boolean success = file.mkdir();
		if (!success) {
			// System.out.println("Ŀ¼�Ѵ���");
		}
		return file;
	}

	/**
	 * ��msg׷�ӵ�д��SD�ļ���
	 * 
	 * @throws IOException
	 */
	public void appendLine(String msg) throws IOException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(
					this.timeFileName, true), "GB2312");
			writer.write(msg + "\r\n");
		} catch (Exception e) {
			Log.i(TAG, "appendLine write==>" + e);
		} finally {
			try {
				writer.close();
			} catch (Exception e2) {
				Log.i(TAG, "appendLine close==>" + e2);
			}
		}
	}

	/**
	 * ��ӻ���ͣ����������Ѿ����ڣ������
	 * 
	 * @throws IOException
	 */
	public boolean addActivityType(String msg) {
		if (isTypeExist(msg))
			return false;

		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(
					this.typeFileName, true), "GB2312");
			writer.write(msg + "\r\n");
		} catch (Exception e) {
			Log.i(TAG, "appendLine write==>" + e);
			return false;
		} finally {
			try {
				writer.close();
			} catch (Exception e2) {
				Log.i(TAG, "appendLine close==>" + e2);
				return false;
			}
		}
		return true;
	}

	/**
	 * �жϻ�����Ƿ��Ѿ�����
	 * 
	 */
	private boolean isTypeExist(String msg) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					this.typeFileName), "GB2312"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (msg.equals(line))
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	/**
	 * ��ȡ���л����
	 * 
	 */
	public ArrayList<String> getAllActivityType() {
		BufferedReader br = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					this.typeFileName), "GB2312"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.equals("") && !result.contains(line))
					result.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * ��ȡ��ǰ����ִ�еĻ���ͣ����û���򷵻�null
	 * 
	 */
	public String GetCurrentActivityName() {
		try {
			File file = new File(this.timeFileName);
			String lastLine = readLastLine(file, "GB2312");
			if (lastLine == null || lastLine.equals(""))
				return null;
			String[] data = lastLine.split(",");
			if (data.length >= 3) {// ���ݸ�ʽΪ:˯��,start,20140814181054
				String status = data[1];
				if (status.equals("start")) {
					return data[0];
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	// ��ȡһ������
	public static String readLastLine(File file, String charset)
			throws IOException {
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			return null;
		}
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			long len = raf.length();
			if (len == 0L) {
				return "";
			} else {
				long pos = len - 1;
				while (pos > 0) {
					pos--;
					raf.seek(pos);
					if (raf.readByte() == '\n') {
						break;
					}
				}
				if (pos == 0) {
					raf.seek(0);
				}
				byte[] bytes = new byte[(int) (len - pos)];
				raf.read(bytes);
				if (charset == null) {
					return new String(bytes);
				} else {
					return new String(bytes, charset);
				}
			}
		} catch (Exception e) {
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (Exception e2) {
				}
			}
		}
		return null;
	}
}

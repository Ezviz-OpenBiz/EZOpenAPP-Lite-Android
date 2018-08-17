package com.ezviz.open.utils;

import android.os.AsyncTask;
import android.widget.ImageView;
import com.ezviz.open.R;

public class BitmapWorkManager {
	/**
	 * 获取传入的ImageView它所对应的BitmapWorkerTask。
	 */
	public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			Object object = imageView.getTag(R.id.tag_key_capture_task);
			if (object instanceof AsyncBitmapTaskTag) {
				AsyncBitmapTaskTag asyncBitmapTaskTag = (AsyncBitmapTaskTag) object;
				return asyncBitmapTaskTag.getBitmapWorkerTask();
			}
		}
		return null;
	}

	/**
	 * 取消掉后台的潜在任务，当认为当前ImageView存在着一个另外图片请求任务时 ，
	 * 则把它取消掉并返回true，否则返回false。
	 */
	public static boolean cancelPotentialWork(String deviceSerial,int cameraNo, ImageView imageView) {
		BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask != null && bitmapWorkerTask.getStatus() == AsyncTask.Status.PENDING) {
			if (bitmapWorkerTask.deviceSerial == null
					|| !(bitmapWorkerTask.deviceSerial.equals(deviceSerial)&& bitmapWorkerTask.cameraNo == cameraNo)) {
				bitmapWorkerTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}
}

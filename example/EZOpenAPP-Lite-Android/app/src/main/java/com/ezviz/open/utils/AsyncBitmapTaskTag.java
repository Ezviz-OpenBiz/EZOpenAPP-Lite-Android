package com.ezviz.open.utils;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/29
 */

import java.lang.ref.WeakReference;

/**
 * 自定义的一个 Drawable，让这个 Drawable持有BitmapWorkerTask的弱引用。
 */
public class AsyncBitmapTaskTag {
    private WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncBitmapTaskTag(BitmapWorkerTask bitmapWorkerTask) {
        bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference .get();
    }
}



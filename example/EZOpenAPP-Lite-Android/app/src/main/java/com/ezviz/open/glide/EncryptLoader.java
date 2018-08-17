package com.ezviz.open.glide;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/22
 */
public class EncryptLoader implements ModelLoader<EncryptUrlInfo,InputStream> {
    @Override
    public DataFetcher<InputStream> getResourceFetcher(EncryptUrlInfo encryptUrlInfo, int i, int i1) {

        return new EncryptFetcher(encryptUrlInfo);
    }

    public static class Factory implements ModelLoaderFactory<EncryptUrlInfo, InputStream> {
        @Override
        public ModelLoader<EncryptUrlInfo, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new EncryptLoader();
        }
        @Override
        public void teardown() {

        }
    }
}



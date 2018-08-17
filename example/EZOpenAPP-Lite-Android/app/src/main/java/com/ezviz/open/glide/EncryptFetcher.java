package com.ezviz.open.glide;

import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.ezviz.open.utils.EZDeviceDBManager;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/22
 */
public class EncryptFetcher implements DataFetcher<InputStream> {

    public static final int ENCRYPT_ERROR = -1;
    public static final String ENCRYPT_ERROR_MSG = "decrypt_error";
    private static final String TAG = "HttpUrlFetcher";

    private static final int MAXIMUM_REDIRECTS = 5;
    private static final HttpUrlConnectionFactory DEFAULT_CONNECTION_FACTORY = new DefaultHttpUrlConnectionFactory();

    private EncryptUrlInfo mEncryptUrlInfo;
    private final HttpUrlConnectionFactory connectionFactory;

    private HttpURLConnection urlConnection;
    private InputStream stream;
    private volatile boolean isCancelled;

    public EncryptFetcher(EncryptUrlInfo deviceEncrypt){
        this(deviceEncrypt,DEFAULT_CONNECTION_FACTORY);
    }
    // Visible for testing.
    EncryptFetcher(EncryptUrlInfo encryptUrlInfo, HttpUrlConnectionFactory connectionFactory) {
        this.mEncryptUrlInfo = encryptUrlInfo;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        return loadDataWithRedirects(new URL(mEncryptUrlInfo.url), 0 /*redirects*/, null /*lastUrl*/);
    }

    private InputStream loadDataWithRedirects(URL url, int redirects, URL lastUrl)
            throws IOException {
        if (redirects >= MAXIMUM_REDIRECTS) {
            throw new IOException("Too many (> " + MAXIMUM_REDIRECTS + ") redirects!");
        } else {
            // Comparing the URLs using .equals performs additional network I/O and is generally broken.
            // See http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make.html.
            try {
                if (lastUrl != null && url.toURI().equals(lastUrl.toURI())) {
                    throw new IOException("In re-direct loop");
                }
            } catch (URISyntaxException e) {
                // Do nothing, this is best effort.
//                e.printStackTrace();
            }
        }
        urlConnection = connectionFactory.build(url);
        urlConnection.setConnectTimeout(2500);
        urlConnection.setReadTimeout(2500);
        urlConnection.setUseCaches(false);
        urlConnection.setDoInput(true);

        // Connect explicitly to avoid errors in decoders if connection fails.
        urlConnection.connect();
        if (isCancelled) {
            return null;
        }
        final int statusCode = urlConnection.getResponseCode();
        if (statusCode / 100 == 2) {
            try {
                return getStreamForSuccessfulRequest(urlConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (statusCode / 100 == 3) {
            String redirectUrlString = urlConnection.getHeaderField("Location");
            if (TextUtils.isEmpty(redirectUrlString)) {
                throw new IOException("Received empty or null redirect url");
            }
            URL redirectUrl = new URL(url, redirectUrlString);
            return loadDataWithRedirects(redirectUrl, redirects + 1, url);
        } else {
            if (statusCode == -1) {
                throw new IOException("Unable to retrieve response code from HttpUrlConnection.");
            }
            throw new IOException("Request failed " + statusCode + ": " + urlConnection.getResponseMessage());
        }
        return null;
    }

    private InputStream getStreamForSuccessfulRequest(HttpURLConnection urlConnection)
            throws Exception {
        if (TextUtils.isEmpty(urlConnection.getContentEncoding())) {
            int contentLength = urlConnection.getContentLength();
            stream = ContentLengthInputStream.obtain(urlConnection.getInputStream(), contentLength);
        } else {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Got non empty content encoding: " + urlConnection.getContentEncoding());
            }
            stream = urlConnection.getInputStream();
        }
        if (stream == null){
            return null;
        }
        if (mEncryptUrlInfo.isEncrypt) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = stream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            output.flush();
            output.close();
            byte[] src = output.toByteArray();
            if (src == null || src.length <= 0) {
                Log.d(TAG, "load image error");
                return null;
            }
            String password = mEncryptUrlInfo.password;
            if (TextUtils.isEmpty(password)) {
                password = EZDeviceDBManager.getDevPwd(mEncryptUrlInfo.deviceSerial);
            }
            if (!TextUtils.isEmpty(password)) {
                byte[] data1 = EZOpenSDK.getInstance().decryptData(output.toByteArray(), password);
                if (data1 == null){
                    throw new BaseException(ENCRYPT_ERROR_MSG,ENCRYPT_ERROR);
                }
                stream = new ByteArrayInputStream(data1);
            }
        }
        return stream;
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
    }

    @Override
    public String getId() {
        return mEncryptUrlInfo.url;
    }

    @Override
    public void cancel() {
        // TODO: we should consider disconnecting the url connection here, but we can't do so directly because cancel is
        // often called on the main thread.
        isCancelled = true;
    }

    interface HttpUrlConnectionFactory {
        HttpURLConnection build(URL url) throws IOException;
    }

    private static class DefaultHttpUrlConnectionFactory implements HttpUrlConnectionFactory {
        @Override
        public HttpURLConnection build(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }
}



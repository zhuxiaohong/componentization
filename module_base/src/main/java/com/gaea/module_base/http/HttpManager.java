package com.gaea.module_base.http;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.gaea.module_base.BuildConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author LongpingZou
 * @date 2016/04/01
 */

public class HttpManager {

    private final static String TAG = "HttpManager";
    private static HttpManager instance;
    private Retrofit.Builder builder;
    private static final String CER_NAME = "server.crt";

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private HttpManager() {
        Gson gson = new Gson();
        try {
//            if (CER_NAME.isEmpty()) {
            //忽略所有证书
            overlockCard();
//            } else {
//                //选择证书
//                try {
//                    setCard(MyApplication.getContext().getAssets().open(CER_NAME));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, e.getMessage());
//                }
//            }
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, message);
                    }

                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory())

                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .cookieJar(new CookieJar() {
                        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                           /* String session = UserDataUtils.getSession(MyApplication.getContext());
                            int userId = UserDataUtils.getUserId(MyApplication.getContext());
                            Request request = chain.request().newBuilder()
                                    .addHeader(USER_SESSION, session)
                                    .addHeader(USER_ID, String.valueOf(userId))
                                    .build();
                            return chain.proceed(request);*/
                           return null;
                        }
                    })
                    .addInterceptor(interceptor)
                    .build();
            builder = new Retrofit.Builder();
            builder.client(okHttpClient);
//            builder.baseUrl(ServerConfig.BASE_URL);
            builder.addConverterFactory(GsonConverterFactory.create(gson));
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpManager getInstance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager();
                }
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return builder.build().create(service);
    }

    private SSLContext sslContext;

    public void destroy() {
        instance = null;
    }

    private void setCard(InputStream certificate) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            String certificateAlias = Integer.toString(0);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
            sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            final TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (CertificateException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (KeyStoreException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (NoSuchProviderException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 忽略所有https证书
     */
    private void overlockCard() {
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        }};
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new SecureRandom());
        } catch (Exception e) {
        }

    }
}

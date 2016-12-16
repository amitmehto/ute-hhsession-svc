package com.rogers.ute.hhsessionsvc.http;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.typesafe.config.Config;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HttpClientProvider {

    private String httpsKeystore;
    private String httpsKeystorePassword;
    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpsClient;

    public HttpClientProvider(Config config){
        this.httpsKeystore = config.getString("keystores.camp.path");
        this.httpsKeystorePassword = config.getString("keystores.camp.password");
        this.httpClient = getHttpClient();
        this.httpsClient = getHttpsClient();
    }

    public AsyncHttpClient getHttpsClient() {
        if (null != httpsClient) {
            return httpsClient;
        } else {
            try {
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                KeyStore keyStore = KeyStore.getInstance("JKS");
                InputStream keyInput = new FileInputStream(httpsKeystore);
                keyStore.load(keyInput, httpsKeystorePassword.toCharArray());
                keyInput.close();
                keyManagerFactory.init(keyStore, httpsKeystorePassword.toCharArray());
                final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }};
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new SecureRandom());
                AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();

                builder.setSSLContext(context);
                builder.setRealm(null);
                this.httpsClient = new AsyncHttpClient(builder.build());
                return httpsClient;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("problem initializing https client", e);
            }
        }

    }

    public AsyncHttpClient getHttpClient() {
        if(null != httpClient) {
            return httpClient;
        } else  {
            try {
                AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
                builder.setRealm(null);
                this.httpClient = new AsyncHttpClient(builder.build());
                return httpClient;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("problem initializing http client", e);
            }
        }

    }

    public AsyncHttpClient resetHttpClient() {
        this.httpClient = null;
        return getHttpClient();
    }

    public AsyncHttpClient resetHttpsClient() {
        this.httpsClient = null;
        return getHttpsClient();
    }

}
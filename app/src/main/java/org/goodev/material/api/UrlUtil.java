package org.goodev.material.api;

import android.text.TextUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by yfcheng on 2015/11/26.
 */
public class UrlUtil {

    public static final String HOME_TYPE_LATEST = "latest";
    public static final String HOME_TYPE_POPULAR = "popular";

    public static Observable<String> getRedirectUrl(String url) {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String u = getFinalURL(url);
                    subscriber.onNext(u);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
        return observable;
    }

    public static String getFinalURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();

        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location");
            if (TextUtils.isEmpty(redirectUrl)) {
                return url;
            }
            if (redirectUrl.startsWith(Api.getEndpoint())) {
                return getFinalURL(redirectUrl);
            }
            return redirectUrl;
        }
        return url;
    }
}

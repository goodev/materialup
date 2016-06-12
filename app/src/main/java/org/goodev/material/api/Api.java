package org.goodev.material.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.goodev.material.App;
import org.goodev.material.BuildConfig;
import org.goodev.material.model.Result;
import org.goodev.material.model.SearchRes;
import org.goodev.material.ui.UserPagerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import retrofit.GsonConverterFactory;
import retrofit.HttpException;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by yfcheng on 2015/11/25.
 */
public class Api {


    public static final String UPVOTED = "upvotes";
    public static final String CREATED = "posts";
    public static final String SHOWCASED = "showcases";
    public static final String COLLECTIONS = "collections";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "followings";
    public static final String EXTRACTOR_WEBSITE = "website";
    public static final String EXTRACTOR_URL = "url";
    public static final String SOURCE_FORMAT = "standard";

    public static Observable<ResponseBody> extractUrl(String url) {
        return Api.getApiService().extractUrl(EXTRACTOR_URL, SOURCE_FORMAT, url);
    }

    /**
     * user tabs type
     *
     * @param type
     * @return
     */
    public static String getUsersContentType(int type) {
        switch (type) {
            case UserPagerAdapter.UPVOTED:
                return UPVOTED;
            case UserPagerAdapter.CREATED:
                return CREATED;
            case UserPagerAdapter.SHOWCASED:
                return SHOWCASED;
            case UserPagerAdapter.COLLECTIONS:
                return COLLECTIONS;
            case UserPagerAdapter.FOLLOWERS:
                return FOLLOWERS;
            case UserPagerAdapter.FOLLOWING:
                return FOLLOWING;
        }
        return UPVOTED;
    }

    private Api() {
    }

    public static final int MU_INDEX = 0;
    public static final int SITE_INDEX = 1;
    public static final int IOS_INDEX = 2;

    public static final String IOS_ENDPOINT = "http://www.ios.uplabs.com";
    public static final String IOS_HOST = "www.ios.uplabs.com";
    private static final String ENDPOINT = "http://www.materialup.com";
    private static final String HOST = "www.materialup.com";
    public static final String MU_ENDPOINT = "http://www.materialup.com";
    public static final String MU_HOST = "www.materialup.com";
    public static final String SITE_ENDPOINT = "http://www.site.uplabs.com";
    public static final String SITE_HOST = "www.site.uplabs.com";
    public static final String DRIBBBLE = "https://dribbble.com/";
    public static final String GITHUB = "https://github.com/";
    public static final String GOOGLE = "https://plus.google.com/";
    public static final String BEHANCE = "https://www.behance.net/";
    public static final String CODEPEN = "http://codepen.io/";


    public static String getAuthToken(String cookies) {
        int start = cookies.indexOf(TOKEN) + TOKEN.length() + 1;
        int end = cookies.indexOf(";", start);
        if (end == -1) {
            end = cookies.length();
        }
        return cookies.substring(start, end);
    }


    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.e("Log",
                    String.format("Sending request %s on %s%n%s", request.url(), chain.connection(),
                            request.headers()).toString());

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.e("Log",
                    String.format("Received response for %s in %.1fms%n%s", response.request().url(),
                            (t2 - t1) / 1e6d, response.headers()).toString());
            return response;
        }
    }

    public static final String PREF_COOKIES = "cooset";
    public static final String PREF_TOKEN = "auth_token";
    public static final String TOKEN = "token";

    // 请求的token 只包含下面的字段
    public static final String CK_cfduid = "__cfduid";
    public static final String CK_ga = "_ga";
    public static final String CK_XSRFTOKEN = "XSRF-TOKEN";
    public static final String CK_up = "_up";
    public static final String CK_auth_token = "auth_token";
    public static final String CK__gat = "_gat";

    private static String[] array = new String[]{
            CK_cfduid.toLowerCase(),
            CK_ga.toLowerCase(),
            CK_XSRFTOKEN.toLowerCase(),
            CK_up.toLowerCase(),
            CK_auth_token.toLowerCase(),
            CK__gat.toLowerCase(),

    };
    public static final List<String> CKS = Arrays.asList(array);

    /**
     * This interceptor put all the Cookies in Preferences in the Request.
     * Your implementation on how to get the Preferences MAY VARY.
     * <p>
     * Created by tsuharesu on 4/1/15.
     */
    static class CookiesInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            String host = chain.request().url().getHost();
            Request.Builder builder = chain.request().newBuilder();
            String xsrf = null;
            if (isMatchHost(host)) {
                Set<String> preferences = App.getPref().getStringSet(PREF_COOKIES, new HashSet<>());
                boolean hasToken = false;
                for (String cookie : preferences) {
                    if (TextUtils.isEmpty(cookie)) {
                        continue;
                    }
                    if (cookie.contains(Api.PREF_TOKEN)) {
                        hasToken = true;
                    }
                    builder.addHeader("Cookie", cookie);
                    if (cookie.contains(CK_XSRFTOKEN)) {
                        int start = cookie.indexOf(CK_XSRFTOKEN) + CK_XSRFTOKEN.length();
                        int end = cookie.indexOf(";", start);
                        xsrf = cookie.substring(start + 1, end);
                        xsrf = URLDecoder.decode(xsrf, "UTF-8");
                        if (BuildConfig.DEBUG) {
                            Log.v("OkHttp", "XSRF: " + xsrf); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
                        }
                    }
                    if (BuildConfig.DEBUG) {
                        Log.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
                    }
                }

                if (!hasToken) {
                    String token = App.getPref().getString(PREF_TOKEN, null);
                    if (!TextUtils.isEmpty(token)) {
                        builder.addHeader("Cookie", PREF_TOKEN + "=" + token);
                    }
                }
            }

            if (!TextUtils.isEmpty(xsrf)) {
                builder.addHeader("X-XSRF-TOKEN", xsrf);
            }

            builder.addHeader("User-Agent", getUserAgent(App.getIns()));

            Response originalResponse = chain.proceed(builder.build());
            // save new cookie
            if (isMatchHost(host)) {
                if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                    HashSet<String> cookies = new HashSet<>();
                    for (String header : originalResponse.headers("Set-Cookie")) {
                        cookies.add(header);
                    }
                    App.getPref().edit()
                            .putStringSet(PREF_COOKIES, cookies)
                            .apply();
                }
            }

            return originalResponse;
        }

    }

    public static boolean isMatchHost(String host) {
        return SITE_HOST.equalsIgnoreCase(host) || MU_HOST.equalsIgnoreCase(host) || IOS_HOST.equalsIgnoreCase(host);
    }

    /**
     * This Interceptor add all received Cookies to the app DefaultPreferences.
     * Your implementation on how to save the Cookies on the Preferences MAY VARY.
     * <p>
     * Created by tsuharesu on 4/1/15.
     */
    static class ReceivedCookiesInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            String host = chain.request().url().getHost();
            if (isMatchHost(host)) {
                if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                    HashSet<String> cookies = new HashSet<>();
                    for (String header : originalResponse.headers("Set-Cookie")) {
                        cookies.add(header);
                    }
                    App.getPref().edit()
                            .putStringSet(PREF_COOKIES, cookies)
                            .apply();
                }
            }

            return originalResponse;
        }

    }

    public static void filterCoolie(Map<String, String> sets, String header) {
        StringTokenizer st = new StringTokenizer(header, ";");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            String name = token.substring(0, token.indexOf("="));
            if (CKS.contains(name.toLowerCase())) {
                sets.put(name, token.substring(token.indexOf("=") + 1, token.length()));
            }
        }
    }


    static String sUserAgent;

    public static String getUserAgent(Context context) {
        if (sUserAgent != null) {
            return sUserAgent;
        }
        String agent = context == null ? "" : WebSettings.getDefaultUserAgent(context);
        sUserAgent = agent + " ; MaterialUp-App/" + BuildConfig.VERSION_CODE;
        return sUserAgent;
    }

    static OkHttpClient sOkHttpClient;
    static Retrofit sRetrofit;
    static ApiService sApiService;
    private static final int PAGE_NUM = 1;
    //2015-05-19T07:01:40.002-07:00
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static int sCurrentSiteIndex;

    public static void resetApiService(int pos) {
        sCurrentSiteIndex = pos;
        sRetrofit = null;
        sApiService = null;
    }

    public static ApiService getApiService() {
        if (sOkHttpClient == null) {
            sOkHttpClient = new OkHttpClient();
            sOkHttpClient.interceptors().add(new CookiesInterceptor());
//            sOkHttpClient.interceptors().add(new ReceivedCookiesInterceptor());
            if (BuildConfig.DEBUG) {
                sOkHttpClient.interceptors().add(new LoggingInterceptor());
            }
        }

        if (sRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(DATE_FORMAT)
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            sRetrofit = new Retrofit.Builder().baseUrl(getEndpoint()).client(sOkHttpClient)
                    .addConverterFactory(new ToStringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }

        if (sApiService == null) {
            sApiService = sRetrofit.create(ApiService.class);
        }
        return sApiService;
    }

    public static String getEndpoint() {
        switch (sCurrentSiteIndex) {
            case MU_INDEX:
                return MU_ENDPOINT;
            case IOS_INDEX:
                return IOS_ENDPOINT;
            case SITE_INDEX:
                return SITE_ENDPOINT;
        }
        return ENDPOINT;
    }

    //TODO fix this
    public static <E> boolean hasNextPage(List<E> data) {
        return data != null && data.size() >= PAGE_NUM;
    }

    public static String getServerErrorMessage(HttpException error) {
        String msg = getErrorMessage(error.response());
        if (msg == null) {
            switch (error.code()) {
//                case NETWORK:
//                    msg = "check your network!";
//                    break;
                default:
                    msg = error.getMessage();
            }
        }
        return msg;
    }

    public static String getErrorMessage(retrofit.Response response) {
        String text = responseToString(response);
        try {
            JSONObject object = new JSONObject(text);
            if (object.has("errors")) {
                JSONArray errors = object.getJSONArray("errors");
                if (errors.length() > 0) {
                    JSONObject error = errors.getJSONObject(0);
                    if (error.has("message")) {
                        return error.getString("message");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response.code() > 500) {
            return "check your network!";
        }
        return null;
    }

    public static String responseToString(retrofit.Response response) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.errorBody().byteStream()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = sb.toString();
        return result;
    }

    //iosup_production
    //{"requests":[{"indexName":"materialup_production","params":""query=d&hitsPerPage=9&page=0"}]}
    public static RequestBody getSearchBody(String query, int page) {
        try {
            JSONObject object = new JSONObject();
            object.put("indexName", getSearchIndexName());
            object.put("params", "query=" + query + "&hitsPerPage=20&page=" + page);
            JSONArray array = new JSONArray();
            array.put(object);
            JSONObject body = new JSONObject();
            body.put("requests", array);


            return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String b = "{\"requests\":[{\"indexName\":\"" + getSearchIndexName() + "\",\"params\":\"\"query=" + query + "&hitsPerPage=20&page=" + page + "\"}]}";
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), b);
    }

    public static String getSearchIndexName() {
        switch (sCurrentSiteIndex) {
            case MU_INDEX:
                return "materialup_production";
            case IOS_INDEX:
                return "iosup_production";
            case SITE_INDEX:
                return "siteup_production";
        }
        return "materialup_production";
    }

    public static Result getSearchResult(SearchRes searchRes) {
        if (searchRes.results == null || searchRes.results.isEmpty()) {
            return null;
        }
        Result result = searchRes.results.get(0);
        return result;
    }
}

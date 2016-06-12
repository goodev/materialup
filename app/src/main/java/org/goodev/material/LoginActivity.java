package org.goodev.material;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.goodev.material.api.Api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN = "http://www.materialup.com/auth/twitter";
    WebView mWebView;
    boolean mLogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.webView);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);

        mWebView.setWebChromeClient(new WebChromeClient() {

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mLogined) {

                }
                //http://www.materialup.com/auth/twitter/callback?oauth_token=QQIa0QAAAAAAelLMAAABUWH2_Ks&oauth_verifier=FfuK9vl4YI6xZTvqrozdjNTewhzjqiP0
                if (url.contains("materialup.com/auth/twitter/callback") && url.contains("oauth_token") && url.contains("oauth_verifier")) {
                    mLogined = true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                String cookies = CookieManager.getInstance().getCookie(url);
                if ((url.contains("materialup.com/auth/twitter/callback") && url.contains("oauth_token") && url.contains("oauth_verifier"))) {
                    mLogined = true;
                }
                if (mLogined && (url.endsWith("www.materialup.com/") || url.endsWith("www.materialup.com"))) {
                    cookies = CookieManager.getInstance().getCookie("http://www.materialup.com");
                    Set<String> set = new HashSet<String>();
//                    Uri uri = Uri.parse(url);
//                    cookies = cookies +";oauth_token="+uri.getQueryParameter("oauth_token")+"; path=/;";
                    set.add(cookies);

                    Map<String, String> map = new HashMap<String, String>();
                    Api.filterCoolie(map, cookies);
                    String token = null;
                    if (cookies.contains(Api.TOKEN)) {
                        token = Api.getAuthToken(cookies);
                    }
                    //TODO ....
                    SharedPreferences.Editor editor = App.getPref().edit();
                    if (!TextUtils.isEmpty(token)) {
                        editor.putString(Api.PREF_TOKEN, token);
                    }
                    editor.putStringSet(Api.PREF_COOKIES, set)
                            .apply();
                    App.getCookieManager().storeCookies(url, cookies);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                String cookies = CookieManager.getInstance().getCookie(url);
                if (mLogined && (url.endsWith("www.materialup.com/") || url.endsWith("www.materialup.com"))) {
                    cookies = CookieManager.getInstance().getCookie("http://www.materialup.com");
                    Set<String> set = new HashSet<String>();
//                    Uri uri = Uri.parse(url);
//                    cookies = cookies +";oauth_token="+uri.getQueryParameter("oauth_token")+"; path=/;";
                    set.add(cookies);

                    Map<String, String> map = new HashMap<String, String>();
                    Api.filterCoolie(map, cookies);
                    String token = null;
                    if (cookies.contains(Api.TOKEN)) {
                        token = Api.getAuthToken(cookies);
                        //TODO ....
                        SharedPreferences.Editor editor = App.getPref().edit();
                        if (!TextUtils.isEmpty(token)) {
                            editor.putString(Api.PREF_TOKEN, token);
                        }
                        editor.putStringSet(Api.PREF_COOKIES, set)
                                .apply();
                        App.getCookieManager().storeCookies(url, cookies);
                        App.setLoginStatus(true);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });

        mWebView.loadUrl(LOGIN);
        Snackbar.make(mWebView, R.string.login_info, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

package org.goodev.material;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.goodev.material.util.CookieManager;
import org.goodev.material.util.Pref;
import org.goodev.ui.Stats;

/**
 * Created by yfcheng on 2015/11/27.
 */
public class App extends Application {
    private Stats mStats;

    synchronized public Stats getStats() {
        if (mStats == null) {
            mStats = new Stats();
        }

        return mStats;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        sThis = this;
        sPref = getSharedPreferences("andgoo", MODE_PRIVATE);
    }

    static App sThis;
    static CookieManager mCookieManager;

    public synchronized static CookieManager getCookieManager() {
        if (sThis == null) {
            return new CookieManager();
        }
        if (sThis.mCookieManager == null) {
            sThis.mCookieManager = new CookieManager();
        }
        return sThis.mCookieManager;
    }

    SharedPreferences sPref;

    public static SharedPreferences getPref() {
        if (sThis.sPref == null) {
            sThis.sPref = sThis.getSharedPreferences("andgoo", MODE_PRIVATE);
            ;
        }
        return sThis.sPref;
    }


    public static final boolean checkLoginInfo(String site, String name, String password) {
        try {
            CookieManager cm = App.getCookieManager();
//            URL url = new URL(site + Api.CSRF_URL);
//            URLConnection conn = url.openConnection();
//            conn.connect();
//            cm.storeCookies(conn);
//            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String inputLine;
//
//            while ((inputLine = in.readLine()) != null)
//                response.append(inputLine);
//
//            in.close();
//
//            String csrfs = response.toString();
//
//            JSONObject csrf = new JSONObject(csrfs);
//            String csrfV = csrf.getString("csrf");
//            Map<String, String> data = new HashMap<String, String>();
//            data.put("login", name);
//            data.put("password", password);
//            data.put("authenticity_token", csrfV);
//            // login=rain_hust&password=147852
//
//            HttpRequest r = HttpRequest.post(site + Api.SESSION_URL);
//            HttpURLConnection con = r.getConnection();
//            cm.setCookies(con);
//
//            r.header("X-CSRF-Token", csrfV).
//                    header("Content-Type", "application/json").
//                    form(data);
//            String user = r.body();
//            cm.storeCookies(con);
//
//            HttpRequest r2 = HttpRequest.post(site + Api.LOGIN_URL);
//            HttpURLConnection con2 = r2.getConnection();
//            cm.setCookies(con2);
//
//            r2.header("X-CSRF-Token", csrfV).
//                    header("Content-Type", "application/json").
//                    form(data);
//
//            String user2 = r2.body();
//            cm.storeCookies(con2);
//            HttpRequest g = HttpRequest.get(site + String.format(Api.MSG_URL, name));
//
//            HttpURLConnection con3 = g.getConnection();
//            cm.setCookies(con3);
//            g.header("X-CSRF-Token", csrfV);
//
//            String b = g.body();
//
//            try {
//                JSONObject rr = new JSONObject(b);
//            } catch (JSONException e) {
//                return false;
//            }
            return true;
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return false;
        }
    }


    static Boolean sLogin;

    public static void setLoginStatus(Boolean login) {
        sLogin = login;
    }

    public static boolean isLogin() {
        if (sLogin == null) {
            return Pref.get(sThis).isLogin();
        }
        return sLogin;
    }

    public static Context getIns() {
        return sThis;
    }
}

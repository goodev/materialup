package org.goodev.material.util;

import android.net.Uri;

import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * CookieManager is a simple utilty for handling cookies when working with java.net.URL and java.net.URLConnection objects. Cookiemanager cm
 * = new CookieManager(); URL url = new URL("http://www.hccp.org/test/cookieTest.jsp"); . . . // getting cookies: URLConnection conn =
 * url.openConnection(); conn.connect(); // setting cookies cm.storeCookies(conn); cm.setCookies(url.openConnection());
 *
 * @author Ian Brown
 */

public class CookieManager {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = ";";
    private static final String PATH = "path";
    private static final String EXPIRES = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    private static final String SET_COOKIE_SEPARATOR = "; ";
    private static final String COOKIE = "Cookie";
    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final char DOT = '.';
    public static String csrfUrl = "http://meta.discourse.org/session/csrf.json";
    public static String sessionUrl = "http://meta.discourse.org/session";
    public static String loginUrl = "http://meta.discourse.org/login";
    public static String msgUrl = "http://meta.discourse.org/topics/private-messages/rain_hust.json";
    private final Map store;
    private final DateFormat dateFormat;

    public CookieManager() {

        store = new HashMap();
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    public static void main(String[] args) {
        CookieManager cm = new CookieManager();
        try {
            URL url = new URL(csrfUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            cm.storeCookies(conn);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);

            in.close();

            String csrfs = response.toString();

            JSONObject csrf = new JSONObject(csrfs);
            String csrfV = csrf.getString("csrf");
            Map<String, String> data = new HashMap<String, String>();
            data.put("login", "rain_hust");
            data.put("password", "147852");
            data.put("authenticity_token", csrfV);
            // login=rain_hust&password=147852

//            HttpRequest r = HttpRequest.post("http://meta.discourse.org/session.json");
//            HttpURLConnection con = r.getConnection();
//            cm.setCookies(con);
//
//            r.header("X-CSRF-Token", csrfV).
//                    header("Content-Type", "application/json").
//                    form(data);
//            String user = r.body();
//            cm.storeCookies(con);
//
//            HttpRequest r2 = HttpRequest.post("http://meta.discourse.org/login.json");
//            HttpURLConnection con2 = r2.getConnection();
//            cm.setCookies(con2);
//
//            r2.header("X-CSRF-Token", csrfV).
//                    header("Content-Type", "application/json").
//                    form(data);
//
//            String user2 = r2.body();
//            cm.storeCookies(con2);
//            HttpRequest g = HttpRequest.get("http://meta.discourse.org/topics/private-messages/rain_hust.json");
//
//            HttpURLConnection con3 = g.getConnection();
//            cm.setCookies(con3);
//            g.header("X-CSRF-Token", csrfV);
//
//            String b = g.body();

            // cm.setCookies(url.openConnection());
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Retrieves and stores cookies returned by the host on the other side of the the open java.net.URLConnection. The connection MUST have
     * been opened using the connect() method or a IOException will be thrown.
     *
     * @param conn a java.net.URLConnection - must be open, or IOException will be thrown
     * @throws java.io.IOException Thrown if conn is not open.
     */
    public void storeCookies(URLConnection conn) throws IOException {

        // let's determine the domain from where these cookies are being sent
        String domain = getDomainFromHost(conn.getURL().getHost());

        Map domainStore; // this is where we will store cookies for this domain

        // now let's check the store to see if we have an entry for this domain
        if (store.containsKey(domain)) {
            // we do, so lets retrieve it from the store
            domainStore = (Map) store.get(domain);
        } else {
            // we don't, so let's create it and put it in the store
            domainStore = new HashMap();
            store.put(domain, domainStore);
        }

        // OK, now we are ready to get the cookies out of the URLConnection

        String headerName = null;
        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
            if (headerName.equalsIgnoreCase(SET_COOKIE)) {
                Map cookie = new HashMap();
                StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), COOKIE_VALUE_DELIMITER);

                // the specification dictates that the first name/value pair
                // in the string is the cookie name and value, so let's handle
                // them as a special case:

                if (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
                    String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
                    domainStore.put(name, cookie);
                    cookie.put(name, value);
                }

                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    try {
                        cookie.put(token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(), token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length()));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void storeCookies(String url, String cookieStr) {
        // let's determine the domain from where these cookies are being sent
        String domain = getDomainFromHost(Uri.parse(url).getHost());
        storeCookiesByDomain(domain, cookieStr);
    }

    private void storeCookiesByDomain(String domain, String cookieStr) {
        Map domainStore; // this is where we will store cookies for this domain

        // now let's check the store to see if we have an entry for this domain
        if (store.containsKey(domain)) {
            // we do, so lets retrieve it from the store
            domainStore = (Map) store.get(domain);
        } else {
            // we don't, so let's create it and put it in the store
            domainStore = new HashMap();
            store.put(domain, domainStore);
        }

        // OK, now we are ready to get the cookies out of the URLConnection

        String headerName = null;
        Map cookie = new HashMap();
        StringTokenizer st = new StringTokenizer(cookieStr, COOKIE_VALUE_DELIMITER);

        // the specification dictates that the first name/value pair
        // in the string is the cookie name and value, so let's handle
        // them as a special case:

        if (st.hasMoreTokens()) {
            String token = st.nextToken();
            String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
            String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
            domainStore.put(name, cookie);
            cookie.put(name, value);
        }

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            try {
                cookie.put(token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(), token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length()));
            } catch (Exception e) {
            }
        }
    }

    public void storeCookies(URL url, String cookieStr) {

        // let's determine the domain from where these cookies are being sent
        String domain = getDomainFromHost(url.getHost());

        storeCookiesByDomain(domain, cookieStr);
    }

    /**
     * Prior to opening a URLConnection, calling this method will set all unexpired cookies that match the path or subpaths for thi
     * underlying URL The connection MUST NOT have been opened method or an IOException will be thrown.
     *
     * @param conn a java.net.URLConnection - must NOT be open, or IOException will be thrown
     * @throws java.io.IOException Thrown if conn has already been opened.
     */
    public void setCookies(URLConnection conn) throws IOException {

        // let's determine the domain and path to retrieve the appropriate cookies
        URL url = conn.getURL();
        String domain = getDomainFromHost(url.getHost());
        String path = url.getPath();

        Map domainStore = (Map) store.get(domain);
        if (domainStore == null)
            return;
        StringBuffer cookieStringBuffer = new StringBuffer();

        Iterator cookieNames = domainStore.keySet().iterator();
        while (cookieNames.hasNext()) {
            String cookieName = (String) cookieNames.next();
            Map cookie = (Map) domainStore.get(cookieName);
            // check cookie to ensure path matches and cookie is not expired
            // if all is cool, add cookie to header string
            if (comparePaths((String) cookie.get(PATH), path) && isNotExpired((String) cookie.get(EXPIRES))) {
                cookieStringBuffer.append(cookieName);
                cookieStringBuffer.append("=");
                cookieStringBuffer.append((String) cookie.get(cookieName));
                if (cookieNames.hasNext())
                    cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
            }
        }
        try {
            conn.setRequestProperty(COOKIE, cookieStringBuffer.toString());
        } catch (java.lang.IllegalStateException ise) {
            IOException ioe = new IOException("Illegal State! Cookies cannot be set on a URLConnection that is already connected. " + "Only call setCookies(java.net.URLConnection) AFTER calling java.net.URLConnection.connect().");
            throw ioe;
        }
    }

    private String getDomainFromHost(String host) {
        if (host.indexOf(DOT) != host.lastIndexOf(DOT)) {
            return host.substring(host.indexOf(DOT) + 1);
        } else {
            return host;
        }
    }

    private boolean isNotExpired(String cookieExpires) {
        if (cookieExpires == null)
            return true;
        Date now = new Date();
        try {
            return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
        } catch (java.text.ParseException pe) {
            pe.printStackTrace();
            return false;
        }
    }

    private boolean comparePaths(String cookiePath, String targetPath) {
        if (cookiePath == null) {
            return true;
        } else if (cookiePath.equals("/")) {
            return true;
        } else if (targetPath.regionMatches(0, cookiePath, 0, cookiePath.length())) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Returns a string representation of stored cookies organized by domain.
     */

    @Override
    public String toString() {
        return store.toString();
    }

    public void setCookies(URL url, Request.Builder builder) {
        // let's determine the domain and path to retrieve the appropriate cookies
        String domain = getDomainFromHost(url.getHost());
        String path = url.getPath();

        Map domainStore = (Map) store.get(domain);
        if (domainStore == null)
            return;
        StringBuffer cookieStringBuffer = new StringBuffer();

        Iterator cookieNames = domainStore.keySet().iterator();
        while (cookieNames.hasNext()) {
            String cookieName = (String) cookieNames.next();
            Map cookie = (Map) domainStore.get(cookieName);
            // check cookie to ensure path matches and cookie is not expired
            // if all is cool, add cookie to header string
            if (comparePaths((String) cookie.get(PATH), path) && isNotExpired((String) cookie.get(EXPIRES))) {
                cookieStringBuffer.append(cookieName);
                cookieStringBuffer.append("=");
                cookieStringBuffer.append((String) cookie.get(cookieName));
                if (cookieNames.hasNext())
                    cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
            }
        }
        builder.addHeader(COOKIE, cookieStringBuffer.toString());
    }
}
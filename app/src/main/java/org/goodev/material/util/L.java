package org.goodev.material.util;

import android.util.Log;

import org.goodev.material.BuildConfig;


/**
 * https://github.com/ANDLABS-Git/AndlabsAndroidUtils/blob/master/library/src/com/andlabs/androidutils/logging/L.java
 * <p>
 * <pre>
 * Logging: Log simple strings or formatted strings in one simple call:
 *
 *     final String formattedTestString = "first argument = %s, second argument = %s";
 *     final String firstArgument = "abc";
 *     final Object secondArgument = new  Object() {
 *         public String toString() {
 *             return "123";
 *         };
 *     };
 *
 *     L.d(formattedTestString, firstArgument, secondArgument);
 * Which results in a log output 09-23 17:41:30.508: D/TestActivity:26(18510): onCreate(): first
 * argument = abc, second argument = 123
 * </pre>
 * <p>
 * Convenience class for logging. Logs the given parameters plus the calling class and line as a tag
 * and the calling method's name
 */
public class L {

    public static void v(Throwable t) {
        log(Log.VERBOSE, t, null);
    }

    public static void v(Object s1, Object... args) {
        log(Log.VERBOSE, null, s1, args);
    }

    public static void v(Throwable t, Object s1, Object... args) {
        log(Log.VERBOSE, t, s1, args);
    }

    public static void d(Throwable t) {
        log(Log.DEBUG, t, null);
    }

    public static void d(Object s1, Object... args) {
        log(Log.DEBUG, null, s1, args);
    }

    public static void d(Throwable t, Object s1, Object... args) {
        log(Log.DEBUG, t, s1, args);
    }

    public static void i(Throwable t) {
        log(Log.INFO, t, null);
    }

    public static void i(Object s1, Object... args) {
        log(Log.INFO, null, s1, args);
    }

    public static void i(Throwable t, Object s1, Object... args) {
        log(Log.INFO, t, s1, args);
    }

    public static void w(Throwable t) {
        log(Log.WARN, t, null);
    }

    public static void w(Object s1, Object... args) {
        log(Log.WARN, null, s1, args);
    }

    public static void w(Throwable t, Object s1, Object... args) {
        log(Log.WARN, t, s1, args);
    }

    public static void e(Throwable t) {
        log(Log.ERROR, t, null);
    }

    public static void e(Object s1, Object... args) {
        log(Log.ERROR, null, s1, args);
    }

    public static void e(Throwable t, Object s1, Object... args) {
        log(Log.ERROR, t, s1, args);
    }

    private static void log(final int pType, final Throwable t, final Object s1,
                            final Object... args) {
        if (pType == Log.ERROR || BuildConfig.DEBUG || Log.isLoggable("L", pType)) {
            final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];

            final String fullClassName = stackTraceElement.getClassName();
            final String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            final int lineNumber = stackTraceElement.getLineNumber();
            final String method = stackTraceElement.getMethodName();

            final String tag = className + ":" + lineNumber;

            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(method);
            stringBuilder.append("(): ");

            if (s1 != null) {
                final String message = (args == null) ? s1.toString()
                        : String.format((String) s1, args);
                stringBuilder.append(message);
            }

            switch (pType) {
                case Log.VERBOSE:
                    if (t != null) {
                        Log.v(tag, stringBuilder.toString(), t);
                    } else {
                        Log.v(tag, stringBuilder.toString());
                    }
                    break;

                case Log.DEBUG:
                    if (t != null) {
                        Log.d(tag, stringBuilder.toString(), t);
                    } else {
                        Log.d(tag, stringBuilder.toString());
                    }
                    break;

                case Log.INFO:
                    if (t != null) {
                        Log.i(tag, stringBuilder.toString(), t);
                    } else {
                        Log.i(tag, stringBuilder.toString());
                    }
                    break;

                case Log.WARN:
                    if (t != null) {
                        Log.w(tag, stringBuilder.toString(), t);
                    } else {
                        Log.w(tag, stringBuilder.toString());
                    }
                    break;

                case Log.ERROR:
                    if (t != null) {
                        Log.e(tag, stringBuilder.toString(), t);
                    } else {
                        Log.e(tag, stringBuilder.toString());
                    }
                    break;
            }
        }
    }

    public static void i() {
        i("....");
    }

    public static void d() {
        d("....");
    }

    public static void e() {
        e("....");
    }

    public static void v() {
        v("....");
    }

    public static void w() {
        w("....");
    }
}
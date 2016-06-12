# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-keepattributes InnerClasses
-keep class butterknife.** { *; }
-keep class android.** { *; }
-dontwarn butterknife.internal.**
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn rx.**
-dontwarn com.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep class rx.** { *; }
-keep class in.** { *; }
-keep class com.** { *; }
-keep class org.jsoup.** { *; }
-keep class org.parceler.** { *; }
-keep class retrofit.** { *; }
-keep class okio.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

# Rxjava-promises

-keep class com.darylteo.rx.** { *; }

-dontwarn com.darylteo.rx.**

# RxJava 0.21

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

## Retrolambda specific rules ##

# as per official recommendation: https://github.com/evant/gradle-retrolambda#proguard
-dontwarn java.lang.invoke.*

-dontwarn com.squareup.okhttp.internal.**
-keep class org.goodev.material.model.** { *; }
-keep class org.goodev.material.binding.** { *; }
# Parcel library
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class org.parceler.Parceler$$Parcels

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
  public <init>(***);
}

-keep class org.goodev.material.widget.ScrollAwareFABBehavior {*; }

#ad model
-keep class com.adxmi.customizedad.ContentAdModel {*; }
-dontwarn com.adxmi.customizedad.**
-keep class com.adxmi.customizedad.** {
    *;
}

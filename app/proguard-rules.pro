# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-ignorewarnings
-dontusemixedcaseclassnames #混淆时不会产生形形色色的类名
-verbose
# -dontshrink #不压缩输入的类文件
-dontpreverify
-optimizationpasses 3
# 关闭code/allocation/variable因为高德API在第2次optimized时会有NPE
# 关闭内联是防止Tinker异常，详见Tinker官网
-optimizations !method/inlining/*, !code/allocation/variable
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-keepattributes EnclosingMethod

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keep class * extends java.lang.annotation.Annotation { *; }

-keepattributes Signature
-keepattributes Exceptions,InnerClasses
-keepattributes *Annotation*
-keepattributes JavascriptInterface

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}


-keep public class * extends android.app.Service
-keep public class * extends android.content.Broadcasteceiver

# We want to keep methods in Application that could be used in the XML attribute onClick
-keepclasseswithmembers class * extends android.app.Application {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Pacelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#序列化属性
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeeplace();
    java.lang.Object readResolve();
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class **.R
-keep class **.R$* {
    <fields>;
}
-keep public class **.R$*{
    public static final int *;
}

-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}



#不混淆android.support包的内容
-keep class android.support.** { *; }


#kotlin
-dontwarn kotlin.**
-keep class kotlin.**

# androidx
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep class androidx.core.app.CoreComponentFactory { *; }
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
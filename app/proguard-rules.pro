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

# No app-specific keep rules: the only reflection in this app is Room's own
# Class.forName lookup of "AppDatabase_Impl" (androidx.room:room-runtime), and
# its consumer rule `-keep class * extends androidx.room.RoomDatabase`
# (bundled in the AAR, applied automatically) already covers it, since
# AppDatabase_Impl extends AppDatabase which extends RoomDatabase. The Event
# entity, EventDao and EventConverters are reached directly by Room's
# KSP-generated code, not by reflection, so R8 keeps them naturally. There is
# no Gson/Moshi/Retrofit, no JNI, and the Jalali date converter
# (converter/GregorianToJalaliConverter.kt, JalaliToGregorianConverter.kt) is
# plain arithmetic with no name-based lookups.
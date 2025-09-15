#
-keep class androidx.appcompat.widget.** { *; }
-keep class com.google.android.** { *; }
-ignorewarnings

#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static *** d(...);
#    public static *** w(...);
#    public static *** v(...);
#    public static *** i(...);
#    public static *** e(...);
#}
#-keepattributes LineNumberTable
-keep public class com.google.android.gms.ads.**{
   public *;
}


# My Customize
-keep class com.ogmediaapps.callerid.helpers.** { *; }
-keep class com.ogmediaapps.callerid.model.** { *; }
-keep class com.ogmediaapps.callerid.ads.** { *; }
-keep class com.ogmediaapps.callerid.db.** { *; }
-keep class com.ogmediaapps.callerid.db.entity.** { *; }
-keep class com.ogmediaapps.callerid.receivers.** { *; }
-keep class com.ogmediaapps.callerid.timepicker.** { *; }
-keep class com.ogmediaapps.callerid.ui.wic.** { *; }

-obfuscationdictionary "D:\Android\Sdk\class_encode_dictionary.txt"
-classobfuscationdictionary "D:\Android\Sdk\class_encode_dictionary.txt"
-packageobfuscationdictionary "D:\Android\Sdk\class_encode_dictionary.txt"

-mergeinterfacesaggressively
-repackageclasses "com.origin.commons.callerid.sample"
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
-keep class com.origin.commons.callerid.helpers.** { *; }
-keep class com.origin.commons.callerid.model.** { *; }
-keep class com.origin.commons.callerid.ads.** { *; }
-keep class com.origin.commons.callerid.db.** { *; }
-keep class com.origin.commons.callerid.db.entity.** { *; }
-keep class com.origin.commons.callerid.timepicker.** { *; }

-obfuscationdictionary "D:\Android\Sdk\class_encode_dictionary.txt"
-classobfuscationdictionary "D:\Android\Sdk\class_encode_dictionary.txt"
-packageobfuscationdictionary "D:\Android\Sdk\class_encode_dictionary.txt"

-mergeinterfacesaggressively
-repackageclasses "com.origin.commons.callerid.sample"
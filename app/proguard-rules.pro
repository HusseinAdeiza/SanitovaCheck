# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Google Sign-In
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keepattributes *Annotation*

# Firebase Auth
-keepclassmembers class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.auth.internal.** { *; }

# RevenueCat
-keep class com.revenuecat.purchases.** { *; }
-keep class com.revenuecat.purchases.interfaces.** { *; }
-keep class com.revenuecat.purchases.models.** { *; }
-dontwarn com.revenuecat.purchases.**
-keepattributes *Annotation*

# Retrofit
-keepattributes Signature, *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Gson
-keepattributes EnclosingMethod
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Kotlin Serialization / Data classes used in API
-keepclassmembers class com.sanitova.sanitovacheck.** {
    *;
}

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Coil
-keep class coil.** { *; }
-dontwarn coil.**

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Media3 (if any remaining references)
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Prevent R8 from stripping model classes
-keep class com.sanitova.sanitovacheck.AssessRequest { *; }
-keep class com.sanitova.sanitovacheck.AssessResponse { *; }
-keep class com.sanitova.sanitovacheck.ChatRequest { *; }
-keep class com.sanitova.sanitovacheck.ChatResponse { *; }
-keep class com.sanitova.sanitovacheck.ScanRecord { *; }

# Generic
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

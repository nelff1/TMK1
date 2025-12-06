# Keep PDF generator classes
-keep class com.example.tmk.utils.PDFGenerator { *; }
-keep class com.example.tmk.utils.AsyncPDFGenerator { *; }
-keep class com.example.tmk.utils.PermissionHelper { *; }

# Keep chart library
-keep class com.github.mikephil.charting.** { *; }

# Keep model classes
-keep class com.example.tmk.models.** { *; }

# Keep database classes
-keep class com.example.tmk.database.** { *; }

# Keep activities
-keep class com.example.tmk.activities.** { *; }

# Keep iText PDF classes
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# Keep AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Keep material components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**
# Preserve the line number information for debugging stack traces and hide the original source file name
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class uk.co.newagedev.animalcompare.**$$serializer { *; }
-keepclassmembers class uk.co.newagedev.animalcompare.** {
    *** Companion;
}
-keepclasseswithmembers class uk.co.newagedev.animalcompare.** {
    kotlinx.serialization.KSerializer serializer(...);
}
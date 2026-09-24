import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
}

fun buildproperty(propertyName: String, fallbackEnv: String? = null): String? {
    val propsFile = rootProject.file("stoatbuild.properties")
    if (propsFile.exists()) {
        val props = Properties()
        FileInputStream(propsFile).use { props.load(it) }
        (props[propertyName] as String?)?.let { return it }
    }
    return fallbackEnv?.let { System.getenv(it) }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

fun host(propertyName: String, fallbackEnv: String): String =
    (buildproperty(propertyName, fallbackEnv)
        ?: error("Missing '$propertyName' in stoatbuild.properties (or \$$fallbackEnv)"))
        .trimEnd('/')

android {
    namespace = "chat.stoat.core.model"
    compileSdk = libs.versions.compileSdk.get().toInt()

    buildFeatures { buildConfig = true }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        buildConfigField("String", "HOST_API",     "\"${host("hosts.api",     "HARDCAST_API")}\"")
        buildConfigField("String", "HOST_WS",      "\"${host("hosts.ws",      "HARDCAST_WS")}\"")
        buildConfigField("String", "HOST_AUTUMN",  "\"${host("hosts.autumn",  "HARDCAST_AUTUMN")}\"")
        buildConfigField("String", "HOST_JANUARY", "\"${host("hosts.january", "HARDCAST_JANUARY")}\"")
        buildConfigField("String", "HOST_APP",     "\"${host("hosts.app",     "HARDCAST_APP")}\"")
        buildConfigField("String", "HOST_INVITES", "\"${host("hosts.invites", "HARDCAST_INVITES")}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
dependencies {
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.datetime)
    compileOnly(libs.android.core.ktx)
}
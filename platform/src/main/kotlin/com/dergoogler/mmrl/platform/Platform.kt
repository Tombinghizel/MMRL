@file:Suppress("unused")

package com.dergoogler.mmrl.platform

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

const val TIMEOUT_MILLIS = 15_000L
const val PLATFORM_KEY = "PLATFORM"
internal const val BINDER_TRANSACTION = 84398154

/**
 * Represents the various platforms supported by the application.
 *
 * @property id A unique identifier for the platform.
 */
enum class Platform(val id: String) {
    Magisk("magisk"),
    KernelSU("kernelsu"),
    KsuNext("ksunext"),
    APatch("apatch"),
    MKSU("mksu"),
    SukiSU("sukisu"),
    RKSU("rksu"),
    Shizuku("shizuku"),
    NonRoot("nonroot"),
    Unknown("unknown");

    companion object {
        fun from(value: String): Platform {
            return entries.firstOrNull { it.id == value } ?: NonRoot
        }

        /**
         * Creates an [Intent] for a specific platform.
         *
         * This function is an inline extension function on the [Context] class.
         * It takes a reified type parameter `T` which represents the target component (e.g., Activity or Service)
         * and a [Platform] enum value indicating the platform for which the intent is being created.
         *
         * The created [Intent] will have its component set to the fully qualified name of the class `T`
         * within the current application's package.
         * It will also include the specified [platform] as an extra, using [PLATFORM_KEY] as the key.
         *
         * @param T The reified type of the target component (e.g., an Activity or Service class).
         * @param platform The [Platform] for which this intent is being created.
         * @return An [Intent] configured to launch the specified component for the given platform.
         */
        inline fun <reified T> Context.createPlatformIntent(platform: Platform): Intent =
            Intent().apply {
                component = ComponentName(
                    packageName,
                    T::class.java.name
                )
                putExtra(PLATFORM_KEY, platform)
            }

        fun Intent.getPlatform(): Platform? = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.getSerializableExtra(PLATFORM_KEY, Platform::class.java)
            } else {
                @Suppress("DEPRECATION")
                this.getSerializableExtra(PLATFORM_KEY) as? Platform
            }
        } catch (e: Exception) {
            Log.e("Platform", "Error getting platform", e)
            null
        }

        fun Intent.putPlatform(platform: Platform) {
            putExtra(PLATFORM_KEY, platform)
        }
    }

    val isMagisk get() = this == Magisk
    val isKernelSU get() = this == KernelSU
    val isKernelSuNext get() = this == KsuNext
    val isAPatch get() = this == APatch
    val isMKSU get() = this == MKSU
    val isSukiSU get() = this == SukiSU
    val isRKSU get() = this == RKSU
    val isShizuku get() = this == Shizuku

    val isNotMagisk get() = !isMagisk
    val isNotKernelSU get() = this != KernelSU && this != KsuNext
    val isNotKernelSuNext get() = !isKernelSuNext
    val isNotAPatch get() = !isAPatch
    val isNotMKSU get() = !isMKSU
    val isNotSukiSU get() = !isSukiSU
    val isNotRKSU get() = !isRKSU
    val isNotShizuku get() = !isShizuku

    val isNotNonRoot get() = this != NonRoot
    val isNonRoot get() = this == NonRoot
    val isValid get() = this != NonRoot
    val isNotValid get() = !isValid
    val isKernelSuOrNext get() = this == KernelSU || this == KsuNext

    val current get() = id
}


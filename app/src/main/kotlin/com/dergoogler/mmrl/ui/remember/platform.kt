package com.dergoogler.mmrl.ui.remember

import com.dergoogler.mmrl.platform.PlatformManager
import com.dergoogler.mmrl.platform.content.NullableBoolean

val versionName: String
    get() = PlatformManager.get("") {
        with(moduleManager) { version }
    }

val isLkmMode: NullableBoolean
    get() = PlatformManager.get(NullableBoolean(null)) {
        with(moduleManager) { isLkmMode }
    }

val versionCode
    get() = PlatformManager.get(0) {
        with(moduleManager) { versionCode }
    }

val seLinuxContext: String
    get() = PlatformManager.get("Failed") {
        seLinuxContext
    }

val superUserCount: Int
    get() = PlatformManager.get(-1) {
        with(moduleManager) {
            superUserCount
        }
    }
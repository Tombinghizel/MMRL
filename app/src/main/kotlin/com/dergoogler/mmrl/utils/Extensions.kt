package com.dergoogler.mmrl.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import com.dergoogler.mmrl.BuildConfig
import com.dergoogler.mmrl.datastore.model.UserPreferences
import com.dergoogler.mmrl.datastore.model.WebUIEngine
import com.dergoogler.mmrl.ext.toFormattedDateSafely
import com.dergoogler.mmrl.platform.model.ModId
import com.dergoogler.mmrl.platform.model.toModuleConfig
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.webui.helper.WebUILauncher
import com.topjohnwu.superuser.NoShellException
import com.topjohnwu.superuser.Shell

val Float.toFormattedDateSafely: String
    @Composable
    get() {
        val prefs = LocalUserPreferences.current
        return this.toFormattedDateSafely(prefs.datePattern)
    }

val Long.toFormattedDateSafely: String
    @Composable
    get() {
        val prefs = LocalUserPreferences.current
        return this.toFormattedDateSafely(prefs.datePattern)
    }


@Throws(NoShellException::class)
inline fun <T> withNewRootShell(
    globalMnt: Boolean = false,
    debug: Boolean = false,
    commands: Array<String> = arrayOf("su"),
    block: Shell.() -> T,
): T {
    return createRootShell(globalMnt, debug, commands).use(block)
}

@Throws(NoShellException::class)
fun createRootShell(
    globalMnt: Boolean = false,
    debug: Boolean = false,
    commands: Array<String> = arrayOf("su"),
): Shell {
    Shell.enableVerboseLogging = debug
    val builder = Shell.Builder.create()
    if (globalMnt) {
        builder.setFlags(Shell.FLAG_MOUNT_MASTER)
    }
    return builder.build(*commands)
}

fun UserPreferences.launchWebUI(context: Context, modId: ModId) {
    val config = modId.toModuleConfig()

    val launcher = WebUILauncher(
        debug = BuildConfig.DEBUG,
        packageName = webuixPackageName
    )

    if (webuiEngine == WebUIEngine.PREFER_MODULE) {
        val configEngine = config.getWebuiEngine(context)

        if (configEngine == null) {
            launcher.launchWX(
                context = context,
                modId = modId,
                platform = workingMode.toPlatform()
            )
            return
        }

        when (configEngine) {
            "wx" -> launcher.launchWX(
                context = context,
                modId = modId,
                platform = workingMode.toPlatform()
            )

            "ksu" -> launcher.launchLegacy(
                context = context,
                modId = modId,
                platform = workingMode.toPlatform()
            )

            else -> Toast.makeText(context, "Unknown WebUI engine", Toast.LENGTH_SHORT).show()
        }

        return
    }


    if (webuiEngine == WebUIEngine.WX) {
        launcher.launchWX(
            context = context,
            modId = modId,
            platform = workingMode.toPlatform()
        )
        return

    }

    if (webuiEngine == WebUIEngine.KSU) {
        launcher.launchLegacy(
            context = context,
            modId = modId,
            platform = workingMode.toPlatform()
        )
        return
    }

    Toast.makeText(context, "Unknown WebUI engine", Toast.LENGTH_SHORT).show()
}
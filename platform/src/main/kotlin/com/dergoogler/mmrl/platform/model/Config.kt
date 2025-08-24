package com.dergoogler.mmrl.platform.model

import android.content.Context
import com.dergoogler.mmrl.platform.file.SuFile
import com.dergoogler.mmrl.platform.util.moshi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Locale

@JsonClass(generateAdapter = true)
data class ModuleConfig(
    @Json(name = "name")
    val nam: Any? = null,
    @Json(name = "description")
    val desc: Any? = null,
    @Json(name = "webui-engine")
    val webuiEngine: Any? = "wx",
    val cover: String? = null,
) {
    val locale: String get() = Locale.getDefault().language

    private fun get(prop: Any?, selector: String, default: String = "en"): String? = try {
        when (prop) {
            is String -> prop
            is Map<*, *> -> prop[selector] as? String ?: prop[default] as? String
            else -> null
        }
    } catch (e: Exception) {
        null
    }

    val description
        get(): String? = get(
            prop = desc,
            selector = locale
        )

    val name
        get(): String? = get(
            prop = nam,
            selector = locale
        )

    fun getWebuiEngine(context: Context): String? = get(
        prop = webuiEngine,
        selector = context.packageName
    )

    companion object {
        val ModId.asModuleConfig get(): ModuleConfig = id.asModuleConfig
        val String.asModuleConfig
            get(): ModuleConfig {
                val moduleDir = SuFile("/data/adb/modules", this)
                val configFile =
                    moduleDir.fromPaths("config.json", "config.mmrl.json") ?: return ModuleConfig()
                val json = configFile.readText()
                val jsonAdapter = moshi.adapter(ModuleConfig::class.java)
                return jsonAdapter.fromJson(json) ?: ModuleConfig()
            }
    }
}
package com.dergoogler.mmrl.ui.remember

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.dergoogler.mmrl.model.json.UpdateJson
import com.dergoogler.mmrl.model.online.VersionItem
import com.dergoogler.mmrl.platform.content.LocalModule
import com.dergoogler.mmrl.platform.model.ModId
import com.dergoogler.mmrl.repository.LocalRepository
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun rememberLocalModules(): State<List<LocalModule>> {
    val localRepository = rememberLocalRepository()
    return produceState(initialValue = emptyList(), localRepository) {
        val localList = localRepository.getLocalAllAsFlow().firstOrNull() ?: emptyList()
        value = localList
    }
}

@Composable
fun rememberLocalModule(id: ModId): State<LocalModule?> {
    val modules by rememberLocalModules()
    return remember(modules, id) {
        derivedStateOf {
            modules.find { it.id == id }
        }
    }
}

@Composable
fun rememberUpdatableModuleCount(): State<Int> {
    val localRepository = rememberLocalRepository()

    val versionItemCache = remember {
        mutableStateMapOf<String, VersionItem?>()
    }

    return produceState(initialValue = 0, localRepository) {
        localRepository.getLocalAllAsFlow().collect { modules ->
            val updatableModules = modules.filter {
                localRepository.hasUpdatableTag(it.id.toString())
            }

            var count = 0

            for (module in updatableModules) {
                val id = module.id.toString()

                val updateVersionItem = if (module.updateJson.isNotBlank()) {
                    UpdateJson.loadToVersionItem(module.updateJson)
                } else {
                    localRepository.getVersionById(id).firstOrNull()
                }

                val installedVersionCode = module.versionCode
                val updateVersionCode = updateVersionItem?.versionCode ?: -1

                if (updateVersionCode > installedVersionCode) {
                    count++
                    versionItemCache[id] = updateVersionItem
                } else {
                    versionItemCache[id] = null
                }
            }

            value = count
        }
    }
}

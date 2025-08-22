package com.dergoogler.mmrl.platform.content

import android.os.Parcelable
import com.dergoogler.mmrl.platform.model.ModId
import com.dergoogler.mmrl.platform.model.ModId.Companion.actionFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.bootCompletedFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.disableFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.modconfDir
import com.dergoogler.mmrl.platform.model.ModId.Companion.postFsDataFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.postMountFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.removeFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.sepolicyFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.serviceFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.systemDir
import com.dergoogler.mmrl.platform.model.ModId.Companion.systemPropFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.uninstallFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.updateFile
import com.dergoogler.mmrl.platform.model.ModId.Companion.webrootDir
import com.dergoogler.mmrl.platform.model.ModuleConfig.Companion.asModuleConfig
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LocalModule(
    val id: ModId,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val updateJson: String,
    val state: State,
    val size: Long,
    val lastUpdated: Long,
) : Parcelable {
    companion object {
        val LocalModule.config get() = id.asModuleConfig
        val LocalModule.hasWebUI get() = id.webrootDir.let { it.exists() && it.isDirectory() }
        val LocalModule.hasModConf get() = id.modconfDir.let { it.exists() && it.isDirectory() }
        val LocalModule.hasAction get() = id.actionFile.exists()
        val LocalModule.hasService get() = id.serviceFile.exists()
        val LocalModule.hasPostFsData get() = id.postFsDataFile.exists()
        val LocalModule.hasPostMount get() = id.postMountFile.exists()
        val LocalModule.hasSystemProp get() = id.systemPropFile.exists()
        val LocalModule.hasBootCompleted get() = id.bootCompletedFile.exists()
        val LocalModule.hasSepolicy get() = id.sepolicyFile.exists()
        val LocalModule.hasUninstall get() = id.uninstallFile.exists()
        val LocalModule.hasSystem get() = id.systemDir.let { it.exists() && it.isDirectory() }
        val LocalModule.hasDisable get() = id.disableFile.exists()
        val LocalModule.hasRemove get() = id.removeFile.exists()
        val LocalModule.hasUpdate get() = id.updateFile.exists()
    }
}
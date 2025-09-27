@file:Suppress("UNCHECKED_CAST")

package com.dergoogler.mmrl.platform.hiddenApi

import android.content.Context
import android.content.pm.UserInfo
import android.os.IUserManager
import android.os.Process
import android.os.RemoteException
import android.util.Log
import com.dergoogler.mmrl.platform.PlatformManager.getSystemService
import com.dergoogler.mmrl.platform.stub.IServiceManager

class HiddenUserManager(
    private val service: IServiceManager,
) {
    private val userManager by lazy {
        IUserManager.Stub.asInterface(
            service.getSystemService(Context.USER_SERVICE)
        )
    }

    @Throws(RemoteException::class)
    fun getUsers(
        excludePartial: Boolean = true,
        excludeDying: Boolean = true,
        excludePreCreated: Boolean = true,
    ): List<UserInfo> {
        try {
            return userManager::class.java
                .getMethod(
                    "getUsers",
                    Boolean::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType
                )
                .invoke(
                    userManager,
                    excludePartial,
                    excludeDying,
                    excludePreCreated
                ) as List<UserInfo>
        } catch (e: NoSuchMethodException) {
            Log.w("HiddenUserManager", "Error getting users, falling back to old method.", e)

            return try {
                userManager::class.java
                    .getMethod(
                        "getUsers",
                        Boolean::class.javaPrimitiveType,
                        Boolean::class.javaPrimitiveType
                    )
                    .invoke(userManager, excludePartial, excludeDying) as List<UserInfo>
            } catch (e2: NoSuchMethodException) {
                Log.w("HiddenUserManager", "Error getting users, falling back to old method.", e2)

                userManager::class.java
                    .getMethod("getUsers", Boolean::class.javaPrimitiveType)
                    .invoke(userManager, excludePartial) as List<UserInfo>
            }
        }
    }

    @Throws(RemoteException::class)
    fun getUsers(): List<UserInfo> = getUsers(
        excludePartial = true,
        excludeDying = true,
        excludePreCreated = true
    )

    fun getUserInfo(userId: Int): UserInfo = userManager.getUserInfo(userId)

    fun getUserId(uid: Int): Int = uid / PER_USER_RANGE

    val myUserId get(): Int = getUserId(Process.myUid())

    fun isSameUser(uid1: Int, uid2: Int): Boolean = getUserId(uid1) == getUserId(uid2)

    companion object {
        const val PER_USER_RANGE: Int = 100000
    }
}
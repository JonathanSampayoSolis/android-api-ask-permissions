package com.sampacodes.askpermissions.sys.util

import android.content.pm.PackageManager
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Asking permissions API HERE, implement listener.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
@UiThread
class PermissionHandler(
    private val fragment: Fragment,
    private val listener: Listener
) {

    interface Listener {

        fun onRequestPermissionResolved(requestCode: Int, result: AppPermissionResult)

        fun onRequestPermissionsCancelled(requestCode: Int)

    }

    class AppPermissionResult(
        val granted: List<AppPermission>,
        val denied: List<AppPermission>,
        val deniedNotAskAgain: List<AppPermission>
    )

    fun hasPermission(permission: AppPermission) = ContextCompat.checkSelfPermission(
        fragment.requireContext(), permission.androidPermission
    ) == PackageManager.PERMISSION_GRANTED

    fun hasAllPermissions(permissions: List<AppPermission>) =
        permissions.any { hasPermission(it).not() }.not()

    fun requestPermission(permission: AppPermission, requestCode: Int) {
        fragment.requestPermissions(
            arrayOf(permission.androidPermission),
            requestCode
        )
    }

    fun requestAllPermissions(permissions: List<AppPermission>, requestCode: Int) {
        fragment.requestPermissions(
            permissions.map { it.androidPermission }.toTypedArray(),
            requestCode
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (permissions.isEmpty() || grantResults.isEmpty()) {
            // permissions request cancelled or pop up dismissed
            listener.onRequestPermissionsCancelled(requestCode)
        }

        val granted = ArrayList<AppPermission>()
        val denied = ArrayList<AppPermission>()
        val deniedNotAskAgain = ArrayList<AppPermission>()

        for ((i, permission) in permissions.withIndex()) {
            val appPermission = AppPermission.fromStringPermission(permission)

            when {
                grantResults[i] == PackageManager.PERMISSION_GRANTED -> {
                    // permission granted
                    granted.add(appPermission)
                }
                fragment.shouldShowRequestPermissionRationale(
                    permission
                ) -> {
                    // permission denied
                    denied.add(appPermission)
                }
                else -> {
                    // permission denied with do not ask again
                    deniedNotAskAgain.add(appPermission)
                }
            }
        }

        AppPermissionResult(granted, denied, deniedNotAskAgain).also { result ->
            listener.onRequestPermissionResolved(requestCode, result)
        }
    }

}
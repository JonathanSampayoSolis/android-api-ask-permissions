package com.sampacodes.askpermissions.sys.util

/**
 * File to manipulate and share all the public permissions functions.
 */

fun requestAppLocationPermissions(handler: PermissionHandler, requestCode: Int) {
    val permissions = listOf(
        AppPermission.AccessCoarseLocation,
        AppPermission.AccessFineLocation,
    )

    if (handler.hasAllPermissions(permissions).not()) {
        handler.requestAllPermissions(permissions, requestCode)
    }
}

fun hasAllLocationPermission(handler: PermissionHandler) =
    handler.hasAllPermissions(
        listOf(
            AppPermission.AccessCoarseLocation,
            AppPermission.AccessFineLocation,
        )
    )
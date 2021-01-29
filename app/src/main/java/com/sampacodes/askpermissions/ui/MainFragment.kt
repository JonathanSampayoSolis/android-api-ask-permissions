package com.sampacodes.askpermissions.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.sampacodes.askpermissions.R
import com.sampacodes.askpermissions.sys.util.PermissionHandler
import com.sampacodes.askpermissions.sys.util.hasAllLocationPermission
import com.sampacodes.askpermissions.sys.util.requestAppLocationPermissions

class MainFragment : Fragment(), PermissionHandler.Listener {

    private lateinit var btnRequestPermission: MaterialButton

    private lateinit var permissionHandler: PermissionHandler

    companion object {

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        fun newInstance(): MainFragment {
            val args = Bundle()

            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // var initialization
        permissionHandler = PermissionHandler(this, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // view init
        btnRequestPermission = view.findViewById(R.id.btn_request_permissions)

        btnRequestPermission.setOnClickListener {
            requestAppLocationPermissions(permissionHandler, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasAllLocationPermission(permissionHandler)) {
            changeRequestBtnTitleAndAction()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onRequestPermissionResolved(
        requestCode: Int,
        result: PermissionHandler.AppPermissionResult
    ) {
        if (result.deniedNotAskAgain.isNotEmpty()) {
            // handle don't ask again permissions
            showPermissionsExplanatoryDialog()
        }

        if (result.granted.isNotEmpty()) {
            // handle granted permissions
            changeRequestBtnTitleAndAction()
        }

        if (result.denied.isNotEmpty()) {
            // handle denied permissions
            showMandatoryPermissionsSnackBar()
        }
    }

    // region :: PRIVATE METHODS

    private fun changeRequestBtnTitleAndAction() {
        btnRequestPermission.apply {
            text = getString(R.string.permissions_allowed)
            isEnabled = false
        }
    }

    override fun onRequestPermissionsCancelled(requestCode: Int) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            showMandatoryPermissionsSnackBar()
        }
    }

    /**
     * Shows a [AlertDialog] in cases user marked permissions as "don't ask again"
     */
    private fun showPermissionsExplanatoryDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.advice)
            .setMessage(R.string.the_app_needs_the_location_permissions_allowed_for_continue)
            .setPositiveButton(R.string.allow_permissions) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                }
                startActivity(intent)
            }.create().show()
    }

    /**
     * Shows a [Snackbar] if user don't agree location permissions
     */
    private fun showMandatoryPermissionsSnackBar() {
        Snackbar.make(
            btnRequestPermission.rootView,
            R.string.agree_location_permissions_for_best_app_behavior,
            Snackbar.LENGTH_LONG
        ).show()
    }

    // endregion

}
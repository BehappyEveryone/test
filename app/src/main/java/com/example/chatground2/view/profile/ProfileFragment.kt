package com.example.chatground2.view.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.chatground2.R
import com.example.chatground2.model.RequestCode
import com.example.chatground2.view.login.LoginActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.lang.Exception

class ProfileFragment : Fragment(), View.OnClickListener, ProfileContract.IProfileView {

    private var presenter: ProfilePresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
    }

    private fun initialize() {
        presenter = context?.let { ProfilePresenter(it, this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        return uiInitialize(view)
    }

    private fun uiInitialize(view: View): View {
        view.run {

            P_save.setOnClickListener(this@ProfileFragment)
            P_logout.setOnClickListener(this@ProfileFragment)
            P_profileImage.setOnClickListener(this@ProfileFragment)

            return this
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.P_save -> presenter?.saveProfile(P_introduce.text.toString())
            R.id.P_logout -> presenter?.logoutClick()
            R.id.P_profileImage -> presenter?.profileImageClick()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter?.profileInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter?.closeCursor()
    }

    override fun setProfileImage(path: String?) {
        if (path == null) {
            P_profileImage.setImageDrawable(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.profile_icon
                )
            })
        } else {
            Picasso.get().load(path).into(P_profileImage, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    println("이미지 에러 : $e")
                    P_profileImage.setImageDrawable(
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.noimage
                            )
                        }
                    )
                }
            })
        }
    }

    override fun setEmail(text: String) {
        P_email.text = text
    }

    override fun setIntroduce(text: String) {
        P_introduce.setText(text)
    }

    override fun logoutDialog() {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle(getString(R.string.default_dialog_title))
        builder?.setMessage(getString(R.string.logout_message))
        builder?.setNegativeButton(R.string.default_dialog_cancel, null)
        builder?.setPositiveButton(R.string.default_dialog_confirm) { _, _ ->
            presenter?.logout()
        }
        builder?.show()
    }

    override fun imageDialog() {
        val items = resources.getStringArray(R.array.profile_dialog_items)
        val dialog = context?.let {
            AlertDialog.Builder(
                it,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert
            )
        }
        dialog?.setTitle(getString(R.string.image_dialog_title))
            ?.setItems(items) { _, which ->
                val selected: String = items[which]
                if (selected == resources.getStringArray(R.array.file_dialog_items)[0]) {
                    presenter?.checkCameraPermission()
                }
                if (selected == resources.getStringArray(R.array.file_dialog_items)[1]) {
                    presenter?.defaultImage()
                }
            }
            ?.create()
            ?.show()
    }

    override fun setNickname(text: String) {
        P_nickname.text = text
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            P_progressBar.visibility = View.VISIBLE
        } else {
            P_progressBar.visibility = View.INVISIBLE
        }
    }

    override fun setEnable(boolean: Boolean) {
        P_profileImage.isEnabled = boolean
        P_logout.isEnabled = boolean
        P_save.isEnabled = boolean
    }

    override fun enterLoginActivity() = startActivity(Intent(context, LoginActivity::class.java))

    override fun finishActivity() {
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.OPEN_GALLERY.code -> {
                    presenter?.galleryResult(data)
                }
            }
        } else {
            presenter?.resultCancel()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RequestCode.CAMERA_REQUEST.code -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        presenter?.deniedPermission()
                    }
                    return
                }
            }
        }
    }
}
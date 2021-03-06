package com.example.chatground2.view.chatGround

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatground2.R
import com.example.chatground2.adapter.ChatAdapter
import com.example.chatground2.adapter.ChatUserAdapter
import com.example.chatground2.model.RequestCode
import com.example.chatground2.model.RequestCode.OPEN_GALLERY
import com.example.chatground2.model.RequestCode.OPEN_VIDEO
import kotlinx.android.synthetic.main.activity_chat_ground.*
import kotlinx.android.synthetic.main.menu_chat_drawer.*

class ChatGroundActivity : AppCompatActivity(), ChatGroundContract.IChatGroundView,
    View.OnClickListener {

    private var presenter: ChatGroundPresenter? = null
    private var chatAdapter: ChatAdapter? = null
    private var chatUserAdapter: ChatUserAdapter? = null
    private var chatLm: LinearLayoutManager? = null
    private var userLm: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_ground)

        initialize()
        presenter?.getIntent(intent)
        presenter?.bindService()
    }

    private fun initialize() {
        chatAdapter = ChatAdapter(this)
        chatUserAdapter = ChatUserAdapter(this)
        chatLm = LinearLayoutManager(this)
        userLm = LinearLayoutManager(this)

        presenter = ChatGroundPresenter(this, this).apply {
            adapterChatModel = chatAdapter
            adapterChatView = chatAdapter
            adapterChatUserModel = chatUserAdapter
            adapterChatUserView = chatUserAdapter
        }

        CG_chatRecycle.run {
            layoutManager = chatLm
            adapter = chatAdapter
            setHasFixedSize(true)
        }

        CD_menuRecycle.run {
            layoutManager = userLm
            adapter = chatUserAdapter
        }

        CG_drawerButton.setOnClickListener(this)
        CG_agree.setOnClickListener(this)
        CG_oppose.setOnClickListener(this)
        CG_sendButton.setOnClickListener(this)
        CG_plus.setOnClickListener(this)
        CD_exit.setOnClickListener(this)
        CD_exitText.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.CG_drawerButton -> presenter?.drawerClick()
            R.id.CG_sendButton -> presenter?.sendMessage(CG_message.text.toString())
            R.id.CG_agree -> presenter?.setOpinion(true,CG_agree.isSelected,CG_oppose.isSelected)
            R.id.CG_oppose -> presenter?.setOpinion(false,CG_agree.isSelected,CG_oppose.isSelected)
            R.id.CG_plus -> presenter?.plusClick()
            R.id.CD_exit -> presenter?.leaveDialog()
            R.id.CD_exitText -> presenter?.leaveDialog()
        }
    }

    override fun onBackPressed() {
        presenter?.leaveDialog()
    }

    override fun setMessageClear() {
        CG_message.setText("")
    }

    override fun onResume() {
        super.onResume()

        presenter?.getMessages()
        presenter?.setBroadCastReceiver()
    }

    override fun onPause() {
        super.onPause()

        presenter?.removeBroadCastReceiver()
        presenter?.removeMessages()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter?.closeCursor()
        presenter?.unbindService()
    }

    override fun openDrawer() {
        CG_drawer.openDrawer(GravityCompat.END)
    }

    override fun setChatScrollPosition(position: Int) {
        CG_chatRecycle.scrollToPosition(position)
    }

    override fun setEnable(boolean: Boolean) {
        CG_message.isEnabled = boolean
        CG_plus.isEnabled = boolean
        CG_sendButton.isEnabled = boolean
    }

    override fun setSubjectText(text: String) {
        CG_subject.text = text
    }

    override fun setAgreeButtonSelected(boolean: Boolean) {
        CG_agree.isSelected = boolean
    }

    override fun setOpposeButtonSelected(boolean: Boolean) {
        CG_oppose.isSelected = boolean
    }

    override fun setTimeText(text: String) {
        CG_time.text = text
    }

    override fun setOpinionVisible(boolean: Boolean) {
        if (boolean) {
            CG_opinion.visibility = View.VISIBLE
        } else {
            CG_opinion.visibility = View.GONE
        }
    }

    override fun finishActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun setResultText(text: String) {
        CG_result.visibility = View.VISIBLE
        CG_result.text = text
    }

    override fun plusDialog() {
        val items = resources.getStringArray(R.array.file_dialog_items)
        val dialog =
            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
        dialog.setTitle(getString(R.string.plus_dialog_title))
            .setItems(items) { _, which ->
                val selected: String = items[which]
                if (selected == resources.getStringArray(R.array.file_dialog_items)[0]) {
                    presenter?.checkCameraPermission(0)
                }
                if (selected == resources.getStringArray(R.array.file_dialog_items)[1]) {
                    presenter?.checkCameraPermission(1)
                }
            }
            .create()
            .show()
    }

    override fun leaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.default_dialog_title))
        builder.setMessage(getString(R.string.chat_ground_exit_text))
        builder.setNegativeButton(R.string.default_dialog_cancel, null)
        builder.setPositiveButton(R.string.default_dialog_confirm) { _, _ ->
            presenter?.leave()
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OPEN_GALLERY.code -> {
                    presenter?.imageGalleryResult(data)
                }
                OPEN_VIDEO.code -> {
                    presenter?.videoGalleryResult(data)
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
package com.example.chatground2.view.chatGround

import android.app.Activity
import android.content.Intent
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
import com.example.chatground2.model.Constants.OPEN_GALLERY
import com.example.chatground2.model.Constants.OPEN_VIDEO
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
            R.id.CG_agree -> presenter?.setOpinion(true)
            R.id.CG_oppose -> presenter?.setOpinion(false)
            R.id.CG_plus -> presenter?.plusClick()
            R.id.CD_exit -> presenter?.leaveDialog()
            R.id.CD_exitText -> presenter?.leaveDialog()
        }
    }

    override fun onBackPressed() {
        presenter?.leaveDialog()
    }

    override fun toastMessage(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

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

    override fun getAgreeButtonSelected(): Boolean = CG_agree.isSelected

    override fun setOpposeButtonSelected(boolean: Boolean) {
        CG_oppose.isSelected = boolean
    }

    override fun getOpposeButtonSelected(): Boolean = CG_oppose.isSelected

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
        val items = arrayOf("이미지", "동영상")
        val dialog =
            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
        dialog.setTitle("파일 전송")
            .setItems(items) { _, which ->
                val selected: String = items[which]
                if (selected == "이미지") {
                    presenter?.checkCameraPermission(0)
                }
                if (selected == "동영상") {
                    presenter?.checkCameraPermission(1)
                }
            }
            .create()
            .show()
    }

    override fun leaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("게임을 나가시겠습니까?")
        builder.setNegativeButton("아니오", null)
        builder.setPositiveButton("네") { _, _ ->
            presenter?.leave()
        }
        builder.show()
    }

    override fun openGallery() {
        val uri: Uri = Uri.parse("content://media/external/images/media")
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun openVideo() {
        val uri: Uri = Uri.parse("content://media/external/images/media")
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "video/*"
        startActivityForResult(intent, OPEN_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OPEN_GALLERY -> {
                    presenter?.imageGalleryResult(data)
                }
                OPEN_VIDEO -> {
                    presenter?.videoGalleryResult(data)
                }
            }
        } else {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
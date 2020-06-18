package com.example.chatground2.view.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chatground2.api.IpAddress
import com.example.chatground2.model.Constants
import com.example.chatground2.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_comment_modify.*
import java.io.File

class CommentModifyActivity : Activity(), CommentModifyContract.ICommentModifyView,
    View.OnClickListener {

    private var presenter: CommentModifyPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Make us non-modal, so that others can receive touch events.
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.dialog_comment_modify)


        initialize()
        presenter?.getIntent(intent)
    }

    private fun initialize() {
        presenter = CommentModifyPresenter(this, this)

        CM_camera.setOnClickListener(this)
        CM_modify.setOnClickListener(this)
        CM_cancel.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter?.closeCursor()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.CM_camera -> presenter?.cameraClick()
            R.id.CM_modify -> presenter?.modifyComment()
            R.id.CM_cancel -> presenter?.cancel()
        }
    }

    override fun deleteCommentImageDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("이미지를 지우시겠습니까?")
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("삭제") { _, _ ->
            presenter?.deleteImage()
        }
        builder.show()
    }

    override fun setContentText(text: String) {
        CM_content.setText(text)
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            CM_progressbar.visibility = View.VISIBLE
        } else {
            CM_progressbar.visibility = View.INVISIBLE
        }
    }

    override fun toastMessage(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun finishActivity() {
        setResult(RESULT_OK)
        finish()
    }

    override fun setEnable(boolean: Boolean) {
        CM_cancel.isEnabled = boolean
        CM_camera.isEnabled = boolean
        CM_modify.isEnabled = boolean
    }

    override fun setCameraImage(path:String?) {
        if(path.isNullOrEmpty())
        {
            CM_camera.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.write_forum_camera_icon
                )
            )
        }else
        {
            println("path is not null")

            if (path.substring(0, 11) == "forumImages") {
                Picasso.get().load(IpAddress.BaseURL + path).into(CM_camera)
            } else {
                Picasso.get().load(File(path)).into(CM_camera)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.OPEN_GALLERY -> {
                    presenter?.galleryResult(data)
                }
            }
        } else {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
        }
    }

    override fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, Constants.OPEN_GALLERY)
    }
}
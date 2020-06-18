package com.example.chatground2.view.detailForum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatground2.model.Constants
import com.example.chatground2.R
import com.example.chatground2.adapter.CommentsAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_forum.*
import kotlinx.android.synthetic.main.activity_detail_forum.DF_camera
import kotlinx.android.synthetic.main.activity_detail_forum.DF_deleteButton
import kotlinx.android.synthetic.main.activity_write_forum.backButton

class DetailForumActivity : AppCompatActivity(), DetailForumContract.IDetailForumView,
    View.OnClickListener {

    private var presenter: DetailForumPresenter? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var lm: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_forum)

        initialize()
        presenter?.detailForum()
    }

    private fun initialize() {
        commentsAdapter = CommentsAdapter(this)
        lm = LinearLayoutManager(this)

        presenter = DetailForumPresenter(this, this).apply {
            this.idx = intent.getIntExtra("idx", -1)
            adapterModel = commentsAdapter
            adapterView = commentsAdapter
        }

        DF_commentRecycle.run {
            layoutManager = lm
            adapter = commentsAdapter
        }

        backButton.setOnClickListener(this)
        DF_camera.setOnClickListener(this)
        DF_deleteButton.setOnClickListener(this)
        DF_commentSend.setOnClickListener(this)
        DF_modifyButton.setOnClickListener(this)
        DF_recommend.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter?.closeCursor()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.backButton -> onBackPressed()
            R.id.DF_camera -> presenter?.onCameraClick()
            R.id.DF_commentSend -> presenter?.onCommentSendClick()
            R.id.DF_deleteButton -> deleteForumDialog()
            R.id.DF_modifyButton -> presenter?.modifyForum()
            R.id.DF_recommend -> presenter?.onRecommendClick()
        }
    }

    override fun getCommentMessageText(): String = DF_commentMessage.text.toString()

    override fun setDateText(text: String) {
        DF_date.text = text
    }

    override fun setSubjectText(text: String) {
        DF_subject.text = text
    }

    override fun setTitleText(text: String) {
        DF_title.text = text
    }

    override fun setContentText(text: String) {
        DF_content.text = text
    }

    override fun setProfileImage(path: String) {
        Picasso.get().load(path).into(DF_profile)
    }

    override fun setNicknameText(text: String) {
        DF_nickname.text = text
    }

    override fun setCommentNumText(text: String) {
        DF_commentNum.text = text
    }

    override fun setRecommendText(text: String) {
        DF_recommendNum.text = text
    }

    override fun setRecommendButtonText(text: String) {
        DF_recommend.text = text
    }

    override fun setRecommendButtonBackground(int: Int) {
        DF_recommend.background = ContextCompat.getDrawable(this,int)
    }

    override fun setImage0(path: String) {
        DF_image0.visibility = View.VISIBLE
        Picasso.get().load(path).into(DF_image0)
    }

    override fun setImage1(path: String) {
        DF_image1.visibility = View.VISIBLE
        Picasso.get().load(path).into(DF_image1)
    }

    override fun setImage2(path: String) {
        DF_image2.visibility = View.VISIBLE
        Picasso.get().load(path).into(DF_image2)
    }

    override fun setImage3(path: String) {
        DF_image3.visibility = View.VISIBLE
        Picasso.get().load(path).into(DF_image3)
    }

    override fun setImage4(path: String) {
        DF_image4.visibility = View.VISIBLE
        Picasso.get().load(path).into(DF_image4)
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            DF_progressbar.visibility = View.VISIBLE
        } else {
            DF_progressbar.visibility = View.INVISIBLE
        }
    }

    override fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, Constants.OPEN_GALLERY)
    }

    override fun createCommentImageDialog() {
        val items = arrayOf("이미지")
        val dialog =
            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
        dialog.setTitle("이미지 첨부")
            .setItems(items) { _, which ->
                val selected: String = items[which]
                if (selected == "이미지") {
                    presenter?.checkCameraPermission()
                }
            }
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.OPEN_GALLERY -> {
                    presenter?.galleryResult(data)
                }
                Constants.MODIFY_FORUM -> {
                    presenter?.detailForum()
                }
            }
        } else {
            toastMessage("취소 되었습니다.")
        }
    }

    override fun setCameraImage(path: String?) {
        if (path.isNullOrEmpty()) {
            DF_camera.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.write_forum_camera_icon
                )
            )
//            Picasso.get().load(R.drawable.write_forum_camera_icon).fit().into(DF_camera)
        } else {
            setImageBitmap(DF_camera, path)
//            Picasso.get().load(path).into(DF_camera)
        }
    }

    private fun setImageBitmap(imageButton: ImageButton, path: String) {
        val bitmap: Bitmap = BitmapFactory.decodeFile(path)
        imageButton.setImageBitmap(bitmap)
    }

    override fun setDeleteForumVisible(boolean: Boolean) {
        if (boolean) {
            DF_deleteButton.visibility = View.VISIBLE
        } else {
            DF_deleteButton.visibility = View.GONE
        }
    }

    override fun setModifyForumVisible(boolean: Boolean) {
        if (boolean) {
            DF_modifyButton.visibility = View.VISIBLE
        } else {
            DF_modifyButton.visibility = View.GONE
        }
    }

    override fun setCommentMessageText(text: String) = DF_commentMessage.setText(text)

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

    override fun modifyCommentDialog() {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        toastMessage("권한이 거부되었습니다.")
                    }
                    return
                }
            }
        }
    }

    override fun enterModifyForum(intent: Intent) {
        startActivityForResult(intent, Constants.MODIFY_FORUM)
    }

    override fun enterModifyComment(intent: Intent) {
        startActivityForResult(intent, Constants.MODIFY_COMMENT)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun finishActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun toastMessage(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    override fun recommendDialog(boolean: Boolean) {
        val message: String = if (boolean) {
            "추천을 취소하시겠습니까?"
        } else {
            "해당 게시글을 추천 하시겠습니까?"
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage(message)
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("확인") { _, _ ->
            presenter?.recommendForum()
        }
        builder.show()
    }

    override fun deleteForumDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("해당 글을 삭제하시겠습니까?")
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("확인") { _, _ ->
            presenter?.deleteForum()
        }
        builder.show()
    }

    override fun deleteCommentDialog(position:Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("해당 댓글을 삭제하시겠습니까?")
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("확인") { _, _ ->
            presenter?.deleteComment(position)
        }
        builder.show()
    }

    override fun setEnable(boolean: Boolean) {
        DF_camera.isEnabled = boolean
        DF_deleteButton.isEnabled = boolean
        DF_commentSend.isEnabled = boolean
        DF_modifyButton.isEnabled = boolean
        DF_recommend.isEnabled = boolean
        backButton.isEnabled = boolean
    }
}
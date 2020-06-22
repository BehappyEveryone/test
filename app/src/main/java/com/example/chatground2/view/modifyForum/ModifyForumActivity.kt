package com.example.chatground2.view.modifyForum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chatground2.api.IpAddress
import com.example.chatground2.model.RequestCode
import com.example.chatground2.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_modify_forum.*
import kotlinx.android.synthetic.main.activity_write_forum.backButton
import java.io.File

class ModifyForumActivity : AppCompatActivity(), ModifyForumContract.IModifyForumView,
    View.OnClickListener {

    private var presenter: ModifyForumPresenter? = null
    private var arrayAdapter: ArrayAdapter<CharSequence>? = null
    private var imageViewList: Array<ImageButton>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_forum)

        initialize()
        presenter?.getIntent(intent)
    }

    private fun initialize() {
        presenter = ModifyForumPresenter(this, this)

        arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.interest,
            android.R.layout.simple_spinner_item
        )
        MF_subject.adapter = arrayAdapter

        MF_saveButton.setOnClickListener(this)
        MF_cameraButton.setOnClickListener(this)
        MF_showImage1.setOnClickListener(this)
        MF_showImage2.setOnClickListener(this)
        MF_showImage3.setOnClickListener(this)
        MF_showImage4.setOnClickListener(this)
        MF_showImage5.setOnClickListener(this)
        backButton.setOnClickListener(this)

        imageViewList = arrayOf(MF_showImage1,MF_showImage2,MF_showImage3,MF_showImage4,MF_showImage5)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.MF_saveButton -> modifyForumDialog()
            R.id.MF_cameraButton -> presenter?.cameraClick()
            R.id.MF_showImage1 -> presenter?.showImageClick(1)
            R.id.MF_showImage2 -> presenter?.showImageClick(2)
            R.id.MF_showImage3 -> presenter?.showImageClick(3)
            R.id.MF_showImage4 -> presenter?.showImageClick(4)
            R.id.MF_showImage5 -> presenter?.showImageClick(5)
            R.id.backButton -> onBackPressed()
        }
    }

    override fun createShowImageDialog(imageNum: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("이미지를 지우시겠습니까?")
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("삭제") { _, _ ->
            presenter?.deleteImage(imageNum)
        }
        builder.show()
    }

    override fun createDialog() {
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

    override fun setImage(imagePathList: ArrayList<String>) {
        var extra:Int = -1
        imagePathList.forEachIndexed {index: Int, s: String ->
            extra = index
            imageViewList?.get(index)?.let {
                it.visibility = View.VISIBLE
                setServerImage(it,s)
            }
        }

        for(i in extra+1 until 5){
            imageViewList?.get(i)?.visibility = View.INVISIBLE
        }
    }

    private fun setServerImage(imageButton: ImageButton, path: String) {

        imageButton.visibility = View.VISIBLE
        if (path.substring(0, 11) == "forumImages") {
            Picasso.get().load(IpAddress.BaseURL + path).into(imageButton)
        } else {
            Picasso.get().load(File(path)).into(imageButton)
        }
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            MF_progressbar.visibility = View.VISIBLE
        } else {
            MF_progressbar.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun finishActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun toastMessage(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    override fun getSelectSubject(): String = MF_subject.selectedItem.toString()
    override fun isTitleEmpty(): Boolean = MF_title.text.isNullOrEmpty()
    override fun isContentEmpty(): Boolean = MF_content.text.isNullOrEmpty()
    override fun getContentText(): String = MF_content.text.toString()
    override fun getTitleText(): String = MF_title.text.toString()

    override fun setDefault(subject: String, title: String, content: String) {
        arrayAdapter?.let {
            MF_subject.setSelection(it.getPosition(subject))
        }
        MF_title.setText(title)
        MF_content.setText(content)
    }

    override fun openGallery() {
        val uri: Uri = Uri.parse("content://media/external/images/media")
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, RequestCode.OPEN_GALLERY.code)
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
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
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
                        toastMessage("권한이 거부되었습니다.")
                    }
                    return
                }
            }
        }
    }

    override fun setEnable(boolean: Boolean) {
        MF_saveButton.isEnabled = boolean
        MF_cameraButton.isEnabled = boolean
        MF_showImage1.isEnabled = boolean
        MF_showImage2.isEnabled = boolean
        MF_showImage3.isEnabled = boolean
        MF_showImage4.isEnabled = boolean
        MF_showImage5.isEnabled = boolean
    }

    private fun modifyForumDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("해당 글을 수정하시겠습니까?")
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("확인") { _, _ ->
            presenter?.saveClick()
        }
        builder.show()
    }
}

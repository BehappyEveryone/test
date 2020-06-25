package com.example.chatground2.view.writeForum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chatground2.model.RequestCode.OPEN_GALLERY
import com.example.chatground2.R
import com.example.chatground2.model.RequestCode
import kotlinx.android.synthetic.main.activity_modify_forum.*
import kotlinx.android.synthetic.main.activity_write_forum.*
import kotlinx.android.synthetic.main.activity_write_forum.backButton

class WriteForumActivity : AppCompatActivity(), WriteForumContract.IWriteForumView,
    View.OnClickListener {

    private var presenter: WriteForumPresenter? = null
    private var arrayAdapter: ArrayAdapter<CharSequence>? = null
    private var imageViewList: Array<ImageButton>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_forum)

        initialize()
    }

    private fun initialize() {
        presenter = WriteForumPresenter(this, this)

        arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.interest,
            android.R.layout.simple_spinner_item
        )
        WF_subject.adapter = arrayAdapter

        WF_saveButton.setOnClickListener(this)
        WF_cameraButton.setOnClickListener(this)
        WF_showImage1.setOnClickListener(this)
        WF_showImage2.setOnClickListener(this)
        WF_showImage3.setOnClickListener(this)
        WF_showImage4.setOnClickListener(this)
        WF_showImage5.setOnClickListener(this)
        backButton.setOnClickListener(this)

        imageViewList = arrayOf(WF_showImage1,WF_showImage2,WF_showImage3,WF_showImage4,WF_showImage5)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.WF_saveButton -> presenter?.saveClick(
                WF_title.text.isNullOrEmpty(),
                WF_content.text.isNullOrEmpty(),
                WF_subject.selectedItem.toString(),
                WF_title.text.toString(),
                WF_content.text.toString()
            )
            R.id.WF_cameraButton -> presenter?.cameraClick()
            R.id.WF_showImage1 -> presenter?.showImageClick(1)
            R.id.WF_showImage2 -> presenter?.showImageClick(2)
            R.id.WF_showImage3 -> presenter?.showImageClick(3)
            R.id.WF_showImage4 -> presenter?.showImageClick(4)
            R.id.WF_showImage5 -> presenter?.showImageClick(5)
            R.id.backButton -> onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter?.closeCursor()
    }

    override fun createShowImageDialog(imageNum: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage(getString(R.string.image_delete_message))
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

    override fun finishActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OPEN_GALLERY.code -> {
                    presenter?.galleryResult(data)
                }
            }
        } else {
            presenter?.resultCancel()
        }
    }

    override fun setImage(imagePathList: ArrayList<String>) {

        var extra:Int = -1
        imagePathList.forEachIndexed {index: Int, s: String ->
            extra = index
            imageViewList?.get(index)?.visibility = View.VISIBLE
            imageViewList?.get(index)?.let { setImageBitmap(it,s) }
        }

        for(i in extra+1 until 5){
            imageViewList?.get(i)?.visibility = View.INVISIBLE
        }
    }

    private fun setImageBitmap(imageButton: ImageButton, path: String) {
        imageButton.visibility = View.VISIBLE
        val bitmap: Bitmap = BitmapFactory.decodeFile(path)
        imageButton.setImageBitmap(bitmap)
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            WF_progressbar.visibility = View.VISIBLE
        } else {
            WF_progressbar.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
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

    override fun setEnable(boolean: Boolean) {
        WF_saveButton.isEnabled = boolean
        WF_cameraButton.isEnabled = boolean
        WF_showImage1.isEnabled = boolean
        WF_showImage2.isEnabled = boolean
        WF_showImage3.isEnabled = boolean
        WF_showImage4.isEnabled = boolean
        WF_showImage5.isEnabled = boolean
    }
}
package com.example.chatground2.view.writeForum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chatground2.model.Constants.OPEN_GALLERY
import com.example.chatground2.R
import kotlinx.android.synthetic.main.activity_write_forum.*

class WriteForumActivity : AppCompatActivity(), WriteForumContract.IWriteForumView,
    View.OnClickListener {

    private var presenter: WriteForumPresenter? = null
    private var arrayAdapter: ArrayAdapter<CharSequence>? = null

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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.WF_saveButton -> presenter?.saveClick()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OPEN_GALLERY -> {
                    presenter?.galleryResult(data)
                }
            }
        } else {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
        }
    }

    override fun setImage(imagePathList: ArrayList<String>) {
        when {
            imagePathList.size == 0 -> {
                WF_showImage1.visibility = View.INVISIBLE
                WF_showImage2.visibility = View.INVISIBLE
                WF_showImage3.visibility = View.INVISIBLE
                WF_showImage4.visibility = View.INVISIBLE
                WF_showImage5.visibility = View.INVISIBLE
            }
            imagePathList.size == 1 -> {
                setImageBitmap(WF_showImage1, imagePathList[0])
                WF_showImage2.visibility = View.INVISIBLE
                WF_showImage3.visibility = View.INVISIBLE
                WF_showImage4.visibility = View.INVISIBLE
                WF_showImage5.visibility = View.INVISIBLE
            }
            imagePathList.size == 2 -> {
                setImageBitmap(WF_showImage1, imagePathList[0])
                setImageBitmap(WF_showImage2, imagePathList[1])
                WF_showImage3.visibility = View.INVISIBLE
                WF_showImage4.visibility = View.INVISIBLE
                WF_showImage5.visibility = View.INVISIBLE
            }
            imagePathList.size == 3 -> {
                setImageBitmap(WF_showImage1, imagePathList[0])
                setImageBitmap(WF_showImage2, imagePathList[1])
                setImageBitmap(WF_showImage3, imagePathList[2])
                WF_showImage4.visibility = View.INVISIBLE
                WF_showImage5.visibility = View.INVISIBLE
            }
            imagePathList.size == 4 -> {
                setImageBitmap(WF_showImage1, imagePathList[0])
                setImageBitmap(WF_showImage2, imagePathList[1])
                setImageBitmap(WF_showImage3, imagePathList[2])
                setImageBitmap(WF_showImage4, imagePathList[3])
                WF_showImage5.visibility = View.INVISIBLE
            }
            imagePathList.size == 5 -> {
                setImageBitmap(WF_showImage1, imagePathList[0])
                setImageBitmap(WF_showImage2, imagePathList[1])
                setImageBitmap(WF_showImage3, imagePathList[2])
                setImageBitmap(WF_showImage4, imagePathList[3])
                setImageBitmap(WF_showImage5, imagePathList[4])
            }
        }
    }

    private fun setImageBitmap(imageButton: ImageButton, path: String) {
        imageButton.visibility = View.VISIBLE
        val bitmap: Bitmap = BitmapFactory.decodeFile(path)
        imageButton.setImageBitmap(bitmap)
    }

    override fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, OPEN_GALLERY)
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

    override fun finishActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun toastMessage(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

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

    override fun getSelectSubject(): String = WF_subject.selectedItem.toString()
    override fun isTitleEmpty(): Boolean = WF_title.text.isNullOrEmpty()
    override fun isContentEmpty(): Boolean = WF_content.text.isNullOrEmpty()
    override fun getContentText():String = WF_content.text.toString()
    override fun getTitleText():String = WF_title.text.toString()

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
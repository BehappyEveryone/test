package com.example.chatground2.`class`

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.example.chatground2.R
import com.example.chatground2.model.KeyName.forumImageServerPath
import com.example.chatground2.model.Paths.mediaPath
import com.example.chatground2.model.RequestCode

class Gallery(val context: Context) {

    private var c: Cursor? = null

    fun getPath(uri: Uri): String? {
        val idx: String = DocumentsContract.getDocumentId(uri).split(":")[1]
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val selection: String = MediaStore.Files.FileColumns._ID + " = " + idx
        c = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            proj,
            selection,
            null,
            null
        )
        val index = c?.getColumnIndexOrThrow(proj[0])
        c?.moveToFirst()
        return index?.let { c?.getString(it) }
    }

    fun closeCursor() {
        if (c != null) {
            c?.close()
        }
    }

    fun isExistFile(path: String): Boolean = path.substring(0, 11) == forumImageServerPath


    fun openGallery() {
        val uri: Uri = Uri.parse(mediaPath)
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        (context as Activity).startActivityForResult(intent, RequestCode.OPEN_GALLERY.code)
    }

    fun openVideo() {
        val uri: Uri = Uri.parse(mediaPath)
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "video/*"
        (context as Activity).startActivityForResult(intent, RequestCode.OPEN_VIDEO.code)
    }
}
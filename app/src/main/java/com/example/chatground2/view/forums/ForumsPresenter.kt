package com.example.chatground2.view.forums

import android.content.Context
import com.example.chatground2.model.dto.ForumDto
import com.example.chatground2.R
import com.example.chatground2.`class`.ToastMessage
import com.example.chatground2.adapter.adapterContract.ForumsAdapterContract
import com.example.chatground2.model.KeyName.bestText
import com.example.chatground2.model.KeyName.keywordText
import com.example.chatground2.model.KeyName.kindText
import com.example.chatground2.model.KeyName.pageText
import com.example.chatground2.model.KeyName.searchText
import kotlin.collections.ArrayList

class ForumsPresenter(val context: Context, val view: ForumsContract.IForumsView) :
    ForumsContract.IForumsPresenter, ForumsContract.CallBack {

    private var model: ForumsModel = ForumsModel(context)
    private var toastMessage:ToastMessage = ToastMessage(context)

    private var isBestForum = false
    private var isSearch = false
    private var pageNum = 0
    private var isRefresh = false//콜백받을 때 clear 유무

    override var adapterModel: ForumsAdapterContract.Model? = null
    override var adapterView: ForumsAdapterContract.View? = null
        set(value) {//커스텀 접근자
            field = value
            field?.onClickFunc = { clickForum(it) }
        }

    override fun isSearching():Boolean = isSearch

    override fun callForums() {
        view.isLoading(false)
        view.progressVisible(true)
        pageNum++

        val hashMap = HashMap<String, Any>()
        hashMap[pageText] = pageNum
        hashMap[bestText] = isBestForum
        hashMap[searchText] = false

        model.callForums(hashMap, this)
    }

    override fun callForums(kind:String, keyword: String) {
        view.isLoading(false)
        view.progressVisible(true)
        pageNum++

        val hashMap = HashMap<String, Any>()
        hashMap[pageText] = pageNum
        hashMap[bestText] = isBestForum
        hashMap[searchText] = true
        hashMap[kindText] = kind
        hashMap[keywordText] = keyword

        model.callForums(hashMap, this)
    }

    override fun refresh() {
        pageNum = 0
        isRefresh = true
        view.isRefreshing(true)
        callForums()
    }

    override fun writeClick() {
        view.enterWriteForum()
    }

    override fun bestForumsClick() {
        if (isBestForum) {
            isBestForum = false
            view.setBestForumBackground(R.drawable.forums_bestforums_icon)
        } else {
            isBestForum = true
            view.setBestForumBackground(R.drawable.forumsitem_recommend)
        }
        pageNum = 0
        isRefresh = true
        callForums()
    }

    override fun searchClick() {
        if (isSearch) {
            isSearch = false
            view.searchVisible(isSearch)
            view.setSearchBackground(R.drawable.forums_search_icon)
        } else {
            isSearch = true
            view.searchVisible(isSearch)
            view.setSearchBackground(R.drawable.forums_search_red_icon)
        }
    }

    override fun searching(kind:String,keyword: String) {
        pageNum = 0
        isRefresh = true
        callForums(kind,keyword)
    }

    private fun clickForum(position: Int) {
        view.enterDetailForum(adapterModel?.getItem(position)?.idx)
    }

    override fun resultCancel() {
        toastMessage.resultCancel()
    }

    override fun onCallForumsSuccess(result: ArrayList<ForumDto>?) {
        view.progressVisible(false)
        view.isRefreshing(false)
        if (isRefresh) {
            adapterModel?.clearItems()
            isRefresh = false
        }

        result?.let {
            view.isLoading(true)
            adapterModel?.addItems(it)
            adapterView?.notifyAdapter()
        }
    }

    override fun onCallForumsFailure() {
        view.progressVisible(false)
        view.isRefreshing(false)
        toastMessage.forumLast()
    }

    override fun onError(t:Throwable) {
        t.printStackTrace()
        view.progressVisible(false)
        view.isRefreshing(false)
        toastMessage.retrofitError()
    }
}
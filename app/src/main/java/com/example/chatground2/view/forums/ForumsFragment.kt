package com.example.chatground2.view.forums

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chatground2.model.RequestCode.DETAIL_FORUM
import com.example.chatground2.model.RequestCode.WRITE_FORUM
import com.example.chatground2.R
import com.example.chatground2.adapter.ForumsAdapter
import com.example.chatground2.view.detailForum.DetailForumActivity
import com.example.chatground2.view.writeForum.WriteForumActivity
import kotlinx.android.synthetic.main.fragment_forums.*
import kotlinx.android.synthetic.main.fragment_forums.view.*


class ForumsFragment : Fragment(), View.OnClickListener, ForumsContract.IForumsView,
    SwipeRefreshLayout.OnRefreshListener {

    private var presenter: ForumsPresenter? = null
    private var forumsAdapter: ForumsAdapter? = null
    private var lm: LinearLayoutManager? = null
    var visibleItemCounter = 0
    var totalItemCount = 0
    var firstVisibleItemPosition = 0

    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
    }

    private fun initialize() {
        forumsAdapter = context?.let { ForumsAdapter(it) }
        lm = LinearLayoutManager(context)

        presenter = context?.let {
            ForumsPresenter(it, this).apply {
                adapterModel = forumsAdapter
                adapterView = forumsAdapter
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_forums, container, false)

        return uiInitialize(view)
    }

    private fun uiInitialize(view: View): View {
        view.run {

            F_swipeRefresh.setOnRefreshListener(this@ForumsFragment)
            F_writeButton.setOnClickListener(this@ForumsFragment)
            F_searchButton.setOnClickListener(this@ForumsFragment)
            F_bestForumsButton.setOnClickListener(this@ForumsFragment)

            F_forumRecycle.run {
                layoutManager = lm
                setHasFixedSize(true)
                adapter = forumsAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0) {
                            visibleItemCounter = lm!!.childCount
                            totalItemCount = lm!!.itemCount
                            firstVisibleItemPosition =
                                (this@run.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                            if (isLoading) {
                                if ((visibleItemCounter + firstVisibleItemPosition) >= totalItemCount) {
                                    isLoading = false
                                    presenter?.let {
                                        if (it.isSearching()) {
                                            presenter?.callForums(
                                                this@ForumsFragment.F_searchSpinner.selectedItem.toString(),
                                                this@ForumsFragment.F_searchEdit.text.toString()
                                            )
                                        } else {
                                            presenter?.callForums()
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
            }

            F_searchEdit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    presenter?.searching(F_searchSpinner.selectedItem.toString(), s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            return this
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter?.callForums()
    }

    override fun isLoading(boolean: Boolean) {
        isLoading = boolean
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.F_writeButton -> presenter?.writeClick()
            R.id.F_searchButton -> presenter?.searchClick()
            R.id.F_bestForumsButton -> presenter?.bestForumsClick()
        }
    }

    override fun searchVisible(boolean: Boolean) {
        if (boolean) {
            F_root.visibility = View.VISIBLE
        } else {
            F_root.visibility = View.GONE
        }
    }

    override fun setSearchBackground(int: Int) {
        F_searchButton.background = context?.let { ContextCompat.getDrawable(it, int) }
    }

    override fun onRefresh() {
        presenter?.refresh()
    }

    override fun isRefreshing(boolean: Boolean) {
        F_swipeRefresh.isRefreshing = boolean
    }

    override fun finishActivity() {
        activity?.finish()
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            F_progressBar.visibility = View.VISIBLE
        } else {
            F_progressBar.visibility = View.INVISIBLE
        }
    }

    override fun enterWriteForum() {
        startActivityForResult(Intent(context, WriteForumActivity::class.java), WRITE_FORUM.code)
    }

    override fun enterDetailForum(idx: Int?) {
        idx?.let {
            startActivityForResult(
                Intent(context, DetailForumActivity::class.java).putExtra(
                    "idx",
                    it
                ), DETAIL_FORUM.code
            )
        }
    }

    override fun setBestForumBackground(int: Int) {
        F_bestForumsButton.background = context?.let { ContextCompat.getDrawable(it, int) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                WRITE_FORUM.code -> {
                    presenter?.refresh()
                }
                DETAIL_FORUM.code -> {
                    presenter?.refresh()
                }
            }
        } else {
            presenter?.resultCancel()
        }
    }
}
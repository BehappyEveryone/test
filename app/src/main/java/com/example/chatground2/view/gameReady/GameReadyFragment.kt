package com.example.chatground2.view.gameReady

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.example.chatground2.R
import kotlinx.android.synthetic.main.fragment_game_ready.*
import kotlinx.android.synthetic.main.fragment_game_ready.view.*

class GameReadyFragment : Fragment(), View.OnClickListener, GameReadyContract.IGameReadyView {

    private var presenter: GameReadyPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
        presenter?.bindService()
    }

    private fun initialize() {
        presenter = context?.let { GameReadyPresenter(it, this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_game_ready, container, false)

        return uiInitialize(view)
    }

    private fun uiInitialize(view: View): View {
        view.run {

            GR_ready.setOnClickListener(this@GameReadyFragment)

            return this
        }
    }

    override fun onResume() {
        super.onResume()

        presenter?.setBroadCastReceiver()
    }

    override fun onPause() {
        super.onPause()

        presenter?.removeBroadCastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter?.unbindService()
    }

    override fun setMatching() {
        GR_ready.background = ContextCompat.getDrawable(activity!!, R.drawable.button4)
        GR_ready.text = "STOP"
    }

    override fun setReady() {
        GR_ready.background = ContextCompat.getDrawable(activity!!, R.drawable.button3)
        GR_ready.text = "READY"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.GR_ready -> {
                if (presenter?.isSocketConnect()!!) {
                    presenter?.disconnectSocket()
                } else {
                    presenter?.connectSocket()
                }
            }
        }
    }
}
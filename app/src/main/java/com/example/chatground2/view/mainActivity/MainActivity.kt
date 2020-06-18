package com.example.chatground2.view.mainActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatground2.R
import kotlinx.android.synthetic.main.activity_main.*
import androidx.fragment.app.Fragment
import com.example.chatground2.view.forums.ForumsFragment
import com.example.chatground2.view.gameReady.GameReadyFragment
import com.example.chatground2.view.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(),MainContract.IMainView {

    private var presenter: MainPresenter? = null

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener {
        var selectedFragment: Fragment? = null

        when (it.itemId) {
            R.id.navigation_game -> selectedFragment = GameReadyFragment()
            R.id.navigation_forums -> selectedFragment = ForumsFragment()
            R.id.navigation_profile -> selectedFragment = ProfileFragment()
        }

        selectedFragment?.let { it1 ->
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                it1
            ).commit()
        }

        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                GameReadyFragment()
            ).commit()
        }
        initialize()
    }

    private fun initialize() {
        presenter = MainPresenter(this,this)

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener)
    }
}

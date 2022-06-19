package pe.andy.bookholic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import pe.andy.bookholic.databinding.ActivityMainBinding
import pe.andy.bookholic.fragment.FavoriteFragment
import pe.andy.bookholic.fragment.SearchFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    val searchFragment: SearchFragment = SearchFragment()
    val favoriteFragment: FavoriteFragment = FavoriteFragment()
    var activeFragment: Fragment = searchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        supportFragmentManager.beginTransaction()
            .add(R.id.host_fragment, favoriteFragment, "favoriteFragment")
            .hide(favoriteFragment)
            .commit()

        supportFragmentManager.beginTransaction()
            .add(R.id.host_fragment, searchFragment, "searchFragment")
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_navigation_search -> {
                    if (activeFragment != searchFragment) {
                        supportFragmentManager.beginTransaction()
                            .show(searchFragment)
                            .hide(favoriteFragment)
                            .commit()

                        activeFragment = searchFragment
                    }
                }

                R.id.bottom_navigation_favorite -> {
                    if (activeFragment != favoriteFragment) {
                        supportFragmentManager.beginTransaction()
                            .show(favoriteFragment)
                            .hide(searchFragment)
                            .commit()

                        activeFragment = favoriteFragment
                    }
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        when (supportFragmentManager.fragments.lastOrNull()) {
            searchFragment -> {
                super.onBackPressed()
            }
            favoriteFragment -> {
                supportFragmentManager.beginTransaction()
                    .show(favoriteFragment)
                    .hide(searchFragment)
                    .commit()

                activeFragment = searchFragment

                binding.bottomNavigation.menu.getItem(0).isChecked = true
            }
        }
    }
}
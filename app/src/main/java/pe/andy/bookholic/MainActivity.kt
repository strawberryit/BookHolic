package pe.andy.bookholic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import pe.andy.bookholic.databinding.ActivityMainBinding
import pe.andy.bookholic.fragment.FavoriteFragment
import pe.andy.bookholic.fragment.SearchFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    val searchFragment: SearchFragment = SearchFragment()
    val favoriteFragment: FavoriteFragment = FavoriteFragment()
    var activeFragment: Fragment = searchFragment

    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            .add(R.id.host_fragment, favoriteFragment, "2")
            .hide(favoriteFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.host_fragment, searchFragment, "1")
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.fragment_search -> {
                    supportFragmentManager.beginTransaction()
                        .hide(favoriteFragment)
                        .show(searchFragment)
                        .commit()

                    activeFragment = searchFragment
                }

                R.id.fragment_favorite -> {
                    supportFragmentManager.beginTransaction()
                        .hide(searchFragment)
                        .show(favoriteFragment)
                        .commit()

                    activeFragment = favoriteFragment
                }
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}
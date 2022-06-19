package pe.andy.bookholic.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import pe.andy.bookholic.R
import pe.andy.bookholic.adapter.FavoriteAdapter
import pe.andy.bookholic.database.FavoriteDatabase
import pe.andy.bookholic.databinding.FragmentFavoriteBinding
import pe.andy.bookholic.viewmodel.FavoriteBookViewModel
import pe.andy.bookholic.viewmodel.FavoriteBookViewModelFactory

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    val binding by viewBinding(FragmentFavoriteBinding::bind)

    lateinit var favoriteViewModel: FavoriteBookViewModel
    lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FavoriteDatabase.getInstance(this.requireContext())
        val viewModelFactory = FavoriteBookViewModelFactory(database)
        favoriteViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteBookViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = FavoriteAdapter(
            onItemLongClick = {
                favoriteViewModel.delete(it)
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT)
                    .show()
            })

        binding.rvFavorites.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        observeFavoriteLiveData()
    }

    private fun observeFavoriteLiveData() {
        favoriteViewModel.observeLiveData().observe(viewLifecycleOwner) {
            favoriteAdapter.differ.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
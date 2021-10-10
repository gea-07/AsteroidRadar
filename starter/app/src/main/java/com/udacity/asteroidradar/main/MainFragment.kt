package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.InternalCoroutinesApi

class MainFragment : Fragment() {

    lateinit private var viewModel: MainViewModel

    @InternalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory. Pass in DAO and context
        val dataSource: AsteroidDatabaseDao? =
            AsteroidDatabase.getInstance(application)?.asteroidDatabaseDao ?: null
        val viewModelFactory = MainViewModelFactory(dataSource!!, application)

        // Get a reference to the ViewModel associated with this fragment.
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        val view = binding.asteroidRecycler
        if (view is RecyclerView) {
            with(view){
                adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener{
                    viewModel.displayAsteroidDetails(it)
                })

                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        LinearLayoutManager.VERTICAL
                    )
                )
            }
        }

        viewModel.navigateToSelectedAsteroid.observe(this, Observer {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}

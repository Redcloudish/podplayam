package com.trios2024ammb.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.trios2024ammb.podplay.R
import com.trios2024ammb.podplay.adapter.PodcastListAdapter
import com.trios2024ammb.podplay.databinding.ActivityPodcastBinding
import com.trios2024ammb.podplay.repository.ItunesRepo
import com.trios2024ammb.podplay.service.ItunesService
import com.trios2024ammb.podplay.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastActivity : AppCompatActivity(),
    PodcastListAdapter.PodcastListAdapterListener {

    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var podcastListAdapter: PodcastListAdapter


    private lateinit var databinding: ActivityPodcastBinding

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databinding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(databinding.root)

        val itunesService = ItunesService.instance

        val itunesRepo = ItunesRepo(itunesService)

        GlobalScope.launch {
            val results = itunesRepo.searchByTerm("Android Developer")
            Log.i(TAG, "Results = ${results.body()}")
        }

        setupToolbar()
        setupViewModels()
        updateControls()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 1
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        // 2
        val searchMenuItem = menu.findItem(R.id.search_item)
        val searchView = searchMenuItem?.actionView as SearchView
        // 3
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        // 4
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    private fun performSearch(term: String) {
        showProgressBar()
        GlobalScope.launch {
            val results = searchViewModel.searchPodcasts(term)
            withContext(Dispatchers.Main) {
                hideProgressBar()
                databinding.toolbar.title = term
                podcastListAdapter.setSearchData(results)
            }
        }
    }


    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) ?: return
            performSearch(query)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(databinding.toolbar)
    }

    private fun setupViewModels() {
        val service = ItunesService.instance
        searchViewModel.iTunesRepo = ItunesRepo(service)
    }

    private fun updateControls() {
        databinding.podcastRecyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        databinding.podcastRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            databinding.podcastRecyclerView.context, layoutManager.orientation)
        databinding.podcastRecyclerView.addItemDecoration(dividerItemDecoration)

        podcastListAdapter = PodcastListAdapter(null, this, this)
        databinding.podcastRecyclerView.adapter = podcastListAdapter
    }

    override fun onShowDetails(
        podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData
    ) {
        // Not implemented yet
    }

    private fun showProgressBar() {
        databinding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        databinding.progressBar.visibility = View.INVISIBLE
    }

}


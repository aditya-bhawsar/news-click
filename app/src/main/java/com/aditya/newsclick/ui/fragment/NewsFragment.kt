package com.aditya.newsclick.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aditya.newsclick.BuildConfig
import com.aditya.newsclick.R
import com.aditya.newsclick.api.ResponseWrapper
import com.aditya.newsclick.databinding.AppInfoViewBinding
import com.aditya.newsclick.databinding.FragmentNewsBinding
import com.aditya.newsclick.other.AppInfoData
import com.aditya.newsclick.other.Constants.Companion.ARTICLE_KEY
import com.aditya.newsclick.other.Constants.Companion.QUERY_PAGE
import com.aditya.newsclick.ui.activity.NewsActivity
import com.aditya.newsclick.ui.adapter.NewsAdapter
import com.aditya.newsclick.viewmodels.NewsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewsFragment : Fragment() {

    lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter:NewsAdapter

    private lateinit var bindingDialog: AppInfoViewBinding

    var isLoading = false
    var isLastPage = false
    var isScrolling =  false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_news,container,false)

        setHasOptionsMenu(true)

        bindingDialog = DataBindingUtil.inflate(inflater,R.layout.app_info_view,container,false)

        (activity as NewsActivity).setSupportActionBar(binding.toolbar)
        (activity as NewsActivity).supportActionBar!!.setIcon(R.drawable.ic_app_icon_40dp)

        newsAdapter = NewsAdapter()

        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@NewsFragment.onScrollListener)
        }

        newsAdapter.setOnItemClickListener {
            val action = NewsFragmentDirections.actionNewsFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }

        viewModel = (activity as NewsActivity).newsViewModel

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {responded->
            when(responded){
                is ResponseWrapper.Success ->{
                    hideProgressBar()
                    responded.data?.let { news->
                        newsAdapter.differ.submitList(news.articles.toList())
                        val totalPages = news.totalResults/ QUERY_PAGE +2
                        isLastPage =viewModel.breakingNewsPage== totalPages
                        if(isLastPage){
                            binding.rvNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is ResponseWrapper.Loading ->{showProgressBar()}
                is ResponseWrapper.Error ->{
                    hideProgressBar()
                    responded.message?.let { msg->
                        Toast.makeText(requireContext(),"An Error Occurred $msg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        return binding.root
    }

    val onScrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount  = layoutManager.childCount
            val totalCount = layoutManager.itemCount

            val isNotLoadingAndNotLast = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPos + visibleItemCount >= totalCount
            val isNotAtBeginning = firstVisibleItemPos >= 0
            val isTotalMoreThanVisible = totalCount >= QUERY_PAGE

            val shouldPaginate = isNotLoadingAndNotLast && isNotAtBeginning && isAtLastItem
                    && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                viewModel.getNewsHeadlines("in")
                isScrolling =false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.app_info_menu){
            val appInfo = AppInfoData( BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)
            bindingDialog.appInfo = appInfo

            MaterialAlertDialogBuilder(requireContext())
                .setView(bindingDialog.root)
                .setCancelable(true)
                .show()
        }
        return super.onOptionsItemSelected(item)
    }
}
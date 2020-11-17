package com.aditya.newsclick.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aditya.newsclick.BuildConfig
import com.aditya.newsclick.R
import com.aditya.newsclick.databinding.AppInfoViewBinding
import com.aditya.newsclick.databinding.FragmentSavedBinding
import com.aditya.newsclick.other.AppInfoData
import com.aditya.newsclick.other.Constants.Companion.ARTICLE_KEY
import com.aditya.newsclick.ui.activity.NewsActivity
import com.aditya.newsclick.ui.adapter.NewsAdapter
import com.aditya.newsclick.viewmodels.NewsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SavedFragment : Fragment() {

    lateinit var binding: FragmentSavedBinding
    lateinit var newsAdapter:NewsAdapter
    private lateinit var viewModel: NewsViewModel

    private lateinit var bindingDialog:AppInfoViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_saved,container,false)

        setHasOptionsMenu(true)

        bindingDialog = DataBindingUtil.inflate(inflater,R.layout.app_info_view,container,false)

        (activity as NewsActivity).setSupportActionBar(binding.toolbar)
        (activity as NewsActivity).supportActionBar!!.setIcon(R.drawable.ic_app_icon_40dp)

        viewModel = (activity as NewsActivity).newsViewModel

        newsAdapter = NewsAdapter()
        binding.rvSaved.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        newsAdapter.setOnItemClickListener {
            val action = SavedFragmentDirections.actionSavedFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return true }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[pos]
                viewModel.deleteArticle(article)
                Snackbar.make(requireView(),"Successfully Removed", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){ viewModel.saveArticle(article) }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(binding.rvSaved)
        }

        viewModel.getAllNews().observe(viewLifecycleOwner, Observer { articles->
            newsAdapter.differ.submitList(articles)
        })

        return binding.root
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
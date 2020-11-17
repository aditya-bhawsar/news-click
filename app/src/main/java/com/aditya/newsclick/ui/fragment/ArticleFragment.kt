package com.aditya.newsclick.ui.fragment

import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.aditya.newsclick.BuildConfig
import com.aditya.newsclick.R
import com.aditya.newsclick.databinding.AppInfoViewBinding
import com.aditya.newsclick.databinding.FragmentArticleBinding
import com.aditya.newsclick.other.AppInfoData
import com.aditya.newsclick.ui.activity.NewsActivity
import com.aditya.newsclick.viewmodels.NewsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment() {

    lateinit var binding: FragmentArticleBinding
    private lateinit var viewModel: NewsViewModel

    private lateinit var bindingDialog: AppInfoViewBinding

    private val args by navArgs<ArticleFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_article,container,false)

        setHasOptionsMenu(true)

        bindingDialog = DataBindingUtil.inflate(inflater,R.layout.app_info_view,container,false)

        (activity as NewsActivity).setSupportActionBar(binding.toolbar)
        (activity as NewsActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        viewModel = (activity as NewsActivity).newsViewModel

        val article = args.Article

        article.title?.let { title->
            viewModel.getArticleIfAny(title).observe(viewLifecycleOwner, Observer {
                binding.fab.isVisible = (it==null)
            })
        }

        binding.wvArticle.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(requireView(),"Article Has Been Saved", Snackbar.LENGTH_LONG).show()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.app_info_menu->{
                val appInfo = AppInfoData( BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)
                bindingDialog.appInfo = appInfo

                MaterialAlertDialogBuilder(requireContext())
                    .setView(bindingDialog.root)
                    .setCancelable(true)
                    .show()
            }
            android.R.id.home->{requireActivity().onBackPressed()}
        }
        return super.onOptionsItemSelected(item)
    }
}
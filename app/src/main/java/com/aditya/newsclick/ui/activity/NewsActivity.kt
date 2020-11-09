package com.aditya.newsclick.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aditya.newsclick.R
import com.aditya.newsclick.databinding.ActivityNewsBinding
import com.aditya.newsclick.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_news.*

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsBinding
    val newsViewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(750)
        setTheme(R.style.AppTheme)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_news)

        binding.bottomNavView.setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.articleFragment->{ binding.bottomNavView.visibility=View.GONE}
                else->{ binding.bottomNavView.visibility=View.VISIBLE}
            }
        }
    }
}
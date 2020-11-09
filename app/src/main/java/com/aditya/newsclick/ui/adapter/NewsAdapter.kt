package com.aditya.newsclick.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.aditya.newsclick.R
import com.aditya.newsclick.databinding.NewsItemBinding
import com.aditya.newsclick.model.Article

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean { return oldItem.url==newItem.url }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean { return oldItem==newItem }
    }

    val differ = AsyncListDiffer(this,differCallback)

    inner class ViewHolder(private val binding: NewsItemBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(article: Article){
            binding.article = article
            binding.ivArticleImage.load(article.urlToImage)
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.news_item
            ,parent,false
        ))
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    private var onItemClickListener: ((Article)->Unit)? =null

    fun setOnItemClickListener(listener: (Article)-> Unit){
        onItemClickListener = listener
    }
}
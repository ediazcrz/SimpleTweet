package com.codepath.apps.restclienttemplate

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.TweetsAdapter.TweetsViewHolder

// Passes in the context and list of tweets
class TweetsAdapter(private val context: Context, private val tweets: ArrayList<Tweet>): Adapter<TweetsViewHolder>() {
    // For each row, inflate the layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsViewHolder {
        val view = ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false)
        return TweetsViewHolder(view)
    }

    // Bind values based on the position of the element
    override fun onBindViewHolder(holder: TweetsViewHolder, position: Int) {
        val tweet: Tweet = tweets[position]
        holder.bind(tweet)
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    // Clean all elements of the recycler
    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: ArrayList<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }

    // Defines a ViewHolder
    inner class TweetsViewHolder(private val binding: ItemTweetBinding): ViewHolder(binding.root) {
        fun bind(tweet: Tweet) {
            binding.tvBody.text = tweet.body
            binding.tvScreenName.text = tweet.user.screenName

            Glide.with(binding.root)
                    .load(tweet.user.publicImageUrl)
                    .centerInside()
                    .into(binding.ivProfileImage)
        }
    }
}
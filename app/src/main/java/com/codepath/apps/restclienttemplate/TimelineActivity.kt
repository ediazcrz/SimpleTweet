package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Headers
import org.json.JSONException

const val TAG = "TimelineActivity"
const val REQUEST_CODE = 20

class TimelineActivity : AppCompatActivity() {
    private val tweets = arrayListOf<Tweet>()
    private val client: TwitterClient = TwitterApp.getRestClient(this)
    private lateinit var tweetAdapter: TweetsAdapter
    private lateinit var timelineBinding: ActivityTimelineBinding
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var rvTweets: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timelineBinding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(timelineBinding.root)

        swipeContainer = timelineBinding.swipeContainer

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener {
            populateHomeTimeline()
        }

        // Init the list of tweets, adapter and layout manager
        tweetAdapter = TweetsAdapter(this, tweets)
        rvTweets = timelineBinding.rvTweets
        val layoutManager = LinearLayoutManager(this)

        // Recycler view setup: layout manager and the adapter
        rvTweets.adapter = tweetAdapter
        rvTweets.layoutManager = layoutManager

        val scrollListener = object: EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.i(TAG, "onLoadMore $page")
                loadMoreData()
            }
        }

        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener)

        populateHomeTimeline()
    }

    private fun loadMoreData() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        client.getNextPageOfTweets(object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                Log.i(TAG, "onSuccess for loadMoreData: ${json.toString()}")

                //  --> Deserialize and construct new model objects from the API response
                //  --> Append the new data objects to the existing set of items inside the array of items
                //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
                val jsonArray = json?.jsonArray
                val typeToken = object : TypeToken<List<Tweet>>() {}.type

                try {
                    tweetAdapter.addAll(Gson().fromJson(jsonArray.toString(), typeToken))
                } catch (e: JSONException) {
                    Log.e(TAG, "Json exception for loadMoreData", e)
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                Log.e(TAG, "onFailure for loadMoreData: $response", throwable)
            }
        }, tweets[tweets.size - 1].id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.compose) {
            // Compose icon has been selected
            Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show()

            // Navigate to the compose activity
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get data from the intent (tweet)
            val tweet = data?.getParcelableExtra<Tweet>("tweet")

            // Update the recycler view with the tweet
            // Modify data source of tweets
            if (tweet != null) {
                tweets.add(0, tweet)
                // Update the adapter
                tweetAdapter.notifyItemInserted(0)
                rvTweets.smoothScrollToPosition(0)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun populateHomeTimeline() {
        client.getHomeTimeline(object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                Log.i(TAG, "onSuccess for populateHomeTimeline: ${json.toString()}")
                val jsonArray = json?.jsonArray
                val typeToken = object : TypeToken<List<Tweet>>() {}.type

                try {
                    tweetAdapter.clear()
                    tweetAdapter.addAll(Gson().fromJson(jsonArray.toString(), typeToken))
                    swipeContainer.isRefreshing = false
                } catch (e: JSONException) {
                    Log.e(TAG, "Json exception for populateHomeTimeline", e)
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                Log.e(TAG, "onFailure for populateHomeTimeline: $response", throwable)
            }
        })
    }
}
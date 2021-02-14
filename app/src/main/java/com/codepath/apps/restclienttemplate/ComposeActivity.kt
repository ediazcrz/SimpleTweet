package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import okhttp3.Headers
import org.json.JSONException

const val MAX_TWEET_LENGTH = 140

class ComposeActivity : AppCompatActivity() {
    private lateinit var composeBinding: ActivityComposeBinding
    private val TAG = "ComposeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        composeBinding = ActivityComposeBinding.inflate(layoutInflater)
        setContentView(composeBinding.root)

        val client = TwitterApp.getRestClient(this)

        val etCompose = composeBinding.etCompose
        val btnTweet = composeBinding.btnTweet
        val tvCount = composeBinding.tvCount
        val defaultTextColor = tvCount.textColors.defaultColor

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
                if (count > MAX_TWEET_LENGTH) {
                    btnTweet.isEnabled = false;
                    tvCount.setTextColor(Color.RED);
                }

                if (count <= MAX_TWEET_LENGTH && !btnTweet.isEnabled) {
                    btnTweet.isEnabled = true
                    tvCount.setTextColor(defaultTextColor)
                }

                tvCount.text = "${MAX_TWEET_LENGTH - count}"
            }
        })

        // Set click listener on button
        btnTweet.setOnClickListener() {
            val tweetContent = etCompose.text.toString()

            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (tweetContent.length > MAX_TWEET_LENGTH) {
                Toast.makeText(this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Toast.makeText(this, tweetContent, Toast.LENGTH_LONG).show()

            // Make an API call to Twitter to publish the tweet
            client.publishTweet(tweetContent, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                    Log.i(TAG, "onSuccess to publish tweet")
                    val jsonObject = json?.jsonObject

                    try {
                        val tweet = Gson().fromJson(jsonObject.toString(), Tweet::class.java)
                        Log.i(TAG, "Published tweet says: $tweetContent")

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        // set result code and bundle data for response
                        setResult(RESULT_OK, intent)
                        // close the activity, pass data to parent
                        finish()
                    } catch (e: JSONException) {
                        Log.e(TAG, "Failed to publish tweet", e)
                    }
                }

                override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                    Log.d(TAG, "onFailure to publish tweet", throwable)
                }
            })
        }
    }
}
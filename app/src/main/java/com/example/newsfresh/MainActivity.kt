package com.example.newsfresh

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var mAdapter: NewsListAdapter
    private lateinit var newsArray: ArrayList<News>
    private lateinit var tempArraylist : ArrayList<News>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        newsArray = arrayListOf<News>()
        tempArraylist=arrayListOf<News>()
        fetchData()
        mAdapter = NewsListAdapter(this)
        recyclerView.adapter = mAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_item, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempArraylist.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    newsArray.forEach {
                        if (it.author.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            tempArraylist.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    tempArraylist.clear()
                    tempArraylist.addAll(newsArray)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }

                return false
            }


        })

        return super.onCreateOptionsMenu(menu)
    }
    private fun fetchData() {
        val url = "https://saurav.tech/NewsAPI/everything/cnn.json"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                val tempArrayList = ArrayList<News>()

                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }
                tempArrayList.addAll(newsArray)
                mAdapter.updateNews(tempArrayList)
            },
            {

            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}
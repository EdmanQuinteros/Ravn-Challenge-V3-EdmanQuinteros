package com.example.ravn_challenge_v3_edmanquinteros.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.ravn_challenge_v3_edmanquinteros.R
import com.example.ravn_challenge_v3_edmanquinteros.ui.adapter.AllPeopleAdapter
import com.example.ravn_challenge_v3_edmanquinteros.apolloClient
import com.example.star_wars.AllPeopleQuery
import kotlinx.android.synthetic.main.activity_all_people.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class AllPeopleActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AllPeopleActivity"
    }

    private var mApolloClient = apolloClient
    private lateinit var mAllPeopleListAdapter: AllPeopleAdapter

    private var mPeopleList = mutableListOf<AllPeopleQuery.Person>()
    private var mCurrEndCursor: String? = null

    //@ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_people)

        mAllPeopleListAdapter = AllPeopleAdapter(this, mPeopleList)
        mAllPeopleListAdapter.onItemClickListener { person ->
            Log.i(TAG, "click on item of ${person.name}")
            getDetailOf(person.id)
        }

        rv_all_people.adapter = mAllPeopleListAdapter
        rv_all_people.layoutManager = LinearLayoutManager(this)


        /**
         * Courutine Scope to work in another thread
         */
        lifecycleScope.launch {
            while (true) {
                val result = try {
                    getDataServer()
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    break
                }

                when (result) {
                    LoadResult.Empty -> {
                        hideLoad()
                        break
                    }
                    LoadResult.Successful -> {
                        rv_all_people.visibility = View.VISIBLE
                        runOnUiThread { ll_loading.visibility = View.VISIBLE }
                    }
                }
            }
        }
    }

    /**
    *Get a list of characters from the server and check if the response has any errors or is empty, else get another 5 characters and save for the next query
    * @return LoadResult.Empty if there is no more character data
    * @return LoadResult.Successful if there is more character data
     */
    private suspend fun getDataServer(): LoadResult = coroutineScope {
        val response = try {
            mApolloClient
                .query(AllPeopleQuery(get = 5, Optional.presentIfNotNull(mCurrEndCursor)))
                .execute()
        } catch (e: ApolloException) {
            runOnUiThread { showLoadError() }
            throw e
        }

        val allPeople = response.data?.allPeople
        if (allPeople == null || response.hasErrors()) {
            runOnUiThread { showLoadError() }
            throw Exception("Failed to get data from endpoint")
        }

        if (allPeople.people?.isEmpty() != false) {
            runOnUiThread { hideLoad() }
            return@coroutineScope LoadResult.Empty
        }

        val lastIndex = mPeopleList.size
        mPeopleList.addAll(allPeople.people.filterNotNull())

        runOnUiThread {
            mAllPeopleListAdapter.notifyItemRangeInserted(lastIndex, 5)
        }
        if (allPeople.pageInfo.endCursor == null)
            throw Exception("Get a null cursor")

        mCurrEndCursor = allPeople.pageInfo.endCursor
        return@coroutineScope LoadResult.Successful
    }

    /**
     * show a failed message in the view
     */
    private fun showLoadError() {
        ll_loading.visibility = View.GONE
        rv_all_people.visibility = View.GONE
        ll_failed.visibility = View.VISIBLE
    }

    /**
     * hide the loading message in the view
     */
    private fun hideLoad(){
        ll_loading.visibility = View.GONE
    }

    private enum class LoadResult {
        Empty,
        Successful
    }

    /**
     * add a extra to the intent that is the id of the character clicked
     */
    private fun getDetailOf(id: String) {
        val intent = Intent(this,  DetailsActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}
package com.example.ravn_challenge_v3_edmanquinteros.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.ravn_challenge_v3_edmanquinteros.R
import com.example.ravn_challenge_v3_edmanquinteros.ui.adapter.VehiclesAdapter
import com.example.ravn_challenge_v3_edmanquinteros.apolloClient
import com.example.star_wars.PersonDetailsQuery
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DetailsActivity"
    }

    private lateinit var actionBar: ActionBar

    private lateinit var mVehiclesListAdapter: VehiclesAdapter
    private lateinit var mApolloClient: ApolloClient

    private var mVehiclesList = mutableListOf<PersonDetailsQuery.Vehicle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        mApolloClient = apolloClient
        actionBar = supportActionBar!!
        mVehiclesListAdapter = VehiclesAdapter(this, mVehiclesList)

        rv_vehicles.adapter = mVehiclesListAdapter
        rv_vehicles.layoutManager = LinearLayoutManager(this)

        rv_vehicles.setHasFixedSize(true)


        // to active the back button
        actionBar.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra("id")
        if (id != null) {
            lifecycleScope.launch {
                getInformation(id)
            }
        } else {
            showLoadError()
        }

    }

    /**
     * Get information from a specific character using @param id, and add the information to the
     * view
     */
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun getInformation(id: String): Unit = coroutineScope {
        val response = try {
            mApolloClient.query(PersonDetailsQuery(id = id)).execute()
        } catch (e: ApolloException) {
            runOnUiThread {
                showLoadError()
            }
            return@coroutineScope
        }

        val person = response.data?.person
        if (person == null || response.hasErrors()) {
            runOnUiThread {
                showLoadError()
            }
            return@coroutineScope
        }

        Log.d(TAG, "getPersonInformation: $person")

        mVehiclesList
            .addAll(person.vehicleConnection?.vehicles?.filterNotNull() ?: emptyList())

        runOnUiThread {
            ll_detail.visibility = View.VISIBLE
            actionBar.title = person.name ?: "Unknown"
            addItemDataView(getString(R.string.eye_color), person.eyeColor ?: "Unknown")
            addItemDataView(getString(R.string.hair_color), person.hairColor ?: "Unknown")
            addItemDataView(getString(R.string.skin_color), person.skinColor ?: "Unknown")
            addItemDataView(getString(R.string.birth_year), person.birthYear ?: "Unknown")
            // Notify changes to the vehicles adapter
            mVehiclesListAdapter.notifyDataSetChanged()
            ll_loading.visibility = View.GONE
        }
    }

    /**
     * show a failed message in the view
     */
    private fun showLoadError() {
        ll_loading.visibility = View.GONE
        ll_failed.visibility = View.VISIBLE
    }

    /**
     * add a row to the linear layout of general information
     */
    private fun addItemDataView(left: String, right: String) {
        val view = layoutInflater.inflate(R.layout.activity_item_informacion, ll_information, false)
        view.findViewById<TextView>(R.id.dataL).text = left
        view.findViewById<TextView>(R.id.dataR).text = right
        ll_information.addView(view)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}
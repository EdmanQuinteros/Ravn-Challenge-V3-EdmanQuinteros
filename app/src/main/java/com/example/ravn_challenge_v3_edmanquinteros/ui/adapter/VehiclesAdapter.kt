package com.example.ravn_challenge_v3_edmanquinteros.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ravn_challenge_v3_edmanquinteros.R
import com.example.star_wars.PersonDetailsQuery

class VehiclesAdapter (context: Context, vehicleList: MutableList<PersonDetailsQuery.Vehicle>) :
    RecyclerView.Adapter<VehiclesAdapter.VehicleViewHolder>() {
    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataL: TextView = itemView.findViewById(R.id.dataL)
        init {
            // the vehicles do not need a right space
            itemView.findViewById<TextView>(R.id.dataR).visibility = View.GONE
        }
    }

    private val mVehicleList = vehicleList
    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val mItemView = mInflater.inflate(R.layout.activity_item_informacion, parent, false)
        return VehicleViewHolder(mItemView)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.dataL.text = mVehicleList[position].name ?: "Unknown"
    }

    override fun getItemCount(): Int {
        return mVehicleList.size
    }
}
package com.example.ravn_challenge_v3_edmanquinteros.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ravn_challenge_v3_edmanquinteros.R
import com.example.star_wars.AllPeopleQuery

class AllPeopleAdapter (
    context: Context,
    peopleList: MutableList<AllPeopleQuery.Person>
) : RecyclerView.Adapter<AllPeopleAdapter.PersonViewHolder>() {
    class PersonViewHolder(itemView: View, adapter: AllPeopleAdapter) :
        RecyclerView.ViewHolder(itemView) {
        private val mAdapter = adapter
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personFooter: TextView = itemView.findViewById(R.id.personFooter)

        init {
            itemView.setOnClickListener {
                mAdapter.onItemClick(layoutPosition)
            }
        }
    }

    private val mPeopleList = peopleList
    private val mInflater = LayoutInflater.from(context)

    private var mOnItemClickListener: ((AllPeopleQuery.Person) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val mItemView = mInflater.inflate(R.layout.activity_item_person, parent, false)
        return PersonViewHolder(mItemView, this)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = mPeopleList[position]
        holder.personName.text = person.name

        val species = person.species?.name ?: "unknown"
        val homeWorld = person.homeworld?.name ?: "far, far away"
        holder.personFooter.text = "$species from $homeWorld"
    }

    override fun getItemCount(): Int {
        return mPeopleList.size
    }

    fun onItemClickListener(listener: (AllPeopleQuery.Person) -> Unit) {
        mOnItemClickListener = listener
    }

    private fun onItemClick(position: Int) {
        mOnItemClickListener?.invoke(mPeopleList[position])
    }
}
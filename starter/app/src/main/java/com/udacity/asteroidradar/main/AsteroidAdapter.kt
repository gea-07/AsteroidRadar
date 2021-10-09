package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding

class AsteroidAdapter(private val onClickListener: OnClickListener): ListAdapter<Asteroid, AsteroidAdapter.AsteroidItemViewHolder>(
    AsteroidDiffCallback()
) {
    inner class AsteroidItemViewHolder(
        private val binding: AsteroidListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Asteroid) {
            binding.apply {
                asteroid = item
                executePendingBindings()
            }
        }
    }

    private class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(
            oldItem: Asteroid,
            newItem: Asteroid
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Asteroid,
            newItem: Asteroid
        ): Boolean = oldItem == newItem
    }

    //override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: AsteroidItemViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(asteroid)
        }
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidItemViewHolder =
        AsteroidItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.asteroid_list_item,
                parent,
                false
            )
        )
    class OnClickListener(val clickListener: (asteroid:Asteroid)->Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}
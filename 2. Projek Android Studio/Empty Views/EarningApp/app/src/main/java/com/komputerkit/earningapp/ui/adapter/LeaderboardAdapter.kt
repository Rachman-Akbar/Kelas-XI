package com.komputerkit.earningapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.earningapp.R
import com.komputerkit.earningapp.data.model.LeaderboardEntry

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    private var leaderboardList = mutableListOf<LeaderboardEntry>()

    fun updateLeaderboard(newList: List<LeaderboardEntry>) {
        leaderboardList.clear()
        leaderboardList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(leaderboardList[position])
    }

    override fun getItemCount(): Int = leaderboardList.size

    inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        private val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        private val tvDisplayName: TextView = itemView.findViewById(R.id.tvDisplayName)
        private val tvLevel: TextView = itemView.findViewById(R.id.tvLevel)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)

        fun bind(entry: LeaderboardEntry) {
            tvRank.text = "#${entry.rank}"
            tvDisplayName.text = entry.getDisplayText()
            tvLevel.text = "Level ${entry.level} • ${entry.totalQuizzes} quiz"
            tvScore.text = entry.getFormattedScore()

            // Set rank badge color
            when (entry.rank) {
                1 -> {
                    tvRank.setBackgroundResource(R.drawable.rank_gold_background)
                    tvRank.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }
                2 -> {
                    tvRank.setBackgroundResource(R.drawable.rank_silver_background)
                    tvRank.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }
                3 -> {
                    tvRank.setBackgroundResource(R.drawable.rank_bronze_background)
                    tvRank.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }
                else -> {
                    tvRank.setBackgroundResource(R.drawable.rank_default_background)
                    tvRank.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary))
                }
            }

            // Load profile image
            Glide.with(itemView.context)
                .load(entry.profileImageUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(ivProfileImage)
        }
    }
}

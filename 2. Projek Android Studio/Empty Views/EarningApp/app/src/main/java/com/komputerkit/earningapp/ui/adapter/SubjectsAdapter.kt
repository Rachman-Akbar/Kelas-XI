package com.komputerkit.earningapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.komputerkit.earningapp.R
import com.komputerkit.earningapp.data.model.Subject

class SubjectsAdapter(
    private val onSubjectClick: (Subject) -> Unit
) : ListAdapter<Subject, SubjectsAdapter.SubjectViewHolder>(SubjectDiffCallback()) {

    fun updateSubjects(subjects: List<Subject>) {
        submitList(subjects)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardSubject)
        private val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        private val tvSubjectDescription: TextView = itemView.findViewById(R.id.tvSubjectDescription)

        fun bind(subject: Subject) {
            tvSubjectName.text = subject.name
            tvSubjectDescription.text = subject.description

            cardView.setOnClickListener {
                Log.d("SubjectsAdapter", "Item clicked: ${subject.name}")
                onSubjectClick(subject)
            }
            
            // Also set on item view for better touch handling
            itemView.setOnClickListener {
                Log.d("SubjectsAdapter", "ItemView clicked: ${subject.name}")
                onSubjectClick(subject)
            }
        }
    }

    class SubjectDiffCallback : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem == newItem
        }
    }
}

package com.example.taskmanagerapp.adapter

import TaskDiffCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.Task

class CompletedTaskAdapter(private val listener: TaskAdapter.TaskListener) :
    ListAdapter<Task, CompletedTaskAdapter.CompletedTaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.completed_task_item, parent, false)
        return CompletedTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompletedTaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class CompletedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Implement ViewHolder bindings for completed task item
        // Example: val taskTitle: TextView = itemView.findViewById(R.id.completedTaskTitle)

        fun bind(task: Task) {
            // Bind task details to UI elements
            // Example: taskTitle.text = task.title

            // Handle click events if needed
        }
    }
}

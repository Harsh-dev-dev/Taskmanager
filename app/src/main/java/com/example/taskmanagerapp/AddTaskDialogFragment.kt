package com.example.taskmanagerapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskmanagerapp.databinding.FragmentAddTaskDialogBinding
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import java.util.UUID
import java.util.Locale



class AddTaskDialogFragment(
    private val taskViewModel: TaskViewModel,
    private val listener: TaskAddedListener,
    private val taskToEdit: Task? = null
) : DialogFragment() {

    interface TaskAddedListener {
        fun onTaskAdded(task: Task)
    }

    private lateinit var binding: FragmentAddTaskDialogBinding
    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentAddTaskDialogBinding.inflate(requireActivity().layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (taskToEdit == null) "Add Task" else "Edit Task")
            .setView(binding.root)
            .setPositiveButton(if (taskToEdit == null) "ADD" else "SAVE") { dialog, which ->
                val title = binding.taskTitleInput.text.toString().trim()
                val description = binding.taskDescriptionInput.text.toString().trim()
                val priority = binding.spinnerPriority.selectedItem.toString()
                val dueDate = binding.taskDueDate.text.toString().trim()

                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val editedTask = if (taskToEdit != null) {
                        // Edit existing task
                        taskToEdit.copy(
                            title = title,
                            description = description,
                            priority = priority,
                            dueDate = dueDate
                        )
                    } else {
                        // Create new task
                        Task(
                            title = title,
                            description = description,
                            priority = priority,
                            dueDate = dueDate,
                            id = UUID.randomUUID().toString()
                        )
                    }

                    listener.onTaskAdded(editedTask)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        // Populate fields if editing an existing task
        taskToEdit?.let { task ->
            binding.taskTitleInput.setText(task.title)
            binding.taskDescriptionInput.setText(task.description)
            binding.spinnerPriority.setSelection(getPriorityIndex(task.priority))
            val dueDate = binding.taskDueDate.text.toString().trim()
        }


        // Set up date picker for due date
        binding.taskDueDate.setOnClickListener {
            showDatePickerDialog()
        }

        return dialog
    }

    private fun getPriorityIndex(priority: String): Int {
        return when (priority) {
            "High" -> 0
            "Medium" -> 1
            "Low" -> 2
            else -> 0 // Default to high priority if unknown
        }
    }
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.taskDueDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}
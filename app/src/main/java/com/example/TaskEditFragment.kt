package com.example
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale



private val calendar = Calendar.getInstance()
class TaskEditFragment : DialogFragment() {

    private lateinit var saveButton: Button
    private lateinit var editTextTaskTitle: EditText
    private lateinit var editTextTaskDescription: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var buttonSave: Button
    private lateinit var selectdatePicker: TextInputEditText


    private val sharedViewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_edit, container, false)

        editTextTaskTitle = view.findViewById(R.id.editTextTaskTitle)
        editTextTaskDescription = view.findViewById(R.id.editTextTaskDescription)
        spinnerPriority = view.findViewById(R.id.spinnerPriority)
        buttonSave = view.findViewById(R.id.buttonSave)
        selectdatePicker = view.findViewById(R.id.buttonSelectDueDate)


        observeViewModel()

        buttonSave.setOnClickListener {
            Toast.makeText(requireContext(), "Edited task Saved", Toast.LENGTH_SHORT).show()
            saveTaskDetails()
            dismiss()
        }
        selectdatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        return view
    }

    private fun observeViewModel() {
        sharedViewModel.editedTitle.observe(viewLifecycleOwner, { title ->
            editTextTaskTitle.setText(title)
        })

        sharedViewModel.editedDescription.observe(viewLifecycleOwner, { description ->
            editTextTaskDescription.setText(description)
        })

        sharedViewModel.editedPriority.observe(viewLifecycleOwner, { priority ->
        })

        sharedViewModel.editedDate.observe(this, Observer { date ->
            selectdatePicker.setText(date)
        })
    }

    private fun saveTaskDetails() {
        val editedTitle = editTextTaskTitle.text.toString()
        val editedDescription = editTextTaskDescription.text.toString()
        val editedPriority = spinnerPriority.selectedItem?.toString() ?: ""
        val editedDate = selectdatePicker.text.toString()

        sharedViewModel.setEditedValues(editedTitle, editedDescription, editedPriority, editedDate)
    }
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
           requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectdatePicker.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}




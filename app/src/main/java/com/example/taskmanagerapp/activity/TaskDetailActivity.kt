package com.example.taskmanagerapp.activity
import android.app.Activity
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.TaskEditFragment
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.Task
import com.example.taskmanagerapp.activity.MainActivity
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class TaskDetailActivity : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var currentLocation: LatLng
    private val permissionCode = 101
    private var mgoogleMap:GoogleMap? = null

    private lateinit var backButton: ImageView
    private lateinit var taskTitle: TextView
    private lateinit var taskDescription: TextView
    private lateinit var dueDate: TextView
    private lateinit var priorityLevel: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var completionSwitch: Switch

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Initialize UI components
        backButton = findViewById(R.id.backButton)
        taskTitle = findViewById(R.id.tasktitle)
        taskDescription = findViewById(R.id.taskdescription)
        dueDate = findViewById(R.id.duedate)
        priorityLevel = findViewById(R.id.prioritylevel)
        editButton = findViewById(R.id.editbutton)
        deleteButton = findViewById(R.id.deletebutton)
        completionSwitch = findViewById(R.id.completionSwitch)

        backButton.setOnClickListener {
            finish()
        }

        taskViewModel.editedTitle.observe(this, { title ->
            taskTitle.text = title
        })

        taskViewModel.editedDescription.observe(this, { description ->
            taskDescription.text = description
        })

        taskViewModel.editedPriority.observe(this, { priority ->
            priorityLevel.text = priority
            setPriorityColor(priority)
        })

        taskViewModel.editedDate.observe(this, Observer { date ->
            dueDate.text = date
        })

        // Get task data from intent
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("dueDate")
        val priority = intent.getStringExtra("priority")
        val isCompleted = intent.getBooleanExtra("isCompleted", false)

        // Set task data to views
        taskTitle.text = title
        taskDescription.text = description
        dueDate.text = date
        priorityLevel.text = priority
        completionSwitch.isChecked = isCompleted

        // Set priority color
        when (priority) {
            "High" -> priorityLevel.setTextColor(Color.RED)
            "Medium" -> priorityLevel.setTextColor(Color.YELLOW)
            "Low" -> priorityLevel.setTextColor(Color.GREEN)
        }


        editButton.setOnClickListener {
            taskViewModel.setEditedValues(title ?: "", description ?: "", priority ?: "", date?: "")

            val fragment = TaskEditFragment()
            fragment.show(supportFragmentManager, "TaskEditDialogFragment")
        }
        completionSwitch.setOnCheckedChangeListener { _, isChecked ->
            val title = intent.getStringExtra("title") ?: ""
            val description = intent.getStringExtra("description") ?: ""
            val date = intent.getStringExtra("dueDate") ?: ""
            val priority = intent.getStringExtra("priority") ?: ""
            val isCompleted = false

            if (isChecked) {
                val task = Task(title.toString(), description, date, priority, isCompleted.toString())
                taskViewModel.completeTask(task)
            } else {
            }
        }


        deleteButton.setOnClickListener {
            // Delete task from ViewModel
            val task = Task(
                title = title!!,
                description = description!!,
                dueDate = date!!,
                priority = priority!!,
                isCompleted = isCompleted
            )
            taskViewModel.deleteTask(task)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        completionSwitch.setOnCheckedChangeListener { _, isChecked ->
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_TASK && resultCode == Activity.RESULT_OK) {
            taskTitle.text = taskViewModel.editedTitle.value
            taskDescription.text = taskViewModel.editedDescription.value
            priorityLevel.text = taskViewModel.editedPriority.value

            val editedTitle = taskViewModel.editedTitle.value ?: ""
            val editedDescription = taskViewModel.editedDescription.value ?: ""
            val editedPriority = taskViewModel.editedPriority.value ?: ""

            val task = Task(
                title = editedTitle,
                description = editedDescription,
                dueDate = dueDate.text.toString(),  // Use the existing due date
                priority = editedPriority,
                isCompleted = completionSwitch.isChecked // Use the existing completion status
            )
            taskViewModel.updateTask(task)

            setPriorityColor(editedPriority)
        }
    }

    private fun setPriorityColor(priority: String?) {
        when (priority) {
            "High" -> priorityLevel.setTextColor(Color.RED)
            "Medium" -> priorityLevel.setTextColor(Color.YELLOW)
            "Low" -> priorityLevel.setTextColor(Color.GREEN)
            else -> priorityLevel.setTextColor(Color.BLACK)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mgoogleMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
        } else {
               googleMap.isMyLocationEnabled = true
            googleMap.setOnMyLocationChangeListener { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mgoogleMap?.isMyLocationEnabled = true
                mgoogleMap?.setOnMyLocationChangeListener { location ->
                    currentLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }


    companion object {
        private const val REQUEST_CODE_EDIT_TASK = 101
    }
}

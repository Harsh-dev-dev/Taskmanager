package com.example.taskmanagerapp.activity

import TaskAdapter
import android.animation.ObjectAnimator
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapp.fragments.AddTaskDialogFragment
import com.example.taskmanagerapp.fragments.Dashboard_Fragment
import com.example.taskmanagerapp.fragments.HomeFragment
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.fragments.SettingsFragment
import com.example.taskmanagerapp.Task
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), TaskAdapter.TaskListener {
    private lateinit var fabBackground: View
    private var cancellationSignal: CancellationSignal? = null

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var searchIcon: ImageView
    private var fragmentContainer: FrameLayout? = null
    lateinit var view:RecyclerView

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@MainActivity, "Authetication "+errString, Toast.LENGTH_SHORT).show()

            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@MainActivity, "Authetication Succeed", Toast.LENGTH_SHORT).show()




            }
        }
    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
        }
        return cancellationSignal as CancellationSignal
    }

    // it checks whether the app the app has fingerprint permission
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isDeviceSecure) {
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }



    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchBar = findViewById<EditText>(R.id.searchBar)
        view = findViewById<RecyclerView>(R.id.recyclerView)
        supportActionBar?.hide()
        val fab: FloatingActionButton = findViewById(R.id.fab)
        checkBiometricSupport()

        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Fingerprint Reqruired")
            .setSubtitle("Please put your Fingerprint on sensor to Unclock!!!")
            .setDescription("Uses Fingerprint")
            .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener { dialog, which ->
            }).build()

        // start the authenticationCallback in mainExecutor
        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)





        val animator = ObjectAnimator.ofFloat(fab, "translationY",
            -10f, 10f).apply {
            duration = 500
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            start()
        }

        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        fab.startAnimation(pulseAnimation)




        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        taskAdapter = TaskAdapter(this,view, taskViewModel)




        searchIcon = findViewById(R.id.searchIcon)
        fragmentContainer = findViewById(R.id.fragment_container);

        searchIcon.setOnClickListener {
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchTask(query)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }




    findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter

            // Enable swipe to edit/delete
            taskAdapter.enableSwipeToDelete(this)

        }

        taskViewModel.tasks.observe(this, Observer { tasks ->
            tasks?.let {
                taskAdapter.submitList(ArrayList(it))
            }

        })

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            AddTaskDialogFragment(taskViewModel, object : AddTaskDialogFragment.TaskAddedListener {
                override fun onTaskAdded(task: Task) {
                    taskViewModel.addTask(task)
                }
            }).show(supportFragmentManager, "AddTaskDialogFragment")
            it.clearAnimation()
            animator.cancel()

        }












        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    view.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }

                R.id.nav_dashboard ->{
                    view.visibility = View.GONE

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, Dashboard_Fragment())
                        .commit()
                    true
            }
                R.id.nav_settings -> {
                    view.visibility = View.GONE


                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container,
                        SettingsFragment()
                    )
                        .commit()
                    true
                }
                else -> false
            }
        }

        applySavedTheme()

    }


    private fun applySavedTheme() {
        val sharedPreferences = getSharedPreferences(SettingsFragment.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPreferences.getString(
            SettingsFragment.THEME_PREF,
            SettingsFragment.LIGHT_THEME
        )
        when (theme) {
            SettingsFragment.LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            SettingsFragment.DARK_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }



    private fun searchTask(query: String) {
        val tasks = taskViewModel.tasks.value
        tasks?.let {
            for ((index, task) in it.withIndex()) {
                if (task.title.contains(query, true)) {
                    taskAdapter.highlightItem(index)
                    findViewById<RecyclerView>(R.id.recyclerView).scrollToPosition(index)
                    return
                }
            }
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onTaskActionClicked(task: Task, action: TaskAdapter.TaskAction) {
        when (action) {
            TaskAdapter.TaskAction.EDIT -> {
                // Show AddTaskDialogFragment for editing
                AddTaskDialogFragment(taskViewModel, object : AddTaskDialogFragment.TaskAddedListener {
                    override fun onTaskAdded(editedTask: Task) {
                        taskViewModel.updateTask(editedTask)
                    }
                }, task).show(supportFragmentManager, "AddTaskDialogFragment")
            }
            TaskAdapter.TaskAction.DELETE -> {
                // Handle delete action
                taskViewModel.deleteTask(task)
                Snackbar.make(findViewById(android.R.id.content), "Task deleted", Snackbar.LENGTH_SHORT)
                    .setAction("UNDO") {
                        taskViewModel.addTask(task)
                    }
                    .show()
            }

        }

    }


    override fun onTaskSwiped(task: Task, action: TaskAdapter.TaskAction) {
    }

    override fun showSelectTaskMessage() {
        Toast.makeText(this, "Please Select something", Toast.LENGTH_SHORT).show()
    }
    private fun stopPulseAnimation() {
        fabBackground.clearAnimation()
    }

    override fun onTaskClicked(task: Task) {
        val intent = Intent(this, TaskDetailActivity::class.java)
        intent.putExtra("title", task.title)
        intent.putExtra("description", task.description)
        intent.putExtra("dueDate", task.dueDate)
        intent.putExtra("priority", task.priority)
        intent.putExtra("isCompleted", task.isCompleted)
        startActivity(intent)
    }
}


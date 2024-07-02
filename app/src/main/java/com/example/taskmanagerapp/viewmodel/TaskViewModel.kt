
package com.example.taskmanagerapp.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskmanagerapp.Task

class TaskViewModel : ViewModel() {

    private val _editedTitle = MutableLiveData<String>()
    private val _editedDescription = MutableLiveData<String>()
    private val _editedPriority = MutableLiveData<String>()
    private val _editedDate = MutableLiveData<String>()

    private val _completedTasks: MutableLiveData<MutableList<Task>> = MutableLiveData(mutableListOf())
    val completedTasks: LiveData<MutableList<Task>>
        get() = _completedTasks

    private val _completedTaskCount = MutableLiveData<Int>()
    val completedTaskCount: LiveData<Int> get() = _completedTaskCount

    init {
        _completedTaskCount.value = 0
    }

    fun completeTasks(task: Task) {
        // Update task completion logic
        _completedTaskCount.value = _completedTaskCount.value?.plus(1)
    }

    val editedTitle: LiveData<String> = _editedTitle
    val editedDescription: LiveData<String> = _editedDescription
    val editedPriority: LiveData<String> = _editedPriority
    val editedDate: LiveData<String> = _editedDate

    private val _removedTaskCount = MutableLiveData<Int>()
    val removedTaskCount: LiveData<Int> get() = _removedTaskCount

    private val _presentTaskCount = MutableLiveData<Int>()
    val presentTaskCount: LiveData<Int> get() = _presentTaskCount

    fun setRemovedTaskCount(count: Int) {
        _removedTaskCount.value = count
    }

    fun setPresentTaskCount(count: Int) {
        _presentTaskCount.value = count
    }

    fun setEditedValues(title: String, description: String, priority: String,  date: String) {
        _editedTitle.value = title
        _editedDescription.value = description
        _editedPriority.value = priority
        _editedDate.value = date
    }
    fun completeTask(task: Task) {
        val currentCompletedTasks = _completedTasks.value ?: mutableListOf()
        currentCompletedTasks.add(task)
        _completedTasks.value = currentCompletedTasks

        val currentTasks = _tasks.value ?: return
        currentTasks.remove(task)
        _tasks.value = currentTasks
    }






    private val _tasks: MutableLiveData<MutableList<Task>> = MutableLiveData(mutableListOf())
    val tasks: LiveData<MutableList<Task>>
        get() = _tasks

    fun loadTasks() {
    }

    fun addTask(task: Task) {
        val currentTasks = _tasks.value ?: mutableListOf()
        currentTasks.add(task)
        _tasks.value = currentTasks
    }

    fun updateTask(updatedTask: Task) {
        val currentTasks = _tasks.value ?: return
        val index = currentTasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            currentTasks[index] = updatedTask
            _tasks.value = currentTasks
        }
    }

    fun deleteTask(task: Task) {
        val currentTasks = _tasks.value ?: return
        currentTasks.remove(task)
        _tasks.value = currentTasks
    }
}

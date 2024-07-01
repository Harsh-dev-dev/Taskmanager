package com.example.taskmanagerapp

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var description: String = "",
    var dueDate: String = "",
    var priority: String = "",
    var location: String = "",
    var isCompleted: Boolean = false,
    var isSelected: Boolean = false,
    var removedTaskCount: Int = 0,
    var presentTaskCount: Int = 0

)

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}
package com.example.mykalendar

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    var isCompleted: Boolean = false // По умолчанию задача не выполнена
)
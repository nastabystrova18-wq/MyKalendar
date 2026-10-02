package com.example.mykalendar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var adapter: TaskAdapter

    // Временный список задач (Заглушки)
    private val taskList = mutableListOf(
        Task(1, "Купить продукты", "Молоко, хлеб, сыр", "12.05.2024", false),
        Task(2, "Сделать зарядку", "30 минут кардио", "13.05.2024", true),
        Task(3, "Позвонить маме", "Обсудить выходные", "14.05.2024", false),
        Task(4, "Прочитать главу книги", "Стр. 50-100", "15.05.2024", false),
        Task(5, "Написать отчет", "По учебе", "16.05.2024", true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Создаем адаптер и передаем ему список и лямбду для клика
        adapter = TaskAdapter(taskList) { task ->
            // Переход на экран деталей (Задание 10)
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id) // Передаем ID
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        // Обработка нажатия на FAB (Задание 9)
        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.example.mykalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var btnTestDb: Button
    private lateinit var adapter: TaskAdapter
    private lateinit var database: AppDatabase

    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        btnTestDb = findViewById(R.id.btnTestDb)

        // Инициализация базы данных
        database = AppDatabase.getDatabase(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(taskList) { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Кнопка теста БД (Задание 14)
        btnTestDb.setOnClickListener {
            lifecycleScope.launch {
                testDatabase()
                // После теста перезагружаем список из БД
                loadTasksFromDatabase()
            }
        }

        // Загрузка данных из БД (Задание 15)
        loadTasksFromDatabase()
    }

    private suspend fun testDatabase() {
        withContext(Dispatchers.IO) {
            val dao = database.taskDao()

            // 1. Добавляем задачи
            dao.insertTask(Task(title = "Тест 1", description = "Описание 1", date = "01.01.2025", time = "10:00"))
            dao.insertTask(Task(title = "Тест 2", description = "Описание 2", date = "02.01.2025", time = "12:00"))
            dao.insertTask(Task(title = "Тест 3", description = "Описание 3", date = "03.01.2025", time = "14:00"))
            Log.d("DB_TEST", "Добавлено 3 задачи")

            // 2. Получаем все задачи
            val allTasks = dao.getAllTasks()
            Log.d("DB_TEST", "Всего задач в БД: ${allTasks.size}")
            allTasks.forEach { task ->
                Log.d("DB_TEST", "ID: ${task.id}, Title: ${task.title}, Date: ${task.date}, Time: ${task.time}")
            }

            // 3. Получаем задачи за конкретную дату
            val tasksByDate = dao.getTasksByDate("02.01.2025")
            Log.d("DB_TEST", "Задач на 02.01.2025: ${tasksByDate.size}")
            tasksByDate.forEach { task ->
                Log.d("DB_TEST", "Найдена задача: ${task.title}")
            }
        }
    }

    private fun loadTasksFromDatabase() {
        lifecycleScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                database.taskDao().getAllTasks()
            }
            taskList.clear()
            taskList.addAll(tasks)
            adapter.notifyDataSetChanged()
            Log.d("DB_TEST", "Загружено задач из БД: ${tasks.size}")
        }
    }
}
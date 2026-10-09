package com.example.mykalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var btnTestDb: Button
    private lateinit var adapter: TaskAdapter

    private val taskList = mutableListOf<Task>()

    // Получаем ViewModel через делегат by viewModels()
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(applicationContext).taskDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        btnTestDb = findViewById(R.id.btnTestDb)

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

        // Кнопка теста БД — теперь используем ViewModel, а не Dao
        btnTestDb.setOnClickListener {
            viewModel.addTask(Task(title = "Тест ${System.currentTimeMillis() % 100}",
                description = "Описание", date = "01.01.2025", time = "10:00"))
            Log.d("DB_TEST", "Задача добавлена через ViewModel")
        }

        // Подписка на StateFlow из ViewModel (Задание 18)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTasks.collect { tasks ->
                    Log.d("DB_TEST", "Получено задач из ViewModel: ${tasks.size}")
                    taskList.clear()
                    taskList.addAll(tasks)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}
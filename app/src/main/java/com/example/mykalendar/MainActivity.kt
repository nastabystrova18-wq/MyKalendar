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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.appcompat.app.AlertDialog

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
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("TASK_ID", task.id) // Передаём ID для редактирования
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Свайп для удаления (Задание 25)
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskToDelete = taskList[position]

                // Показываем диалог подтверждения
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.delete_confirmation)
                    .setMessage(taskToDelete.title)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        viewModel.deleteTask(taskToDelete)
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                        // Возвращаем элемент на место, если отменили
                        adapter.notifyItemChanged(position)
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            // Не передаём TASK_ID — значит, режим создания
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
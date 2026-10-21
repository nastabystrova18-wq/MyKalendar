package com.example.mykalendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var tilTitle: TextInputLayout
    private lateinit var etTitle: TextInputEditText
    private lateinit var tilDescription: TextInputLayout
    private lateinit var etDescription: TextInputEditText
    private lateinit var tilDate: TextInputLayout
    private lateinit var etDate: TextInputEditText
    private lateinit var tilTime: TextInputLayout
    private lateinit var etTime: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TaskRepository(AppDatabase.getDatabase(applicationContext).taskDao())
        )
    }

    private var taskId: Int = -1 // -1 означает режим создания
    private var existingTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        tilTitle = findViewById(R.id.tilTitle)
        etTitle = findViewById(R.id.etTitle)
        tilDescription = findViewById(R.id.tilDescription)
        etDescription = findViewById(R.id.etDescription)
        tilDate = findViewById(R.id.tilDate)
        etDate = findViewById(R.id.etDate)
        tilTime = findViewById(R.id.tilTime)
        etTime = findViewById(R.id.etTime)
        btnSave = findViewById(R.id.btnSave)

        // Получаем ID из Intent (если перешли из списка для редактирования)
        taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            // Режим редактирования: загружаем задачу
            loadTaskForEdit(taskId)
            title = getString(R.string.edit_task_title)
        } else {
            title = getString(R.string.add_task_title)
        }

        // Настройка диалогов выбора даты и времени
        setupDatePicker()
        setupTimePicker()

        // Обработка кнопки сохранения
        btnSave.setOnClickListener {
            saveTask()
        }
    }

    private fun setupDatePicker() {
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Форматируем дату как dd.MM.yyyy
                    val formattedDate = String.format(
                        "%02d.%02d.%04d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    etDate.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun setupTimePicker() {
        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    etTime.setText(formattedTime)
                },
                hour, minute, true // is24HourView = true
            )
            timePickerDialog.show()
        }
    }

    private fun saveTask() {
        val titleText = etTitle.text?.toString()?.trim() ?: ""
        val descriptionText = etDescription.text?.toString()?.trim() ?: ""
        val dateText = etDate.text?.toString()?.trim() ?: ""
        val timeText = etTime.text?.toString()?.trim() ?: ""

        // Валидация (Задание 22)
        tilTitle.error = null
        tilDate.error = null
        tilTime.error = null

        var isValid = true

        if (titleText.isEmpty()) {
            tilTitle.error = getString(R.string.error_empty_title)
            isValid = false
        }
        if (dateText.isEmpty()) {
            tilDate.error = getString(R.string.error_empty_date)
            isValid = false
        }
        if (timeText.isEmpty()) {
            tilTime.error = getString(R.string.error_empty_time)
            isValid = false
        }

        if (!isValid) {
            return // Не сохраняем, если есть ошибки
        }

        val task = existingTask?.copy(
            title = titleText,
            description = descriptionText,
            date = dateText,
            time = timeText
        ) ?: Task(
            title = titleText,
            description = descriptionText,
            date = dateText,
            time = timeText
        )

        if (taskId != -1 && existingTask != null) {
            // Режим редактирования
            viewModel.updateTask(task)
            Toast.makeText(this, "Задача обновлена", Toast.LENGTH_SHORT).show()
        } else {
            // Режим создания
            viewModel.addTask(task)
            Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show()
        }

        finish() // Возвращаемся на главный экран
    }

    // Задание 24: загрузка задачи для редактирования
    private fun loadTaskForEdit(id: Int) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTasks.collect { tasks ->
                    val task = tasks.find { it.id == id }
                    if (task != null) {
                        existingTask = task
                        etTitle.setText(task.title)
                        etDescription.setText(task.description)
                        etDate.setText(task.date)
                        etTime.setText(task.time)
                    }
                }
            }
        }
    }
}
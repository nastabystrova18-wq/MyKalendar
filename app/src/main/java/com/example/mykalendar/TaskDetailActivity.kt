package com.example.mykalendar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Получаем ID, который передали из MainActivity
        val taskId = intent.getIntExtra("TASK_ID", -1)

        // Пока просто покажем его в Toast (всплывающее сообщение)
        if (taskId != -1) {
            Toast.makeText(this, "Открыта задача с ID: $taskId", Toast.LENGTH_SHORT).show()
        }
    }
}
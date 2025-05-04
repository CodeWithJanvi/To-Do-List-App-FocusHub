package com.example.myapplication1

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private val taskList: MutableList<Task>,
    private val db: DatabaseHandler,
    private val onStarClick: (Task) -> Unit,
    private val onEditClick: (Task, Int) -> Unit,
    private val onDeleteClick: (Task, Int) -> Unit,
    private val onTaskChecked: (Task, Boolean) -> Unit // ✅ Ensure this exists
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {



    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkTask)
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskTimeDate: TextView = itemView.findViewById(R.id.taskTime)
        val starButton: ImageButton = itemView.findViewById(R.id.starIcon)
        val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.taskTitle
        holder.taskTimeDate.text = "Time: ${task.taskTime} | Date: ${task.taskDate}"

        // ✅ Prevent multiple listener triggers
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.isCompleted
        applyCompletionEffect(holder.taskTitle, task.isCompleted)

        // ⭐ Handle Star Button Click
        holder.starButton.setOnClickListener {
            try {
                val newStarStatus = !task.isStarred
                val isUpdated = db.updateTaskStarStatus(task.id, newStarStatus)

                if (isUpdated) {
                    task.isStarred = newStarStatus
                    notifyItemChanged(position)
                    val message = if (newStarStatus) "⭐ Task Starred!" else "✖ Task Unstarred!"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "❌ Error starring task!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("TaskAdapter", "Error updating star status", e)
                Toast.makeText(context, "⚠ Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Handle Checkbox Change (Task Completion) ✅
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            try {
                task.isCompleted = isChecked
                db.updateTaskCompletionStatus(task.id, isChecked) // ✅ Update in DB
                applyCompletionEffect(holder.taskTitle, isChecked)

                if (isChecked) {
                    db.markTaskAsCompleted(task)  // ✅ Move to CompletedTasks
                    taskList.removeAt(holder.adapterPosition) // ✅ Remove from PendingTasks UI
                    notifyItemRemoved(holder.adapterPosition)
                    notifyItemRangeChanged(holder.adapterPosition, taskList.size)
                    Toast.makeText(context, "✔ Task Completed!", Toast.LENGTH_SHORT).show()
                } else {
                    db.moveTaskToPending(task)  // ✅ Move back to PendingTasks
                    notifyItemChanged(holder.adapterPosition)
                    Toast.makeText(context, "↩ Task Moved to Pending!", Toast.LENGTH_SHORT).show()
                }

                onTaskChecked(task, isChecked) // ✅ Call the lambda function

            } catch (e: Exception) {
                Log.e("TaskAdapter", "Error updating task completion", e)
                Toast.makeText(context, "Error updating task", Toast.LENGTH_SHORT).show()
            }
        }


        // ✏ Edit Task
        holder.editButton.setOnClickListener { onEditClick(task, position) }

        // ❌ Delete Task
        holder.deleteButton.setOnClickListener { showDeleteConfirmation(task, position) }
    }

    override fun getItemCount(): Int = taskList.size

    // ✅ Apply Strikethrough Effect to Completed Tasks
    private fun applyCompletionEffect(textView: TextView, isCompleted: Boolean) {
        if (isCompleted) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    // 🗑 Delete Confirmation Dialog
    private fun showDeleteConfirmation(task: Task, position: Int) {
        android.app.AlertDialog.Builder(context)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                db.deleteTask(task.id)
                taskList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, taskList.size)
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun updateTasks(newTasks: List<Task>) {
        taskList.clear()
        taskList.addAll(newTasks)
        notifyDataSetChanged()
    }

}

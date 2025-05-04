package com.example.myapplication1

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 14 // Updated version for new column
        private const val DATABASE_NAME = "FocusHubDB"

        // Tasks Table
        const val TABLE_TASKS = "Tasks"
        const val COLUMN_TASK_ID = "id"
        const val COLUMN_TASK_TITLE = "task_title"
        const val COLUMN_TASK_DATE = "date"
        const val COLUMN_TASK_TIME = "time"
        const val COLUMN_IS_STARRED = "is_starred"
        const val COLUMN_IS_COMPLETED = "is_completed" // New column

        // Starred Tasks Table
        const val TABLE_STARRED_TASKS = "StarredTasks"
        const val COLUMN_STAR_ID = "id"
        const val COLUMN_STAR_TITLE = "taskTitle"
        const val COLUMN_STAR_DATE = "taskDate"
        const val COLUMN_STAR_TIME = "taskTime"

        // Completed Tasks Table
        const val TABLE_COMPLETED_TASKS = "CompletedTasks"
        const val COLUMN_COMPLETED_ID = "id"
        const val COLUMN_COMPLETED_TITLE = "taskTitle"
        const val COLUMN_COMPLETED_DATE = "taskDate"
        const val COLUMN_COMPLETED_TIME = "taskTime"

        // User Table
        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTasksTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK_TITLE TEXT NOT NULL,
                $COLUMN_TASK_DATE TEXT NOT NULL,
                $COLUMN_TASK_TIME TEXT NOT NULL,
                $COLUMN_IS_STARRED INTEGER DEFAULT 0,
                $COLUMN_IS_COMPLETED INTEGER DEFAULT 0
            )
        """.trimIndent()

        val createStarredTasksTable = """
            CREATE TABLE $TABLE_STARRED_TASKS (
                $COLUMN_STAR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_STAR_TITLE TEXT NOT NULL,
                $COLUMN_STAR_DATE TEXT NOT NULL,
                $COLUMN_STAR_TIME TEXT NOT NULL
            )
        """.trimIndent()

        val createCompletedTasksTable = """
            CREATE TABLE $TABLE_COMPLETED_TASKS (
                $COLUMN_COMPLETED_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COMPLETED_TITLE TEXT NOT NULL,
                $COLUMN_COMPLETED_DATE TEXT NOT NULL,
                $COLUMN_COMPLETED_TIME TEXT NOT NULL
            )
        """.trimIndent()

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTasksTable)
        db.execSQL(createStarredTasksTable)
        db.execSQL(createCompletedTasksTable)
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STARRED_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPLETED_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")


        onCreate(db)
    }

    // ✅ Add New Task
    fun addTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("task_title", task.taskTitle)
            put("date", task.taskDate)
            put("time", task.taskTime)
            put("is_starred", if (task.isStarred) 1 else 0)
            put("is_completed", if (task.isCompleted) 1 else 0)
        }

        val success = db.insert("Tasks", null, values)
        db.close()
        return success
    }




    // ✅ Move Task to Completed List
    fun completeTask(taskId: Int): Boolean {
        val db = this.writableDatabase
        val task = getTaskById(taskId)

        if (task != null) {
            val values = ContentValues().apply {
                put(COLUMN_COMPLETED_TITLE, task.taskTitle)
                put(COLUMN_COMPLETED_DATE, task.taskDate)
                put(COLUMN_COMPLETED_TIME, task.taskTime)
            }

            val result = db.insert(TABLE_COMPLETED_TASKS, null, values)

            if (result != -1L) {
                deleteTask(taskId) // Remove from pending tasks
            }

            db.close()
            return result != -1L
        }

        return false
    }

    fun getTaskById(taskId: Int): Task? {
        val db = this.readableDatabase
        var task: Task? = null
        val query = "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_TASK_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(taskId.toString()))

        if (cursor.moveToFirst()) {
            task = Task(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)),
                taskDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)),
                taskTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TIME)),
                isStarred = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_STARRED)) == 1,
                isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
            )
        }
        cursor.close()
        db.close()
        return task
    }



    // ✅ Get Task by ID
    
    // ✅ Delete Task
    fun deleteTask(taskId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_TASKS, "$COLUMN_TASK_ID=?", arrayOf(taskId.toString()))
        db.close()

        return result > 0
    }

    // ✅ User Registration
    fun registerUser(user: Register): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.userName) // ✅ Extract the name as String
            put(COLUMN_EMAIL, user.userEmail)
            put(COLUMN_PASSWORD, user.userPassword) // TODO: Hash passwords before storing
        }

        val result = db.insert(TABLE_USERS, null, values)
        db.close()

        return result != -1L
    }




    // ✅ User Login
    fun loginUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        val isLoggedIn = cursor.count > 0
        cursor.close()
        db.close()
        return isLoggedIn
    }

    @SuppressLint("Range")
    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS", null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                    taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)),
                    taskDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)),
                    taskTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TIME)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
                            // ✅ Fixed
                    isStarred = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_STARRED)) == 1


                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return taskList // ✅ Returns a list of all tasks
    }

    fun updateTaskStarStatus(taskId: Int, isStarred: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("is_starred", if (isStarred) 1 else 0)

        val result = db.update("Tasks", values, "id=?", arrayOf(taskId.toString()))
        db.close()

        return result > 0 // ✅ Returns true if update is successful
    }


    fun updateTask(task: Task): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK_TITLE, task.taskTitle)
            put(COLUMN_TASK_DATE, task.taskDate)
            put(COLUMN_TASK_TIME, task.taskTime)
            put(COLUMN_IS_STARRED, if (task.isStarred) 1 else 0)
        }

        val result = db.update(TABLE_TASKS, values, "$COLUMN_TASK_ID=?", arrayOf(task.id.toString()))
        db.close()
        return result > 0  // ✅ Returns true if update was successful
    }

    fun isUserExists(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        val exists = cursor.count > 0  // ✅ Returns true if the email exists in the database

        cursor.close()
        db.close()
        return exists
    }

    fun getStarredTasks(): List<Task> {
        val starredTasks = mutableListOf<Task>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_IS_STARRED = 1"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                    taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)),
                    taskDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)),
                    taskTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TIME)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
                    isStarred = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_STARRED)) == 1


                )
                starredTasks.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return starredTasks
    }





    fun updateTaskCompletionStatus(taskId: Int, isCompleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)

        db.update("Tasks", values, "id=?", arrayOf(taskId.toString()))
        db.close()
    }

    fun getTasksByDate(selectedDate: String): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_TASK_DATE = ? ORDER BY $COLUMN_TASK_TIME ASC"

        val cursor = db.rawQuery(query, arrayOf(selectedDate))

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                    taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)),
                    taskDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)),
                    taskTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TIME)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
                    isStarred = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_STARRED)) == 1
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return taskList
    }


    fun markTaskAsCompleted(task: Task) {
        // Move task to CompletedTasks table


    val db = writableDatabase

        // Insert into CompletedTasks table
        val values = ContentValues().apply {
            put("taskTitle", task.taskTitle)
            put("taskDate", task.taskDate)
            put("taskTime", task.taskTime)
        }
        db.insert("CompletedTasks", null, values)

        // Remove the task from the Tasks table
        db.delete("Tasks", "id = ?", arrayOf(task.id.toString()))

        db.close()
    }

    fun moveTaskToPending(task: Task) {

    }

    fun getPendingTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_IS_COMPLETED = 0"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            taskList.add(
                Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                    taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)),
                    taskDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE)),
                    taskTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TIME)),
                    isStarred = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_STARRED)) == 1,
                    isCompleted = false
                )
            )
        }
        cursor.close()
        db.close()
        return taskList
    }





    fun moveTaskToCompleted(task: Task) {
        addTaskToCompleted(task)
        deleteTask(task.id)
    }

    private fun addTaskToCompleted(task: Task) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("title", task.taskTitle)

            put("date", task.taskDate)
            put("time", task.taskTime)

        }

        // Insert task into the CompletedTasks table
        val result = db.insert("CompletedTasks", null, contentValues)

        if (result != -1L) {
            // If insertion is successful, remove the task from the Tasks table
            db.delete("Tasks", "id=?", arrayOf(task.id.toString()))
        }

        db.close()
    }

    fun getCompletedTasks(): List<TaskModel> {
        val taskList = mutableListOf<TaskModel>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_COMPLETED_ID, $COLUMN_COMPLETED_TITLE FROM $TABLE_COMPLETED_TASKS"

        try {
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val task = TaskModel(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_ID)),
                        taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_TITLE))
                    )
                    taskList.add(task)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHandler", "Error fetching completed tasks", e)
        } finally {
            db.close()
        }
        return taskList
    }




}




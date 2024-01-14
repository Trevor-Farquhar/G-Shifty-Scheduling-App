package com.example.mycmpt395proj

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate


/*

TODO:SHIFT TYPE MAPPING //////////////////////////////////////////////////////////////////////////////

    MONDAY MORNING == 1
    MONDAY AFTERNOON == 2
    TUESDAY MORNING == 3
    TUESDAY AFTERNOON == 4
    WEDNESDAY MORNING == 5
    WEDNESDAY AFTERNOON == 6
    THURSDAY MORNING == 7
    THURSDAY AFTERNOON == 8
    FRIDAY MORNING == 9
    FRIDAY AFTERNOON == 10
    SATURDAY == 11
    SUNDAY == 12

TODO: SHIFT TYPE MAPPING /////////////////////////////////////////////////////////////////////////////

 */


data class ScheduledEmployee(
    val id: Long,
    val employeeId: Long,
    val date: String, // Date in a suitable format (e.g., "YYYY-MM-DD")
    val shiftType: Int // Shift type (e.g., Employee.MONDAY_MORNING, Employee.TUESDAY_MORNING, etc.) get from Employee
)

class ScheduledEmployeeDataBase (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EmployeeScheduler.db"
        private const val TABLE_SCHEDULED_EMPLOYEES = "scheduled_employees_db"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMPLOYEE_ID = "employee_id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_SHIFT_TYPE = "shift_type"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = "CREATE TABLE $TABLE_SCHEDULED_EMPLOYEES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_EMPLOYEE_ID INTEGER," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_SHIFT_TYPE INTEGER);"
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //TODO: implement this function later
    }

    fun addScheduledEmployee(employee: ScheduledEmployee): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMPLOYEE_ID, employee.employeeId)
        values.put(COLUMN_DATE, employee.date)
        values.put(COLUMN_SHIFT_TYPE, employee.shiftType)
        val id = db.insert(TABLE_SCHEDULED_EMPLOYEES, null, values)

        return id
    }

    fun getScheduledEmployeesForDate(date: String): List<ScheduledEmployee> {
        val employees = mutableListOf<ScheduledEmployee>()
        val query = "SELECT * FROM $TABLE_SCHEDULED_EMPLOYEES WHERE $COLUMN_DATE = ?"
        val db = readableDatabase
        val cursor = db.rawQuery(query, arrayOf(date))
        Log.d("DatabaseQuery", "Query: $query, Date: $date")

        if (cursor.count > 0) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(0)
                    val employeeId = cursor.getLong(1)
                    val shiftType = cursor.getInt(3)
                    employees.add(ScheduledEmployee(id, employeeId, date, shiftType))
                } while (cursor.moveToNext())
            }
        }

        cursor.close()

        return employees
    }

    fun removeEmployeeFromShift(employeeId: Long, date: String): Boolean {
        val db = writableDatabase

        // Define the WHERE clause to identify the row to delete
        val whereClause = "$COLUMN_EMPLOYEE_ID = ? AND $COLUMN_DATE = ?"
        val whereArgs = arrayOf(employeeId.toString(), date)

        // Perform the delete operation
        val rowsAffected = db.delete(TABLE_SCHEDULED_EMPLOYEES, whereClause, whereArgs)


        // Return true if at least one row was affected (i.e., the employee was removed), false otherwise
        return rowsAffected > 0
    }


    private fun getPreviousSunday(date: String): String {
        Log.d("Date In getPrevSunday", "$date")
        var startDate = LocalDate.parse(date)
        while (startDate.dayOfWeek != DayOfWeek.SUNDAY) {
            startDate = startDate.minusDays(1)
        }

        return startDate.toString()
    }

    private fun getShiftCountPerWeek(employee: Employee, date: String): Int {
        var count = 0
        val startDate = LocalDate.parse(getPreviousSunday(date))
        Log.d("Date", "$startDate")
        for (i in 0..7) {
            val newDate = startDate.plusDays(i.toLong())
            val schedule = getScheduledEmployeesForDate(newDate.toString())
            for (shift in schedule) {
                if (employee.id.toLong() == shift.employeeId)
                    count += 1
            }
        }

        return count
    }

    fun updateEmployeeShiftCounts(employees: List<Employee>, date: String): List<Employee> {
        val employees2 = employees.toMutableList()
        for (employee in employees2) {
            employee.shiftCountPerWeekShift = getShiftCountPerWeek(employee, date)
        }
        return employees2
    }
}
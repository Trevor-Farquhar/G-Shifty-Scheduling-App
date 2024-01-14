package com.example.mycmpt395proj

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.widget.Toast
import java.time.DayOfWeek
import java.time.LocalDate


data class Employee (
    var id: Int = -1,
    var firstname: String,
    var lastname: String,
    var email: String,
    var phone: String,
    var trainedopening: Int,
    var trainedclosing: Int,
    var daysOff: String,
    var shiftpref: Int,
    var shiftCountPerWeekShift: Int
) {
    // Parameters
    // week: the week of the month (0-3)
    // shiftType: is in employee data
    companion object { //This is to associate certain shifts with the employe. Ex) John Doe works monday mornings, and saturdays
        // Bit fields for the class
        // This is the shiftType
        const val MONDAY_MORNING = 1
        const val TUESDAY_MORNING = 1 shl 1
        const val WEDNESDAY_MORNING = 1 shl 2
        const val THURSDAY_MORNING = 1 shl 3
        const val FRIDAY_MORNING = 1 shl 4
        const val MONDAY_AFTERNOON = 1 shl 5
        const val TUESDAY_AFTERNOON = 1 shl 6
        const val WEDNESDAY_AFTERNOON = 1 shl 7
        const val THURSDAY_AFTERNOON = 1 shl 8
        const val FRIDAY_AFTERNOON = 1 shl 9
        const val SATURDAY = 1 shl 10
        const val SUNDAY = 1 shl 11
    }


    fun checkShiftPreference(employee: Employee, shiftType: Int): Boolean {
        val valueChecked = employee.shiftpref and shiftType
        return valueChecked > 0
    }

    fun setShiftPreference(employee: Employee, shiftType: Int, isOnShift: Boolean): Int {
        assert(shiftType <= SUNDAY)

        return if (isOnShift) {
            employee.shiftpref.or(shiftType)
        } else {
            employee.shiftpref.and(shiftType.inv())
        }
        return 0
    }
}

class EmployeeComparator(): Comparator<Employee> {
    // Comparison function to determine the order of employees.
    override fun compare(a: Employee, b: Employee): Int {

        // If weekly shift count differs, prioritize based on weekly shift count.
        if (a.shiftCountPerWeekShift != b.shiftCountPerWeekShift)
            return a.shiftCountPerWeekShift compareTo b.shiftCountPerWeekShift

        // If all comparisons are equal, return 0.
        return 0
    }
}





class EmployeeDataBaseOperations(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "EmployeeInfo.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "employeeDB"
        const val COL_FIRSTNAME = "firstname"
        const val COL_LASTNAME = "lastname"
        const val COL_EMAIL = "email"
        const val COL_PHONE = "phone" // Added the phone column here
        const val COL_TRAINEDOPENING = "trainedopening"
        const val COL_TRAINEDCLOSING = "trainedclosing"
        const val COL_DAYSOFF = "daysoff"
        const val COL_ID = "_id"
        const val COL_SHIFT_PREF = "shiftpreference"
    }


    override fun onCreate(db: SQLiteDatabase) {

        val createTableSQL = """
            CREATE TABLE $TABLE_NAME (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_FIRSTNAME TEXT,
            $COL_LASTNAME TEXT,
            $COL_EMAIL TEXT,
            $COL_PHONE TEXT,
            $COL_TRAINEDOPENING INTEGER,
            $COL_TRAINEDCLOSING INTEGER,
            $COL_DAYSOFF TEXT,
            $COL_SHIFT_PREF INTEGER
            )
        """.trimIndent()

        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN new_column TEXT")
        }
    }
    fun isNameAlreadyTaken(employee: Employee): Boolean {
        val db = readableDatabase

        val query = "SELECT $COL_FIRSTNAME, $COL_LASTNAME FROM $TABLE_NAME WHERE $COL_FIRSTNAME = ? AND $COL_LASTNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(employee.firstname, employee.lastname))

        val nameExists = cursor.moveToFirst()

        cursor.close()
        db.close()

        return nameExists
    }

    fun isFirstNameTaken(employee: Employee): Boolean {
        val db = readableDatabase

        val query = "SELECT $COL_FIRSTNAME FROM $TABLE_NAME WHERE $COL_FIRSTNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(employee.firstname))

        val nameExists = cursor.moveToFirst()

        cursor.close()
        db.close()

        return nameExists
    }

    fun insertData(employee: Employee): Long {
        val db = writableDatabase
        val values = ContentValues()


        values.put(COL_FIRSTNAME, employee.firstname)
        values.put(COL_LASTNAME, employee.lastname)
        values.put(COL_EMAIL, employee.email)
        values.put(COL_PHONE, employee.phone) // Set the value for the phone column
        values.put(COL_TRAINEDOPENING, employee.trainedopening)
        values.put(COL_TRAINEDCLOSING, employee.trainedclosing)
        values.put(COL_DAYSOFF, employee.daysOff)
        values.put(COL_SHIFT_PREF, employee.shiftpref)

        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        return success
    }

    fun getEmployee(id: Int): Employee {
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PHONE,
                COL_TRAINEDOPENING, COL_TRAINEDCLOSING, COL_DAYSOFF, COL_SHIFT_PREF),
            "$COL_ID=?",
            arrayOf(id.toString()),
            null, null, null, null
        )

        if (cursor.moveToFirst()) {
            val employee = Employee(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4), // Retrieve the phone column as a string
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getString(7),
                cursor.getInt(8),
                0

            )
            cursor.close()
            return employee
        } else {
            cursor?.close()
            return Employee(0, "", "", "", "", 0, 0, "", 0, 0)
        }

    }

    @SuppressLint("Range")
    fun getAllEmployees(): List<Employee> {
        val employeeList = mutableListOf<Employee>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PHONE, COL_TRAINEDOPENING,
                COL_TRAINEDCLOSING, COL_DAYSOFF, COL_SHIFT_PREF),
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val employee = Employee(
                cursor.getInt(cursor.getColumnIndex(COL_ID)),
                cursor.getString(cursor.getColumnIndex(COL_FIRSTNAME)),
                cursor.getString(cursor.getColumnIndex(COL_LASTNAME)),
                cursor.getString(cursor.getColumnIndex(COL_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COL_PHONE)),
                cursor.getInt(cursor.getColumnIndex(COL_TRAINEDOPENING)),
                cursor.getInt(cursor.getColumnIndex(COL_TRAINEDCLOSING)),
                cursor.getString(cursor.getColumnIndex(COL_DAYSOFF)),
                cursor.getInt(cursor.getColumnIndex(COL_SHIFT_PREF)),
                0
            )
            employeeList.add(employee)
        }

        cursor.close()
        return employeeList
    }

    fun getEmployeesSorted(context: Context, date: String, shiftType: Int): List<Employee> {
        val comparator = EmployeeComparator()
        val db = ScheduledEmployeeDataBase(context)
        val shifts = db.getScheduledEmployeesForDate(date)
        val employeesOnShiftIds = shifts.map { it.employeeId.toInt() }

        val scheduledType = when(LocalDate.parse(date).dayOfWeek) {
            DayOfWeek.MONDAY -> Employee.MONDAY_AFTERNOON or Employee.MONDAY_MORNING
            DayOfWeek.TUESDAY -> Employee.TUESDAY_AFTERNOON or Employee.TUESDAY_MORNING
            DayOfWeek.WEDNESDAY -> Employee.WEDNESDAY_AFTERNOON or Employee.WEDNESDAY_MORNING
            DayOfWeek.THURSDAY -> Employee.THURSDAY_AFTERNOON or Employee.THURSDAY_MORNING
            DayOfWeek.FRIDAY -> Employee.FRIDAY_AFTERNOON or Employee.FRIDAY_MORNING
            DayOfWeek.SATURDAY -> Employee.SATURDAY
            DayOfWeek.SUNDAY -> Employee.SUNDAY
        }

        // here is  a comment explainging stuff
        // Filter out employees already scheduled for the shift
        var filteredEmployeeList = getAllEmployees().filter { it.id !in employeesOnShiftIds && it.checkShiftPreference(it, scheduledType)}


        filteredEmployeeList = db.updateEmployeeShiftCounts(filteredEmployeeList, date)
        // Sort the filtered list using the comparator
        val sortedEmployeeList = filteredEmployeeList.sortedWith(comparator)

        return sortedEmployeeList
    }


    fun updateData(employee: Employee): Int {
        val db = writableDatabase
        val values = ContentValues()

        values.put(COL_FIRSTNAME, employee.firstname)
        values.put(COL_LASTNAME, employee.lastname)
        values.put(COL_EMAIL, employee.email)
        values.put(COL_PHONE, employee.phone)
        values.put(COL_TRAINEDOPENING, employee.trainedopening)
        values.put(COL_TRAINEDCLOSING, employee.trainedclosing)
        values.put(COL_DAYSOFF, employee.daysOff)
        values.put(COL_SHIFT_PREF, employee.shiftpref)

        val whereClause = "$COL_ID = ?"
        val whereArgs = arrayOf(employee.id.toString())

        val rowsAffected = db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()

        return rowsAffected
    }

    fun getDatabaseSize(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

}
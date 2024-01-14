package com.example.mycmpt395proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.E

class CreateSchedule_PickEmployee : AppCompatActivity(),  EmployeeRecAdapterSchedulePicker.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var EmployeeRecAdapterSchedulePicker: EmployeeRecAdapterSchedulePicker
    private lateinit var employeeList: List<Employee>
    private lateinit var sqlitescheduledb: ScheduledEmployeeDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_schedule_pick_employee)
        sqlitescheduledb = ScheduledEmployeeDataBase(this)

        //ELEMENT DECLARATIONS
        val backBTN: ImageButton = findViewById(R.id.pick_employee_backBTN)
        val listOfEmployee: RecyclerView = findViewById(R.id.pick_employee_RecView)
        val dateview: TextView = findViewById(R.id.pick_employee_textview)

        val dayOfWeek = intent.getStringExtra("dayofweek")
        val dateTime = intent.getStringExtra("date")
        val shiftType = intent.getStringExtra("shiftType")


        dateview.text = "Date Selected: $dateTime, $dayOfWeek"

//        Toast.makeText(this, "$dayOfWeek, $dateTime", Toast.LENGTH_SHORT).show()



        //BUTTON DECLARATION
        backBTN.setOnClickListener{
            if (dayOfWeek != "Saturday" && dayOfWeek != "Sunday") {
                val backBTNweekday = Intent(this, CreateSchedule_Weekday::class.java)
                backBTNweekday.putExtra("date", dateTime)
                backBTNweekday.putExtra("dayofweek", dayOfWeek)
                backBTNweekday.putExtra("shiftType", shiftType)
                startActivity(backBTNweekday)
            }
            else if (dayOfWeek == "Saturday" || dayOfWeek == "Sunday"){
                val backBTNWeekend = Intent(this, CreateSchedule_Weekend::class.java)
                backBTNWeekend.putExtra("date", dateTime)
                backBTNWeekend.putExtra("dayofweek", dayOfWeek)
                startActivity(backBTNWeekend)
            }
        }

        val employeeShiftTypeType = 0



        val db = EmployeeDataBaseOperations(this)
        Toast.makeText(this, "$dayOfWeek, $shiftType", Toast.LENGTH_SHORT).show()

        //Make changes here for employee list
        //Do a shit ton checks here
        employeeList = db.getEmployeesSorted(this, dateTime.toString(), getShiftTypeForSorting(dayOfWeek, shiftType))
        //Change ^^^

        EmployeeRecAdapterSchedulePicker = EmployeeRecAdapterSchedulePicker(this, employeeList, this)
        listOfEmployee.adapter = EmployeeRecAdapterSchedulePicker
        listOfEmployee.layoutManager = LinearLayoutManager(this)

    }

    override fun onItemClick(employee: Employee){
        val employeeId = employee.id.toLong()
        val shiftType = intent.getStringExtra("shiftType").toString()
        val dayOfWeek = intent.getStringExtra("dayofweek").toString()
        val dateTime = intent.getStringExtra("date").toString()
        val db = ScheduledEmployeeDataBase(this)

        var dbshiftType = 0

        fun findEmployeePosition(employee: Employee): Int {
            for ((index, emp) in employeeList.withIndex()) {
                if (emp.id == employee.id) {
                    return index
                }
            }
            return -1 // Employee not found
        }

        val position = findEmployeePosition(employee)

        if (shiftType == "Morning" && dayOfWeek == "Monday") {dbshiftType = Employee.MONDAY_MORNING}
        else if (shiftType == "Afternoon" && dayOfWeek == "Monday") {dbshiftType = Employee.MONDAY_AFTERNOON}
        else if (shiftType == "Morning" && dayOfWeek == "Tuesday") {dbshiftType = Employee.TUESDAY_MORNING}
        else if (shiftType == "Afternoon" && dayOfWeek == "Tuesday") {dbshiftType = Employee.TUESDAY_AFTERNOON}
        else if (shiftType == "Morning" && dayOfWeek == "Wednesday") {dbshiftType = Employee.WEDNESDAY_MORNING}
        else if (shiftType == "Afternoon" && dayOfWeek == "Wednesday") {dbshiftType = Employee.WEDNESDAY_AFTERNOON}
        else if (shiftType == "Morning" && dayOfWeek == "Thursday") {dbshiftType = Employee.THURSDAY_MORNING}
        else if (shiftType == "Afternoon" && dayOfWeek == "Thursday") {dbshiftType = Employee.THURSDAY_AFTERNOON}
        else if (shiftType == "Morning" && dayOfWeek == "Friday") {dbshiftType = Employee.FRIDAY_MORNING}
        else if (shiftType == "Afternoon" && dayOfWeek == "Friday") {dbshiftType = Employee.FRIDAY_AFTERNOON}
        else if (shiftType == "Weekend" && dayOfWeek == "Saturday") {dbshiftType = Employee.SATURDAY}
        else if (shiftType == "Weekend" && dayOfWeek == "Sunday") {dbshiftType = Employee.SUNDAY}
        else {Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()}

        val scheduledEmployee = ScheduledEmployee(id = employeeId, employeeId = employeeId, date = dateTime, shiftType = dbshiftType)

        val status = sqlitescheduledb.addScheduledEmployee(scheduledEmployee)

        if (status > -1) {
            Toast.makeText(this, "Employee Added Successfully", Toast.LENGTH_SHORT).show()
            if (dayOfWeek != "Saturday" && dayOfWeek != "Sunday") {
                val backBTNweekday = Intent(this, CreateSchedule_Weekday::class.java)
                backBTNweekday.putExtra("date", dateTime)
                backBTNweekday.putExtra("dayofweek", dayOfWeek)
                backBTNweekday.putExtra("shiftType", shiftType)
                startActivity(backBTNweekday)
            }
            else if (dayOfWeek == "Saturday" || dayOfWeek == "Sunday"){
                val backBTNWeekend = Intent(this, CreateSchedule_Weekend::class.java)
                backBTNWeekend.putExtra("date", dateTime)
                backBTNWeekend.putExtra("dayofweek", dayOfWeek)
                startActivity(backBTNWeekend)
            }
        }

        else {
            Toast.makeText(this, "Record not saved. Check all fields are filled", Toast.LENGTH_LONG).show()
        }

    }
}


private fun getShiftTypeForSorting(dayOfWeek: String?, shiftType: String?): Int {


    return when (dayOfWeek) {
        "Monday" -> {
            when (shiftType) {
                "Morning" -> Employee.MONDAY_MORNING
                "Afternoon" -> Employee.MONDAY_AFTERNOON
                else -> throw IllegalArgumentException("Invalid shiftType: $shiftType")
            }
        }
        "Tuesday" -> {
            when (shiftType) {
                "Morning" -> Employee.TUESDAY_MORNING
                "Afternoon" -> Employee.TUESDAY_AFTERNOON
                else -> throw IllegalArgumentException("Invalid shiftType: $shiftType")
            }
        }
        "Wednesday" -> {
            when (shiftType) {
                "Morning" -> Employee.WEDNESDAY_MORNING
                "Afternoon" -> Employee.WEDNESDAY_AFTERNOON
                else -> throw IllegalArgumentException("Invalid shiftType: $shiftType")
            }
        }
        "Thursday" -> {
            when (shiftType) {
                "Morning" -> Employee.THURSDAY_MORNING
                "Afternoon" -> Employee.THURSDAY_AFTERNOON
                else -> throw IllegalArgumentException("Invalid shiftType: $shiftType")
            }
        }
        "Friday" -> {
            when (shiftType) {
                "Morning" -> Employee.FRIDAY_MORNING
                "Afternoon" -> Employee.FRIDAY_AFTERNOON
                else -> throw IllegalArgumentException("Invalid shiftType: $shiftType")
            }
        }
        "Saturday", "Sunday" -> {
            if (shiftType == "Weekend") {
                Employee.SATURDAY
            } else {
                throw IllegalArgumentException("Invalid shiftType for weekend: $shiftType")
            }
        }
        else -> throw IllegalArgumentException("Invalid dayOfWeek: $dayOfWeek")
    }
}

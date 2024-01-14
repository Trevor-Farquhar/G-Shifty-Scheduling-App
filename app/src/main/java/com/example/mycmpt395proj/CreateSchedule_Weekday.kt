package com.example.mycmpt395proj

import CustomPopup
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycmpt395proj.EmployeeRecAdapterWeekdayWeekend.OnItemClickListener


class CreateSchedule_Weekday : AppCompatActivity(), OnItemClickListener {

    private lateinit var recyclerViewMorn: RecyclerView
    private lateinit var recyclerViewNoon: RecyclerView
    private lateinit var employeeRecAdapterWeekdayWeekend: EmployeeRecAdapterWeekdayWeekend
    private lateinit var employeeList: List<Employee>
    private lateinit var sqliteschedule: ScheduledEmployeeDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_schedule_weekday)

        sqliteschedule = ScheduledEmployeeDataBase(this)

        //BTN Declarations
        val backBTN: ImageButton = findViewById(R.id.create_schedule_weekday_backBTN)
        val homeBTN: ImageButton = findViewById(R.id.create_schedule_weekday_homeBTN)
        val dateholder: TextView = findViewById(R.id.createScheduleWeekday_DateHolder)
        val applyBTN: Button = findViewById(R.id.createScheduleWeekday_ApplyBTN)

        val addEmpMornBTN: ImageButton = findViewById(R.id.createScheduleWeekdayMorn_plusBTN)
        val addEmpNoonBTN: ImageButton = findViewById(R.id.createScheduleWeekdayNoon_plusBTN)

        //RECYCLER VIEW DECLARATIONS
        recyclerViewMorn = findViewById(R.id.createScheduleWeekday_morn_recview)
        recyclerViewNoon = findViewById(R.id.createScheduleWeekday_noon_recview)

        //INTENT GET
        val dateTime = intent.getStringExtra("date")
        val dayofweek = intent.getStringExtra("dayofweek")
        dateholder.text = "Date Selected: ${dateTime}, ${dayofweek}"
//        Toast.makeText(this, "$dayofweek, $dateTime", Toast.LENGTH_SHORT).show()

        val db = EmployeeDataBaseOperations(this)
        val scdb = ScheduledEmployeeDataBase(this)

        val tempObjectofScheduledEmployees = scdb.getScheduledEmployeesForDate(dateTime.toString())

        fun mapShiftType(shiftType: Int): String {
            return when (shiftType) {
                Employee.MONDAY_MORNING, Employee.TUESDAY_MORNING, Employee.WEDNESDAY_MORNING, Employee.THURSDAY_MORNING, Employee.FRIDAY_MORNING -> "Morning" // These shift types correspond to "Morning"
                Employee.MONDAY_AFTERNOON, Employee.TUESDAY_AFTERNOON, Employee.WEDNESDAY_AFTERNOON, Employee.THURSDAY_AFTERNOON, Employee.FRIDAY_AFTERNOON -> "Afternoon" // These shift types correspond to "Afternoon"
                else -> "Unknown" // Handle any other shift type values
            }
        }

        val morningShiftEmployees = tempObjectofScheduledEmployees.filter { mapShiftType(it.shiftType) == "Morning" }
        val afternoonShiftEmployees = tempObjectofScheduledEmployees.filter { mapShiftType(it.shiftType) == "Afternoon" }

        val listofmorningemployeeIds = getEmployeeIdsFromScheduledEmployees(morningShiftEmployees)
        val listofafternoonemployeeIds = getEmployeeIdsFromScheduledEmployees(afternoonShiftEmployees)

//        Toast.makeText(this, "$listofmorningemployeeIds Morning", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "$listofafternoonemployeeIds Afternoon", Toast.LENGTH_SHORT).show()

        val morningEmployees = mutableListOf<Employee>()
        for (listofmorningemployeeId in listofmorningemployeeIds) {
            val mornemp = db.getEmployee(listofmorningemployeeId.toInt())
            morningEmployees.add(mornemp)
        }

        val afternoonEmployees = mutableListOf<Employee>()
        for (listofafternoonemployeeId in listofafternoonemployeeIds){
            val afteremp = db.getEmployee(listofafternoonemployeeId.toInt())
            afternoonEmployees.add(afteremp)
        }

        val shiftType = intent.getStringExtra("shiftType")
//        Toast.makeText(this, "Shift Type is: $shiftType", Toast.LENGTH_SHORT).show()
        employeeRecAdapterWeekdayWeekend = EmployeeRecAdapterWeekdayWeekend(this, morningEmployees, this, recyclerViewMorn)
        recyclerViewMorn.adapter = employeeRecAdapterWeekdayWeekend
        recyclerViewMorn.layoutManager = LinearLayoutManager(this)

        employeeRecAdapterWeekdayWeekend = EmployeeRecAdapterWeekdayWeekend(this, afternoonEmployees, this, recyclerViewNoon)
        recyclerViewNoon.adapter = employeeRecAdapterWeekdayWeekend
        recyclerViewNoon.layoutManager = LinearLayoutManager(this)

        Log.d("Morning Weekday Array", "$morningEmployees")
        Log.d("Afternoon Weekday Array", "$afternoonEmployees")
        Log.d("Full Schedule Database", "$scdb")

        addEmpMornBTN.setOnClickListener {
            if(morningEmployees.size == 2){
                val alertBackBuilder = AlertDialog.Builder(this)
                alertBackBuilder.setTitle("Too Many Employees")
                alertBackBuilder.setMessage("There can only be 2 employees per shift.")
                alertBackBuilder.setPositiveButton("Okay") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()
            } else {
                val mbIntent = Intent(this, CreateSchedule_PickEmployee::class.java)
                mbIntent.putExtra("date", dateTime)
                mbIntent.putExtra("dayofweek", dayofweek)
                mbIntent.putExtra("shiftType", "Morning")
                startActivity(mbIntent)
            }

        }

        addEmpNoonBTN.setOnClickListener {
            if(afternoonEmployees.size == 2){
                val alertBackBuilder = AlertDialog.Builder(this)
                alertBackBuilder.setTitle("Too Many Employees")
                alertBackBuilder.setMessage("There can only be 2 employees per shift.")
                alertBackBuilder.setPositiveButton("Okay") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()
            } else {
                val nbIntent = Intent(this, CreateSchedule_PickEmployee::class.java)
                nbIntent.putExtra("date", dateTime)
                nbIntent.putExtra("dayofweek", dayofweek)
                nbIntent.putExtra("shiftType", "Afternoon")
                startActivity(nbIntent)
            }
        }


        backBTN.setOnClickListener{
            val isMorningTrained = morningEmployees.any{it.trainedopening == 1}
            val isMorningLessThanTwoEmployees = morningEmployees.size >= 2

            //Afternoon Checks
            val isAfternoonTrained = afternoonEmployees.any { it.trainedclosing == 1 }
            val isAfternoonLessThanTwoEmployees = afternoonEmployees.size >= 2

            // Track error messages
            val errorMessages = StringBuilder()

            // Check for morning shift
            if (!isMorningTrained) {
                errorMessages.append("Ensure someone is trained in the morning.\n\n")
            }

            if (!isMorningLessThanTwoEmployees) {
                errorMessages.append("Ensure two employees are scheduled in the morning.\n\n")
            }

            if (!isAfternoonTrained) {
                errorMessages.append("Ensure someone is trained in the afternoon.\n\n")
            }

            if (!isAfternoonLessThanTwoEmployees) {
                errorMessages.append("Ensure two employees are scheduled in the afternoon.\n\n")
            }

            if (errorMessages.isNotEmpty()) {
                val alertBackBuilder = AlertDialog.Builder(this)
                alertBackBuilder.setTitle("Warning")
                alertBackBuilder.setMessage("There are missing parameters for this day:\n\n$errorMessages" +
                        "\nWould you like to go the Schedule Employee Page and fix it?\n")

                alertBackBuilder.setPositiveButton("Yes") { dialog, which ->
                    val bbIntent = Intent(this, CreateSchedulePage::class.java)
                    startActivity(bbIntent)
                }
                alertBackBuilder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()

//                CustomPopup.show(this, "Warning", errorMessages.toString())
            } else {
                // Your logic when all conditions are met
                Toast.makeText(this, "Day has been updated.", Toast.LENGTH_SHORT).show()
                val BackIntent = Intent(this, CreateSchedulePage::class.java)
                startActivity(BackIntent)

            }

        }

        homeBTN.setOnClickListener {
            val isMorningTrained = morningEmployees.any{it.trainedopening == 1}
            val isMorningLessThanTwoEmployees = morningEmployees.size >= 2

            //Afternoon Checks
            val isAfternoonTrained = afternoonEmployees.any { it.trainedclosing == 1 }
            val isAfternoonLessThanTwoEmployees = afternoonEmployees.size >= 2

            // Track error messages
            val errorMessages = StringBuilder()

            // Check for morning shift
            if (!isMorningTrained) {
                errorMessages.append("Ensure someone is trained in the morning.\n\n")
            }

            if (!isMorningLessThanTwoEmployees) {
                errorMessages.append("Ensure two employees are scheduled in the morning.\n\n")
            }

            if (!isAfternoonTrained) {
                errorMessages.append("Ensure someone is trained in the afternoon.\n\n")
            }

            if (!isAfternoonLessThanTwoEmployees) {
                errorMessages.append("Ensure two employees are scheduled in the afternoon.\n\n")
            }

            if (errorMessages.isNotEmpty()) {
                val alertBackBuilder = AlertDialog.Builder(this)
                alertBackBuilder.setTitle("Warning")
                alertBackBuilder.setMessage("There are missing parameters for this day:\n\n$errorMessages" +
                        "\nWould you like to go the home page anyways?\n")

                alertBackBuilder.setPositiveButton("Yes") { dialog, which ->
                    val bbIntent = Intent(this, MainActivity::class.java)
                    startActivity(bbIntent)
                }
                alertBackBuilder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()

//                CustomPopup.show(this, "Warning", errorMessages.toString())
            } else {
                // Your logic when all conditions are met
                Toast.makeText(this, "Day has been updated.", Toast.LENGTH_SHORT).show()
                val BackIntent = Intent(this, CreateSchedulePage::class.java)
                startActivity(BackIntent)

            }
        }


        applyBTN.setOnClickListener{
            val isMorningTrained = morningEmployees.any{it.trainedopening == 1}
            val isMorningLessThanTwoEmployees = morningEmployees.size >= 2

            //Afternoon Checks
            val isAfternoonTrained = afternoonEmployees.any { it.trainedclosing == 1 }
            val isAfternoonLessThanTwoEmployees = afternoonEmployees.size >= 2

            // Track error messages
            val errorMessages = StringBuilder()

            // Check for morning shift
            if (!isMorningTrained) {
                errorMessages.append("Ensure someone is trained in the morning.\n\n")
            }

            if (!isMorningLessThanTwoEmployees) {
                errorMessages.append("Ensure two employees are scheduled in the morning.\n\n")
            }

            if (!isAfternoonTrained) {
                errorMessages.append("Ensure someone is trained in the afternoon.\n\n")
            }

            if (!isAfternoonLessThanTwoEmployees) {
                errorMessages.append("Ensure two employees are scheduled in the afternoon.\n\n")
            }

            if (errorMessages.isNotEmpty()) {
                CustomPopup.show(this, "Warning", errorMessages.toString())
            } else {
                // Your logic when all conditions are met
                Toast.makeText(this, "Day has been updated.", Toast.LENGTH_SHORT).show()
                val BackIntent = Intent(this, CreateSchedulePage::class.java)
                startActivity(BackIntent)

            }

        }

        //ADD RECYCLER VIEW ON CLICK CONDITIONS HERE

    }

    override fun onBackPressed() {
        val bbIntent = Intent(this, CreateSchedulePage::class.java)
        val alertBackBuilder = AlertDialog.Builder(this)
        alertBackBuilder.setTitle("Warning")
        alertBackBuilder.setMessage("Would You like to return to the calendar?\nAll unsaved changes will be lost.")

        alertBackBuilder.setPositiveButton("Yes") { dialog, which ->
            startActivity(bbIntent)
        }
        alertBackBuilder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val alertBackDialog = alertBackBuilder.create()
        alertBackDialog.show()

    }

    override fun onItemClick(employee: Employee, recyclerView: RecyclerView){
        val dateTime = intent.getStringExtra("date")

        val alertHomeBuilder = AlertDialog.Builder(this)
        alertHomeBuilder.setTitle("Remove Employee")
        alertHomeBuilder.setMessage("Would you like to remove ${employee.firstname} ${employee.lastname} from the shift?")
        alertHomeBuilder.setPositiveButton("Yes") { dialog, which ->

            val position = findEmployeePosition(recyclerView, employee)
            if (position != -1) {
                sqliteschedule.removeEmployeeFromShift(employee.id.toLong(), dateTime.toString())
                removeEmployeeFromRecyclerView(recyclerView, position)
            }
        }
        alertHomeBuilder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val alertHomeDialog = alertHomeBuilder.create()
        alertHomeDialog.show()

    }

}



private fun findEmployeePosition(recyclerView: RecyclerView, employee: Employee): Int {
    val adapter = recyclerView.adapter as? EmployeeRecAdapterWeekdayWeekend
    return adapter?.employeeList?.indexOf(employee) ?: -1
}

private fun removeEmployeeFromRecyclerView(recyclerView: RecyclerView, position: Int) {
    val adapter = recyclerView.adapter as? EmployeeRecAdapterWeekdayWeekend
    adapter?.employeeList?.removeAt(position)
    adapter?.notifyItemRemoved(position)
}

fun getEmployeeIdsFromScheduledEmployees(employees: List<ScheduledEmployee>): List<Long> {
    val employeeIds = mutableListOf<Long>()
    for (employee in employees) {
        employeeIds.add(employee.employeeId)
    }
    return employeeIds
}


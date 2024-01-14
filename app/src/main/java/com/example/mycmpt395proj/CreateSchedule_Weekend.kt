package com.example.mycmpt395proj

import CustomPopup
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CreateSchedule_Weekend : AppCompatActivity(), EmployeeRecAdapterWeekdayWeekend.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeRecAdapterWeekdayWeekend: EmployeeRecAdapterWeekdayWeekend

    private lateinit var sqliteschedule: ScheduledEmployeeDataBase




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_schedule_weekend)
        sqliteschedule = ScheduledEmployeeDataBase(this)

        //ELEMENT DECLARATION
        val backBTN: ImageButton = findViewById(R.id.create_schedule_weekend_backBTN)
        val homeBTN: ImageButton = findViewById(R.id.create_schedule_weekend_homeBTN)
        val addEmpBTN: ImageButton = findViewById(R.id.createScheduleWeekend_plusBTN)
        val dateHolder: TextView = findViewById(R.id.createScheduleWeekend_DateHolder)
        val applyBTN: Button = findViewById(R.id.createScheduleWeekend_applyBTN)

        //INTENT GETS
        val dayOfWeek = intent.getStringExtra("dayofweek")
        val dateTime = intent.getStringExtra("date")

        dateHolder.text = "Date Selected: $dateTime, $dayOfWeek"

        //RECYCLER VIEW DECLARATIONS
        recyclerView = findViewById(R.id.createScheduleWeekend_recview)


        val db = EmployeeDataBaseOperations(this)
        val scdb = ScheduledEmployeeDataBase(this)
        val tempObjectofScheduledEmployees = scdb.getScheduledEmployeesForDate(dateTime.toString())

        val listofweekendemployeeIDs = getEmployeeIdsFromScheduledEmployees(tempObjectofScheduledEmployees)

//        Toast.makeText(this, "$listofweekendemployeeIDs Morning", Toast.LENGTH_SHORT).show()

        val ListofEmployees = mutableListOf<Employee>()
        for (listofweekendemployeeID in listofweekendemployeeIDs) {
            val mornemp = db.getEmployee(listofweekendemployeeID.toInt())
            ListofEmployees.add(mornemp)
        }

        employeeRecAdapterWeekdayWeekend = EmployeeRecAdapterWeekdayWeekend(this, ListofEmployees, this, recyclerView)
        recyclerView.adapter = employeeRecAdapterWeekdayWeekend
        recyclerView.layoutManager = LinearLayoutManager(this)

        //BTN FUNCTIONS
        homeBTN.setOnClickListener {

            val istrainedMorning = ListofEmployees.count{ it.trainedopening == 1} >= 1
            val istrainedAfternoon = ListofEmployees.count{ it.trainedclosing == 1 } >= 1

            val istwoemployees = ListofEmployees.size >= 2

            val errorMessages = StringBuilder()

            if (!istrainedMorning || !istrainedAfternoon) {
                errorMessages.append("Ensure there is someone trained in the morning and someone trained in the afternoon.\n\n")
            }

            if (!istwoemployees) {
                errorMessages.append("Ensure two employees are scheduled.\n\n")
            }

            if (errorMessages.isNotEmpty()) {
                val alertBackBuilder = AlertDialog.Builder(this)
                alertBackBuilder.setTitle("Warning")
                alertBackBuilder.setMessage("There are missing parameters for this day:\n\n$errorMessages" +
                        "\nWould you still like to go back?\n")

                alertBackBuilder.setPositiveButton("Yes") { dialog, which ->
                    val bbIntent = Intent(this, CreateSchedulePage::class.java)
                    startActivity(bbIntent)
                }
                alertBackBuilder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()
            } else {
                // Your logic when all conditions are met
                Toast.makeText(this, "Day has been updated.", Toast.LENGTH_SHORT).show()
                val hbIntent = Intent(this, CreateSchedulePage::class.java)
                startActivity(hbIntent)

            }

        }

        addEmpBTN.setOnClickListener {
            if (ListofEmployees.size == 2){
                val alertBackBuilder = AlertDialog.Builder(this)
                alertBackBuilder.setTitle("Too Many Employees")
                alertBackBuilder.setMessage("There can only be 2 employees per shift.")
                alertBackBuilder.setPositiveButton("Okay") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()
            }else{
                val aebIntent = Intent(this, CreateSchedule_PickEmployee::class.java)
                aebIntent.putExtra("date", dateTime)
                aebIntent.putExtra("dayofweek", dayOfWeek)
                aebIntent.putExtra("shiftType", "Weekend")
                startActivity(aebIntent)
            }

        }

        backBTN.setOnClickListener{
            val istrainedMorning = ListofEmployees.count{ it.trainedopening == 1} >= 1
            val istrainedAfternoon = ListofEmployees.count{ it.trainedclosing == 1 } >= 1

            val istwoemployees = ListofEmployees.size >= 2

            val errorMessages = StringBuilder()

            if (!istrainedMorning || !istrainedAfternoon) {
                errorMessages.append("Ensure there is someone trained in the morning and someone trained in the afternoon.\n\n")
            }

            if (!istwoemployees) {
                errorMessages.append("Ensure two employees are scheduled.\n\n")
            }

            if (errorMessages.isNotEmpty()) {
                val alertBackBuilder = AlertDialog.Builder(this)
                alertBackBuilder.setTitle("Warning")
                alertBackBuilder.setMessage("There are missing parameters for this day:\n\n$errorMessages" +
                        "\nWould you still like to go back?\n")

                alertBackBuilder.setPositiveButton("Yes") { dialog, which ->
                    val bbIntent = Intent(this, CreateSchedulePage::class.java)
                    startActivity(bbIntent)
                }
                alertBackBuilder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()
            } else {
                // Your logic when all conditions are met
                Toast.makeText(this, "Day has been updated.", Toast.LENGTH_SHORT).show()
                val BackIntent = Intent(this, CreateSchedulePage::class.java)
                startActivity(BackIntent)

            }
        }


        applyBTN.setOnClickListener{
            val istrainedMorning = ListofEmployees.count{ it.trainedopening == 1} >= 1
            val istrainedAfternoon = ListofEmployees.count { it.trainedclosing == 1} >= 1
            val istwoemployees = ListofEmployees.size >= 2

            val errorMessages = StringBuilder()

            if (!istrainedMorning || !istrainedAfternoon) {
                errorMessages.append("Ensure there is someone trained in the morning and someone trained in the afternoon.\n\n")
            }

            if (!istwoemployees) {
                errorMessages.append("Ensure two employees are scheduled.\n\n")
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

    }


    override fun onItemClick(employee:Employee, recyclerView: RecyclerView){
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

package com.example.mycmpt395proj

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ManageAvailabilityPage : AppCompatActivity() {

    private lateinit var sqliteemployeedb: EmployeeDataBaseOperations

    // Morning Shifts
    private lateinit var manage_avail_morning_mon: Switch
    private lateinit var manage_avail_morning_tue: Switch
    private lateinit var manage_avail_morning_wed: Switch
    private lateinit var manage_avail_morning_thu: Switch
    private lateinit var manage_avail_morning_fri: Switch

    // Afternoon Shifts
    private lateinit var manage_avail_afternoon_mon: Switch
    private lateinit var manage_avail_afternoon_tue: Switch
    private lateinit var manage_avail_afternoon_wed: Switch
    private lateinit var manage_avail_afternoon_thu: Switch
    private lateinit var manage_avail_afternoon_fri: Switch

    // Weekend Shifts
    private lateinit var manage_avail_weekend_sat: Switch
    private lateinit var manage_avail_weekend_sun: Switch

    private var employeeID: Int = -1
    private lateinit var db : EmployeeDataBaseOperations
    private lateinit var employee: Employee

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_availability_page)
        //BTN Declraration

        val userInfoBTN: Button = findViewById(R.id.manageavail_userinfoBTN)
        val daysOffBTN: Button = findViewById(R.id.manageavail_daysoffBTN)
        val manageAvailBackBTN: ImageButton = findViewById(R.id.manageavail_backBTN)
        val manageAvailHomeBTN: ImageButton = findViewById(R.id.manage_avail_homeBTN)

        val applyButton: Button = findViewById(R.id.button2)

        //Get employeeID from id
        employeeID = intent.getIntExtra("id", -1)

        sqliteemployeedb = EmployeeDataBaseOperations(this)

        db = EmployeeDataBaseOperations(this)
        employee = db!!.getEmployee(employeeID)


        val sharedPreferences = getSharedPreferences("SwitchStates", Context.MODE_PRIVATE)

        //======================================= CHANGE ONCE IMPLEMENTED ===================================

        manage_avail_morning_mon = findViewById(R.id.manage_avail_morning_mon)
        manage_avail_morning_tue = findViewById(R.id.manage_avail_morning_tue)
        manage_avail_morning_wed = findViewById(R.id.manage_avail_morning_wed)
        manage_avail_morning_thu = findViewById(R.id.manage_avail_morning_thu)
        manage_avail_morning_fri = findViewById(R.id.manage_avail_morning_fri)

        manage_avail_afternoon_mon = findViewById(R.id.manage_avail_afternoon_mon)
        manage_avail_afternoon_tue = findViewById(R.id.manage_avail_afternoon_tue)
        manage_avail_afternoon_wed = findViewById(R.id.manage_avail_afternoon_wed)
        manage_avail_afternoon_thu = findViewById(R.id.manage_avail_afternoon_thu)
        manage_avail_afternoon_fri = findViewById(R.id.manage_avail_afternoon_fri)

        manage_avail_weekend_sat = findViewById(R.id.manage_avail_weekend_sat)
        manage_avail_weekend_sun = findViewById(R.id.manage_avail_weekend_sun)


        manage_avail_morning_mon.isChecked = employee!!.checkShiftPreference(employee!!, Employee.MONDAY_MORNING)
        manage_avail_morning_tue.isChecked = employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_MORNING)
        manage_avail_morning_wed.isChecked = employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_MORNING)
        manage_avail_morning_thu.isChecked = employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_MORNING)
        manage_avail_morning_fri.isChecked = employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_MORNING)
        manage_avail_afternoon_mon.isChecked = employee!!.checkShiftPreference(employee!!, Employee.MONDAY_AFTERNOON)
        manage_avail_afternoon_tue.isChecked = employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_AFTERNOON)
        manage_avail_afternoon_wed.isChecked = employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_AFTERNOON)
        manage_avail_afternoon_thu.isChecked = employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_AFTERNOON)
        manage_avail_afternoon_fri.isChecked = employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_AFTERNOON)
        manage_avail_weekend_sat.isChecked = employee!!.checkShiftPreference(employee!!, Employee.SATURDAY)
        manage_avail_weekend_sun.isChecked = employee!!.checkShiftPreference(employee!!, Employee.SUNDAY)

        userInfoBTN.setOnClickListener{
            val ubIntent = Intent(this, EditEmployeePage::class.java)
            Toast.makeText(this, "Any Unapplied Changes Were Not Saved.", Toast.LENGTH_SHORT).show()
            ubIntent.putExtra("id", employeeID)
            startActivity(ubIntent)
        }

        daysOffBTN.setOnClickListener{
            val dbIntent = Intent(this, DaysOffPage::class.java)
            Toast.makeText(this, "Any Unapplied Changes Were Not Saved.", Toast.LENGTH_SHORT).show()
            dbIntent.putExtra("id", employeeID)
            startActivity(dbIntent)
        }

        manageAvailBackBTN.setOnClickListener {
            val bbIntent = Intent(this, ManageEmployeePage::class.java)

            if (employee!!.checkShiftPreference(employee!!, Employee.MONDAY_MORNING) != manage_avail_morning_mon.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_MORNING) != manage_avail_morning_tue.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_MORNING) != manage_avail_morning_wed.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_MORNING) != manage_avail_morning_thu.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_MORNING) != manage_avail_morning_fri.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.MONDAY_AFTERNOON) != manage_avail_afternoon_mon.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_AFTERNOON) != manage_avail_afternoon_tue.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_AFTERNOON) != manage_avail_afternoon_wed.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_AFTERNOON) != manage_avail_afternoon_thu.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_AFTERNOON) != manage_avail_afternoon_fri.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.SATURDAY) != manage_avail_weekend_sat.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.SUNDAY) != manage_avail_weekend_sun.isChecked
            ) {

                val alertBackBuilder = AlertDialog.Builder(this)

                alertBackBuilder.setTitle("Saving Changes")
                alertBackBuilder.setMessage("Would You Like To Save Your Changes?")
                alertBackBuilder.setPositiveButton("Yes") { dialog, which ->
                    updateShifts(employee!!)
                    startActivity(bbIntent)

                    // Display a confirmation message
                    if (db!!.updateData(employee!!) > 0){
                        Toast.makeText(this, "Updated Successfully.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Something went terribly wrong", Toast.LENGTH_SHORT).show()
                    }
                    startActivity(bbIntent)
                }
                alertBackBuilder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                val alertBackDialog = alertBackBuilder.create()
                alertBackDialog.show()
            }else{
                startActivity(bbIntent)
            }
        }

        manageAvailHomeBTN.setOnClickListener {
            val hbIntent = Intent(this, MainActivity::class.java)

            val alertHomeBuilder = AlertDialog.Builder(this)

            if (employee!!.checkShiftPreference(employee!!, Employee.MONDAY_MORNING) != manage_avail_morning_mon.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_MORNING) != manage_avail_morning_tue.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_MORNING) != manage_avail_morning_wed.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_MORNING) != manage_avail_morning_thu.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_MORNING) != manage_avail_morning_fri.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.MONDAY_AFTERNOON) != manage_avail_afternoon_mon.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_AFTERNOON) != manage_avail_afternoon_tue.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_AFTERNOON) != manage_avail_afternoon_wed.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_AFTERNOON) != manage_avail_afternoon_thu.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_AFTERNOON) != manage_avail_afternoon_fri.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.SATURDAY) != manage_avail_weekend_sat.isChecked
                || employee!!.checkShiftPreference(employee!!, Employee.SUNDAY) != manage_avail_weekend_sun.isChecked
                ){
                    alertHomeBuilder.setTitle("Saving Changes")
                    alertHomeBuilder.setMessage("Would You Like To Save Your Changes?")
                    alertHomeBuilder.setPositiveButton("Yes"){dialog, which->

                    updateShifts(employee!!)

                    // Display a confirmation message
                    if (db!!.updateData(employee!!) > 0){
                        Toast.makeText(this, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Something went terribly wrong", Toast.LENGTH_SHORT).show()
                    }
                    startActivity(hbIntent)
                } // end of Yes dialog
                alertHomeBuilder.setNegativeButton("No"){dialog, which->
                    dialog.dismiss()
                } // end of No dialog
                val alertHomeDialog = alertHomeBuilder.create()
                alertHomeDialog.show()

            }else{
                startActivity(hbIntent)
            }
        }

        applyButton.setOnClickListener{
            val restartIntent = Intent(this, ManageEmployeePage::class.java)

            updateShifts(employee!!)

            // Display a confirmation message
            if (db!!.updateData(employee!!) > 0){
                Toast.makeText(this, "Updated Successfully.", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this, "Something went terribly wrong", Toast.LENGTH_SHORT).show()

            startActivity(restartIntent)
        }
    } // End of On Create

    private fun updateShifts(employee: Employee){
        // Update Weekday Morning Shifts
        employee.shiftpref = employee.setShiftPreference(employee, Employee.MONDAY_MORNING, manage_avail_morning_mon.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.TUESDAY_MORNING, manage_avail_morning_tue.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.WEDNESDAY_MORNING, manage_avail_morning_wed.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.THURSDAY_MORNING, manage_avail_morning_thu.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.FRIDAY_MORNING, manage_avail_morning_fri.isChecked)

        // Update Weekday Afternoon Shifts
        employee.shiftpref = employee.setShiftPreference(employee, Employee.MONDAY_AFTERNOON, manage_avail_afternoon_mon.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.TUESDAY_AFTERNOON, manage_avail_afternoon_tue.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.WEDNESDAY_AFTERNOON, manage_avail_afternoon_wed.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.THURSDAY_AFTERNOON, manage_avail_afternoon_thu.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.FRIDAY_AFTERNOON, manage_avail_afternoon_fri.isChecked)

        // Update Weekend Shifts
        employee.shiftpref = employee.setShiftPreference(employee, Employee.SATURDAY, manage_avail_weekend_sat.isChecked)
        employee.shiftpref = employee.setShiftPreference(employee, Employee.SUNDAY, manage_avail_weekend_sun.isChecked)
    }

    override fun onBackPressed() {
        val bbIntent = Intent(this, ManageEmployeePage::class.java)

        employeeID = intent.getIntExtra("id", -1)
        db = EmployeeDataBaseOperations(this)
        employee = db.getEmployee(employeeID)

        if (employee!!.checkShiftPreference(employee!!, Employee.MONDAY_MORNING) != manage_avail_morning_mon.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_MORNING) != manage_avail_morning_tue.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_MORNING) != manage_avail_morning_wed.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_MORNING) != manage_avail_morning_thu.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_MORNING) != manage_avail_morning_fri.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.MONDAY_AFTERNOON) != manage_avail_afternoon_mon.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.TUESDAY_AFTERNOON) != manage_avail_afternoon_tue.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.WEDNESDAY_AFTERNOON) != manage_avail_afternoon_wed.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.THURSDAY_AFTERNOON) != manage_avail_afternoon_thu.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.FRIDAY_AFTERNOON) != manage_avail_afternoon_fri.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.SATURDAY) != manage_avail_weekend_sat.isChecked
            || employee!!.checkShiftPreference(employee!!, Employee.SUNDAY) != manage_avail_weekend_sun.isChecked
        ){
            val alertBackBuilder = AlertDialog.Builder(this)

            alertBackBuilder.setTitle("Saving Changes")
            alertBackBuilder.setMessage("Would You Like To Save Your Changes?")
            alertBackBuilder.setPositiveButton("Yes") { dialog, which ->
                updateShifts(employee!!)

                // Display a confirmation message
                if (db!!.updateData(employee!!) > 0){
                    Toast.makeText(this, "Updated Successfully.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Something went terribly wrong", Toast.LENGTH_SHORT).show()
                }
                startActivity(bbIntent)
            }
            alertBackBuilder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val alertBackDialog = alertBackBuilder.create()
            alertBackDialog.show()
        }else{
            startActivity(bbIntent)
        }

    }

}
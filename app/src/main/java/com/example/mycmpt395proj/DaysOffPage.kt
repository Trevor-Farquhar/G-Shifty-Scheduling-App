package com.example.mycmpt395proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.util.Calendar

class DaysOffPage : AppCompatActivity() {

    private lateinit var sqliteemployeedb: EmployeeDataBaseOperations
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days_off_page)

        var year: Int = 0
        var month: Int = 0
        var day: Int = 0
        val userInfoBTN: Button = findViewById(R.id.daysoff_userinfoBTN)
        val availabilityBTN: Button = findViewById(R.id.daysoff_availBTN)
        val daysOffCheckAllBTN: Button = findViewById(R.id.Days_Off_Check_All_Button)
        val daysOffBackBTN: ImageButton = findViewById(R.id.daysoff_backBTN)
        val daysOffHomeBTN: ImageButton = findViewById(R.id.days_off_homeBTN)
        val saveDayOffBTN:  Button = findViewById(R.id.days_off_savedate)
        val cal = findViewById<CalendarView>(R.id.calendarView)

        val employeeID = intent.getIntExtra("id", -1)

        sqliteemployeedb = EmployeeDataBaseOperations(this)

        val db = EmployeeDataBaseOperations(this)
        val employee = db.getEmployee(employeeID)


        userInfoBTN.setOnClickListener{
            val ubIntent = Intent(this, EditEmployeePage::class.java)
            ubIntent.putExtra("id", employeeID)
            startActivity(ubIntent)
        }

        availabilityBTN.setOnClickListener{
            val abIntent = Intent(this, ManageAvailabilityPage::class.java)
            abIntent.putExtra("id", employeeID)
            startActivity(abIntent)
        }

        daysOffCheckAllBTN.setOnClickListener {
            val dbIntent = Intent(this, CheckAllDaysOffPage::class.java)
            startActivity(dbIntent)
        }

        daysOffBackBTN.setOnClickListener {
            val alertBackBuilder = AlertDialog.Builder(this)

            alertBackBuilder.setTitle("Saving A Day Off")
            alertBackBuilder.setMessage("Would you like to leave this page? Unsaved Selections will not be saved.")
            alertBackBuilder.setPositiveButton("Yes"){dialog, which->
                val bbIntent = Intent(this, ManageEmployeePage ::class.java)
                startActivity(bbIntent)
            }
            alertBackBuilder.setNegativeButton("No"){dialog, which->
                dialog.dismiss()
            }
            val alertBackDialog = alertBackBuilder.create()
            alertBackDialog.show()
        }

        daysOffHomeBTN.setOnClickListener {
        val alertHomeBuilder = AlertDialog.Builder(this)

        alertHomeBuilder.setTitle("Saving A Day Off")
        alertHomeBuilder.setMessage("Would you like to leave this page? Unsaved Selections will not be saved.")
            alertHomeBuilder.setPositiveButton("Yes"){dialog, which->
                val hbIntent = Intent(this, MainActivity::class.java)
                startActivity(hbIntent)
            }
        alertHomeBuilder.setNegativeButton("No"){dialog, which->
            dialog.dismiss()
        }
        val alertHomeDialog = alertHomeBuilder.create()
        alertHomeDialog.show()
        }

        cal.setOnDateChangeListener{
                _, i, i1, i2 ->
            year = i
            month = i1
            day = i2

            val tempCal = Calendar.getInstance()
            tempCal.set(year, month, day)

            val text = "$year-$month-$day:"
            val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
            toast.show()

            saveDayOffBTN.setOnClickListener {
                val finalToast = Toast.makeText(this, "Date has been taken off", Toast.LENGTH_LONG)
                finalToast.show()

                val finalStr = "${employee.daysOff} + $text"
                employee.daysOff = finalStr

                db.updateData(employee)
            }
        }
    }
}

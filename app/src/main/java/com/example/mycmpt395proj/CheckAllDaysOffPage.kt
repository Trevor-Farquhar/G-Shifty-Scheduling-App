package com.example.mycmpt395proj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckAllDaysOffPage : AppCompatActivity() {

    private lateinit var sqliteemployeedb: EmployeeDataBaseOperations
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_all_days_off_page)

        /*Button Declarations*/
        val checkAllBackBTN: ImageButton = findViewById(R.id.check_all_days_off_backBTN)
        val checkAllHomeBTN: ImageButton = findViewById(R.id.check_all_days_off_homeBTN)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val employeeID = intent.getIntExtra("id", -1)

        sqliteemployeedb = EmployeeDataBaseOperations(this)

        val db = EmployeeDataBaseOperations(this)
        val employee = db.getEmployee(employeeID)

//        val allDaysOffStr = employee.daysOff
//        val daysOffArray = allDaysOffStr.split(":") // ["year-month-date", "year-month-date"]

//        val datesList = mutableListOf<String>()
//        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//        for (dateStr in daysOffArray) {
//            val date = dateFormatter.parse(dateStr)
//            if (date != null) {
//                datesList.add(date.toString())
//            }
//        }

//        val adapter = EmployeeRecAdapterDaysOff(this, datesList, object : EmployeeRecAdapterDaysOff.OnItemClickListener {
//            override fun onItemClick(date: Date) {
//                Toast.makeText(this, "Poked", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)


        checkAllBackBTN.setOnClickListener {
            val cbIntent = Intent(this, DaysOffPage::class.java)
            startActivity(cbIntent)
        }

        checkAllHomeBTN.setOnClickListener {
            val hbIntent = Intent(this, MainActivity::class.java)
            startActivity(hbIntent)
        }

    }
}
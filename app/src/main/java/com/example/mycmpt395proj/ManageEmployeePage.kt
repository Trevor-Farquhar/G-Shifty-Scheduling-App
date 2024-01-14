package com.example.mycmpt395proj

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageEmployeePage : AppCompatActivity(), EmployeeRecAdapter.OnItemClickListener{

    /*Declaring Late init vars for Recycler View*/
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeRecAdapter: EmployeeRecAdapter
    private lateinit var employeeList: List<Employee>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_employee_page)

        /*To Dos:*/
        /* Var Declaration (When called in Activity switch, shortening to acronym. ex. manage_employeeBTN = meb_*) */
        val backBTN: ImageButton = findViewById(R.id.manageemployee_backBTN)
        val addNewEmployeeBTN: Button = findViewById(R.id.manageemployeepage_addnewemployee)
        recyclerView = findViewById(R.id.manageemployeepage_RecyclerView)

        /*TESTING DATABASE OPERATIONS*/
        val db = EmployeeDataBaseOperations(this)
        /*Get list of EACH EMPLOYEE*/

        employeeList = db.getAllEmployees()
        Log.d("EmployeeListSize", employeeList.size.toString())
        employeeRecAdapter = EmployeeRecAdapter(this, employeeList, this)
        recyclerView.adapter = employeeRecAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        /* back BTN Activity Switch */
        backBTN.setOnClickListener {
            val bbIntent = Intent(this, MainActivity::class.java)
            startActivity(bbIntent)
        }

        addNewEmployeeBTN.setOnClickListener{
            val ane_intent = Intent(this, EditEmployeePage::class.java)
            val newEmployeeID = db.getDatabaseSize()

//            val toast = Toast.makeText(this, "Size of Database is $newEmployeeID", Toast.LENGTH_SHORT)
//            toast.show()
            newEmployeeID + 1
            intent.putExtra("id",newEmployeeID)
            startActivity(ane_intent)
        }

    }

    override fun onBackPressed() {
        val bbIntent = Intent(this, MainActivity::class.java)
        startActivity(bbIntent)
    }

    override fun onItemClick(employee: Employee) {
        val intent = Intent(this, EditEmployeePage::class.java)
        intent.putExtra("id", employee.id)

//        val toast = Toast.makeText(this, "Employee Id: ${employee.id}", Toast.LENGTH_SHORT)
//        toast.show()

        startActivity(intent)
    }
}
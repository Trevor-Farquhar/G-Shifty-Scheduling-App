package com.example.mycmpt395proj

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import org.w3c.dom.Text
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class daysOfTheMonth {
    companion object {
        var monthDays: MutableList<String> = mutableListOf()
    }
}

class ViewCalendarPage : AppCompatActivity(), EmployeeRecAdapterCalednarView.OnItemClickListener {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var dateText: String = ""

    private var dayOfWeekString: String = ""

    private lateinit var calendarView: CalendarView
    private lateinit var pdfTestInput : TextInputEditText
    private val STORAGE_CODE= 1234

    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeRecAdapterCalendarView: EmployeeRecAdapterCalednarView
    private lateinit var employeeList: List<Employee>
    private lateinit var scheduledEmployee: List<ScheduledEmployee>

    private lateinit var sqliteschedule: ScheduledEmployeeDataBase
    private lateinit var sqliteemployeedb: EmployeeDataBaseOperations


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_calendar_page)

        val backBTN : ImageButton = findViewById(R.id.viewCalendarpage_backBTN)
        val exportBTN: Button = findViewById(R.id.viewCalendarpage_Export)
        calendarView = findViewById(R.id.calendarView2)
        recyclerView = findViewById(R.id.viewCalendarPage_RecyclerView)
        val workingTextView = findViewById<TextView>(R.id.textView6)
        val emptyTextView = findViewById<TextView>(R.id.viewCalendarPage_emptyRec)

        sqliteschedule = ScheduledEmployeeDataBase(this)
        sqliteemployeedb = EmployeeDataBaseOperations(this)


        //Back BTN
        backBTN.setOnClickListener{
            val backBTN_IN = Intent(this, MainActivity::class.java)
            startActivity(backBTN_IN)
        }
        //val monthDays: MutableList<String> = mutableListOf()
        exportBTN.isEnabled = false

        if (!exportBTN.isEnabled) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
        }

        exportBTN.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    requestPermissions(permission, STORAGE_CODE)
                }else{
                    savePDF()
                }
            }else{
                savePDF()
            }
        }

        val db = EmployeeDataBaseOperations(this)
        val scdb = ScheduledEmployeeDataBase(this)



        calendarView.setOnDateChangeListener{
                _, i, i1, i2 ->
            year = i
            month = i1
            day = i2

            val tempCal = Calendar.getInstance()
            tempCal.set(year, month, day)

            val dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)

            dayOfWeekString = when (dayOfWeek) {
                Calendar.SUNDAY -> "Sunday"
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                else -> "Invalid Day"
            }

            dateText = String.format("%04d-%02d-%02d", year, month+1, day)

            val tempObjectofScheduledEmployees = scdb.getScheduledEmployeesForDate(dateText)
            val listofTodayEmployees = getEmployeeIdsFromScheduledEmployees(tempObjectofScheduledEmployees)

            val ListofEmployees = mutableListOf<Employee>()
            for (listofTodayEmployee in listofTodayEmployees) {
                val todayemp = db.getEmployee(listofTodayEmployee.toInt())
                ListofEmployees.add(todayemp)
            }

            workingTextView.text= "Shifts for $dateText:"

            if(ListofEmployees.isEmpty()){
                emptyTextView.text = "No employees working!"
            }else{
                emptyTextView.text = ""
            }


            employeeRecAdapterCalendarView = EmployeeRecAdapterCalednarView(this, ListofEmployees, this)
            recyclerView.adapter = employeeRecAdapterCalendarView
            recyclerView.layoutManager = LinearLayoutManager(this)


            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (day in 1..maxDay) {

                val currentDate = calendar.time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = dateFormat.format(currentDate)

                calendar.add(Calendar.DAY_OF_MONTH, 1)

                if (daysOfTheMonth.monthDays.size < maxDay) {
                    daysOfTheMonth.monthDays.add(formattedDate)
                }
            }

            exportBTN.isEnabled = true

        }

    }

    private fun savePDF() {
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())

//        val mFilePath = Environment.getExternalFilesDir(null)?.absolutePath + "/" + mFileName + ".pdf"
//        val mFilePath = Environment.getExternalStoragePublicDirectory()
        val mFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/" + mFileName + ".pdf"

        val scheduleDict = createExportDataStructure(daysOfTheMonth.monthDays)

        val outPutStr = createOutPutStr(scheduleDict)

        try {

            val pdfFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "$mFileName.pdf")
            val contentUri = FileProvider.getUriForFile(this, "${packageName}.provider", pdfFile)

            PdfWriter.getInstance(mDoc, contentResolver.openOutputStream(contentUri))
            mDoc.open()

            val data = outPutStr.trim()
            mDoc.addAuthor("Team Lovelace")
            mDoc.add(Paragraph(data))
            mDoc.close()
            Log.d("PDF", "File saved at: $mFilePath")
            Toast.makeText(this, "$mFileName.pdf\n was Saved!", Toast.LENGTH_SHORT).show()

        }catch (e: Exception){
            Toast.makeText(this, ""+e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun createExportDataStructure(monthDays: MutableList<String>): MutableMap<String, MutableList<MutableList<String>>> { //TODO: add pop up to confirm if the schedule isnt full yet (maybe)
        val scheduleDict: MutableMap<String, MutableList<MutableList<String>>> = mutableMapOf() // "Date": [[morningEmployee1, morningEmployee2], [afternoonEmployee1, afternoonEmployee2]]

        val scheduledEmployeeIds = mutableListOf<List<ScheduledEmployee>>()
        val scheduledEmployeeObjs = mutableListOf<Employee>()

        for (date in monthDays) {
            val scheduledEmployees = sqliteschedule.getScheduledEmployeesForDate(date)
            scheduledEmployeeIds.add(scheduledEmployees)
        }


        for (scheduledEmployeeList in scheduledEmployeeIds) {
            for (scheduledEmployee in scheduledEmployeeList) {

                val employee = sqliteemployeedb.getEmployee(scheduledEmployee.employeeId.toInt())
                scheduledEmployeeObjs.add(employee)

                val targetList = scheduleDict.getOrPut(scheduledEmployee.date) {
                    mutableListOf(
                        mutableListOf(),
                        mutableListOf()
                    )
                }

                if (scheduledEmployee.shiftType == 1 || scheduledEmployee.shiftType == 2 || scheduledEmployee.shiftType == 4 ||
                    scheduledEmployee.shiftType == 8 || scheduledEmployee.shiftType == 16) {

                    targetList.first().add("Morning: ${employee.firstname} ${employee.lastname}")

                }
                if (scheduledEmployee.shiftType == 1024 || scheduledEmployee.shiftType == 2048) {
                    targetList.first().add("Weekend: ${employee.firstname} ${employee.lastname}")
                }
                if (scheduledEmployee.shiftType == 32 || scheduledEmployee.shiftType == 64 || scheduledEmployee.shiftType == 128 ||
                    scheduledEmployee.shiftType == 265 || scheduledEmployee.shiftType == 512) {

                    targetList.last().add("Afternoon: ${employee.firstname} ${employee.lastname}")
                }
            }
        }

        return scheduleDict
    }

    private fun createOutPutStr(scheduleDict: MutableMap<String, MutableList<MutableList<String>>>): String {
        var outPutString: String = ""

        for ((date, employeeLists) in scheduleDict) {
            outPutString += "$date:"

            for ((index, employeeList) in employeeLists.withIndex()) {
                outPutString += "\n"
                if (index == 0) {
                    outPutString += "    |"
                    for (employee in employeeList) {
                        outPutString += "    $employee"
                    }
                }
                if (index == 1) {
                    outPutString += "    |"
                    for (employee in employeeList) {
                        outPutString += "    $employee"
                    }
                }
            }
            outPutString += "\n    -------------------------------------------------------------------------------------------------------------------------\n"
        }

        return outPutString
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // maybe unnecessary
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    savePDF()
                }else{
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemClick(employee: Employee) {
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
    }

}
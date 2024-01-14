package com.example.mycmpt395proj

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


class CreateSchedulePage : AppCompatActivity(),EmployeeRecAdapterCreateSchedule.OnItemClickListener {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var dateText: String = ""

    private var dayOfWeekString: String = ""
    private lateinit var calendarView: CalendarView

    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeRecAdapterCreateSchedule: EmployeeRecAdapterCreateSchedule
    private lateinit var dateList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_schedule_page)
        /*Declarations of Layout*/

        /*Button Declarations*/
        val backBTN: ImageButton = findViewById(R.id.create_schedule_backBTN)
        val homeBTN: ImageButton = findViewById(R.id.create_schedule_homeBTN)
        val monthSpinner: Spinner = findViewById(R.id.month_spinner)
        val yearSpinner: Spinner = findViewById(R.id.year_spinner)
        val submitButton: Button = findViewById(R.id.select_unfinished_BTN)
        val unfinishedTextView: TextView = findViewById(R.id.textView10)
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerView2)


        //Times
        /*Back BTN*/
        backBTN.setOnClickListener{
            val bbIntent = Intent(this, MainActivity::class.java)
            startActivity(bbIntent)
        }

        /*Home BTN*/
        homeBTN.setOnClickListener {
            val hbIntent = Intent(this, MainActivity::class.java)
            startActivity(hbIntent)
        }

        /*Calendar Implementation TODO("Calendar Implementation")*/
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
            //When Displaying, Year and Day are correct however Month counts from Zero so -1
            //Day of Week: Weekend = 1,7, Weekday 2-6
            //val text = "You clicked on $year, $month, $day. Which is $dayOfWeekString, $dayOfWeek"
//            val text = "You've selected:\n$dayOfWeekString ,${month+1}-$day-$year."
//            dateText = "$year-$month-$day"
            dateText = String.format("%04d-%02d-%02d", year, month+1, day)

            //Change where there is no more select button. Just on click
            if (dayOfWeekString != "Saturday" && dayOfWeekString != "Sunday") {
                val selectBTNWeekday = Intent(this, CreateSchedule_Weekday::class.java)
                selectBTNWeekday.putExtra("date", dateText)
                selectBTNWeekday.putExtra("dayofweek", dayOfWeekString)
                startActivity(selectBTNWeekday)
            }
            else if (dayOfWeekString == "Saturday" || dayOfWeekString == "Sunday"){
                val selectBTNWeekend = Intent(this, CreateSchedule_Weekend::class.java)
                selectBTNWeekend.putExtra("date", dateText)
                selectBTNWeekend.putExtra("dayofweek", dayOfWeekString)
                startActivity(selectBTNWeekend)
            }

        }


        //Spinner for month and date:
        val monthsArray = resources.getStringArray(R.array.months_array)
        val yearsArray = resources.getStringArray(R.array.years_array)

        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monthsArray)
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearsArray)

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        monthSpinner.adapter = monthAdapter
        yearSpinner.adapter = yearAdapter

        monthSpinner.onItemSelectedListener = createItemSelectedListener(monthsArray, "month")
        yearSpinner.onItemSelectedListener = createItemSelectedListener(yearsArray, "year")

        val currentMoth = LocalDate.now().month.value - 1
        monthSpinner.setSelection(currentMoth)


        fun getalldaysinmonth(selectedMonth: String, selectedYear: String): MutableList<String> {
            val tempDate = mutableListOf<String>()
            val calendar = Calendar.getInstance()

            val monthNumber = monthNameToNumber(selectedMonth)
            calendar.set(Calendar.YEAR, selectedYear.toInt())
            calendar.set(Calendar.MONTH, monthNumber - 1)

            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            for (day in 1..maxDay) {
                //Gotta do a bunch of checks here
                calendar.set(Calendar.DAY_OF_MONTH,day)
                val formatedDate = dateFormat.format(calendar.time)
                tempDate.add(formatedDate)
//            calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            return tempDate
        }

        val db = EmployeeDataBaseOperations(this)
        val scdb = ScheduledEmployeeDataBase(this)

        fun mapShiftType(shiftType: Int): String {
            return when (shiftType) {
                Employee.MONDAY_MORNING, Employee.TUESDAY_MORNING, Employee.WEDNESDAY_MORNING, Employee.THURSDAY_MORNING, Employee.FRIDAY_MORNING -> "Morning" // These shift types correspond to "Morning"
                Employee.MONDAY_AFTERNOON, Employee.TUESDAY_AFTERNOON, Employee.WEDNESDAY_AFTERNOON, Employee.THURSDAY_AFTERNOON, Employee.FRIDAY_AFTERNOON -> "Afternoon" // These shift types correspond to "Afternoon"
                else -> "Unknown" // Handle any other shift type values
            }
        }

        submitButton.setOnClickListener {
            val selectedMonth = monthSpinner.selectedItem.toString()
            val selectedYear = yearSpinner.selectedItem.toString()

            val mutListofSelectedDate = getalldaysinmonth(selectedMonth, selectedYear)

            unfinishedTextView.text = "Unfinished dates for $selectedMonth, $selectedYear:"
            println(mutListofSelectedDate)

            val mutListofUnfinished = mutableListOf<String>()

            for(i in mutListofSelectedDate){
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val parsedDate = dateFormat.parse(i)
                calendar.time = parsedDate

                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

                val tempObjectofScheduledEmployees = scdb.getScheduledEmployeesForDate(i)

                if (isWeekend){
                    //Is Weekend
                    val listofweekendemployeeIDs = getEmployeeIdsFromScheduledEmployees(tempObjectofScheduledEmployees)

                    val ListofEmployees = mutableListOf<Employee>()
                    for (listofweekendemployeeID in listofweekendemployeeIDs) {
                        val mornemp = db.getEmployee(listofweekendemployeeID.toInt())
                        ListofEmployees.add(mornemp)
                    }

                    // checks if any of the employees are opening and/or closing trained for the weekend shift
                    val istrainedopeningWeekend = ListofEmployees.any{it.trainedopening == 1}
                    val istrainedclosingWeekend = ListofEmployees.any{it.trainedclosing ==1}
                    // checks if there are two employees for the weekend shift
                    val istwoemployeesWeekend = ListofEmployees.size >= 2

                    //checks if ALL conditions have been met ***, as all of them must be fufilled (hence the logical AND)
                    if(istrainedopeningWeekend && istrainedclosingWeekend && istwoemployeesWeekend){
                        continue
                    }else{
                        mutListofUnfinished.add(i)
                        continue
                    }

                }else{
                    //Is WeekDay
                    val morningShiftEmployees = tempObjectofScheduledEmployees.filter { mapShiftType(it.shiftType) == "Morning" }
                    val afternoonShiftEmployees = tempObjectofScheduledEmployees.filter { mapShiftType(it.shiftType) == "Afternoon" }

                    val listofmorningemployeeIds = getEmployeeIdsFromScheduledEmployees(morningShiftEmployees)
                    val listofafternoonemployeeIds = getEmployeeIdsFromScheduledEmployees(afternoonShiftEmployees)

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

                    //Morning Checks
                    val isMorningTrained = morningEmployees.any{it.trainedopening == 1}
                    val isMorningLessThanTwoEmployees = morningEmployees.size >= 2

                    //Afternoon Checks
                    val isAfternoonTrained = afternoonEmployees.any { it.trainedclosing == 1 }
                    val isAfternoonLessThanTwoEmployees = afternoonEmployees.size >= 2

                    if (isMorningTrained || isMorningLessThanTwoEmployees || isAfternoonTrained || isAfternoonLessThanTwoEmployees){
                        continue
                    }else{
                        mutListofUnfinished.add(i)
                        continue
                    }
                }
            }

            employeeRecAdapterCreateSchedule = EmployeeRecAdapterCreateSchedule(this, mutListofUnfinished, this)
            recyclerView.adapter = employeeRecAdapterCreateSchedule
            recyclerView.layoutManager = LinearLayoutManager(this)
            // Now you can use the selectedMonth and selectedYear as needed
            // For example, you can print them or perform further actions
            println("Selected Month: $selectedMonth, Selected Year: $selectedYear")
        }

    }

    private fun createItemSelectedListener(array: Array<String>, type: String): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedValue = array[position]
                when (type) {
                    "month" -> {
                        // Do something with the selected month
                    }
                    "year" -> {
                        // Do something with the selected year
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle the case where nothing is selected (optional)
            }
        }
    }
    override fun onItemClick(date: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        println(date)
        val calendar = Calendar.getInstance()

        try {
            // Parse the selected date
            val parsedDate = dateFormat.parse(date)
            calendar.time = parsedDate

            // Check if the day of the week is Saturday or Sunday
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

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


            if (isWeekend) {
                // Handle the case for a weekend
                val onitemclickintent = Intent(this, CreateSchedule_Weekend::class.java)
                onitemclickintent.putExtra("date", date)
                onitemclickintent.putExtra("dayofweek", dayOfWeekString)
                println("Date Passed: $parsedDate, DayofWeek: $dayOfWeek")
                startActivity(onitemclickintent)
            } else {
                // Handle the case for a weekday
                val onitemclickintent = Intent(this, CreateSchedule_Weekday::class.java)
                onitemclickintent.putExtra("date", date)
                onitemclickintent.putExtra("dayofweek", dayOfWeekString)
                println("Date Passed: $parsedDate, DayofWeek: $dayOfWeek")
                startActivity(onitemclickintent)
            }

        } catch (e: ParseException) {
            // Handle the case where date parsing fails
            println("Error parsing date: $date")
            e.printStackTrace()
        }
    }

}

fun monthNameToNumber(monthName: String): Int {
    return when (monthName.lowercase(Locale.getDefault())) {
        "january" -> 1
        "february" -> 2
        "march" -> 3
        "april" -> 4
        "may" -> 5
        "june" -> 6
        "july" -> 7
        "august" -> 8
        "september" -> 9
        "october" -> 10
        "november" -> 11
        "december" -> 12
        else -> 0 // Return 0 or handle the case for an unknown month
    }
}
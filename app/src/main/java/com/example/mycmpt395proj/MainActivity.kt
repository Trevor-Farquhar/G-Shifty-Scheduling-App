package com.example.mycmpt395proj

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/* Turn this to false for debug purposes*/
private var debugdataInsert = true
class MainActivity : AppCompatActivity(), EmployeeRecAdapterCalednarView.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeRecAdapterMainPage: EmployeeRecAdapterCalednarView
    private lateinit var employeeList: List<Employee>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /* Button Declaration
        (When called in Activity switch, shortening to acronym. ex. manage_employeeBTN = meb_*)
        */
        val manage_employeeBTN : Button = findViewById(R.id.mainpage_manageEmployee_BTN)
        val create_scheduleBTN : Button = findViewById(R.id.mainpage_createSchedule_BTN)
        val view_calendarBTN : Button = findViewById(R.id.mainpage_viewtoday_BTN)
        val mainpage_dateTV = findViewById<TextView>(R.id.mainpage_dateholder_TV)
        recyclerView = findViewById(R.id.mainpage_recview)



        /* Manage Employee BTN Activity Switch */
        manage_employeeBTN.setOnClickListener{
            val meb_intent = Intent(this, ManageEmployeePage::class.java)
            startActivity(meb_intent)
        }
        /* Create Schedule BTN Activity Switch */
        create_scheduleBTN.setOnClickListener{
            val csb_intent = Intent(this, CreateSchedulePage::class.java)
            startActivity(csb_intent)
        }
        /* View Calendar BTN Activity Switch */
        view_calendarBTN.setOnClickListener{
            val vcb_intent = Intent(this, ViewCalendarPage::class.java)
            startActivity(vcb_intent)
        }

        /*Date Format TextView*/
        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy dd MM")
        val text = date.format(formatter)
        val parsedDate = LocalDate.parse(text, formatter)
        mainpage_dateTV.text = parsedDate.toString()

        val currentDate = LocalDate.now()
        val year = currentDate.year
        val month = currentDate.monthValue
        val day = currentDate.dayOfMonth

        val newparsedDate = String.format("%04d-%02d-%02d", year, month, day)
//        Toast.makeText(this, "DATE: $newparsedDate", Toast.LENGTH_SHORT).show()

        /* Database init*/
        val db = EmployeeDataBaseOperations(this)
        //DEBUG, used for adding mock employees to view if database is correect
        if (!debugdataInsert) {
            val testEmployee1 = Employee(
                firstname = "John",
                lastname = "Doe",
                email = "john.doe@gmail.com",
                phone = "7802938470",
                trainedopening = 1, // Trained
                trainedclosing = 0, // Not trained
                daysOff = "2023-11-10",
                shiftpref = Employee.MONDAY_MORNING or Employee.TUESDAY_MORNING or Employee.WEDNESDAY_MORNING or Employee.THURSDAY_MORNING or Employee.FRIDAY_MORNING or Employee.MONDAY_AFTERNOON or Employee.TUESDAY_AFTERNOON or Employee.WEDNESDAY_AFTERNOON or Employee.THURSDAY_AFTERNOON or Employee.FRIDAY_AFTERNOON or Employee.SATURDAY or Employee.SUNDAY,
                shiftCountPerWeekShift = 0
            )

            val testEmployee2 = Employee(
                firstname = "Emma",
                lastname = "Smith",
                email = "emma.smith@gmail.com",
                phone = "7805551111",
                trainedopening = 0, // Not trained
                trainedclosing = 1, // Trained
                daysOff = "2023-11-10",
                shiftpref = Employee.MONDAY_MORNING or Employee.TUESDAY_MORNING or Employee.WEDNESDAY_MORNING or Employee.THURSDAY_MORNING or Employee.FRIDAY_MORNING or Employee.MONDAY_AFTERNOON or Employee.TUESDAY_AFTERNOON or Employee.WEDNESDAY_AFTERNOON or Employee.THURSDAY_AFTERNOON or Employee.FRIDAY_AFTERNOON or Employee.SATURDAY or Employee.SUNDAY, // Prefers Tuesday mornings and Thursday afternoons
                shiftCountPerWeekShift = 0
            )

            val testEmployee3 = Employee(
                firstname = "Oliver",
                lastname = "Johnson",
                email = "oliver.johnson@gmail.com",
                phone = "7805552222",
                trainedopening = 1, // Trained
                trainedclosing = 1, // Trained
                daysOff = "2023-11-12:2023-11-14", // No shifts on November 12th and November 14th
                shiftpref = Employee.MONDAY_MORNING or Employee.TUESDAY_MORNING or Employee.WEDNESDAY_MORNING or Employee.THURSDAY_MORNING or Employee.FRIDAY_MORNING or Employee.MONDAY_AFTERNOON or Employee.TUESDAY_AFTERNOON or Employee.WEDNESDAY_AFTERNOON or Employee.THURSDAY_AFTERNOON or Employee.FRIDAY_AFTERNOON or Employee.SATURDAY or Employee.SUNDAY, // Prefers Monday mornings and Wednesday afternoons
                shiftCountPerWeekShift = 0
            )

            val testEmployee4 = Employee(
                firstname = "Isabella",
                lastname = "Brown",
                email = "isabella.brown@gmail.com",
                phone = "7805553333",
                trainedopening = 1, // Trained
                trainedclosing = 0, // Not trained
                daysOff = "2023-11-15",
                shiftpref = Employee.MONDAY_MORNING or Employee.TUESDAY_MORNING or Employee.WEDNESDAY_MORNING or Employee.THURSDAY_MORNING or Employee.FRIDAY_MORNING or Employee.MONDAY_AFTERNOON or Employee.TUESDAY_AFTERNOON or Employee.WEDNESDAY_AFTERNOON or Employee.THURSDAY_AFTERNOON or Employee.FRIDAY_AFTERNOON or Employee.SATURDAY or Employee.SUNDAY, // Prefers Wednesday mornings and Sunday shifts
                shiftCountPerWeekShift = 0
            )

            val testEmployee5 = Employee(
                firstname = "Liam",
                lastname = "Martinez",
                email = "liam.martinez@gmail.com",
                phone = "7805551234",
                trainedopening = 0, // Not trained
                trainedclosing = 1, // Trained
                daysOff = "2023-11-17:2023-11-18", // No shifts on November 17th and November 18th
                shiftpref = Employee.MONDAY_MORNING or Employee.TUESDAY_MORNING or Employee.WEDNESDAY_MORNING or Employee.THURSDAY_MORNING or Employee.FRIDAY_MORNING or Employee.MONDAY_AFTERNOON or Employee.TUESDAY_AFTERNOON or Employee.WEDNESDAY_AFTERNOON or Employee.THURSDAY_AFTERNOON or Employee.FRIDAY_AFTERNOON or Employee.SATURDAY or Employee.SUNDAY, // Prefers Monday afternoons and Friday mornings
                shiftCountPerWeekShift = 0
            )

            val testEmployee6 = Employee(
                firstname = "Olivia",
                lastname = "Davis",
                email = "olivia.davis@gmail.com",
                phone = "7805555678",
                trainedopening = 1, // Trained
                trainedclosing = 0, // Not trained
                daysOff = "2023-11-19:2023-11-20", // No shifts on November 19th and November 20th
                shiftpref = Employee.MONDAY_MORNING or Employee.TUESDAY_MORNING or Employee.WEDNESDAY_MORNING or Employee.THURSDAY_MORNING or Employee.FRIDAY_MORNING or Employee.MONDAY_AFTERNOON or Employee.TUESDAY_AFTERNOON or Employee.WEDNESDAY_AFTERNOON or Employee.THURSDAY_AFTERNOON or Employee.FRIDAY_AFTERNOON or Employee.SATURDAY or Employee.SUNDAY, // Prefers Wednesday mornings, Sunday shifts, and Friday afternoons
                shiftCountPerWeekShift = 0
            )

            val testEmployee7 = Employee(
                firstname = "Bubbles",
                lastname = "McGiggles",
                email = "bubbles.mcgiggles@gmail.com",
                phone = "7805551111",
                trainedopening = 1,
                trainedclosing = 1,
                daysOff = "2023-11-29",
                shiftpref = Employee.MONDAY_MORNING or Employee.WEDNESDAY_AFTERNOON or Employee.FRIDAY_MORNING or Employee.SUNDAY,
                shiftCountPerWeekShift = 0
            )

            val testEmployee8 = Employee(
                firstname = "Snicker",
                lastname = "BananaPeel",
                email = "snickerdoodle.bananapeel@gmail.com",
                phone = "7805552222",
                trainedopening = 0,
                trainedclosing = 1,
                daysOff = "2023-11-30",
                shiftpref = Employee.TUESDAY_MORNING or Employee.THURSDAY_MORNING or Employee.SATURDAY or Employee.SUNDAY,
                shiftCountPerWeekShift = 0
            )

            val testEmployee9 = Employee(
                firstname = "Giggly",
                lastname = "Waffle",
                email = "giggly.wafflebottom@gmail.com",
                phone = "7805553333",
                trainedopening = 1,
                trainedclosing = 0,
                daysOff = "2023-12-01",
                shiftpref = Employee.MONDAY_AFTERNOON or Employee.WEDNESDAY_MORNING or Employee.FRIDAY_AFTERNOON or Employee.SATURDAY,
                shiftCountPerWeekShift = 0
            )




            db.insertData(testEmployee1)
            db.insertData(testEmployee2)
            db.insertData(testEmployee3)
            db.insertData(testEmployee4)
            db.insertData(testEmployee5)
            db.insertData(testEmployee6)
            db.insertData(testEmployee7)
            db.insertData(testEmployee8)
            db.insertData(testEmployee9)

            debugdataInsert = true
        }

        val scdb = ScheduledEmployeeDataBase(this)

        fun mapShiftType(shiftType: Int): String {
            return when (shiftType) {
                Employee.MONDAY_MORNING, Employee.TUESDAY_MORNING, Employee.WEDNESDAY_MORNING, Employee.THURSDAY_MORNING, Employee.FRIDAY_MORNING -> "Morning" // These shift types correspond to "Morning"
                Employee.MONDAY_AFTERNOON, Employee.TUESDAY_AFTERNOON, Employee.WEDNESDAY_AFTERNOON, Employee.THURSDAY_AFTERNOON, Employee.FRIDAY_AFTERNOON -> "Afternoon" // These shift types correspond to "Afternoon"
                Employee.SATURDAY, Employee.SUNDAY -> "Weekend" //These shift types correspond to "Weekend"
                else -> "Unknown" // Handle any other shift type values
            }
        }

        val tempObjectofScheduledEmployees = scdb.getScheduledEmployeesForDate(newparsedDate.toString())

        Log.d("Temp Ob Main", "$tempObjectofScheduledEmployees")

        val listofTodayEmployees = getEmployeeIdsFromScheduledEmployees(tempObjectofScheduledEmployees)

        val ListofEmployees = mutableListOf<Employee>()
        for (listofTodayEmployee in listofTodayEmployees) {
            val todayemp = db.getEmployee(listofTodayEmployee.toInt())
            ListofEmployees.add(todayemp)
        }

        employeeRecAdapterMainPage = EmployeeRecAdapterCalednarView(this, ListofEmployees, this)
        recyclerView.adapter = employeeRecAdapterMainPage
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onItemClick(employee: Employee) {
//        Toast.makeText(this, "Stop Poking Me!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, EditEmployeePage::class.java)
        intent.putExtra("id", employee.id)
        startActivity(intent)

    }
}

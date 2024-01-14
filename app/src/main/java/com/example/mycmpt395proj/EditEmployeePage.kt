package com.example.mycmpt395proj

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditEmployeePage : AppCompatActivity(){

    lateinit var textfieldFirstName: EditText
    lateinit var textfieldLastName: EditText
    lateinit var textfieldPhoneNumber: EditText
    lateinit var textfieldEmail: EditText
    lateinit var checkboxMorning: CheckBox
    lateinit var checkboxAfternoon: CheckBox
    lateinit var buttonApply: Button

    private lateinit var sqliteemployeedb: EmployeeDataBaseOperations

    //When entering this page from an existing employee, we need to know what the original data
    //was so that when/if it's changed, we can use these strings to check if any changes occured.
    private var originalFirstName: String = ""
    private var originalLastName: String = ""
    private var originalEmail: String = ""
    private var originalPhone: String = ""

    private var originalOpening: Int = -1
    private var originalClosing: Int = -1
    private var employeeID: Int = -1

    //checks track of if your creating a new employee or editing an old one
    private var newEmployee = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_employee_page)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        sqliteemployeedb = EmployeeDataBaseOperations(this)
        /* Initialize Textfields */

        //Employee and Textfield init
        textfieldFirstName = findViewById(R.id.text_field_first_name)
        textfieldLastName = findViewById(R.id.text_field_last_name)
        textfieldPhoneNumber = findViewById(R.id.text_field_phone_number)
        textfieldEmail = findViewById(R.id.text_field_email)
        checkboxMorning = findViewById(R.id.morning_checkbox)
        checkboxAfternoon = findViewById(R.id.afternoon_checkbox)
        buttonApply = findViewById(R.id.apply_button)

        //Check if apply button has been pressed flag
        var applyPressed = false

        //Get ID from On click from Intent
        employeeID = intent.getIntExtra("id", -1)

//        val toast = Toast.makeText(this, "Current Employee ID is: $employeeID", Toast.LENGTH_SHORT)
//        toast.show()

        if (employeeID != -1){

            //Fetch the employee details based on id from onclick
            val db = EmployeeDataBaseOperations(this)
            val employee = db.getEmployee(employeeID)

            if (employee != null) {
                //Update UI with employee details
                //FName, LName, Phone, Email
//                val toast = Toast.makeText(this, "Using Employee ${employee.firstname}", Toast.LENGTH_SHORT)
//                toast.show()

                textfieldFirstName.setText(employee.firstname)
                textfieldLastName.setText(employee.lastname)
                textfieldEmail.setText(employee.email)
                textfieldPhoneNumber.setText(employee.phone)

                /* for checking if textfields and checkboxes were changed -> for saving changes
                warning popup */
                originalFirstName = employee.firstname
                originalLastName = employee.lastname
                originalEmail = employee.email
                originalPhone = employee.phone
                originalOpening = employee.trainedopening
                originalClosing = employee.trainedclosing

                // Bottom Checkboxes for training
                checkboxMorning.isChecked = employee.trainedopening == 1

                checkboxAfternoon.isChecked = employee.trainedclosing == 1

            } else {
                //Case with employee with specified ID was not found
                Toast.makeText(this, "This should never show up.", Toast.LENGTH_SHORT).show()
            }

        } else {
            //Handle the case where no employee Id was provided in the Intent
            newEmployee = 1
            //Toast.makeText(this, "No Id Given", Toast.LENGTH_SHORT).show()
        }

        /* Initialize Buttons */
        val availabilityBTN: Button = findViewById(R.id.edit_emp_availability)
        val daysOffBTN: Button = findViewById(R.id.edit_emp_days_off)
        val EditEmpBackBTN: ImageButton = findViewById(R.id.edit_emp_backBTN)
        val editEmpHomeBTN: ImageButton=findViewById(R.id.edit_emp_homeBTN)

        /* Add functionality to the buttons */
        availabilityBTN.setOnClickListener {
            if(newEmployee == 1){
                Toast.makeText(this, "Please Add Employee First", Toast.LENGTH_SHORT).show()
            } else {
                val abIntent = Intent(this, ManageAvailabilityPage::class.java)
                Toast.makeText(this, "Any Unapplied Changes Were Not Saved.", Toast.LENGTH_SHORT).show()
                abIntent.putExtra("id", employeeID)
                startActivity(abIntent)
            }
        }

        daysOffBTN.setOnClickListener {
            if(newEmployee == 1){
                Toast.makeText(this, "Please Add Employee First", Toast.LENGTH_SHORT).show()
            } else {
                val dbIntent = Intent(this, DaysOffPage::class.java)
                Toast.makeText(this, "Any Unapplied Changes Were Not Saved.", Toast.LENGTH_SHORT).show()
                dbIntent.putExtra("id", employeeID)
                startActivity(dbIntent)
            }
        }

        EditEmpBackBTN.setOnClickListener {

            val bbIntent = Intent(this, ManageEmployeePage::class.java)

            if ((isFieldChanged(originalFirstName, textfieldFirstName.text.toString())
                        || isFieldChanged(originalLastName, textfieldLastName.text.toString())
                        || isFieldChanged(originalEmail, textfieldEmail.text.toString())
                        || isFieldChanged(originalPhone, textfieldPhoneNumber.text.toString())
                        || isTrainingChanged(originalOpening, checkboxMorning.isChecked)
                        || isTrainingChanged(originalClosing, checkboxAfternoon.isChecked))
                        && !applyPressed && employeeID != -1
            ){
                startSaveIntent(bbIntent)
            }else{
                startActivity(bbIntent)
            }
        }

        editEmpHomeBTN.setOnClickListener {

            val hbIntent = Intent(this, MainActivity::class.java)

            if ((isFieldChanged(originalFirstName, textfieldFirstName.text.toString())
                        || isFieldChanged(originalLastName, textfieldLastName.text.toString())
                        || isFieldChanged(originalEmail, textfieldEmail.text.toString())
                        || isFieldChanged(originalPhone, textfieldPhoneNumber.text.toString())
                        || isTrainingChanged(originalOpening, checkboxMorning.isChecked)
                        || isTrainingChanged(originalClosing, checkboxAfternoon.isChecked))
                        && !applyPressed && employeeID != -1
            ) {
                startSaveIntent(hbIntent)
            }else{
                startActivity(hbIntent)
            }
        }

        buttonApply.setOnClickListener {

            if (newEmployee == 1){
                    addEmployee()

            } else {
                    updateEmployee()
            }
            applyPressed = true

        }

//        /* Moving from one textfield to the next */
//
//        textfieldFirstName.addTextChangedListener(new TextWatcher()
    }
    //Email Regex Check, found off of https://www.regexlib.com/Search.aspx?k=email&AspxAutoDetectCookieSupport=1
    private fun isEmailValid(email: String): Boolean {
        val regexPattern = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(Regex(regexPattern))
    }

    private fun isPhoneValid(phone: String): Boolean{
        val pattern = Regex("""^(\d{3}-\d{3}-\d{4}|\(\d{3}\) \d{3}-\d{4}|\d{10})$""")
        return phone.matches(pattern)
    }

    private fun isFirstNameValid(firstName: String): Boolean {
        val pattern = "^[a-zA-Z' -]+$"
        return firstName.matches(Regex(pattern))
    }

    private fun isLastNameValid(lastName: String): Boolean {
        val pattern = "^[a-zA-Z' -]+$"
        return lastName.matches(Regex(pattern))
    }

    /* IMPORTANT: Used to start Intent for home and back buttons for EditEmployeePage,
    ManageAvailabilityPage, etc.. WHEN THEY ARE SAVING UPDATED INFO*/
    private fun startSaveIntent(intentType: Intent){
        val alertHomeBuilder = AlertDialog.Builder(this)
        alertHomeBuilder.setTitle("Saving Changes")
        alertHomeBuilder.setMessage("Would You Like To Save Your Changes?")
        alertHomeBuilder.setPositiveButton("Yes") { dialog, which ->
            updateEmployee()
//            startActivity(intentType)
        }
        alertHomeBuilder.setNegativeButton("No") { dialog, which ->
//            dialog.dismiss()
            startActivity(intentType)
        }
        val alertHomeDialog = alertHomeBuilder.create()
        alertHomeDialog.show()
    }

    override fun onBackPressed() {
        val bbIntent = Intent(this, ManageEmployeePage::class.java)

        employeeID = intent.getIntExtra("id", -1)

        if ((isFieldChanged(originalFirstName, textfieldFirstName.text.toString())
                    || isFieldChanged(originalLastName, textfieldLastName.text.toString())
                    || isFieldChanged(originalEmail, textfieldEmail.text.toString())
                    || isFieldChanged(originalPhone, textfieldPhoneNumber.text.toString())
                    || isTrainingChanged(originalOpening, checkboxMorning.isChecked)
                    || isTrainingChanged(originalClosing, checkboxAfternoon.isChecked))
            && employeeID != -1
        ) {
            if (!isEmailValid(textfieldEmail.text.toString())) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }else if (!isPhoneValid(textfieldPhoneNumber.text.toString())) {
                Toast.makeText(this, "Invalid phone format", Toast.LENGTH_SHORT).show()
            }else{
                startSaveIntent(bbIntent)
            }
        } else {
            startActivity(bbIntent)
        }
    }

    private fun addEmployee() {
        val first_name = textfieldFirstName.text.toString()
        val last_name = textfieldLastName.text.toString()
        val email = textfieldEmail.text.toString()
        val phone = textfieldPhoneNumber.text.toString()

        val employeeID = intent.getIntExtra("id", -1)

        //No Longer Used, these are redundant
        val isTrainedMorning = 0
        val isTrainedAfternoon = 0

        val employee = Employee(id = employeeID, firstname = first_name, lastname = last_name, email = email, phone = phone, trainedopening = isTrainedMorning,
            trainedclosing = isTrainedAfternoon, daysOff = "", shiftpref = 0, shiftCountPerWeekShift = 0)

        if (first_name.isEmpty() || last_name.isEmpty() || email.isEmpty() || phone.isEmpty()) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()

            if(first_name.isEmpty()){
                textfieldFirstName.hint = "First Name*"
                textfieldFirstName.setHintTextColor(resources.getColor(R.color.red))
            }
            if(last_name.isEmpty()){
                textfieldLastName.hint = "Last Name*"
                textfieldLastName.setHintTextColor(resources.getColor(R.color.red))
            }
            if(email.isEmpty()){
                textfieldEmail.hint = "Email*"
                textfieldEmail.setHintTextColor(resources.getColor(R.color.red))
            }
            if(phone.isEmpty()){
                textfieldPhoneNumber.hint = "Phone*"
                textfieldPhoneNumber.setHintTextColor(resources.getColor(R.color.red))
            }

        } else if (!isEmailValid(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
        } else if (!isPhoneValid(phone)) {
            Toast.makeText(this, "Invalid phone format", Toast.LENGTH_SHORT).show()
        } else if (!isFirstNameValid(first_name)) {
            Toast.makeText(this, "Invalid first name (Must only include A-Z and ')", Toast.LENGTH_SHORT).show()
        } else if (!isLastNameValid(last_name)) {
            Toast.makeText(this, "Invalid last name (Must only include A-Z and ')", Toast.LENGTH_SHORT).show()
        } else if (sqliteemployeedb.isNameAlreadyTaken(employee)) {
            Toast.makeText(this, "Employee already exists (Please ensure there is no employee with this first and last name)", Toast.LENGTH_LONG).show()
        }
        else {
            //val employee = Employee(id = employeeID, firstname = first_name, lastname = last_name, email = email, phone = phone, trainedopening = isTrainedMorning,
                //trainedclosing = isTrainedAfternoon, daysOff = "", shiftpref = 0, shiftCountPerWeekShift = 0)

            if(checkboxMorning.isChecked){
                employee.trainedopening = 1
            }
            if(checkboxAfternoon.isChecked){
                employee.trainedclosing = 1
            }

            if (sqliteemployeedb.isFirstNameTaken(employee) && newEmployee == 1) {
                Toast.makeText(
                    this,
                    "This first name already exists, please consider editing by using a nickname",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val status = sqliteemployeedb.insertData(employee)

            if (status > -1) {
                Toast.makeText(this, "Employee Added Successfully", Toast.LENGTH_SHORT).show()
                clearFields()
                val restartIntent = Intent(this, ManageEmployeePage::class.java)
                startActivity(restartIntent)

            }

            else {
                Toast.makeText(this, "Record not saved. Check all fields are filled", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearFields() {
        textfieldFirstName.setText("")
        textfieldLastName.setText("")
        textfieldPhoneNumber.setText("")
        textfieldEmail.setText("")
        checkboxMorning.isChecked = false
        checkboxAfternoon.isChecked = false

        textfieldFirstName.setHintTextColor(resources.getColor(R.color.grey))
        textfieldLastName.setHintTextColor(resources.getColor(R.color.grey))
        textfieldPhoneNumber.setHintTextColor(resources.getColor(R.color.grey))
        textfieldEmail.setHintTextColor(resources.getColor(R.color.grey))
    }

    //Used to check for changes in training to decide if save changes warning will appear
    private fun isTrainingChanged(originalValue: Int, currentValue: Boolean): Boolean{
        //for original value if 1 is passed tha means they are trained, 0 is not trained
        var training = 0
        if (currentValue){
            training = 1
        }

        if(originalValue != training) {
            return true
        }
        return false
    }

    // Used to check for changes in training to decide if save changes warning will appear
    private fun isFieldChanged(originalValue: String, currentValue: String): Boolean{
        if(originalValue != currentValue){
            return true
        }
        return false
    }

    //Used to updateEmployee rather than add an already existing employee to database
    private fun updateEmployee() {
        val employeeID = intent.getIntExtra("id", -1)

        if (employeeID != -1) {
            val first_name = textfieldFirstName.text.toString()
            val last_name = textfieldLastName.text.toString()
            val email = textfieldEmail.text.toString()
            val phone = textfieldPhoneNumber.text.toString()

            val isTrainedMorning = if (checkboxMorning.isChecked) 1 else 0
            val isTrainedAfternoon = if (checkboxAfternoon.isChecked) 1 else 0

            val employee = Employee(id = employeeID, firstname = first_name, lastname = last_name, email = email, phone = phone, trainedopening = isTrainedMorning,
                trainedclosing = isTrainedAfternoon, daysOff = "", shiftpref = 0, shiftCountPerWeekShift = 0)

            if (first_name.isEmpty() || last_name.isEmpty() || email.isEmpty() || phone.isEmpty()) {

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()

                if(first_name.isEmpty()){
                    textfieldFirstName.hint = "First Name*"
                    textfieldFirstName.setHintTextColor(resources.getColor(R.color.red))
                }
                if(last_name.isEmpty()){
                    textfieldLastName.hint = "Last Name*"
                    textfieldLastName.setHintTextColor(resources.getColor(R.color.red))
                }
                if(email.isEmpty()){
                    textfieldEmail.hint = "Email*"
                    textfieldEmail.setHintTextColor(resources.getColor(R.color.red))
                }
                if(phone.isEmpty()){
                    textfieldPhoneNumber.hint = "Phone*"
                    textfieldPhoneNumber.setHintTextColor(resources.getColor(R.color.red))
                }
            }else if (!isEmailValid(email)) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }else if (!isPhoneValid(phone)){
                Toast.makeText(this, "Invalid phone format", Toast.LENGTH_SHORT).show()
            } else if (!isFirstNameValid(first_name)) {
                Toast.makeText(this, "Invalid first name (Must only include A-Z and ')", Toast.LENGTH_SHORT).show()
            } else if (!isLastNameValid(last_name)) {
                Toast.makeText(this, "Invalid last name (Must only include A-Z and ')", Toast.LENGTH_SHORT).show()
            }
            else {
                //val updatedEmployee = employee

                if (sqliteemployeedb.isNameAlreadyTaken(employee)) {
                    Toast.makeText(
                        this,
                        "Employee already exists (Please ensure there is no employee with this first and last name)",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (sqliteemployeedb.isFirstNameTaken(employee)) {
                    Toast.makeText(
                        this,
                        "This first name already exists, please consider editing by using a nickname",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                val status = sqliteemployeedb.updateData(employee)

                if (status > 0) {
                    Toast.makeText(this, "Employee Updated Successfully", Toast.LENGTH_SHORT).show()
                    val restartIntent = Intent(this, ManageEmployeePage::class.java)
                    startActivity(restartIntent)
                } else {
                    Toast.makeText(this, "Failed to update employee. Check all fields are filled.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Invalid Employee ID. Cannot update.", Toast.LENGTH_SHORT).show()
        }
    }

}
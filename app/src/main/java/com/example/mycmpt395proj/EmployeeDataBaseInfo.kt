package com.example.mycmpt395proj
import android.provider.BaseColumns
object ShiftFlags {
    const val MONDAY_MORNING = 1
    const val TUESDAY_MORNING = 1 shl 1
    const val WEDNESDAY_MORNING = 1 shl 2
    const val THURSDAY_MORNING = 1 shl 3
    const val FRIDAY_MORNING = 1 shl 4
    const val MONDAY_AFTERNOON = 1 shl 5
    const val TUESDAY_AFTERNOON = 1 shl 6
    const val WEDNESDAY_AFTERNOON = 1 shl 7
    const val THURSDAY_AFTERNOON = 1 shl 8
    const val FRIDAY_AFTERNOON = 1 shl 9
    const val SATURDAY = 1 shl 10
    const val SUNDAY = 1 shl 11
    const val USELESS = 0
}

class StudentsDataBaseHandler {

    object TableInfo: BaseColumns {
        const val TABLE_NAME = "employeeDB"
        const val COL_FIRSTNAME = "firstname"
        const val COL_LASTNAME = "lastname"
        const val COL_AGE = "age"
        const val COL_PHONE = "phone"
        const val COL_ADDRESS = "address"
        const val COL_CITY = "city"
        const val COL_PROVINCE = "province"
        const val COL_EMERGENCYPHONE = "emergencyphone"
        const val COL_TRAINEDOPENING = "trainedopening" // 0 = False, 1 = True
        const val COL_TRAINEDCLOSING = "trainedclosing" // 0 = False, 1 = True
        const val COL_EMAIL = "email"
        const val COL_SHIFT_PREF = "shiftpreference"
    }

    val createTable = "CREATE TABLE ${TableInfo.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${TableInfo.COL_FIRSTNAME} TEXT," +
            "${TableInfo.COL_LASTNAME} TEXT," +
            "${TableInfo.COL_AGE} INTEGER," +
            "${TableInfo.COL_PHONE} INTEGER," +
            "${TableInfo.COL_ADDRESS} INTEGER," +
            "${TableInfo.COL_CITY} TEXT," +
            "${TableInfo.COL_PROVINCE} TEXT," +
            "${TableInfo.COL_EMERGENCYPHONE} INTEGER," +
            "${TableInfo.COL_TRAINEDOPENING} INTEGER," +
            "${TableInfo.COL_TRAINEDCLOSING} INTEGER," +
            "${TableInfo.COL_EMAIL} TEXT, " +
            "${TableInfo.COL_SHIFT_PREF} INTEGER)"
}

/*

//Per staff member for implementation into the calendar

date
shift type: morning evening full (1 2 3)
staff id

//full calendar with day reqs

date
number of staff required: (1 2 3)
shift type (2)
staff id
available staff for this day
scheduled staff


COL_DATESSCHEDULED
    TEXT

    1;12;23
 */
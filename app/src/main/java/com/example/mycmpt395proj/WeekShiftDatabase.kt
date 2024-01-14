package com.example.mycmpt395proj

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class WeekShift(
    var date: String,
    var morningID0: Int,
    var morningID1: Int,
    var afternoonID0: Int,
    var afternoonID1: Int)

class WeekShiftDatabaseOperations(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "WeekShift.db"
        const val DATABASE_VERSION = 1
        const val DATABASE_TABLE_NAME = "weekshiftDB"
        const val COL_DATE = "date"
        const val COL_MORNINGID0 = "morningid0"
        const val COL_MORNINGID1 = "morningid1"
        const val COL_AFTERNOONID0 = "afternoonid0"
        const val COL_AFTERNOONID1 = "afternoonid1"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableSQL = """
            CREATE TABLE $DATABASE_TABLE_NAME(
            $COL_DATE STRING,
            $COL_MORNINGID0 INTEGER,
            $COL_MORNINGID1 INTEGER,
            $COL_AFTERNOONID0 INTEGER,
            $COL_AFTERNOONID1 INTEGER);
        """.trimIndent()

        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")

    }

    fun insertData(weekShift: WeekShift): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put(COL_DATE, weekShift.date)
        values.put(COL_MORNINGID0, weekShift.morningID0)
        values.put(COL_MORNINGID1, weekShift.morningID1)
        values.put(COL_AFTERNOONID1, weekShift.afternoonID0)
        values.put(COL_AFTERNOONID1, weekShift.afternoonID1)

        val success = db.insert(DATABASE_TABLE_NAME, null, values)

        db.close()

        return success
    }

    // Make sure to check for null
    fun getWeekShift(date: String): WeekShift? {
        val db = readableDatabase

        val cursor = db.query(
            DATABASE_TABLE_NAME,
            arrayOf(COL_DATE, COL_MORNINGID0, COL_MORNINGID1, COL_AFTERNOONID0, COL_AFTERNOONID1),
            "$COL_DATE=?",
            arrayOf(date),
            null, null, null, null
        )

        var result: WeekShift? = null

        if (cursor.moveToFirst()) {

            result = WeekShift(
                cursor.getString(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4)
            )
        }

        db.close()

        return null
    }
}

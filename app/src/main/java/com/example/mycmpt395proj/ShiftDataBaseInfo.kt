package com.example.mycmpt395proj
import android.provider.BaseColumns
class ShiftDataBaseHandler {
    object TableInfo : BaseColumns {
        const val TABLE_NAME = "shiftDB"
        const val COL_DATE = "date"
        const val COL_TYPE = "type"

    }
}
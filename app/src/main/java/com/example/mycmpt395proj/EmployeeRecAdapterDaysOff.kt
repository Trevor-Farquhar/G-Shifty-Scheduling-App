package com.example.mycmpt395proj
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmployeeRecAdapterDaysOff(
    private val context: Context,
    private val dates: MutableList<String>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<EmployeeRecAdapterDaysOff.EmployeeViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(employee: Date)
    }

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.emp_days_off_textview)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.employee_view_daysofffordate_design, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val date = dates[position]

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormatter.format(date)

        holder.dateTextView.text = formattedDate


    }


    override fun getItemCount(): Int {
        return dates.size
    }

}
package com.example.mycmpt395proj
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmployeeRecAdapterWeekdayWeekend(
    private val context: Context,
    internal val employeeList: MutableList<Employee>,
    private val itemClickListener: OnItemClickListener,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<EmployeeRecAdapterWeekdayWeekend.EmployeeViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(employee: Employee, recyclerView: RecyclerView)
    }

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.emp_view_weekdayend_names_text)
        val trainedTextView: TextView = itemView.findViewById(R.id.emp_view_weekdayend_trained_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.employee_view_weekendweekday_design, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int){
        val employee = employeeList[position]

        val fullName = "${employee.firstname} ${employee.lastname}"
        holder.nameTextView.text = fullName

        val trainedStatus = if (employee.trainedopening == 1){
            if (employee.trainedclosing == 1){
                "Fully Trained"
            }else{
                "Morning Trained"
            }
        }else if (employee.trainedclosing == 1){
            "Afternoon Trained"
        }else {
            "Not Trained"
        }

        holder.trainedTextView.text = trainedStatus

        holder.itemView.setOnClickListener{
            itemClickListener.onItemClick(employee, recyclerView)
        }

    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

}
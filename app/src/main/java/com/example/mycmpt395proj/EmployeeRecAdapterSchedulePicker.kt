package com.example.mycmpt395proj
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.util.Locale
import kotlin.math.log

/*Imma be honest, I really dont know whats going on here, Ill try my best to set comments*/
class EmployeeRecAdapterSchedulePicker(
    private val context: Context,
    private val employeeList: List<Employee>,
    private val itemClickListener: OnItemClickListener
    /* Not sure the purpose of this line, Something to do with using the inner class being used as the input for the adapter  */
) : RecyclerView.Adapter<EmployeeRecAdapterSchedulePicker.EmployeeViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(employee: Employee)
    }

    /* Setting up values to the Id of the listdesign*/
    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.emp_view_schedpick_names_text)
        val TrainedTextView: TextView = itemView.findViewById(R.id.emp_schedpick_trained_text)
        val countTextView: TextView = itemView.findViewById(R.id.emp_schedpick_count)
    }

    /*On Creation of View Holder*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.employee_view_schedulepicker_design, parent, false)
        return EmployeeViewHolder(view)
    }

    /* Binding database to each employee design*/
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]

        val fullName = "${employee.firstname} ${employee.lastname}"
        holder.nameTextView.text = fullName

        val trainedTextView = if (employee.trainedopening == 1) {
            if (employee.trainedclosing == 1) {
                "Fully Trained"
            } else {
                "Morning Trained"
            }
        } else if (employee.trainedclosing == 1) {
            "Afternoon Trained"
        } else {
            "Not Trained"
        }

        holder.TrainedTextView.text = trainedTextView

        val shiftcount = "Shifts this week: ${employee.shiftCountPerWeekShift}"


        holder.countTextView.text = shiftcount.toString()

        /*ON Click Listener*/
        holder.itemView.setOnClickListener{
            itemClickListener.onItemClick(employee)
        }
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

}

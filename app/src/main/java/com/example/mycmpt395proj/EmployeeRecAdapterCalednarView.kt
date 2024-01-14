package com.example.mycmpt395proj
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*Imma be honest, I really dont know whats going on here, Ill try my best to set comments*/
class EmployeeRecAdapterCalednarView(
    private val context: Context,
    private val employeeList: List<Employee>,
    private val itemClickListener: OnItemClickListener
    /* Not sure the purpose of this line, Something to do with using the inner class being used as the input for the adapter  */
) : RecyclerView.Adapter<EmployeeRecAdapterCalednarView.EmployeeViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(employee: Employee)
    }

    /* Setting up values to the Id of the listdesign*/
    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.employee_mainpage_name)
        val profileTextView: TextView = itemView.findViewById(R.id.employee_mainpage_profile)
        val trainedTextView: TextView = itemView.findViewById(R.id.employee_mainpage_training)
    }

    /*On Creation of View Holder*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.employee_view_mainpage_design, parent, false)
        return EmployeeViewHolder(view)
    }

    /* Binding database to each employee design*/
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]

        val fullName = "${employee.firstname} ${employee.lastname}"
        holder.nameTextView.text = fullName
        Log.d("EmployeeRecAdapterMain", "Employee: $employee")
        //Tell user if they are working in the morning or afternoon

        /*ON Click Listener*/
        holder.itemView.setOnClickListener{
            itemClickListener.onItemClick(employee)
        }

        val firstletter = employee.firstname.substring(0,1).uppercase()
        holder.profileTextView.text = firstletter

        val trainedStatus = if (employee.trainedopening == 1) {
            if (employee.trainedclosing == 1) {
                "Fully\nTrained"
            } else {
                "Morning\nTrained"
            }
        } else if (employee.trainedclosing == 1) {
            "Afternoon\nTrained"
        } else {
            "Not\nTrained"
        }

        holder.trainedTextView.text = trainedStatus
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

}

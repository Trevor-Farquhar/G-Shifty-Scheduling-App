package com.example.mycmpt395proj
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycmpt395proj.Employee
import java.util.Locale

/*Imma be honest, I really dont know whats going on here, Ill try my best to set comments*/
class EmployeeRecAdapter(
    private val context: Context,
    private val employeeList: List<Employee>,
    private val itemClickListener: OnItemClickListener
    /* Not sure the purpose of this line, Something to do with using the inner class being used as the input for the adapter  */
) : RecyclerView.Adapter<EmployeeRecAdapter.EmployeeViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(employee: Employee)
    }
    /* Setting up values to the Id of the listdesign*/
    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.employee_listdesign_name)
        val trainedTextView: TextView = itemView.findViewById(R.id.employee_listdesign_subname)
        val phoneTextView: TextView = itemView.findViewById(R.id.employee_listdesign_phonenumber)
        val profileTextView: TextView = itemView.findViewById(R.id.employee_listdesign_profile_text)
    }

    /*On Creation of View Holder*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.employee_view_list_design, parent, false)
        return EmployeeViewHolder(view)
    }

    /* Binding database to each employee design*/
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]

        val fullName = "${employee.firstname} ${employee.lastname}"
        holder.nameTextView.text = fullName

        // Assuming trainedopening and trainedclosing are 0 or 1, you can display "Trained" or "Not Trained"

        val trainedStatus = if (employee.trainedopening == 1) {
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

        holder.trainedTextView.text = trainedStatus

        holder.phoneTextView.text = employee.phone

        val firstletter = employee.firstname.substring(0,1).uppercase()
        holder.profileTextView.text = firstletter

        /*ON Click Listener*/
        holder.itemView.setOnClickListener{
            itemClickListener.onItemClick(employee)
        }
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

}

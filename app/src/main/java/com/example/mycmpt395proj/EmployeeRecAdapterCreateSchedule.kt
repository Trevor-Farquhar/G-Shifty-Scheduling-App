package com.example.mycmpt395proj
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*Imma be honest, I really dont know whats going on here, Ill try my best to set comments*/
class EmployeeRecAdapterCreateSchedule(
    private val context: Context,
    private val dateList: List<String>,
    private val itemClickListener: OnItemClickListener
    /* Not sure the purpose of this line, Something to do with using the inner class being used as the input for the adapter  */
) : RecyclerView.Adapter<EmployeeRecAdapterCreateSchedule.DateViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(date: String)
    }
    /* Setting up values to the Id of the listdesign*/
    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateholder_Recview)
    }

    /*On Creation of View Holder*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.employee_view_date_design, parent, false)
        return DateViewHolder(view)
    }

    /* Binding database to each employee design*/
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dateList[position]
        Log.d("MyTag", date)
        holder.dateTextView.text = date

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(date)
        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

}

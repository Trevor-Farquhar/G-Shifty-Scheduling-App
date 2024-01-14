import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView

class CustomPopup(context: Context, title: String, message: String) : AlertDialog(context) {

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("$message")
            .setTitle("$title")


        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    companion object {
        fun show(context: Context, title: String, message: String) {
            val customPopup = CustomPopup(context, title, message)
        }
    }

}

package com.agronutritions.shop.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.agronutritions.shop.R
import java.text.SimpleDateFormat

class Utils{
    companion object{

        fun showAlertWarning(context: Context, title: String, message: String, listener: DialogInteractionListener?, id: Int){
            val builder = AlertDialog.Builder(context,R.style.CustomAlertDialog)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Yes"){ _, _ ->
                listener?.onPositiveResponse(id)
            }
            builder.setNegativeButton("Cancel"){ _, _ ->
                listener?.onNegativeResponse(id)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        fun showAlertOk(context: Context, title: String, message: String, listener: DialogInteractionListener, id: Int){
            val builder = AlertDialog.Builder(context,R.style.CustomAlertDialog)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Ok"){ _, _ ->
                listener.onPositiveResponse(id)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        fun showAlertSuccess(context: Context, title: String, message: String, listener: DialogInteractionListener, id: Int){
            val builder = AlertDialog.Builder(context,R.style.CustomAlertDialog)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Close"){ _, _ ->
                listener.onPositiveResponse(id)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        fun showAlertError(context: Context, title: String, message: String, listener: DialogInteractionListener, id: Int){
            val builder = AlertDialog.Builder(context,R.style.CustomAlertDialog)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Close"){ _, _ ->
                listener.onPositiveResponse(id)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        fun showSuccessDialog(
            context: Context,
            transId: String?,
            amount: String?,
            orderId: String?,
            listener: DialogInteractionListener?,
            dialogueRequestCode: Int
        ) {
            val dialog = Dialog(context, R.style.custom_ui_dialogue)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog_order_placed)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tid = dialog.findViewById<TextView>(R.id.tid)
            val tidLay = dialog.findViewById<LinearLayout>(R.id.tidLay)
            val oid = dialog.findViewById<TextView>(R.id.oid)
            val oidLay = dialog.findViewById<LinearLayout>(R.id.oidLay)
            val orderAmt = dialog.findViewById<TextView>(R.id.orderAmt)
            val buttonOk = dialog.findViewById<LinearLayout>(R.id.buttonOk)
            if (transId!!.isNotEmpty()) {
                tidLay.visibility =View.VISIBLE
                tid.text = transId
            } else {
                tidLay.visibility =View.GONE
            }
            oid.text = orderId
            orderAmt.text = context!!.resources.getString(R.string.Rs) + amount
            buttonOk.setOnClickListener { v: View? ->
                dialog.dismiss()
                listener?.onPositiveResponse(dialogueRequestCode)
            }
            dialog.show()
        }

        fun formatDate_yyyyMMddHHmmss(inputDate: String?): String? {
            var outputDate: String? = ""
            try {
                val date =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputDate)
                outputDate = SimpleDateFormat("dd-MM-yyyy").format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return outputDate
        }
    }

}
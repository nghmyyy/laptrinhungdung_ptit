package com.map.nguyen_ha_my.bt2_lancuoi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.model.TransactionWithInOut

class TransactionAdapter(
    private val context: Context,
    private val transactions: List<TransactionWithInOut>
) : BaseAdapter() {
    override fun getCount(): Int {
        return transactions.size
    }

    override fun getItem(position: Int): TransactionWithInOut {
        return transactions[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.items_list, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val transactionWithInOut = getItem(position)
        holder.txtItemCategory.text = transactionWithInOut.transaction.name
        holder.txtItemAmount.text = transactionWithInOut.transaction.amount.toString()
        holder.txtItemNotes.text = transactionWithInOut.transaction.note

        // Set màu sắc cho TextView dựa trên idInOut
        if (transactionWithInOut.idInOut == 1) {
            holder.txtItemAmount.setTextColor(context.getColor(R.color.green)) // Ví dụ màu xanh
        } else {
            holder.txtItemAmount.setTextColor(context.getColor(R.color.red)) // Ví dụ màu đỏ
        }

        return view
    }

    private class ViewHolder(view: View) {
        val txtItemCategory: TextView = view.findViewById(R.id.txtItemCategory)
        val txtItemAmount: TextView = view.findViewById(R.id.txtItemAmount)
        val txtItemNotes: TextView = view.findViewById(R.id.txtItemNotes)
    }
}

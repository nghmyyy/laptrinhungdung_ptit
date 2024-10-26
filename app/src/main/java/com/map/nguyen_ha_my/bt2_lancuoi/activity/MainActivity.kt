package com.map.nguyen_ha_my.bt2_lancuoi.activity

import android.content.Intent
import java.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.adapter.TransactionAdapter
import com.map.nguyen_ha_my.bt2_lancuoi.database.SQLHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var sqlHelper: SQLHelper
    private lateinit var textDate: TextView
    private lateinit var txtSum: TextView
    private lateinit var txtSumIn: TextView
    private lateinit var txtSumOut: TextView
    private lateinit var listTransaction: ListView
    private lateinit var btnAddTransaction: Button
    private lateinit var imgCalendar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main)
        initView()

        // Khởi tạo SQLHelper
        sqlHelper = SQLHelper(this)

        // Set Date cho textDate
        setCurrentDate()

        // Set Sum cho txtSum
        setCurrentSum()

        // Set SumIn cho txtSumIn, setSumOut cho txtSumOut
        setCurrentSumIn()
        setCurrentSumOut()

        // Set ListView
        updateTransactionList(Date())

        // Set Intent cho btnAdd
        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }



        // Sự kiện cho imgCalendar
        imgCalendar.setOnClickListener {
            val intent = Intent(this, MaterialCalendarViewAct::class.java)
            startActivity(intent)
        }


    }

    private fun initView() {
        textDate = findViewById(R.id.textDate)
        txtSum = findViewById(R.id.txtSum)
        txtSumIn = findViewById(R.id.txtSumIn)
        txtSumOut = findViewById(R.id.txtSumOut)
        listTransaction = findViewById(R.id.listTransaction)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        imgCalendar = findViewById(R.id.imgCalendar)
    }

    private fun setCurrentDate() {
        val calendar = Calendar.getInstance().time
        val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        textDate.text = formatDate.format(calendar)
    }

    private fun setCurrentSum() {
        val dateString = textDate.text.toString()
        val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date = formatDate.parse(dateString) ?: Date()
        val getTotalByDate = sqlHelper.getTotalTransactionByDate(date)
        txtSum.text = getTotalByDate.toString()
    }

    private fun setCurrentSumIn() {
        val dateString = textDate.text.toString()
        val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date = formatDate.parse(dateString) ?: Date()
        val getTotalSumIn = sqlHelper.getSumOfInByDate(date)
        txtSumIn.text = getTotalSumIn.toString()
    }

    private fun setCurrentSumOut() {
        val dateString = textDate.text.toString()
        val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date = formatDate.parse(dateString) ?: Date()
        val getTotalSumOut = sqlHelper.getSumOfOutByDate(date)
        txtSumOut.text = getTotalSumOut.toString()
    }

    private fun updateUIWithSelectedDate(selectedDate: Date) {
        val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        textDate.text = formatDate.format(selectedDate)

        setCurrentSum()
        setCurrentSumIn()
        setCurrentSumOut()

        updateTransactionList(selectedDate)
    }

    private fun updateTransactionList(date: Date) {
        val transactionWithInOuts = sqlHelper.getTransactionByDate(date)
        val adapter = TransactionAdapter(this, transactionWithInOuts)
        listTransaction.adapter = adapter
    }
}

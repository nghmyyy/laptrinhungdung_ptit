package com.map.nguyen_ha_my.bt2_lancuoi.activity

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.adapter.TransactionAdapter
import com.map.nguyen_ha_my.bt2_lancuoi.database.SQLHelper
import com.map.nguyen_ha_my.bt2_lancuoi.model.Transaction
import com.map.nguyen_ha_my.bt2_lancuoi.model.TransactionWithInOut
import com.map.nguyen_ha_my.bt2_lancuoi.ui.theme.Bt2_lancuoiTheme
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
    private lateinit var transactions: List<Transaction>
    private lateinit var btnAddTransaction: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main)
        initView()

        //gọi database
        sqlHelper = SQLHelper(this)

        //setDate cho textDate
        setCurrentDate()

        //setSum cho textSum
        setCurrentSum()

        //setSumIn cho txtSumIn, setSumOut cho txtSumOut
        setCurrentSumIn()
        setCurrentSumOut()

        // Set ListView
        val transactionWithInOuts = sqlHelper.getTransactionByDate(Date()) // Chỉ lấy danh sách Transaction
        val adapter = TransactionAdapter(this, transactionWithInOuts) // Vẫn sử dụng TransactionAdapter
        listTransaction.adapter = adapter

        //setIntent cho btnAdd
        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
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
    }

    private fun setCurrentDate() {
        val calendar = Calendar.getInstance().time
        val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        textDate.text = formatDate.format(calendar)
    }

    private fun setCurrentSum() {
        //lay Date tu textDate
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
}

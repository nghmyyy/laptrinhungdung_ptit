package com.map.nguyen_ha_my.bt2_lancuoi.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.database.SQLHelper
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import java.text.SimpleDateFormat
import java.util.*

class MaterialCalendarViewAct : ComponentActivity() {
    private lateinit var calView: CalendarView
    private lateinit var txtSumInCalendar: TextView
    private lateinit var txtSumOutCalendar: TextView
    private lateinit var sqlHelper: SQLHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.material_calendar_view)

        initView()
        sqlHelper = SQLHelper(this)

        // Hiển thị tổng giao dịch mặc định cho ngày hiện tại
        val currentDate = Calendar.getInstance().timeInMillis
        updateTransactionSummaryForDate(currentDate)

        // Đánh dấu các ngày có giao dịch trên lịch
        markTransactionDays()

        // Lắng nghe sự kiện khi người dùng chọn ngày
        calView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val selectedDate = eventDay.calendar.timeInMillis
                updateTransactionSummaryForDate(selectedDate)
            }
        })
    }

    private fun initView() {
        calView = findViewById(R.id.calView)
        txtSumInCalendar = findViewById(R.id.txtSumInCalendar)
        txtSumOutCalendar = findViewById(R.id.txtSumOutCalendar)
    }

    private fun updateTransactionSummaryForDate(dateInMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

        val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateFormatted: String = formatDate.format(calendar.time)

        val sumIn = sqlHelper.getSumOfInByDate(formatDate.parse(dateFormatted))
        val sumOut = sqlHelper.getSumOfOutByDate(formatDate.parse(dateFormatted))

        txtSumInCalendar.text = "Thu: $sumIn VND"
        txtSumOutCalendar.text = "Chi: $sumOut VND"
    }

    private fun markTransactionDays() {
        val events = mutableListOf<EventDay>()
        val calendar = Calendar.getInstance()

        // Giả sử sqlHelper.getTransactionDates trả về một danh sách các ngày có giao dịch
        val transactionDates: List<Date> = sqlHelper.getTransactionDates()

        // Duyệt qua các ngày giao dịch và thêm vào danh sách events
        for (date in transactionDates) {
            calendar.time = date

            // Lấy tổng tiền vào và tiền ra cho ngày hiện tại
            val sumIn = sqlHelper.getSumOfInByDate(date)
            val sumOut = sqlHelper.getSumOfOutByDate(date)

            val drawable: Drawable? = when {
                sumIn > 0 && sumOut > 0 -> {
                    ContextCompat.getDrawable(this, R.drawable.transaction_icon) // Biểu tượng cho cả tiền vào và tiền ra
                }
                sumIn > 0 -> {
                    ContextCompat.getDrawable(this, R.drawable.salary) // Biểu tượng cho tiền vào
                }
                sumOut > 0 -> {
                    ContextCompat.getDrawable(this, R.drawable.down) // Biểu tượng cho tiền ra
                }
                else -> {
                    null // Không có giao dịch
                }
            }

            drawable?.let {
                // Thêm sự kiện với drawable tương ứng
                events.add(EventDay(calendar.clone() as Calendar, it))
            }

            // Cập nhật tổng số tiền vào và ra cho ngày này để hiển thị trong TextView
            txtSumInCalendar.text = "Thu: $sumIn VND"
            txtSumOutCalendar.text = "Chi: $sumOut VND"
        }

        // Đặt các sự kiện này vào CalendarView
        calView.setEvents(events)
    }

}

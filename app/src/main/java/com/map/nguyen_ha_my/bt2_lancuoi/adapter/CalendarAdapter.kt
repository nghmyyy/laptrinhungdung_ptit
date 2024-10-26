import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.database.SQLHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
class CalendarAdapter(
    private val transactionsByDate: Map<Date, Pair<Double, Double>>,  // Dữ liệu tổng tiền vào/ra theo ngày
    private val month: Int,
    private val year: Int,
    private val sqlHelper: SQLHelper
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private val daysWithTransactions: List<Date> = transactionsByDate.keys.toList()  // Lấy danh sách các ngày có giao dịch

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val currentDate = daysWithTransactions[position] // Lấy ngày giao dịch từ danh sách

        // Lấy dữ liệu từ transactionsByDate
        val sumInOut = transactionsByDate[currentDate] ?: Pair(0.0, 0.0) // Lấy tổng tiền từ map

        // Hiển thị dữ liệu trong view holder
        holder.bind(currentDate, sumInOut.first, sumInOut.second)
    }

    override fun getItemCount(): Int {
        return daysWithTransactions.size // Trả về số ngày có giao dịch
    }

    private fun getDateForPosition(position: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, position + 1) // +1 vì position bắt đầu từ 0
        return calendar.time
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1) // Đặt ngày đầu tiên của tháng
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // Lấy số ngày thực tế trong tháng
    }

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        private val txtSumInDay: TextView = itemView.findViewById(R.id.txtSumInDay)
        private val txtSumOutDay: TextView = itemView.findViewById(R.id.txtSumOutDay)

        fun bind(date: Date, sumIn: Double, sumOut: Double) {
            val day = SimpleDateFormat("dd", Locale.getDefault()).format(date)
            txtDate.text = day
            txtSumInDay.text = "In: $sumIn"
            txtSumOutDay.text = "Out: $sumOut"
        }
    }
}

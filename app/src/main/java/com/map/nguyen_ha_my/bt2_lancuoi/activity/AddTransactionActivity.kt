package com.map.nguyen_ha_my.bt2_lancuoi.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.database.SQLHelper
import com.map.nguyen_ha_my.bt2_lancuoi.model.Category

class AddTransactionActivity : ComponentActivity() {
    private lateinit var radBtnInOut: RadioGroup
    private lateinit var spinnerCat: Spinner
    private lateinit var edTxtAddAmount: EditText
    private lateinit var edTxtAddDate: EditText
    private lateinit var edTxtAddNote: EditText // Đã sửa lỗi từ edtxtAddNote
    private lateinit var btnAdd: Button
    private lateinit var sqlHelper: SQLHelper
    private lateinit var imgDot: ImageView
    private lateinit var btnAddCat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_transaction)
        initView()

        // Khởi tạo sqlHelper
        sqlHelper = SQLHelper(this)

        // Xử lý Spinner
        radBtnInOut.setOnCheckedChangeListener { group, checkedId ->
            val categories = if (checkedId == R.id.radBtnIn) {
                sqlHelper.getCategoriesByInOut(1) // "THU"
            } else {
                sqlHelper.getCategoriesByInOut(0) // "CHI"
            }
            updateSpinner(categories)
        }

        // DatePicker
        edTxtAddDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Xử lý nút thêm
        btnAdd.setOnClickListener {
            // Kiểm tra xem radio button đã được chọn hay chưa
            if (radBtnInOut.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Vui lòng chọn một tùy chọn In/Out", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra xem spinner có mục nào được chọn hay chưa
            if (spinnerCat.selectedItem == null || spinnerCat.selectedItemPosition == AdapterView.INVALID_POSITION) {
                Toast.makeText(this, "Vui lòng chọn một danh mục", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra dữ liệu nhập
            if (edTxtAddNote.text.toString().isEmpty() || edTxtAddDate.text.toString().isEmpty() || edTxtAddAmount.text.toString().isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                val amount = edTxtAddAmount.text.toString().toDoubleOrNull()
                val note = edTxtAddNote.text.toString()
                val date = edTxtAddDate.text.toString()
                val categoryName = spinnerCat.selectedItem.toString()

                // Xác định idInOut dựa trên lựa chọn của RadioGroup
                val idInOut = if (radBtnInOut.checkedRadioButtonId == R.id.radBtnIn) 1 else 0

                // Thêm giao dịch vào cơ sở dữ liệu
                if (amount != null) {
                    val transactionId = sqlHelper.insertTransactionWithCategory(
                        name = categoryName,
                        amount = amount,
                        note = note,
                        date = date,
                        idInOut = idInOut
                    )

                    // Kiểm tra kết quả và thông báo cho người dùng
                    if (transactionId != -1L) {
                        Toast.makeText(this, "Thêm giao dịch thành công!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Thêm giao dịch thất bại!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        imgDot.setOnClickListener {
            btnAddCat.visibility = if (btnAddCat.visibility == View.VISIBLE) {
                View.GONE // Nếu nút đang hiển thị thì ẩn nó đi
            } else {
                View.VISIBLE // Nếu nút đang ẩn thì hiển thị nó
            }
        }

        // Xử lý nút thêm danh mục
        btnAddCat.setOnClickListener {
            val intent = Intent(this, AddCatAct::class.java)
            startActivity(intent)
        }
    }

    private fun initView() {
        radBtnInOut = findViewById(R.id.radBtnInOut)
        spinnerCat = findViewById(R.id.spinnerCat)
        edTxtAddNote = findViewById(R.id.edTxtAddNote) // Đã sửa lỗi từ edtxtAddNote
        edTxtAddAmount = findViewById(R.id.edTxtAddAmount)
        edTxtAddDate = findViewById(R.id.edTxtAddDate)
        btnAdd = findViewById(R.id.btnAdd)
        imgDot = findViewById(R.id.imgDot)
        btnAddCat = findViewById(R.id.btnAddCat)
    }

    private fun updateSpinner(categories: List<Category>) {
        // Tạo danh sách tên danh mục để hiển thị trong Spinner
        val categoryNames = categories.map { it.name }

        // Tạo ArrayAdapter cho Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Gán Adapter cho Spinner
        spinnerCat.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                edTxtAddDate.setText(selectedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }

}

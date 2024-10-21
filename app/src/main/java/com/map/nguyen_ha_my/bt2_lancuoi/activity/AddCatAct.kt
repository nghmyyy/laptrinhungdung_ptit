package com.map.nguyen_ha_my.bt2_lancuoi.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.database.SQLHelper
import com.map.nguyen_ha_my.bt2_lancuoi.model.Category

class AddCatAct : ComponentActivity() {
    private lateinit var spinnerInOut: Spinner
    private lateinit var edTxtName: EditText
    private lateinit var spinnerIcon: Spinner
    private lateinit var spinnerParent: Spinner
    private lateinit var btnCatAdd: Button
    private lateinit var sqlHelper: SQLHelper
    private lateinit var imageViewIcon: ImageView  // Thêm ImageView để hiển thị icon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_category)
        initView()

        // Khởi tạo SQL
        sqlHelper = SQLHelper(this)

        // Setup spinner type
        setUpSpinnerType()
        setUpSpinnerIcon()  // Setup spinner cho icon

        // Listener cho spinnerInOut
        spinnerInOut.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedType = spinnerInOut.selectedItem.toString()
                updateIconAndParent(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener cho spinnerParent
        spinnerParent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Lấy ID cha khi người dùng chọn
                val parentIds = spinnerParent.tag as? List<Int>
                val selectedParentId = parentIds?.get(position)
                // Sử dụng selectedParentId nếu cần
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //btnAdd
        btnCatAdd.setOnClickListener {
            addCategory()
        }
    }

    private fun initView() {
        spinnerIcon = findViewById(R.id.spinnerIcon)
        spinnerParent = findViewById(R.id.spinnerParent)
        spinnerInOut = findViewById(R.id.spinnerInOut)
        btnCatAdd = findViewById(R.id.btnCatAdd)
        edTxtName = findViewById(R.id.edTxtName)
        imageViewIcon = findViewById(R.id.imageViewIcon)  // Khởi tạo ImageView cho icon
    }

    private fun setUpSpinnerType() {
        val types = arrayOf("Thu", "Chi")

        // Tạo Adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Gán Adapter
        spinnerInOut.adapter = adapter
    }

    private fun setUpSpinnerIcon() {
        // Tạo danh sách icon tương ứng
        val icons = arrayOf(R.drawable.salary, R.drawable.down) // Thay đổi theo icon của bạn
        val iconNames = arrayOf("Lương", "Chi tiêu") // Tên tương ứng với icon

        // Tạo Adapter cho spinnerIcon
        val iconAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, iconNames)
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIcon.adapter = iconAdapter

        // Cập nhật icon hiển thị khi người dùng chọn
        spinnerIcon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Cập nhật icon hiển thị
                imageViewIcon.setImageResource(icons[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateIconAndParent(selectedType: String) {
        // Xác định giá trị inOut dựa trên loại đã chọn
        val inOutValue = if (selectedType == "Thu") 1 else 0

        // Lấy danh sách danh mục đã có sẵn dựa trên giá trị inOut
        val categories = sqlHelper.getCategoriesByInOut(inOutValue)

        // Tạo danh sách tên và ID cho spinnerParent
        val parentNames = categories.map { it.name }
        val parentIds = categories.map { it.id }

        // Tạo Adapter cho spinnerParent
        val parentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, parentNames)
        parentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerParent.adapter = parentAdapter

        // Lưu trữ danh sách ID cha để sử dụng sau này
        spinnerParent.tag = parentIds
    }
    private fun addCategory() {
        val name = edTxtName.text.toString()
        val selectedIconPosition = spinnerIcon.selectedItemPosition
        val icon = when (selectedIconPosition) {
            0 -> R.drawable.salary // Thay đổi theo icon của bạn
            1 -> R.drawable.down // Thay đổi theo icon của bạn
            else -> R.drawable.salary // Icon mặc định nếu cần
        }

        val selectedInOutType = spinnerInOut.selectedItem.toString()
        val inOutValue = if (selectedInOutType == "Thu") 1 else 0

        val parentIds = spinnerParent.tag as? List<Int>
        val selectedParentId = parentIds?.get(spinnerParent.selectedItemPosition)

        // Thêm danh mục vào cơ sở dữ liệu
        val categoryId = sqlHelper.insertCategory(name, "", icon, selectedParentId, inOutValue) // Ghi chú có thể bỏ qua nếu không cần

        if (categoryId != -1L) {
            // Thêm thành công
            // Có thể hiển thị thông báo hoặc reset form
            Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show()
            clearFields() // Hàm để xóa các trường nhập liệu
        } else {
            // Thêm không thành công
            Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show()
        }
    }
    private fun clearFields() {
        edTxtName.text.clear()
        spinnerIcon.setSelection(0) // Reset về lựa chọn đầu tiên
        spinnerParent.setSelection(0) // Reset về lựa chọn đầu tiên
        spinnerInOut.setSelection(0) // Reset về lựa chọn đầu tiên
        imageViewIcon.setImageResource(R.drawable.salary) // Đặt icon mặc định
    }


}

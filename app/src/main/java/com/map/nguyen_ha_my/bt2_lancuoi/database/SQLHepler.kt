package com.map.nguyen_ha_my.bt2_lancuoi.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.util.Log
import com.map.nguyen_ha_my.bt2_lancuoi.R
import com.map.nguyen_ha_my.bt2_lancuoi.model.Category
import com.map.nguyen_ha_my.bt2_lancuoi.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.map.nguyen_ha_my.bt2_lancuoi.model.TransactionWithInOut


class SQLHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 1
        private const val DATE_FORMAT = "dd/MM/yyyy" // Moved date format to constant
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(InOutTable.CREATE_TABLE)
        db.execSQL(CategoryTable.CREATE_TABLE)
        db.execSQL(CatInOutTable.CREATE_TABLE)
        db.execSQL(TransactionTable.CREATE_TABLE)

        getDefaultInOut(db)
        insertDefaultCategory(db)
        //insertDefaultTransaction(db)
        //insertDefaultCatInOut(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(InOutTable.CREATE_TABLE)
        db.execSQL(CategoryTable.CREATE_TABLE)
        db.execSQL(CatInOutTable.CREATE_TABLE)
        db.execSQL(TransactionTable.CREATE_TABLE)
    }


    object InOutTable {
        const val TABLE_NAME = "InOut"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"

        const val CREATE_TABLE = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_NAME TEXT NOT NULL" +
                        ")"
                )
    }

    private fun getDefaultInOut(db: SQLiteDatabase) {
        val values1 = ContentValues().apply { put(InOutTable.COLUMN_NAME, "in") }
        val values2 = ContentValues().apply { put(InOutTable.COLUMN_NAME, "out") }
        db.insert(InOutTable.TABLE_NAME, null, values1)
        db.insert(InOutTable.TABLE_NAME, null, values2)
    }

    object CategoryTable {
        const val TABLE_NAME = "Category"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_IDPARENT = "idParent"
        const val COLUMN_ICON = "icon"
        const val COLUMN_NOTE = "note"
        const val COLUMN_INOUT = "inout"

        const val CREATE_TABLE = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_NAME TEXT NOT NULL, " +
                        "$COLUMN_NOTE TEXT NOT NULL, " +
                        "$COLUMN_ICON INTEGER NOT NULL, " +
                        "$COLUMN_IDPARENT INTEGER, " + // Thêm cột này
                        "$COLUMN_INOUT INTEGER, " +
                        "FOREIGN KEY($COLUMN_IDPARENT) REFERENCES $TABLE_NAME($COLUMN_ID)" +
                        ")"
                )
    }

    private fun insertDefaultCategory(db: SQLiteDatabase) {
        val categories = listOf(
            Pair("Lương", R.drawable.salary),
            Pair("Làm thêm", R.drawable.salary),
            Pair("Học bổng", R.drawable.salary),
            Pair("Bố mẹ cho", R.drawable.salary),
            Pair("Quà tặng", R.drawable.salary),
            Pair("Chi tiêu hàng ngày", R.drawable.down),
            Pair("Học phí", R.drawable.down),
            Pair("Tiền nhà", R.drawable.down),
            Pair("Tiền đi chợ", R.drawable.down),
            Pair("Tiền điện", R.drawable.down),
            Pair("Tiền nước", R.drawable.down),
            Pair("Tiền điện thoại", R.drawable.down),
            Pair("Tiền ăn", R.drawable.down)
        )

        categories.forEachIndexed { index, pair ->
            val values = ContentValues().apply {
                put(CategoryTable.COLUMN_NAME, pair.first)
                put(CategoryTable.COLUMN_ICON, pair.second)
                put(CategoryTable.COLUMN_NOTE, "hàng tháng")
                if (index >= 7) put(CategoryTable.COLUMN_IDPARENT, 7) // Gán ID cha cho các danh mục cụ thể
                if(index < 6) put(CategoryTable.COLUMN_INOUT, 1)
                else{
                    put(CategoryTable.COLUMN_INOUT, 0)
                }
            }
            db.insert(CategoryTable.TABLE_NAME, null, values)
        }
    }
    fun getCategoriesByInOut(inOut: Int): List<Category> {
        val categories = mutableListOf<Category>()
        val db = this.readableDatabase

        // Truy vấn để lấy các chủ đề dựa trên giá trị InOut
        val query = """
            SELECT * 
            FROM ${CategoryTable.TABLE_NAME} 
            WHERE ${CategoryTable.COLUMN_INOUT} = ?
        """.trimIndent()

        // Thực thi truy vấn với tham số
        val cursor = db.rawQuery(query, arrayOf(inOut.toString()))

        // Duyệt qua các kết quả và ánh xạ vào mô hình CategoryTable
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME))
                val inOut = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_INOUT))
                val icon = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ICON))
                val idParent = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_IDPARENT))
                val note = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NOTE))

                val category = Category(id, name, idParent, icon, note, inOut)
                categories.add(category)
            } while (cursor.moveToNext())
        }

        // Đóng cursor và cơ sở dữ liệu
        cursor.close()
        db.close()

        return categories
    }
    fun insertCategory(name: String, note: String, icon: Int, idParent: Int?, inOut: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(CategoryTable.COLUMN_NAME, name)
            put(CategoryTable.COLUMN_NOTE, note)
            put(CategoryTable.COLUMN_ICON, icon)
            put(CategoryTable.COLUMN_IDPARENT, idParent) // Có thể là null nếu không có ID cha
            put(CategoryTable.COLUMN_INOUT, inOut)
        }

        val categoryId = db.insert(CategoryTable.TABLE_NAME, null, values)
        db.close()
        return categoryId // Trả về ID của danh mục vừa tạo
    }

    object CatInOutTable {
        const val TABLE_NAME = "CatInOut"
        const val COLUMN_ID = "id"
        const val COLUMN_IDCAT = "idCat"
        const val COLUMN_IDINOUT = "idInOut"

        const val CREATE_TABLE = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_IDCAT INTEGER NOT NULL, " +
                        "$COLUMN_IDINOUT INTEGER NOT NULL, " +
                        "FOREIGN KEY($COLUMN_IDCAT) REFERENCES ${CategoryTable.TABLE_NAME}(${CategoryTable.COLUMN_ID}), " +
                        "FOREIGN KEY($COLUMN_IDINOUT) REFERENCES ${InOutTable.TABLE_NAME}(${InOutTable.COLUMN_ID})" +
                        ")"
                )
    }

    object TransactionTable {
        const val TABLE_NAME = "TransactionTable"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_IDCATINOUT = "idCatInOut" // Đảm bảo tên cột đúng
        const val COLUMN_DATE = "date"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_NOTE = "note"

        const val CREATE_TABLE = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_NAME TEXT, " +
                        "$COLUMN_NOTE TEXT, " +
                        "$COLUMN_AMOUNT REAL, " +
                        "$COLUMN_DATE TEXT, " +
                        "$COLUMN_IDCATINOUT INTEGER, " + // Thêm cột này
                        "FOREIGN KEY($COLUMN_IDCATINOUT) REFERENCES ${CatInOutTable.TABLE_NAME}(${CatInOutTable.COLUMN_ID})" +
                        ")"
                )
    }
    public fun getTransactionByDate(date: Date): List<TransactionWithInOut> {
        val transactions = mutableListOf<TransactionWithInOut>()
        val db = this.readableDatabase

        val formatDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val dateString = formatDate.format(date)

        val query = """
            SELECT t.*, catInOut.idInOut
            FROM TransactionTable t
            JOIN CatInOut catInOut ON t.idCatInOut = catInOut.id
            WHERE t.date = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(dateString))

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_NAME)),
                    date = formatDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE))),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_AMOUNT)),
                    note = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_NOTE)),
                    idCatInOut = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_IDCATINOUT))
                )

                val idInOut = cursor.getInt(cursor.getColumnIndexOrThrow("idInOut"))
                transactions.add(TransactionWithInOut(transaction, idInOut))
            } while (cursor.moveToNext())
        } else {
            Log.e("getTransactionByDate", "No transactions found for date: $dateString")
        }

        cursor.close()
        db.close()

        return transactions
    }
    public fun getTotalTransactionByDate(date: Date): Double {
        var sum = 0.0
        val db = this.readableDatabase

        val formatDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val dateString = formatDate.format(date)

        val query = """
            SELECT t.amount, catInOut.idInOut
            FROM TransactionTable AS t
            JOIN CatInOut catInOut ON t.idCatInOut = catInOut.id
            WHERE t.date = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(dateString))

        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val idInOut = cursor.getInt(cursor.getColumnIndexOrThrow("idInOut"))

                if (idInOut == 1) {
                    sum += amount
                } else {
                    sum -= amount
                }
            } while (cursor.moveToNext())
        } else {
            Log.e("getTotalTransactionByDate", "No transactions found for date: $dateString")
        }

        cursor.close()
        db.close()
        return sum
    }
    public fun getSumOfInByDate(date: Date): Double {
        var sum = 0.0
        val db = this.readableDatabase

        val formatDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val dateString = formatDate.format(date)

        val query = """
        SELECT t.amount, catInOut.idInOut
        FROM TransactionTable t
        JOIN CatInOut catInOut ON t.idCatInOut = catInOut.id
        WHERE t.date = ?
    """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(dateString))

        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val idInOut = cursor.getInt(cursor.getColumnIndexOrThrow("idInOut"))
                if (idInOut == 1) {
                    sum += amount
                }
            } while (cursor.moveToNext())
        } else {
            Log.e("getSumOfInByDate", "No transactions found for date: $dateString")
        }

        cursor.close()
        db.close()
        return sum
    }
    public fun getSumOfOutByDate(date: Date): Double {
        var sum = 0.0
        val db = this.readableDatabase

        val formatDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val dateString = formatDate.format(date)

        val query = """
        SELECT t.amount, catInOut.idInOut
        FROM TransactionTable t
        JOIN CatInOut catInOut ON t.idCatInOut = catInOut.id
        WHERE t.date = ?
    """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(dateString))

        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val idInOut = cursor.getInt(cursor.getColumnIndexOrThrow("idInOut"))
                if (idInOut == 0) {
                    sum += amount
                }
            } while (cursor.moveToNext())
        } else {
            Log.e("getSumOfOutByDate", "No transactions found for date: $dateString")
        }

        cursor.close()
        db.close()
        return sum
    }
    fun insertTransactionWithCategory(name: String, amount: Double, note: String, date: String, idInOut: Int): Long {
        val db = this.writableDatabase

        // Lấy idCat từ bảng Category dựa trên categoryName
        val query = """
        SELECT id, inOut 
        FROM ${CategoryTable.TABLE_NAME} 
        WHERE ${CategoryTable.COLUMN_NAME} = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(name))
        var idCat: Int? = null
        var inOut: Int? = null
        if (cursor.moveToFirst()) {
            idCat = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ID))
            inOut = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_INOUT))
        }
        cursor.close()

        // Nếu không tìm thấy idCat, trả về -1 để chỉ ra lỗi
        if (idCat == null) {
            Log.e("Insert Transaction", "Category not found: $name")
            return -1
        }

        // Lấy idCatInOut từ bảng CatInOut, nếu không tìm thấy thì thêm bản ghi mới
        var idCatInOut: Long? = null // Đổi kiểu từ Int? thành Long?
        val catInOutQuery = """
        SELECT id 
        FROM ${CatInOutTable.TABLE_NAME} 
        WHERE ${CatInOutTable.COLUMN_IDCAT} = ? AND ${CatInOutTable.COLUMN_IDINOUT} = ?
    """.trimIndent()

        val catInOutCursor = db.rawQuery(catInOutQuery, arrayOf(idCat.toString(), idInOut.toString()))

        if (catInOutCursor.moveToFirst()) {
            idCatInOut = catInOutCursor.getLong(catInOutCursor.getColumnIndexOrThrow(CatInOutTable.COLUMN_ID))
        }
        catInOutCursor.close()

        // Nếu không tìm thấy idCatInOut, thực hiện insert vào bảng CatInOut
        if (idCatInOut == null) {
            val valuesCatInOut = ContentValues().apply {
                put(CatInOutTable.COLUMN_IDCAT, idCat)
                put(CatInOutTable.COLUMN_IDINOUT, inOut)
            }
            idCatInOut = db.insert(CatInOutTable.TABLE_NAME, null, valuesCatInOut)
            if (idCatInOut == -1L) { // So sánh với -1L để kiểm tra lỗi
                Log.e("Insert Transaction", "Failed to insert into CatInOut")
                return -1
            }
        }

        // Thêm vào bảng Transaction
        val valuesTransaction = ContentValues().apply {
            put(TransactionTable.COLUMN_NAME, name)
            put(TransactionTable.COLUMN_AMOUNT, amount)
            put(TransactionTable.COLUMN_NOTE, note)
            put(TransactionTable.COLUMN_DATE, date)
            put(TransactionTable.COLUMN_IDCATINOUT, idCatInOut) // Sử dụng idCatInOut đã tìm thấy hoặc mới tạo
        }

        // Thực hiện insert giao dịch
        val transactionId = db.insert(TransactionTable.TABLE_NAME, null, valuesTransaction)
        db.close()
        return transactionId // Trả về ID của giao dịch vừa tạo
    }
    fun getTransactionDates(): List<Date> {
        val dates = mutableListOf<Date>()
        val db = this.readableDatabase

        val query = """
            SELECT DISTINCT ${TransactionTable.COLUMN_DATE}
            FROM ${TransactionTable.TABLE_NAME}
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        val formatDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        if (cursor.moveToFirst()) {
            do {
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE))
                val date = formatDate.parse(dateString)
                date?.let { dates.add(it) }
            } while (cursor.moveToNext())
        } else {
            Log.e("getTransactionDates", "No dates found in TransactionTable")
        }

        cursor.close()
        db.close()
        return dates
    }





}

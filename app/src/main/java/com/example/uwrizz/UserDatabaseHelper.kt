import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(USER_TABLE_CREATE)


        val contentValues = ContentValues().apply {
            put(COLUMN_USER_NAME, "admin")
            put(COLUMN_USER_PASSWORD, "password") // Consider hashing the password
        }
        // Use insertWithOnConflict with CONFLICT_IGNORE to avoid inserting if the user already exists
        db.insertWithOnConflict(USER_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $USER_TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "UserDatabase.db"
        const val USER_TABLE_NAME = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_NAME = "username"
        const val COLUMN_USER_PASSWORD = "password"

        private const val USER_TABLE_CREATE = """
            CREATE TABLE $USER_TABLE_NAME (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_NAME TEXT,
                $COLUMN_USER_PASSWORD TEXT
            )
            """
    }

    fun addUser(username: String, password: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, username)
            put(COLUMN_USER_PASSWORD, password)
        }
        val newRowId = db.insert(USER_TABLE_NAME, null, values)

        if (newRowId == -1L) {
            Log.e("Database", "Failed to add user")
        } else {
            Log.d("Database", "User added with row ID: $newRowId")
        }
        db.close()
    }

    fun validateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val projection = arrayOf(COLUMN_USER_ID)
        val selection = "$COLUMN_USER_NAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(
            USER_TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

}

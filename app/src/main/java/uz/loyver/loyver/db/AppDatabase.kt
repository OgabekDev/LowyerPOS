package uz.loyver.loyver.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.model.Printer
import uz.loyver.loyver.model.Product

@Database(
    entities = [Product::class, Category::class, ChequeProduct::class, Printer::class],
    version = 7,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getProductDao(): ProductDao
    abstract fun getCategoryDao(): CategoryDao
    abstract fun getChequeDao(): ChequeDao
    abstract fun getPrinterDao(): PrinterDao

    companion object {

        private var DB_INSTANCE: AppDatabase? = null

        fun getAppDBInstance(context: Context): AppDatabase {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return DB_INSTANCE!!
        }

    }

}
package uz.loyver.loyver.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.loyver.loyver.model.Printer

@Dao
interface PrinterDao {

    @Query("SELECT * FROM printer")
    suspend fun getAllPrinters(): List<Printer>

    @Query("DELETE FROM printer WHERE name = :name")
    suspend fun deletePrinter(name: String)

    @Query("SELECT * FROM printer WHERE name = :name")
    suspend fun getPrinter(name: String): Printer

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createPrinter(printer: Printer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePrinter(printer: Printer)

    @Query("SELECT * FROM printer WHERE isMain = 1")
    suspend fun getMainPrinter(): Printer?

}
package uz.loyver.loyver.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.model.Product

@Dao
interface ChequeDao {

    @Query("SELECT * FROM cheque")
    suspend fun getAllCheques(): List<ChequeProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createCheque(chequeProduct: ChequeProduct)

    @Query("DELETE FROM cheque")
    suspend fun deleteCheques()

    @Query("DELETE FROM cheque WHERE id = :id")
    suspend fun deleteCheque(id: Int)

    @Query("SELECT * FROM cheque WHERE id = :id")
    suspend fun getCheque(id: Int): ChequeProduct

}
package uz.loyver.loyver.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Cheque(
    val id: Int,
    val user: Customer,
    val time: String,
    val total_summa: Float,
    val items: ArrayList<ChequeProduct>,
    val is_saved: Boolean = false,
    val cart_number: String,
    val create_date: String
): Serializable

@Entity(tableName = "cheque")
data class ChequeProduct (
    @PrimaryKey val id: Int,
    val name: String?,
    val price: Double,
    val type: String, // WEIGHT, PIECES
    val quantity: Double
): Serializable

data class ChequeCreate(
    val user: Int,
    val is_saved: Boolean,
    val products: ArrayList<ChequeProductCreate>
)

data class ChequeProductCreate(
    val product: Int,
    val quantity: Float
)

data class Cart(
    val id: Int,
    val user: Int,
    val cart_number: String,
    val create_date: String,
    val total_summa: Float,
    val time: String,
    val is_saved: Boolean
)
package uz.loyver.loyver.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String, // WEIGHT, EACH
    var category: String?,
    val price: Int
): Serializable

data class ProductCreateUpdate(
    val name: String,
    val type: String, // WEIGHT, EACH
    val price: Int,
    val category: String? = "",
    val categoryName: String?,
    val id: Int? = null
): Serializable

data class ProductUpdate(
    val name: String,
    val type: String, // WEIGHT, EACH
    val price: Int,
    val category: Int?,
    val categoryName: String?,
    val id: Int? = null
): Serializable

data class ChangeCategoryProducts(
    val category: Int,
    val products: ArrayList<Int>
)

data class CategoryProduct(
    val id: Int,
    val name: String,
    val category: String?,
    var isChecked: Boolean = false
)
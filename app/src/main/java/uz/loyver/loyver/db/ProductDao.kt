package uz.loyver.loyver.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.loyver.loyver.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY id DESC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id DESC")
    suspend fun getCategoryProducts(category: String): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteProducts()

    @Query("SELECT * FROM products WHERE name LIKE :name ORDER BY id DESC")
    suspend fun searchProduct(name: String): List<Product>

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Int)

}
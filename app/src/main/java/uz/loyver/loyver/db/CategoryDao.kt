package uz.loyver.loyver.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.loyver.loyver.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(category: Category)

    @Query("DELETE FROM categories")
    suspend fun deleteCategories()

    @Query("SELECT * FROM categories WHERE title LIKE :name ORDER BY id DESC")
    suspend fun searchCategory(name: String): List<Category>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Int)

}
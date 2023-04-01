package uz.loyver.loyver.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "categories")
data class Category (
    @PrimaryKey val id: Int,
    val title: String,
    var quantity: Int = 0
): Serializable

data class CategoryHttp(
    val title: String
)
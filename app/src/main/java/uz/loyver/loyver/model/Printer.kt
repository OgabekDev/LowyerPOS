package uz.loyver.loyver.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "printer")
data class Printer(
     @PrimaryKey val name: String,
    val isBluetooth: Boolean,
    var isMain: Boolean = false,
    val address: String
)

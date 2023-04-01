package uz.loyver.loyver.model

import java.io.Serializable

data class Customer(
    val id: Int,
    val name: String,
    val phone_number: String?,
    val comments: String?,
    val cart_count: Int = 0,
    val cart_created_at: String?
): Serializable

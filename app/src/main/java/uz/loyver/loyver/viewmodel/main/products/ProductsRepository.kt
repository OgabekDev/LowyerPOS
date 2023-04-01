package uz.loyver.loyver.viewmodel.main.products

import uz.loyver.loyver.db.ProductDao
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val productDao: ProductDao
) {

    suspend fun getProducts() = productDao.getAllProducts()

    suspend fun searchProducts(name: String) = productDao.searchProduct(name)

}
package uz.loyver.loyver.viewmodel.add_category

import uz.loyver.loyver.db.ProductDao
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class AddToCategoryRepository @Inject constructor(
    private val productDao: ProductDao,
    private val appService: AppService
) {

    suspend fun getAllProducts() = productDao.getAllProducts()

}
package uz.loyver.loyver.viewmodel.main.home

import uz.loyver.loyver.db.CategoryDao
import uz.loyver.loyver.db.ChequeDao
import uz.loyver.loyver.db.ProductDao
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.ChequeCreate
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val chequeDao: ChequeDao,
    private val appService: AppService
) {

    suspend fun getAllCategories() = categoryDao.getAllCategories()

    suspend fun getAllProducts() = productDao.getAllProducts()

    suspend fun getCategoryProducts(category: String) = productDao.getCategoryProducts(category)

    suspend fun searchProducts(name: String) = productDao.searchProduct(name)

    suspend fun clearCheque() = chequeDao.deleteCheques()

    suspend fun getAllCheque() = chequeDao.getAllCheques()

    suspend fun createCheque(chequeCreate: ChequeCreate) = appService.createCheque(chequeCreate)

}
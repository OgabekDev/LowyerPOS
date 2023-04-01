package uz.loyver.loyver.viewmodel.main

import uz.loyver.loyver.db.CategoryDao
import uz.loyver.loyver.db.ProductDao
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class SplashRepository @Inject constructor(
    private val appService: AppService,
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao
){

    suspend fun getProducts() = appService.getProducts()

    suspend fun getCategories() = appService.getCategories()

    suspend fun saveProduct(product: Product) = productDao.addProduct(product)

    suspend fun saveCategory(category: Category) = categoryDao.addCategory(category)

    suspend fun deleteProducts() = productDao.deleteProducts()

    suspend fun deleteCategories() = categoryDao.deleteCategories()

}
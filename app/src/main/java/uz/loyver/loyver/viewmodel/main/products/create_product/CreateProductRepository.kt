package uz.loyver.loyver.viewmodel.main.products.create_product

import uz.loyver.loyver.db.CategoryDao
import uz.loyver.loyver.db.ProductDao
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.model.ProductCreateUpdate
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class CreateProductRepository @Inject constructor(
    private val appService: AppService,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {

    suspend fun createProduct(product: ProductCreateUpdate) = appService.createProduct(product)

    suspend fun getCategories() = categoryDao.getAllCategories()

    suspend fun saveProduct(product: Product) = productDao.addProduct(product)

}
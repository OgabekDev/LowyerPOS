package uz.loyver.loyver.viewmodel.main.products.update_product

import uz.loyver.loyver.db.CategoryDao
import uz.loyver.loyver.db.ProductDao
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.model.ProductCreateUpdate
import uz.loyver.loyver.model.ProductUpdate
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class UpdateProductRepository @Inject constructor(
    private val appService: AppService,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {

    suspend fun getCategories() = categoryDao.getAllCategories()

    suspend fun getProduct(id: Int) = appService.getProduct(id)

    suspend fun updateProduct(id: Int, product: ProductCreateUpdate) = appService.updateProduct(id, product)

    suspend fun updateProduct(id: Int, product: ProductUpdate) = appService.updateProduct(id, product)

    suspend fun deleteProduct(id: Int) = appService.deleteProduct(id)

    suspend fun updateProductDB(product: Product) = productDao.addProduct(product)

    suspend fun deleteProductDB(id: Int) = productDao.deleteProduct(id)

}
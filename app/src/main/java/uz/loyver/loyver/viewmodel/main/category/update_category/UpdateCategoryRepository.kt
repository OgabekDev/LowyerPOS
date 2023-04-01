package uz.loyver.loyver.viewmodel.main.category.update_category

import uz.loyver.loyver.db.CategoryDao
import uz.loyver.loyver.db.ProductDao
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.CategoryHttp
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class UpdateCategoryRepository @Inject constructor(
    private val appService: AppService,
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao
) {

    suspend fun getCategory(id: Int) = appService.getCategory(id)

    suspend fun updateCategory(id: Int, category: CategoryHttp) = appService.updateCategory(id, category)

    suspend fun deleteCategory(id: Int) = appService.deleteCategory(id)

    suspend fun updateCategoryDB(category: Category) = categoryDao.addCategory(category)

    suspend fun deleteCategoryDB(id: Int) = categoryDao.deleteCategory(id)

    suspend fun getCategoryProducts(category: String) = productDao.getCategoryProducts(category)

    suspend fun updateProduct(product: Product) = productDao.addProduct(product)

}
package uz.loyver.loyver.viewmodel.main.category

import uz.loyver.loyver.db.CategoryDao
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val appService: AppService
) {

    suspend fun getAllCategories() = appService.getCategories()

    suspend fun searchCategory(name: String) = categoryDao.searchCategory(name)

    suspend fun deleteCategories() = categoryDao.deleteCategories()

    suspend fun addCategory(category: Category) = categoryDao.addCategory(category)

}
package uz.loyver.loyver.viewmodel.main.category.create_category

import uz.loyver.loyver.db.CategoryDao
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.CategoryHttp
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class CreateCategoryRepository @Inject constructor(
    private val appService: AppService,
    private val categoryDao: CategoryDao
) {

    suspend fun createCategory(category: CategoryHttp) = appService.createCategory(category)

    suspend fun saveCategory(category: Category) = categoryDao.addCategory(category)

}
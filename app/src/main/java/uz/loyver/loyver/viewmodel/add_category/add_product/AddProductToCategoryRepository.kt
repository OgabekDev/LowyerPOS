package uz.loyver.loyver.viewmodel.add_category.add_product

import uz.loyver.loyver.db.ProductDao
import uz.loyver.loyver.model.ChangeCategoryProducts
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class AddProductToCategoryRepository @Inject constructor(
    private val appService: AppService,
    private val productDao: ProductDao
) {

    suspend fun getProducts() = productDao.getAllProducts()

    suspend fun changeCategory(changeCategoryProducts: ChangeCategoryProducts) = appService.changeProductCategories(changeCategoryProducts)

    suspend fun saveProduct(product: Product) = productDao.addProduct(product)

    suspend fun searchProduct(name: String) = productDao.searchProduct(name)

}
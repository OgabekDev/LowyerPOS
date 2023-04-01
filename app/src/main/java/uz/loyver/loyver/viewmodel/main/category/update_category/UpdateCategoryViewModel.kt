package uz.loyver.loyver.viewmodel.main.category.update_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.CategoryHttp
import uz.loyver.loyver.model.DeleteMessage
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class UpdateCategoryViewModel @Inject constructor(
    private val repository: UpdateCategoryRepository
): ViewModel() {

    private val _getCategory = MutableStateFlow<UiStateObject<Category>>(UiStateObject.EMPTY)
    val getCategory = _getCategory

    fun getCategory(id: Int) = viewModelScope.launch {
        _getCategory.value = UiStateObject.LOADING
        try {
            val response = repository.getCategory(id)
            if (response.isSuccessful) {
                _getCategory.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _getCategory.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _getCategory.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _updateCategory = MutableStateFlow<UiStateObject<Category>>(UiStateObject.EMPTY)
    val updateCategory = _updateCategory

    fun updateCategory(id: Int, category: CategoryHttp) = viewModelScope.launch {
        _updateCategory.value = UiStateObject.LOADING
        try {
            val response = repository.updateCategory(id, category)
            if (response.isSuccessful) {
                _updateCategory.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _updateCategory.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _updateCategory.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _deleteCategory = MutableStateFlow<UiStateObject<DeleteMessage>>(UiStateObject.EMPTY)
    val deleteCategory = _deleteCategory

    fun deleteCategory(id: Int) = viewModelScope.launch {
        _deleteCategory.value = UiStateObject.LOADING
        try {
            val response = repository.deleteCategory(id)
            if (response.isSuccessful) {
                _deleteCategory.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _deleteCategory.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _deleteCategory.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _updateCategoryDB = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val updateCategoryDB = _updateCategoryDB

    fun updateCategoryDB(category: Category) = viewModelScope.launch {
        _updateCategoryDB.value = UiStateObject.LOADING
        try {
            val response = repository.updateCategoryDB(category)
            _updateCategoryDB.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _updateCategoryDB.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _deleteCategoryDB = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val deleteCategoryDB = _deleteCategoryDB

    fun deleteCategoryDB(id: Int) = viewModelScope.launch {
        _deleteCategoryDB.value = UiStateObject.LOADING
        try {
            val response = repository.deleteCategoryDB(id)
            _deleteCategoryDB.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _deleteCategoryDB.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _getCategoryProducts = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val getCategoryProducts = _getCategoryProducts

    fun getCategoryProducts(category: String) = viewModelScope.launch {
        _getCategoryProducts.value = UiStateList.LOADING
        try {
            val response = repository.getCategoryProducts(category)
            _getCategoryProducts.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _getCategoryProducts.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _updateProductDB = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val updateProductDB = _updateProductDB

    fun updateProductDB(product: Product) = viewModelScope.launch {
        _updateProductDB.value = UiStateObject.LOADING
        try {
            val response = repository.updateProduct(product)
            _updateProductDB.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _updateProductDB.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}
package uz.loyver.loyver.viewmodel.main.category.create_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.CategoryHttp
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val repository: CreateCategoryRepository
): ViewModel() {

    private val _create = MutableStateFlow<UiStateObject<Category>>(UiStateObject.EMPTY)
    val create = _create

    fun createProduct(category: CategoryHttp) = viewModelScope.launch {
        _create.value = UiStateObject.LOADING
        try {
            val response = repository.createCategory(category)
            if (response.isSuccessful) {
                _create.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _create.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _create.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _category = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val save = _category

    fun saveProduct(category: Category) = viewModelScope.launch {
        _category.value = UiStateObject.LOADING
        try {
            val response = repository.saveCategory(category)
            _category.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _category.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}
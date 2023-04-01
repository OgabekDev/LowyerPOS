package uz.loyver.loyver.viewmodel.main.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
): ViewModel() {

    private val _categories = MutableStateFlow<UiStateList<Category>>(UiStateList.EMPTY)
    val categories = _categories

    fun getCategories() = viewModelScope.launch {
        _categories.value = UiStateList.LOADING
        try {
            val response = repository.getAllCategories()
            if (response.isSuccessful) {
                _categories.value = UiStateList.SUCCESS(response.body()!!)
            } else {
                _categories.value = UiStateList.ERROR(response.message())
            }
        } catch (e: Exception) {
            _categories.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _search = MutableStateFlow<UiStateList<Category>>(UiStateList.EMPTY)
    val search = _search

    fun searchCategories(name: String) = viewModelScope.launch {
        _search.value = UiStateList.LOADING
        try {
            val response = repository.searchCategory(name)
            _search.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _search.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _delete = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val delete = _delete

    fun deleteCategories() = viewModelScope.launch {
        _delete.value = UiStateObject.LOADING
        try {
            val response = repository.deleteCategories()
            _delete.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _delete.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _add = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val add = _add

    fun addCategory(category: Category) = viewModelScope.launch {
        _add.value = UiStateObject.LOADING
        try {
            val response = repository.addCategory(category)
            _add.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _add.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}
package uz.loyver.loyver.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import uz.loyver.loyver.model.*

interface AppService {

    @GET("api/v1/admin/products/list/")
    suspend fun getProducts(): Response<ArrayList<Product>>

    @GET("api/v1/admin/products/detail/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductUpdate>

    @POST("api/v1/admin/products/create/")
    suspend fun createProduct(@Body product: ProductCreateUpdate): Response<ProductCreateUpdate>

    @PUT("api/v1/admin/products/update/{id}/")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: ProductCreateUpdate): Response<ProductUpdate>

    @PUT("api/v1/admin/products/update/{id}/")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: ProductUpdate): Response<ProductUpdate>

    @DELETE("api/v1/admin/products/delete/{id}/")
    suspend fun deleteProduct(@Path("id") id: Int): Response<DeleteMessage>

    @GET("api/v1/admin/products/category/list/")
    suspend fun getCategories(): Response<ArrayList<Category>>

    @POST("api/v1/admin/products/category/create/")
    suspend fun createCategory(@Body category: CategoryHttp): Response<Category>

    @DELETE("api/v1/admin/products/category/delete/{id}/")
    suspend fun deleteCategory(@Path("id") id: Int): Response<DeleteMessage>

    @PUT("api/v1/admin/products/category/update/{id}/")
    suspend fun updateCategory(@Path("id") id: Int, @Body category: CategoryHttp): Response<Category>

    @GET("api/v1/admin/products/category/detail/{id}")
    suspend fun getCategory(@Path("id") id: Int): Response<Category>

    @GET("api/v1/admin/customers/list/")
    suspend fun getCustomers(): Response<ArrayList<Customer>>

    @GET("api/v1/admin/customers/list/")
    suspend fun searchCustomers(@Query("name") name: String): Response<ArrayList<Customer>>

    @POST("api/v1/admin/customers/create/")
    suspend fun createCustomer(@Body customer: Customer): Response<Customer>

    @GET("api/v1/admin/customers/detail/{id}/")
    suspend fun getCustomer(@Path("id") id: Int): Response<Customer>

    @PUT("api/v1/admin/customers/update/{id}/")
    suspend fun updateCustomer(@Path("id") id: Int, @Body customer: Customer): Response<Customer>

    @POST("api/v1/admin/customers/cart/create/")
    suspend fun createCheque(@Body chequeCreate: ChequeCreate): Response<Cheque>

    @PUT("api/v1/admin/customers/cart/update/{id}/")
    suspend fun updateCheque(@Path("id") id: Int, @Body message: DeleteMessage): Response<DeleteMessage>

    @GET("api/v1/admin/customers/cart/list")
    suspend fun getAllCheques(@Query("created_at_after") after: String, @Query("created_at_before") before: String): Response<ArrayList<Cart>>

    @GET("api/v1/admin/customers/cart/list")
    suspend fun searchCheques(@Query("cart_number") cardNumber: String): Response<ArrayList<Cart>>

    @GET("api/v1/admin/customers/cart/detail/{id}/")
    suspend fun getCheque(@Path("id") id: Int): Response<Cheque>

    @GET("api/v1/admin/customers/cart/list")
    suspend fun getCustomerCheque(@Query("user") user: Int, @Query("created_at_after") after: String, @Query("created_at_before") before: String): Response<ArrayList<Cart>>

    @POST("api/v1/admin/products/category-product/update/")
    suspend fun changeProductCategories(@Body changeCategoryProducts: ChangeCategoryProducts): Response<ChangeCategoryProducts>

}
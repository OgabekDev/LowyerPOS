package uz.loyver.loyver.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.loyver.loyver.db.AppDatabase
import uz.loyver.loyver.network.AppService
import uz.loyver.loyver.network.Server
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

class AppModule {

    @Provides
    @Singleton
    fun getServer() = if (Server.IS_TESTER) Server.SERVER_DEVELOPMENT else Server.SERVER_PRODUCTION

    @Provides
    @Singleton
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(getServer())
            .addConverterFactory(getNullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun getNullOnEmptyConverterFactory() = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ) = object : Converter<ResponseBody, Any?> {
            val converter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

            override fun convert(value: ResponseBody) =
                if (value.contentLength() != 0L) converter.convert(value) else null
        }
    }

    @Provides
    @Singleton
    fun apiService(): AppService = getRetrofit().create(AppService::class.java)

    @Provides
    @Singleton
    fun appDatabase(context: Application) = AppDatabase.getAppDBInstance(context)

    @Provides
    @Singleton
    fun productDao(appDatabase: AppDatabase) = appDatabase.getProductDao()

    @Provides
    @Singleton
    fun categoryDao(appDatabase: AppDatabase) = appDatabase.getCategoryDao()

    @Provides
    @Singleton
    fun chequeDao(appDatabase: AppDatabase) = appDatabase.getChequeDao()

    @Provides
    @Singleton
    fun printerDao(appDatabase: AppDatabase) = appDatabase.getPrinterDao()

}
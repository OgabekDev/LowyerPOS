package uz.loyver.loyver.viewmodel.main

import uz.loyver.loyver.db.PrinterDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val printerDao: PrinterDao
){

    suspend fun getMainPrinter() = printerDao.getMainPrinter()

}
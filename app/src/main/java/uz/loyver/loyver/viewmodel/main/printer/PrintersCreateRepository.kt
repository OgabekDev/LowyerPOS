package uz.loyver.loyver.viewmodel.main.printer

import uz.loyver.loyver.db.PrinterDao
import uz.loyver.loyver.model.Printer
import javax.inject.Inject

class PrintersCreateRepository @Inject constructor(
    private val printerDao: PrinterDao
) {

    suspend fun createPrinter(printer: Printer) = printerDao.createPrinter(printer)

    suspend fun updatePrinter(printer: Printer) = printerDao.updatePrinter(printer)

    suspend fun getPrinter(name: String) = printerDao.getPrinter(name)

    suspend fun deletePrinter(name: String) = printerDao.deletePrinter(name)

}
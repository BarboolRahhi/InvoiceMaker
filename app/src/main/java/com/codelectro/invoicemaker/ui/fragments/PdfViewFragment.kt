package com.codelectro.invoicemaker.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.UserAndItem
import com.codelectro.invoicemaker.ui.MainActivity
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.ui.dateTimeFormat
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pdf_view.*
import timber.log.Timber
import java.io.*

@AndroidEntryPoint
class PdfViewFragment : Fragment(R.layout.fragment_pdf_view) {

    private val DEST = "/sdcard/test1.pdf"

    companion object {
        const val CREATE_FILE = 1
    }

    private val args: PdfViewFragmentArgs by navArgs()

    private val viewmodel: MainViewModel by viewModels()

    private lateinit var userAndItem: UserAndItem
    private lateinit var fileName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setDrawerEnabled(true)
        userAndItem = args.userAnditem
        fileName = "${userAndItem.user?.customerName}-INV-${String.format("%03d", userAndItem.item.invoiceNumber)}.pdf"

        createPdf()

        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save -> {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_TITLE, fileName)
                    }
                    startActivityForResult(intent, ProfileFragment.CREATE_FILE)
                    true
                }
                else -> false
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ProfileFragment.CREATE_FILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                val outputStream = requireContext().contentResolver.openOutputStream(uri)
                val file =
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath + "/$fileName/"
                val filePath = File(file)
                val input: InputStream = FileInputStream(filePath)
                copyFile(input, outputStream!!)
                input.close()
                outputStream.flush()
                outputStream.close()
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
        }
    }

    private fun showPdf() {
        val file =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath + "/$fileName/"
        val filePath = File(file)
        pdfView.fromFile(filePath)
            .load()
    }


    private fun createPdf() {
        val file =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath + "/$fileName/"
        val filePath = File(file)

        val pdfDoc = PdfDocument(PdfWriter(filePath))
        val doc = Document(pdfDoc)

        addHeader(doc)
    }

    private fun addHeader(doc: Document) {

        val res: Resources = resources
        val drawable: Drawable = res.getDrawable(R.drawable.logo)
        val bitmap: Bitmap = (drawable as BitmapDrawable).toBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bitMapData: ByteArray = stream.toByteArray()

        val logo = ImageDataFactory.create(bitMapData)
        val image = Image(logo).apply {
            setWidth(124F)
        }

        val companyInfo = Paragraph()
            .add(
                Text("BARBOOL SHOP\n")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(16f)
            )
            .add("9419293427\n")
            .add("rahhi60@gmail.com\n")

        val companyAddress = Paragraph()
            .add(
                Text("ADDRESS\n")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(16f)
            )
            .add("Khara Madana, Samba\n")


        val header: Table = Table(UnitValue.createPercentArray(3))
            .useAllAvailableWidth().apply {
                addCell(Cell().add(image).setBorder(Border.NO_BORDER))
                addCell(getCell(companyAddress, TextAlignment.CENTER))
                addCell(getCell(companyInfo, TextAlignment.RIGHT))
            }

        doc.add(header)
        doc.add(addLineSeparator().setMarginTop(10f))

        val customerInfo = Paragraph()
            .add(
                Text("BILLED TO\n")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(16f)
            )
            .add("${userAndItem.user?.customerName?.capitalize()}\n")
            .add("${userAndItem.user?.address?.capitalize()}\n")
            .add("${userAndItem.user?.phoneNo ?: ""}")

        val invoiceInfo = Paragraph()
            .add(
                Text("INVOICE\n")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(26f)
            )
            .add("Invoice Number: INV-${String.format("%03d", userAndItem.item.invoiceNumber)}\n")
            .add("Issued Date: ${userAndItem.item.date.dateTimeFormat()}")

        val infoHeader: Table = Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()

        infoHeader.apply {
            addCell(getCell(customerInfo, TextAlignment.LEFT))
            addCell(getCell(invoiceInfo, TextAlignment.RIGHT))
            setMarginTop(10f)
        }

        doc.add(infoHeader)
        addTableHeader(doc)

    }


    private fun addTableHeader(doc: Document) {
        val table: Table = Table(UnitValue.createPercentArray(7)).useAllAvailableWidth()
        table.setMarginTop(16f)

        val sn = Cell().add(Paragraph("S/N")).apply {
            setBackgroundColor(ColorConstants.ORANGE)
        }
        table.addCell(sn)

        val name = Cell().add(Paragraph("Name")).apply {
            setBackgroundColor(ColorConstants.ORANGE)
        }
        table.addCell(name)

        val quantity = Cell().add(Paragraph("Quantity")).apply {
            setBackgroundColor(ColorConstants.ORANGE)
        }
        table.addCell(quantity)

        val unit = Cell().add(Paragraph("Price/Unit")).apply {
            setBackgroundColor(ColorConstants.ORANGE)
        }
        table.addCell(unit)

        val subTotal = Cell().add(Paragraph("Sub Total")).apply {
            setBackgroundColor(ColorConstants.ORANGE)
        }
        table.addCell(subTotal)

        val discount = Cell().add(Paragraph("Discount")).apply {
            setBackgroundColor(ColorConstants.ORANGE)
        }
        table.addCell(discount)

        val total = Cell().add(Paragraph("Total")).apply {
            setBackgroundColor(ColorConstants.ORANGE)
        }
        table.addCell(total)

        viewmodel.getItemAndLineItems(userAndItem.item.id!!).observe(viewLifecycleOwner, Observer {
            Timber.d("Table: $it")
            it.lineItems.forEachIndexed { index, lineItem ->
                val sNum = Cell().add(Paragraph("${index + 1}"))
                    .setTextAlignment(TextAlignment.CENTER)
                table.addCell(sNum)

                val nameCell = Cell().add(Paragraph(lineItem.name.capitalize()))
                table.addCell(nameCell)

                val quantityCell = Cell().add(Paragraph(lineItem.quantity.toString()))
                    .setTextAlignment(TextAlignment.CENTER)
                table.addCell(quantityCell)

                val unit = lineItem.unit.split("-")[1]
                val pricePerUnit = lineItem.subTotal / lineItem.quantity
                val unitCell = Cell().add(Paragraph("$pricePerUnit/$unit"))
                    .setTextAlignment(TextAlignment.CENTER)
                table.addCell(unitCell)

                val subTotalCell = Cell().add(Paragraph("${lineItem.subTotal}"))
                    .setTextAlignment(TextAlignment.RIGHT)
                table.addCell(subTotalCell)

                val discountCell =
                    Cell().add(Paragraph("${(lineItem.discount / lineItem.subTotal) * 100}%"))
                        .setTextAlignment(TextAlignment.RIGHT)
                table.addCell(discountCell)

                val totalCell = Cell().add(Paragraph("${lineItem.total}"))
                    .setTextAlignment(TextAlignment.RIGHT)
                table.addCell(totalCell)
            }
            doc.add(table)

            val tableFinal: Table = Table(UnitValue.createPercentArray(6)).useAllAvailableWidth()
            tableFinal.setMarginTop(10f)

            // sub total amount
            val emptyCell = Cell(1, 3).add(Paragraph(""))
                .setBorder(Border.NO_BORDER)
            tableFinal.addCell(emptyCell)

            val subTotalTextFinal = Cell(1, 2).add(Paragraph("SubTotal Amount"))
            tableFinal.addCell(subTotalTextFinal)

            val subTotalFinal = Cell(1, 1).add(Paragraph("Rs.${it.item.subTotal}"))
                .setTextAlignment(TextAlignment.RIGHT)
            tableFinal.addCell(subTotalFinal)

            // Discount amount
            val emptyCell2 = Cell(1, 3).add(Paragraph(""))
                .setBorder(Border.NO_BORDER)
            tableFinal.addCell(emptyCell2)

            val discountTextFinal = Cell(1, 2).add(Paragraph("Discount Amount"))
            tableFinal.addCell(discountTextFinal)

            val discountFinal = Cell(1, 1).add(Paragraph("Rs.${it.item.discount}"))
                .setTextAlignment(TextAlignment.RIGHT)
            tableFinal.addCell(discountFinal)

            // Total amount
            val emptyCell3 = Cell(1, 3).add(Paragraph(""))
                .setBorder(Border.NO_BORDER)
            tableFinal.addCell(emptyCell3)

            val totalTextFinal = Cell(1, 2).add(Paragraph("Total Amount"))
            tableFinal.addCell(totalTextFinal)

            val totalFinal = Cell(1, 1).add(Paragraph("Rs.${it.item.total}"))
                .setTextAlignment(TextAlignment.RIGHT)
            tableFinal.addCell(totalFinal)

            doc.add(tableFinal)

            doc.close()

            showPdf()
        })

    }

    private fun getCell(
        paragraph: Paragraph,
        alignment: TextAlignment?
    ): Cell? {
        val cell = Cell().add(paragraph)
        cell.setPadding(0f)
        cell.setTextAlignment(alignment)
        cell.setBorder(Border.NO_BORDER)
        return cell
    }

    private fun addLineSeparator(): LineSeparator {
        val line = SolidLine(1f)
        line.color = ColorConstants.GRAY
        return LineSeparator(line)
    }

}
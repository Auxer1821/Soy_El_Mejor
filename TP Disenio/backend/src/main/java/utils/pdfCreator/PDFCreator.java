package utils.pdfCreator;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.IOException;

public class PDFCreator {
    public void editarPDF(String string, Document document, PdfDocument pdfDoc, int cantidadPaginas) throws IOException {

        PdfFont font = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

        // Crear un separador de línea con color
        SolidLine line = new SolidLine();
        line.setLineWidth(1f);
        line.setColor(new DeviceRgb(0, 0, 0)); // Color rojo
        LineSeparator separator = new LineSeparator(line);
        separator.setMarginLeft(-36);
        separator.setMarginRight(-36);

        // Añadir el separador al documento
        document.add(separator);

        // Añadir un espacio después del separador
        document.add(new Paragraph("\n"));

        // Añadir más contenido después del separador
        document.add(new Paragraph(string));

        // Añadir número de página en el pie de página
        PdfPage currentPage = pdfDoc.getLastPage();
        float x = (pdfDoc.getDefaultPageSize().getWidth() - document.getRightMargin() - document.getLeftMargin()) / 2 + document.getLeftMargin();
        float y = document.getBottomMargin() / 2;
        document.showTextAligned(new Paragraph(String.format("%d", pdfDoc.getPageNumber(currentPage))).setFont(font).setFontSize(12), x, y, pdfDoc.getPageNumber(currentPage), TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
    }
}




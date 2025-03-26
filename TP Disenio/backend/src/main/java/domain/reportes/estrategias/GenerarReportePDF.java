package domain.reportes.estrategias;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import config.ServiceLocator;
import domain.heladera.Heladera;
import domain.reportes.ReporteGeneral;
import domain.reportes.ReporteHeladera;
import repositorios.RepoGenerico;
import repositorios.heladera.RepoHeladera;
import utils.pdfCreator.PDFCreator;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GenerarReportePDF {
    PDFCreator pdfCreator = new PDFCreator();

    public ReporteGeneral generarReportePDF(List<ReporteHeladera> reporteHeladeras) throws FileNotFoundException{
        String filename = LocalDate.now() + "reporteSemanal.pdf";
        PdfWriter writer = new PdfWriter("backend/src/main/resources/public/reportesSemanales/" + filename);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(36, 36, 36, 36);

        reporteHeladeras.forEach(reporteHeladera -> {
            try {
                StringBuilder reporteString = new StringBuilder();
                StringBuilder viandasXColaborador = new StringBuilder();

                CantidadDeViandasXColaboradorAString(reporteHeladera, viandasXColaborador);

                reporteString.append("Heladera: ").append(reporteHeladera.getHeladera().getNombre()).append("\t").append("(").append(reporteHeladera.getHeladera().getEstado().toString()).append(")").append("\n");
                reporteString.append("Dirección: ").append(reporteHeladera.getHeladera().getDireccion().getDireccion()).append("\n\n");
                reporteString.append("Cantidad de Fallas: ").append(reporteHeladera.getReporteFallas().getCantidadIncidentes()).append("\n");
                reporteString.append("Cantidad de viandas entrantes: ").append(reporteHeladera.getReporteDonaciones().getCantidadEntradas()).append("\n");
                reporteString.append("Cantidad de viandas salientes: ").append(reporteHeladera.getReporteDonaciones().getCantidadSalidas()).append("\n");
                reporteString.append("Cantidad de viandas por colaborador: ").append("\n").append(viandasXColaborador).append("\n");

                pdfCreator.editarPDF(
                        String.valueOf(reporteString),
                        document,
                        pdfDoc,
                        reporteHeladeras.size()
                );

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        document.close();

        return new ReporteGeneral("/reportesSemanales/" + filename, LocalDate.now());
    }

    public void CantidadDeViandasXColaboradorAString(ReporteHeladera reporteHeladera, StringBuilder stringBuilder){
        if(!reporteHeladera.getReporteViandasPorColaborador().getCantidadViandasPorColaboradorLista().isEmpty()){
            reporteHeladera.getReporteViandasPorColaborador().getCantidadViandasPorColaboradorLista().forEach(colaborador -> stringBuilder.append("ID: ").append(colaborador.getId()).append(" - Nombre: ").append(colaborador.getNombreColaborador()).append(" - Cantidad: ").append(colaborador.getCantidadDeViandas()).append("\n"));
        }else{
            stringBuilder.append("- No hubo viandas nuevas esta semana.");
        }
    }
}

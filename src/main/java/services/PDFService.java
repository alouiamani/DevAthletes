package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import entities.Commande;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PDFService {
    
    public void generateCommandePDF(List<Commande> commandes, String filePath) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Add header
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Liste des Commandes", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Add date
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Paragraph date = new Paragraph("Généré le : " + dateFormat.format(new java.util.Date()), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);
        
        // Create table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        float[] columnWidths = {1f, 2f, 2f, 2f};
        table.setWidths(columnWidths);
        
        // Add table headers
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        addTableHeader(table, headerFont, "ID", "Total", "Statut", "Date");
        
        // Add table rows
        Font rowFont = new Font(Font.FontFamily.HELVETICA, 12);
        float totalAmount = 0;
        
        for (Commande commande : commandes) {
            addTableRow(table, rowFont,
                String.valueOf(commande.getId_c()),
                String.format("%.2f dt", commande.getTotal_c()),
                commande.getStatut_c(),
                commande.getDateC().toString()
            );
            totalAmount += commande.getTotal_c();
        }
        
        document.add(table);
        
        // Add total
        Paragraph total = new Paragraph(
            String.format("Total des commandes: %.2f dt", totalAmount),
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)
        );
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(20);
        document.add(total);
        
        document.close();
    }
    
    private void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
    
    private void addTableRow(PdfPTable table, Font font, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
} 
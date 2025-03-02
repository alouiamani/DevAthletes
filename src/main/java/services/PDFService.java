package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import entities.Commande;
import entities.Produit;
import entities.User;
import utils.MyDatabase;
import utils.AuthToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PDFService {
    
    private String getUserName(int userId) {
        // Get the current logged in user
        User currentUser = AuthToken.getCurrentUser();
        if (currentUser != null) {
            // Return the current user's name in lowercase format
            return (currentUser.getNom() + " " + currentUser.getPrenom()).toLowerCase();
        }
        return "Unknown User";
    }
    
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
        
        // Create table with 4 columns instead of 5
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        // Update column widths (removed ID column)
        float[] columnWidths = {2f, 2f, 2f, 2f};
        table.setWidths(columnWidths);
        
        // Add table headers (removed ID)
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        addTableHeader(table, headerFont, "Client", "Total", "Statut", "Date");
        
        // Add table rows
        Font rowFont = new Font(Font.FontFamily.HELVETICA, 12);
        float totalAmount = 0;
        
        User currentUser = AuthToken.getCurrentUser();
        String clientName = currentUser != null ? 
            (currentUser.getNom() + " " + currentUser.getPrenom()).toLowerCase() : 
            "Unknown User";
        
        for (Commande commande : commandes) {
            addTableRow(table, rowFont,
                clientName,
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

    public void exportCommandeToPDF(Commande commande, String filePath) throws DocumentException {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add header with logo (if you have one)
            addHeader(document);
            
            // Add invoice title
            Paragraph title = new Paragraph("FACTURE", 
                new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);
            
            // Add order details
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(20);
            
            // Style for headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
            Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            
            // Add order information
            addTableRow(infoTable, "Numéro de Commande:", String.valueOf(commande.getId_c()), headerFont, valueFont);
            addTableRow(infoTable, "Date:", commande.getDateC(), headerFont, valueFont);
            addTableRow(infoTable, "Statut:", commande.getStatut_c(), headerFont, valueFont);
            addTableRow(infoTable, "Total:", String.format("%.2f DT", commande.getTotal_c()), headerFont, valueFont);
            
            document.add(infoTable);
            
            // Add footer
            addFooter(document);
            
            document.close();
        } catch (Exception e) {
            throw new DocumentException("Error creating PDF: " + e.getMessage());
        }
    }
    
    private void addHeader(Document document) throws DocumentException {
        try {
            // Add your company logo if you have one
            // Image logo = Image.getInstance("path/to/your/logo.png");
            // logo.scaleToFit(100, 100);
            // document.add(logo);
            
            Paragraph header = new Paragraph("Votre Entreprise", 
                new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            
            Paragraph contact = new Paragraph("123 Rue Example, Ville\nTél: +216 XX XXX XXX\nEmail: contact@example.com", 
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY));
            contact.setAlignment(Element.ALIGN_CENTER);
            contact.setSpacingAfter(30);
            document.add(contact);
        } catch (Exception e) {
            throw new DocumentException("Error adding header: " + e.getMessage());
        }
    }
    
    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("Merci de votre confiance!", 
            new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.DARK_GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
        
        Paragraph terms = new Paragraph(
            "Conditions: Cette facture est valable pendant 30 jours.\n" +
            "Pour toute question, veuillez nous contacter.", 
            new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY));
        terms.setAlignment(Element.ALIGN_CENTER);
        document.add(terms);
    }
    
    private void addTableRow(PdfPTable table, String label, Object value, Font labelFont, Font valueFont) {
        table.addCell(new Phrase(label, labelFont));
        
        String valueStr;
        if (value instanceof java.sql.Timestamp) {
            // Format the timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            valueStr = sdf.format(value);
        } else {
            valueStr = String.valueOf(value);
        }
        
        table.addCell(new Phrase(valueStr, valueFont));
    }
} 
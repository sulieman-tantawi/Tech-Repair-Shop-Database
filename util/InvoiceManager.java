package com.techfix.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvoiceManager {

    public void generatePDF(int jobId) {
        String folderPath = "TechFix_Invoices";
        java.io.File folder = new java.io.File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = folderPath + java.io.File.separator + "Invoice_Job_" + jobId + ".pdf";

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font mainTitleFont = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, new BaseColor(44, 62, 80)); 
            Paragraph title = new Paragraph("TECHFIX REPAIR SHOP", mainTitleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);
            Paragraph subTitle = new Paragraph("Main Branch - Nablus, Palestine\nPhone: +972-597-382964", subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line = new LineSeparator();
            line.setLineColor(new BaseColor(189, 195, 199));
            document.add(new Chunk(line));
            document.add(new Paragraph("\n"));

            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            
            String customerQuery = "SELECT c.FullName, d.Model, d.SerialNo, mj.TotalCost " +
                                 "FROM maintenance_job mj " +
                                 "JOIN device d ON mj.DeviceID = d.DeviceID " +
                                 "JOIN customer c ON d.CustomerID = c.CustomerID " +
                                 "WHERE mj.JobID = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(customerQuery)) {
                pstmt.setInt(1, jobId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    PdfPTable infoTable = new PdfPTable(2);
                    infoTable.setWidthPercentage(100);
                    infoTable.setWidths(new float[]{1f, 1f});
                    
                    PdfPCell leftCell = new PdfPCell();
                    leftCell.setBorder(Rectangle.NO_BORDER);
                    leftCell.addElement(new Paragraph("Customer: " + rs.getString("FullName"), boldFont));
                    leftCell.addElement(new Paragraph("Device: " + rs.getString("Model"), normalFont));
                    leftCell.addElement(new Paragraph("SN: " + rs.getString("SerialNo"), normalFont));
                    
                    PdfPCell rightCell = new PdfPCell();
                    rightCell.setBorder(Rectangle.NO_BORDER);
                    Paragraph datePara = new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont);
                    datePara.setAlignment(Element.ALIGN_RIGHT);
                    Paragraph invoicePara = new Paragraph("Invoice #: INV-" + jobId, boldFont);
                    invoicePara.setAlignment(Element.ALIGN_RIGHT);
                    Paragraph statusPara = new Paragraph("Status: PAID", boldFont);
                    statusPara.setAlignment(Element.ALIGN_RIGHT);
                    statusPara.getFont().setColor(new BaseColor(39, 174, 96)); 
                    
                    rightCell.addElement(datePara);
                    rightCell.addElement(invoicePara);
                    rightCell.addElement(statusPara);
                    
                    infoTable.addCell(leftCell);
                    infoTable.addCell(rightCell);
                    document.add(infoTable);
                }
            }
            
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(4); 
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 1.5f, 1.5f});
            table.setSpacingBefore(10f);

            String[] headers = {"Item / Service Description", "Qty", "Unit Price", "Subtotal"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
                cell.setBackgroundColor(new BaseColor(44, 62, 80)); 
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8f);
                cell.setBorderColor(BaseColor.WHITE);
                table.addCell(cell);
            }

            String itemsQuery = "SELECT i.Name, ji.Quantity, i.Price, (ji.Quantity * i.Price) as Subtotal " +
                              "FROM job_item ji JOIN item i ON ji.ItemID = i.ItemID " +
                              "WHERE ji.JobID = ?";
            
            double total = 0;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(itemsQuery)) {
                pstmt.setInt(1, jobId);
                ResultSet rs = pstmt.executeQuery();
                
                Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
                boolean alternateColor = false;
                
                while (rs.next()) {
                    PdfPCell c1 = new PdfPCell(new Phrase(rs.getString("Name"), cellFont));
                    PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(rs.getInt("Quantity")), cellFont));
                    PdfPCell c3 = new PdfPCell(new Phrase("$" + rs.getDouble("Price"), cellFont));
                    PdfPCell c4 = new PdfPCell(new Phrase("$" + rs.getDouble("Subtotal"), cellFont));
                    
                    PdfPCell[] cells = {c1, c2, c3, c4};
                    for (PdfPCell c : cells) {
                        c.setPadding(8f);
                        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        c.setBorderColor(new BaseColor(236, 240, 241)); 
                        if (c != c1) c.setHorizontalAlignment(Element.ALIGN_CENTER);
                        if (alternateColor) c.setBackgroundColor(new BaseColor(249, 250, 251)); 
                        table.addCell(c);
                    }
                    alternateColor = !alternateColor;
                    total += rs.getDouble("Subtotal");
                }
            }
            document.add(table);

            document.add(new Paragraph("\n"));
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{7.5f, 2.5f}); 
            
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            totalTable.addCell(emptyCell);
            
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL: $" + total, totalFont));
            totalCell.setBackgroundColor(new BaseColor(231, 76, 60));
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalCell.setPadding(10f);
            totalCell.setBorder(Rectangle.NO_BORDER);
            
            totalTable.addCell(totalCell);
            document.add(totalTable);

            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Thank you for your business!", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            java.awt.Desktop.getDesktop().open(new java.io.File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
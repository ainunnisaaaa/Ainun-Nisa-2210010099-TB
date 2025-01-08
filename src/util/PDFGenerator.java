package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JTable;
import java.text.SimpleDateFormat;

public class PDFGenerator {
    private static Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
    private static Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    private static Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    private static BaseColor headerColor = new BaseColor(102, 187, 106); // Pastel green
    private static BaseColor altRowColor = new BaseColor(232, 245, 233); // Light pastel green
    
    private static void addMetaData(Document document, String title) {
        document.addTitle(title);
        document.addAuthor("Sistem Informasi Kepegawaian");
        document.addCreator("Aplikasi Kepegawaian v1.0");
        document.addCreationDate();
    }
    
    private static void addHeader(Document document, String title) throws DocumentException {
        // Add logo or header image if needed
        // Image img = Image.getInstance("path/to/logo.png");
        // document.add(img);
        
        Paragraph header = new Paragraph("SISTEM INFORMASI KEPEGAWAIAN", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(10);
        document.add(header);
        
        Paragraph subTitle = new Paragraph(title, new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.GRAY));
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(20);
        document.add(subTitle);
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        Paragraph date = new Paragraph("Tanggal Cetak: " + sdf.format(new Date()), normalFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(30);
        document.add(date);
    }
    
    private static void addFooter(Document document, String reportType, int totalRecords) throws DocumentException {
        Paragraph line = new Paragraph("_____________________________________________________________________________");
        line.setAlignment(Element.ALIGN_CENTER);
        line.setSpacingBefore(20);
        document.add(line);
        
        Font footerTitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Paragraph footerTitle = new Paragraph("Informasi Laporan", footerTitleFont);
        footerTitle.setAlignment(Element.ALIGN_CENTER);
        footerTitle.setSpacingAfter(10);
        document.add(footerTitle);
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        String footer = String.format(
            "Jenis Laporan: %s\n" +
            "Total Data: %d record(s)\n" +
            "Waktu Cetak: %s\n" +
            "Dicetak oleh: Admin Sistem\n" +
            "Status: VALID\n" +
            "© %d Sistem Informasi Kepegawaian. All rights reserved.",
            reportType,
            totalRecords,
            sdf.format(new Date()),
            Calendar.getInstance().get(Calendar.YEAR)
        );
        
        Paragraph footerText = new Paragraph(footer, footerFont);
        footerText.setAlignment(Element.ALIGN_CENTER);
        document.add(footerText);
    }
    
    private static PdfPTable createTable(JTable table, int columnCount) throws DocumentException {
        PdfPTable pdfTable = new PdfPTable(columnCount);
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(10f);
        pdfTable.setSpacingAfter(10f);
        
        // Add headers
        for (int i = 0; i < columnCount; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(table.getColumnName(i), headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(headerColor);
            cell.setPadding(8f);
            pdfTable.addCell(cell);
        }
        
        // Add data
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < columnCount; j++) {
                PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(table.getValueAt(i, j)), normalFont));
                cell.setPadding(6f);
                if (i % 2 == 1) {
                    cell.setBackgroundColor(altRowColor);
                }
                pdfTable.addCell(cell);
            }
        }
        
        return pdfTable;
    }
    
    public static void generateKaryawanReport(JTable table, String path) throws Exception {
        if (table.getRowCount() == 0) {
            throw new Exception("Tidak ada data untuk dicetak");
        }
        
        Document document = new Document(PageSize.A4, 36, 36, 48, 36);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            
            // Add watermark
            PdfContentByte canvas = writer.getDirectContentUnder();
            Font watermarkFont = new Font(Font.FontFamily.HELVETICA, 60, Font.BOLD, new BaseColor(232, 245, 233));
            Phrase watermark = new Phrase("KEPEGAWAIAN", watermarkFont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 
                                     297, 421, 45); // coordinates and rotation
            
            addMetaData(document, "Laporan Data Karyawan");
            addHeader(document, "Laporan Data Karyawan");            
            document.add(createTable(table, 4));            
            addFooter(document, "Data Karyawan", table.getRowCount());
            
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
    
    public static void generateJabatanReport(JTable table, String path) throws Exception {
        if (table.getRowCount() == 0) {
            throw new Exception("Tidak ada data untuk dicetak");
        }
        
        Document document = new Document(PageSize.A4, 36, 36, 48, 36);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            
            // Add watermark
            PdfContentByte canvas = writer.getDirectContentUnder();
            Font watermarkFont = new Font(Font.FontFamily.HELVETICA, 60, Font.BOLD, new BaseColor(232, 245, 233));
            Phrase watermark = new Phrase("KEPEGAWAIAN", watermarkFont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 
                                     297, 421, 45); // coordinates and rotation
            
            addMetaData(document, "Laporan Data Jabatan");
            addHeader(document, "Laporan Data Jabatan");            
            document.add(createTable(table, 2));            
            addFooter(document, "Data Jabatan", table.getRowCount());
            
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
    
    public static void generateAbsensiReport(JTable table, String path) throws Exception {
        if (table.getRowCount() == 0) {
            throw new Exception("Tidak ada data untuk dicetak");
        }
        
        Document document = new Document(PageSize.A4, 36, 36, 48, 36);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            
            // Add watermark
            PdfContentByte canvas = writer.getDirectContentUnder();
            Font watermarkFont = new Font(Font.FontFamily.HELVETICA, 60, Font.BOLD, new BaseColor(232, 245, 233));
            Phrase watermark = new Phrase("KEPEGAWAIAN", watermarkFont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 
                                     297, 421, 45); // coordinates and rotation
            
            addMetaData(document, "Laporan Data Absensi");
            addHeader(document, "Laporan Data Absensi");            
            document.add(createTable(table, 3));            
            addFooter(document, "Data Absensi", table.getRowCount());
            
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
}

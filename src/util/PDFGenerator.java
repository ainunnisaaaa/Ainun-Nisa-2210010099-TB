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
    
    static class FooterEvent extends PdfPageEventHelper {
        private String reportType;
        private int totalRecords;
        
        public FooterEvent(String reportType, int totalRecords) {
            this.reportType = reportType;
            this.totalRecords = totalRecords;
        }
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfPTable footerTable = new PdfPTable(1);
                footerTable.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
                footerTable.setLockedWidth(true);
                
                // Add line
                PdfPCell lineCell = new PdfPCell(new Phrase("_____________________________________________________________________________"));
                lineCell.setBorder(Rectangle.NO_BORDER);
                lineCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                footerTable.addCell(lineCell);
                
                // Footer content
                Font footerTitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                PdfPCell titleCell = new PdfPCell(new Phrase("Informasi Laporan", footerTitleFont));
                titleCell.setBorder(Rectangle.NO_BORDER);
                titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell.setPaddingTop(10);
                footerTable.addCell(titleCell);
                
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
                
                PdfPCell footerCell = new PdfPCell(new Phrase(footer, footerFont));
                footerCell.setBorder(Rectangle.NO_BORDER);
                footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                footerTable.addCell(footerCell);
                
                // Position footer at bottom of page
                footerTable.writeSelectedRows(0, -1, 
                    document.leftMargin(),
                    document.bottomMargin() + 80,
                    writer.getDirectContent()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        
        Document document = new Document(PageSize.A4, 36, 36, 48, 120); // Increased bottom margin for footer
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            writer.setPageEvent(new FooterEvent("Data Karyawan", table.getRowCount()));
            document.open();
            
            addMetaData(document, "Laporan Data Karyawan");
            addHeader(document, "Laporan Data Karyawan");
            
            PdfPTable pdfTable = createTable(table, 4);
            pdfTable.setKeepTogether(true); // Prevents table from breaking across pages
            document.add(pdfTable);
            
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
        
        Document document = new Document(PageSize.A4, 36, 36, 48, 120); // Increased bottom margin for footer
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            writer.setPageEvent(new FooterEvent("Data Jabatan", table.getRowCount()));
            document.open();
            
            addMetaData(document, "Laporan Data Jabatan");
            addHeader(document, "Laporan Data Jabatan");
            
            PdfPTable pdfTable = createTable(table, 2);
            pdfTable.setKeepTogether(true); // Prevents table from breaking across pages
            document.add(pdfTable);
            
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
        
        Document document = new Document(PageSize.A4, 36, 36, 48, 120); // Increased bottom margin for footer
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            writer.setPageEvent(new FooterEvent("Data Absensi", table.getRowCount()));
            document.open();
            
            addMetaData(document, "Laporan Data Absensi");
            addHeader(document, "Laporan Data Absensi");
            
            PdfPTable pdfTable = createTable(table, 3);
            pdfTable.setKeepTogether(true); // Prevents table from breaking across pages
            document.add(pdfTable);
            
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
}

package com.shortlet.servlet;

import com.shortlet.model.Booking;
import com.shortlet.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExportExcelServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            List<Booking> bookings = bookingService.findAllWithUsers();
            Sheet sheet = workbook.createSheet("Bookings");
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(dataFormat.getFormat("\"NGN\" #,##0.00"));

            String[] headers = {
                    "Booking ID", "Name", "Email", "Password Hash", "Check-in", "Check-out", "Nights",
                    "Nightly Rate (NGN)", "Total (NGN)", "Payment Method", "Payment Status", "Apartment Title", "Status"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
                header.getCell(i).setCellStyle(headerStyle);
            }

            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(booking.getId());
                row.createCell(1).setCellValue(booking.getUserName());
                row.createCell(2).setCellValue(booking.getUserEmail());
                row.createCell(3).setCellValue(booking.getPasswordHash() == null ? "GOOGLE_ACCOUNT_NO_LOCAL_PASSWORD" : booking.getPasswordHash());
                row.createCell(4).setCellValue(booking.getCheckIn() == null ? "" : booking.getCheckIn().toString());
                row.createCell(5).setCellValue(booking.getCheckOut() == null ? "" : booking.getCheckOut().toString());
                row.createCell(6).setCellValue(booking.getNights());
                row.createCell(7).setCellValue(booking.getNightlyRate() == null ? 0 : booking.getNightlyRate().doubleValue());
                row.getCell(7).setCellStyle(moneyStyle);
                row.createCell(8).setCellValue(booking.getTotalAmount() == null ? 0 : booking.getTotalAmount().doubleValue());
                row.getCell(8).setCellStyle(moneyStyle);
                row.createCell(9).setCellValue(booking.getPaymentMethod() == null ? "" : booking.getPaymentMethod());
                row.createCell(10).setCellValue(booking.getPaymentStatus() == null ? "" : booking.getPaymentStatus());
                row.createCell(11).setCellValue(booking.getApartmentTitle() == null ? "No booking yet" : booking.getApartmentTitle());
                row.createCell(12).setCellValue(booking.getStatus());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"shortlet-bookings.xlsx\"");
            workbook.write(response.getOutputStream());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

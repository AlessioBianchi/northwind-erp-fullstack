package it.zerob.erp.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import it.zerob.erp.dao.CustomersDAO;
import it.zerob.erp.dao.OrderDetailsDAO;
import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.model.Customer;
import it.zerob.erp.model.Order;
import it.zerob.erp.model.OrderDetail;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportsService {

    private final OrdersDAO ordersDao;
    private final OrderDetailsDAO orderDetailsDao;
    private final CustomersDAO customersDAO;

    public ReportsService(OrdersDAO ordersDao, OrderDetailsDAO orderDetailsDao, CustomersDAO customersDAO) {
        this.ordersDao = ordersDao;
        this.orderDetailsDao = orderDetailsDao;
        this.customersDAO = customersDAO;
    }

    public byte[] getOrdersReport(Long orderId) {
        Order order = ordersDao.findById(orderId).orElseThrow();
        List<OrderDetail> details = orderDetailsDao.findAllByOrder(order);
        Customer customer = customersDAO.findById(order.getCustomer().getCustomerId()).orElseThrow();

        return createOrderPdf(order, details, customer);
    }

    private byte[] createOrderPdf(Order order, List<OrderDetail> details, Customer customer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        document.add(new Paragraph("RIEPILOGO ORDINE #" + order.getOrderId()));
        document.add(new Paragraph("Cliente: " + customer.getContactName()));

        document.add(new Paragraph("Data: " + order.getOrderDate()));
        document.add(new Chunk("\n"));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{4f, 2f, 1f, 1.5f, 2f});

        String[] headers = {"Product", "Unit Price", "Qty", "Discount", "Subtotal"};
        for (String columnTitle : headers) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            header.setBorderWidth(1);
            header.setPhrase(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(5);
            table.addCell(header);
        }

        double granTotal = 0;
        for (OrderDetail item : details) {
            table.addCell(item.getProduct().getProductName());
            table.addCell(String.format("%.2f €", item.getUnitPrice()));
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(String.format("%.0f%%", (item.getDiscount() != null ? item.getDiscount() : 0) * 100));

            double lineTotal = item.getUnitPrice() * item.getQuantity() * (1 - (item.getDiscount() != null ? item.getDiscount() : 0));
            table.addCell(String.format("%.2f €", lineTotal));

            granTotal += lineTotal;
        }

        Paragraph freightParagraph = new Paragraph("Freight: " + String.format("%.2f €", order.getFreight()));
        freightParagraph.setAlignment(Element.ALIGN_LEFT);
        document.add(freightParagraph);

        granTotal += order.getFreight();

        Paragraph totalPara = new Paragraph("\nTOTAL: " + String.format("%.2f €", granTotal), titleFont);
        totalPara.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalPara);

        document.add(table);

        document.close();

        return baos.toByteArray();
    }
}

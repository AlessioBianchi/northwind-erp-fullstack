package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.converter.OrderBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.OrderDetailsBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.OrderDetailsDtoToBeanConverter;
import com.github.alessiobianchi.erp.converter.OrderDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.*;
import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.exception.BadRequestException;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

@Service
public class OrdersService {

    private final EmployeesDAO employeesDao;
    private final OrdersDAO ordersDao;
    private final OrderDetailsDAO orderDetailsDao;
    private final ProductsDAO productsDao;

    public OrdersService(EmployeesDAO employeesDao,
                         OrdersDAO ordersDao,
                         OrderDetailsDAO orderDetailsDao,
                         ProductsDAO productsDao) {
        this.employeesDao = employeesDao;
        this.ordersDao = ordersDao;
        this.orderDetailsDao = orderDetailsDao;
        this.productsDao = productsDao;
    }

    public List<OrderDTO> findAllByOrderByOrderIdDesc() {
        return ordersDao.findAllByOrderByOrderIdDesc()
                .stream()
                .map(OrderBeanToDtoConverter::convert)
                .toList();
    }

    public Page<OrderDTO> findAllByOrderByOrderIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return ordersDao.findAllByOrderByOrderIdDesc(pageable)
                .map(OrderBeanToDtoConverter::convert);
    }

    public OrderDTO create(OrderDTO orderDto, String username) {
        Employee userLogged = employeesDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        Order orderToSave = new OrderBuilder(OrderDtoToBeanConverter.convert(orderDto))
                .withEmployee(userLogged)
                .build();

        return OrderBeanToDtoConverter.convert(ordersDao.save(orderToSave));
    }

    public OrderDTO update(int orderId, OrderDTO orderDto) {
        if (!ordersDao.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        Order orderToUpdate = new OrderBuilder(OrderDtoToBeanConverter.convert(orderDto))
                .withOrderId(orderId)
                .build();

        return OrderBeanToDtoConverter.convert(ordersDao.save(orderToUpdate));
    }

    public void delete(int orderId) {
        Order orderToDelete = ordersDao.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!orderDetailsDao.findAllByOrder(orderToDelete).isEmpty()) {
            throw new ConflictException("There are details on this order. It can't be deleted.");
        }

        ordersDao.delete(orderToDelete);
    }

    public List<OrderDetailsDTO> findAllOrderDetailsByOrderId(int orderId) {
        Order order = new OrderBuilder()
                .withOrderId(orderId)
                .build();
        return orderDetailsDao.findAllByOrder(order).stream()
                .map(OrderDetailsBeanToDtoConverter::convert)
                .toList();
    }

    public OrderDetailsDTO create(int orderId, OrderDetailsDTO orderDetailDto) {
        if (!Objects.equals(orderDetailDto.getOrderId(), orderId)) {
            throw new BadRequestException("orderId in body does not match the path.");
        }

        OrderDetail orderDetail = OrderDetailsDtoToBeanConverter.convert(orderDetailDto);
        Product product = productsDao.findById(orderDetail.getProduct().getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderDetail orderDetailToSave = new OrderDetailBuilder(orderDetail)
                .withProduct(product)
                .withUnitPrice(product.getUnitPrice())
                .build();

        return OrderDetailsBeanToDtoConverter.convert(orderDetailsDao.save(orderDetailToSave));
    }

    public void delete(int orderId, int productId) {
        if (!orderDetailsDao.existsByOrderOrderIdAndProductProductId(orderId, productId)) {
            throw new ResourceNotFoundException("Order detail not found");
        }

        OrderDetail detailToDelete = new OrderDetailBuilder()
                .withOrder(new OrderBuilder()
                        .withOrderId(orderId)
                        .build())
                .withProduct(new ProductBuilder()
                        .withProductId(productId)
                        .build())
                .build();
        orderDetailsDao.delete(detailToDelete);
    }

    public byte[] getOrdersReport(int orderId) {
        Order order = ordersDao.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        List<OrderDetail> details = orderDetailsDao.findAllByOrder(order);

        return createOrderPdf(order, details, order.getCustomer());
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

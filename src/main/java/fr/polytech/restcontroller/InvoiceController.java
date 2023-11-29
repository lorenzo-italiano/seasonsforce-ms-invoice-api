package fr.polytech.restcontroller;

import fr.polytech.annotation.IsAdmin;
import fr.polytech.annotation.IsRecruiter;
import fr.polytech.annotation.IsRecruiterOrAdmin;
import fr.polytech.model.Invoice;
import fr.polytech.model.InvoiceDataDTO;
import fr.polytech.service.InvoiceService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * Create a new invoice
     *
     * @param invoice Invoice to create
     * @return Created invoice
     */
    @PostMapping("/")
    @IsRecruiter
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceDataDTO invoice) {
        try {
            return ResponseEntity.ok(invoiceService.createInvoice(invoice));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all invoices
     *
     * @return List of all invoices
     */
    @GetMapping("/")
    @IsAdmin
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        try {
            return ResponseEntity.ok(invoiceService.getAllInvoices());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get the invoice with the specified ID
     *
     * @param id ID of the invoice to get
     * @return Invoice with the specified ID
     */
    @GetMapping("/{id}")
    @IsRecruiterOrAdmin
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(invoiceService.getInvoiceById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get the invoice URL with the specified ID
     *
     * @param id ID of the invoice to get
     * @return Invoice URL with the specified ID or 404 if not found or 400 if error
     */
    @GetMapping("/url/{id}")
    @IsRecruiterOrAdmin
    @Produces(MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getInvoiceUrlById(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(invoiceService.getInvoiceUrl(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete the invoice with the specified ID
     *
     * @param id ID of the invoice to delete
     * @return 200 if the invoice has been deleted, 400 otherwise
     */
    @DeleteMapping("/{id}")
    @IsRecruiterOrAdmin
    public ResponseEntity<Void> deleteInvoice(@PathVariable("id") UUID id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

package fr.polytech.restcontroller;

import fr.polytech.model.Invoice;
import fr.polytech.model.InvoiceDataDTO;
import fr.polytech.service.InvoiceService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/")
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceDataDTO invoice) {
        try {
            return ResponseEntity.ok(invoiceService.createInvoice(invoice));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        try {
            return ResponseEntity.ok(invoiceService.getAllInvoices());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(invoiceService.getInvoiceById(id));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/url/{id}")
    public ResponseEntity<String> getInvoiceUrlById(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(invoiceService.getInvoiceUrl(id));
        }
        catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable("id") UUID id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

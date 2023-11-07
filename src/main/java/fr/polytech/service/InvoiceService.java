package fr.polytech.service;

import fr.polytech.model.Invoice;
import fr.polytech.model.InvoiceDataDTO;
import fr.polytech.repository.InvoiceRepository;
import io.minio.errors.MinioException;
import jakarta.ws.rs.NotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MinioService minioService;

    /**
     * Create a new invoice
     * @param invoice Invoice to create
     * @return Created invoice
     * @throws IOException If an error occurs while creating the PDF
     * @throws MinioException If an error occurs while uploading the PDF to Minio
     * @throws NoSuchAlgorithmException If an error occurs while uploading the PDF to Minio
     * @throws InvalidKeyException If an error occurs while uploading the PDF to Minio
     * @throws RuntimeException If an error occurs while creating the invoice
     * @throws NotFoundException If an error occurs while uploading the PDF to Minio
     */
    public Invoice createInvoice(InvoiceDataDTO invoice) {

        if (invoice.getCreationDate() == null) {
            invoice.setCreationDate(new java.sql.Date(System.currentTimeMillis()));
        }

        if (invoice.getPrice() == 0.0 || invoice.getPlan() == null || invoice.getName() == null || invoice.getSurname() == null || invoice.getAddress() == null) {
            throw new RuntimeException("At least one of the fields is missing");
        }

        // Crée un nouveau document PDF
        logger.info("Creating invoice");
        PDDocument document = new PDDocument();
        logger.info("Creating page");
        PDPage page = new PDPage(PDRectangle.A4);
        logger.info("Adding page to document");
        document.addPage(page);

        Invoice invoiceToStore = new Invoice();
        invoiceToStore.setCreationDate(invoice.getCreationDate());

        Invoice storedInvoice = invoiceRepository.save(invoiceToStore);

        try {
            logger.info("Creating content stream");
            // Initialise le contenu de la page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
//            contentStream.setFont(PDType1Font, 16);
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.COURIER);
            contentStream.setFont(font, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Facture");
            contentStream.endText();

            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Numéro de la facture : " + storedInvoice.getId());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Nom du client : " + invoice.getName() + " " + invoice.getSurname());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Adresse : " + invoice.getAddress());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Prestation choisie : Abonnement annuel " + invoice.getPlan());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Montant payé : " + invoice.getPrice() + "€");
            contentStream.endText();

            // Ajoutez une image (décommentez et ajustez le chemin si nécessaire)
            // PDImageXObject image = PDImageXObject.createFromFile("chemin_vers_votre_image.jpg", document);
            // contentStream.drawImage(image, 100, 500);

            contentStream.close();

            // Enregistrez le document PDF
            document.save(storedInvoice.getId() + ".pdf");
            document.close();

            File file = new File(storedInvoice.getId() + ".pdf");

            // Create Minio bucket
            try {
                // Upload to minio
                minioService.uploadFile(storedInvoice.getId().toString(), storedInvoice.getId() + ".pdf", file, false);
                storedInvoice.setPdfUrl(storedInvoice.getId() + ".pdf");
            } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException e) {
                invoiceRepository.delete(storedInvoice);
                throw new RuntimeException(e);
            } finally {
                file.delete();
            }

            System.out.println("Facture générée avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO handle minio

        return invoiceRepository.save(storedInvoice);
    }

    /**
     * Get the invoice PDF URL
     *
     * @param id: invoice id
     * @return invoice PDF URL
     * @throws MinioException if an error occurs while getting the invoice URL
     * @throws IOException if an error occurs while getting the invoice URL
     * @throws NoSuchAlgorithmException if an error occurs while getting the invoice URL
     * @throws InvalidKeyException if an error occurs while getting the invoice URL
     * @throws NotFoundException if an error occurs while getting the invoice by id
     */
    public String getInvoiceUrl(UUID id) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException, NotFoundException {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);

        if (invoice == null) {
            throw new NotFoundException();
        }

        String url = minioService.getPrivateDocumentUrl(invoice.getId().toString(), invoice.getPdfUrl()).split("http://invoice-minio:9000/")[1];

        return "http://localhost:8090/api/v1/invoice-files/" + url;
    }

    /**
     * Get all invoices
     * @return List of all invoices
     */
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    /**
     * Get the invoice with the specified ID
     * @param id ID of the invoice to get
     * @return Invoice with the specified ID
     */
    public Invoice getInvoiceById(UUID id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    /**
     * Delete the invoice with the specified ID
     * @param id ID of the invoice to delete
     */
    public void deleteInvoice(UUID id) {
        invoiceRepository.deleteById(id);
    }

}

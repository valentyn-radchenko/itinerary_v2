package org.mohyla.itinerary.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.utils.exceptions.PdfGenerationException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
public class HtmlToPdfConverter {
    private final TemplateEngine templateEngine;

    public HtmlToPdfConverter(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateTicketPdf(Ticket ticket) {
        Context context = new Context();
        context.setVariable("ticket", ticket);
        String html = templateEngine.process("ticket", context);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new PdfGenerationException("PDF generation failed", e);
        }
    }
}

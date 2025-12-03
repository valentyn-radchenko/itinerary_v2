package org.mohyla.itinerary.tickets;

import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.tickets.application.TicketsService;
import org.mohyla.itinerary.tickets.domain.models.Ticket;
import org.mohyla.itinerary.utils.HtmlToPdfConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketApi {

    private final TicketsService ticketsService;
    private final HtmlToPdfConverter htmlToPdfConverter;
    public TicketApi(TicketsService ticketsService, HtmlToPdfConverter htmlToPdfConverter) {
        this.ticketsService = ticketsService;
        this.htmlToPdfConverter = htmlToPdfConverter;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createTicket(@RequestParam Long userId, @RequestParam Long routeId) {
        try {
            String message = ticketsService.createTicket(userId, routeId);
            return ResponseEntity.ok(new ApiResponse<>(true, message, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketsService.getAllTickets());
    }

    @GetMapping(value = "/{id}/html", produces = MediaType.TEXT_HTML_VALUE)
    public String getTicketHtml(@PathVariable Long id, Model model) {
        Optional<Ticket> ticket = ticketsService.getTicket(id);
        if(ticket.isPresent()){
            model.addAttribute("ticket", ticket.get());
            return "ticket";
        }
        else{
            return "Ticket-not-found";
        }
    }
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getTicketPdf(@PathVariable Long id){
        Optional<Ticket> ticket = ticketsService.getTicket(id);
        if(ticket.isPresent()){
            byte[] pdf = htmlToPdfConverter.generateTicketPdf(ticket.get());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ticket-" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF).body(pdf);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}


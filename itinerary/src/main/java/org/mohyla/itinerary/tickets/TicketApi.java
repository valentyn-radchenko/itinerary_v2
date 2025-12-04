package org.mohyla.itinerary.tickets;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.itinerary.dto.ApiResponse;
import org.mohyla.itinerary.security.SecurityUtils;
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

@Slf4j
@Controller
@RequestMapping("/tickets")
public class TicketApi {

    private final TicketsService ticketsService;
    private final HtmlToPdfConverter htmlToPdfConverter;
    private final SecurityUtils securityUtils;
    
    public TicketApi(TicketsService ticketsService, HtmlToPdfConverter htmlToPdfConverter, SecurityUtils securityUtils) {
        this.ticketsService = ticketsService;
        this.htmlToPdfConverter = htmlToPdfConverter;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createTicket(@RequestParam Long routeId) {
        try {
            // Use current authenticated user's ID
            Long currentUserId = securityUtils.getCurrentUserId();
            log.info("Creating ticket for userId={}, routeId={}", currentUserId, routeId);
            
            String message = ticketsService.createTicket(currentUserId, routeId);
            return ResponseEntity.ok(new ApiResponse<>(true, message, null));
        } catch (RuntimeException e) {
            log.error("Failed to create ticket: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getMyTickets() {
        Long currentUserId = securityUtils.getCurrentUserId();
        log.info("Getting tickets for userId={}", currentUserId);
        return ResponseEntity.ok(ticketsService.getTicketsByUserId(currentUserId));
    }

    @GetMapping(value = "/{id}/html", produces = MediaType.TEXT_HTML_VALUE)
    public String getTicketHtml(@PathVariable Long id, Model model) {
        Long currentUserId = securityUtils.getCurrentUserId();
        Optional<Ticket> ticket = ticketsService.getTicket(id);
        
        if(ticket.isEmpty()){
            return "Ticket-not-found";
        }
        
        // Verify the ticket belongs to the current user
        if(!ticket.get().getUserId().equals(currentUserId)){
            log.warn("User {} attempted to access ticket {} owned by user {}", 
                    currentUserId, id, ticket.get().getUserId());
            return "Ticket-forbidden";
        }
        
        model.addAttribute("ticket", ticket.get());
        return "ticket";
    }
    
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getTicketPdf(@PathVariable Long id){
        Long currentUserId = securityUtils.getCurrentUserId();
        Optional<Ticket> ticket = ticketsService.getTicket(id);
        
        if(ticket.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        
        // Verify the ticket belongs to the current user
        if(!ticket.get().getUserId().equals(currentUserId)){
            log.warn("User {} attempted to access PDF of ticket {} owned by user {}", 
                    currentUserId, id, ticket.get().getUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only access your own tickets");
        }
        
        byte[] pdf = htmlToPdfConverter.generateTicketPdf(ticket.get());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ticket-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}



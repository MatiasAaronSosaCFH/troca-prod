package com.c174.controllers;

import com.c174.models.ticket.TicketEnterpriceDto;
import com.c174.models.ticket.TicketRequest;
import com.c174.models.ticket.TicketResponse;
import com.c174.services.abstraccion.TicketService;
import com.c174.services.implementation.EnterpriseConsumeServiceImp;
import com.c174.exception.EntityNotFoundException;
import com.c174.utils.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "Ticket API")
public class TicketController {

    private final TicketService ticketServiceImp;
    private final EnterpriseConsumeServiceImp enterpriseConsumeServiceImp;
    private final CloudinaryService cloudinaryService;

    @PutMapping("/changeService/{id}")
    public ResponseEntity<?> changeServiceTicket(@PathVariable Long id){
        TicketResponse ticketResponse = ticketServiceImp.changeServiceTicket(id);
        if (ticketResponse == null) return new ResponseEntity<>("Ticket not found",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ticketResponse,HttpStatus.OK);
    }

    @GetMapping("/onService")
    public ResponseEntity<?> takeTicketsFromProfileOnService(@RequestParam Boolean onService){
        List<TicketResponse> ticketResponse = ticketServiceImp.findByOnService(onService);
        return new ResponseEntity<>(ticketResponse, HttpStatus.OK);
    }

    @PutMapping("/postOnService")
    public ResponseEntity<?> postOnService(@RequestParam TicketRequest ticket){
        if (ticket ==  null) return new ResponseEntity<>("ticket is not acceptable",HttpStatus.NOT_ACCEPTABLE);
        if (ticket.getId() == null) return new ResponseEntity<>("id is blank", HttpStatus.NOT_ACCEPTABLE);
        if (ticket.getPrice() == 0 || ticket.getPrice() == null) return new ResponseEntity<>("price is not acceptable", HttpStatus.NOT_ACCEPTABLE);
        TicketResponse ticketResponse = ticketServiceImp.sellTicket(ticket);
        Map<TicketResponse, String> response = new HashMap<>();
        response.put(ticketResponse, "successfuly create");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/wallet/{id}")
    public ResponseEntity<?> walletTickets(@PathVariable @NotBlank Long id, @RequestParam @NotBlank Boolean onService){
        List<TicketResponse> tickets = ticketServiceImp.takeTicketsOnServiceByProfile(id,onService);
        if (tickets == null) return new ResponseEntity<>("Profile not found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PostMapping("/create/fast")
    public ResponseEntity<?> createTicete(@RequestParam TicketRequest ticketRequest, @RequestParam Long profileId){

        TicketResponse ticketResponse = ticketServiceImp.saveFast(ticketRequest, profileId);

        return new ResponseEntity<>(ticketResponse, HttpStatus.CREATED);
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@RequestPart(value="file", required = false) MultipartFile file,
                                    @PathVariable Long id) throws EntityNotFoundException, IOException {

        BufferedImage entry = ImageIO.read(file.getInputStream());
        if (entry == null) return new ResponseEntity<>("Qr is not supported",HttpStatus.NOT_ACCEPTABLE);
        File img = cloudinaryService.convetir(file);

        Optional<TicketEnterpriceDto> ticketResponseEnterprice = Optional.of(enterpriseConsumeServiceImp.checkTicket(file));

        if (ticketResponseEnterprice.get().getIsLocked()){
            return new ResponseEntity<>("Ticket is already on service", HttpStatus.NOT_ACCEPTABLE);
        }
        if (ticketResponseEnterprice.get().getIsPresent()) return new ResponseEntity<>("Ticket is not available", HttpStatus.NOT_ACCEPTABLE);

        TicketResponse ticketResponse = ticketServiceImp.create(ticketResponseEnterprice.get(), id);

        return new ResponseEntity<>(ticketResponse,HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listTickets(){
        List<TicketResponse> tickets = ticketServiceImp.listTickets();
        if (tickets.isEmpty()) return new ResponseEntity<>(tickets, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(tickets,HttpStatus.OK);
    }

    @GetMapping("/checkTicket")
    public ResponseEntity<?> checkTicket(@RequestParam MultipartFile file){
        TicketResponse ticketResponse = ticketServiceImp.checkTicket(file);
        if (ticketResponse == null) return new ResponseEntity<>("ticket is not available", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ticketResponse, HttpStatus.OK);
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewQr(@RequestParam File file){
        TicketResponse ticketResponse = ticketServiceImp.renewQr(file);
        return  new ResponseEntity<>(ticketResponse,HttpStatus.OK);
    }

    @PutMapping("/lockTicket/{id}")
    public ResponseEntity<?> lockTicket(@PathVariable Long id){
        TicketResponse ticketResponse = ticketServiceImp.lockTicket(id);
        return new ResponseEntity<>(ticketResponse,HttpStatus.LOCKED);
    }

    @Operation(summary = "Get all Tickets")
    @GetMapping("/all")
    public ResponseEntity<?> getAll() throws EntityNotFoundException {
        Map<String, Object> bodyResponse = new HashMap<>();
        List<TicketResponse> response = ticketServiceImp.getAll();
        bodyResponse.put("data", response);
        bodyResponse.put("success", Boolean.TRUE);
        return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
    }

    @Operation(summary = "Get ticket by PROFILE_ID, if this include in the PROFILE return the ticket")
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getByProfile( @PathVariable Long id){
        Map<String, Object> bodyResponse = new HashMap<>();
        try{
            List<TicketResponse> response = ticketServiceImp.getTicketByProfile(id);
            bodyResponse.put("data", response);
            bodyResponse.put("success", Boolean.TRUE);
            return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
        } catch (Exception e) {
            bodyResponse.put("data", e.getMessage());
            bodyResponse.put("success", Boolean.FALSE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bodyResponse);
        }
    }

    @Operation(summary = "Get ticket by EVENT_NAME, if this include in the EVENT return the ticket")
    @GetMapping("/event")
    public ResponseEntity<?> getByEvent( @RequestParam String name){
        Map<String, Object> bodyResponse = new HashMap<>();
        try{
            List<TicketResponse> response = ticketServiceImp.getTicketByEvent(name);
            bodyResponse.put("data", response);
            bodyResponse.put("success", Boolean.TRUE);
            return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
        } catch (Exception e) {
            bodyResponse.put("data", e.getMessage());
            bodyResponse.put("success", Boolean.FALSE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bodyResponse);
        }
    }

    @Operation(summary = "Get ticket by EVENT_ID, if this include in the EVENT return the ticket")
    @GetMapping("/event/{id}")
    public ResponseEntity<?> getByEvent( @PathVariable Long id){
        Map<String, Object> bodyResponse = new HashMap<>();
        try{
            List<TicketResponse> response = ticketServiceImp.getTicketByEvent(id);
            bodyResponse.put("data", response);
            bodyResponse.put("success", Boolean.TRUE);
            return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
        } catch (Exception e) {
            bodyResponse.put("data", e.getMessage());
            bodyResponse.put("success", Boolean.FALSE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bodyResponse);
        }
    }

    @Operation(summary = "Get ticket by ID")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws EntityNotFoundException {
        Map<String, Object> bodyResponse = new HashMap<>();
        TicketResponse response = ticketServiceImp.getById(id);
        bodyResponse.put("data", response);
        bodyResponse.put("success", Boolean.TRUE);
        return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
    }


}

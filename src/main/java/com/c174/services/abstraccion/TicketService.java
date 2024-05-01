package com.c174.services.abstraccion;

import com.c174.exception.EntityNotFoundException;
import com.c174.models.ticket.TicketEnterpriceDto;
import com.c174.models.ticket.TicketRequest;
import com.c174.models.ticket.TicketResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface TicketService extends GenericService<TicketResponse, TicketRequest>{

    TicketResponse create(TicketEnterpriceDto ticketRequestm, Long id) throws EntityNotFoundException;
    List<TicketResponse> takeTicketsOnServiceByProfile(Long profileId,Boolean onService);
    TicketResponse changeServiceTicket(Long id);
    List<TicketResponse> listTickets();
    TicketResponse checkTicket(MultipartFile file);

    TicketResponse saveFast(TicketRequest ticketRequest, Long id);
    TicketResponse renewQr(File file);
    TicketResponse lockTicket(Long id);
    List<TicketResponse> getTicketByProfile(Long id);
    List<TicketResponse> getTicketByEvent(Long id);
    List<TicketResponse> getTicketByEvent(String  name);
    List<TicketResponse> findTicketsByProfileAndByLock(Long id, Boolean lock);
    List<TicketResponse> findAllTicketsNotLock();
    List<TicketResponse> findByOnService(Boolean onService);
    TicketResponse sellTicket(TicketRequest ticketRequest);
}

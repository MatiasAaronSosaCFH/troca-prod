package com.c174.services.abstraccion;

import com.c174.models.ticket.TicketEnterpriceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface EnterpriseConsumeService {

    TicketEnterpriceDto checkTicket(MultipartFile file) throws IOException;
    TicketEnterpriceDto lockTicket(Long id);
    TicketEnterpriceDto renewQr(File file);


}

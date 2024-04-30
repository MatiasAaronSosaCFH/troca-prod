package com.c174.services.abstraccion;

import com.c174.exception.EntityExistsException;
import com.c174.models.event.EventRequest;
import com.c174.models.event.EventResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService extends GenericService<EventResponse, EventRequest>{


    EventResponse saveImg(EventRequest event, MultipartFile file) throws IOException;
}

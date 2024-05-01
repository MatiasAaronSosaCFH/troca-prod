package com.c174.services.implementation;

import com.c174.models.ticket.TicketEnterpriceDto;
import com.c174.services.abstraccion.EnterpriseConsumeService;
import com.c174.utils.AbstractClient;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class EnterpriseConsumeServiceImp extends AbstractClient implements EnterpriseConsumeService {

    protected EnterpriseConsumeServiceImp(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public TicketEnterpriceDto checkTicket(MultipartFile file) throws IOException {

        String url = baseURl + "ticket/checkTicket";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        File tempFile= File.createTempFile("temp-file", null);

        try (OutputStream os = new FileOutputStream( tempFile)) {
           os.write(file.getBytes());
        }
        FileSystemResource resource = new FileSystemResource( tempFile);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<TicketEnterpriceDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity , TicketEnterpriceDto.class);

        tempFile.delete();

        if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
        else return null;
    }

    @Override
    public TicketEnterpriceDto lockTicket(Long id) {

        String url = baseURl + "/ticket/lock/{id}";

        Long pathVariable = id;

        ResponseEntity<TicketEnterpriceDto> response = restTemplate.getForEntity(url, TicketEnterpriceDto.class,pathVariable);

        if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
        else return null;

    }

    @Override
    public TicketEnterpriceDto renewQr(File file) {

        String url = baseURl + "/ticket/renewQr";

        ResponseEntity<TicketEnterpriceDto> response = restTemplate.postForEntity(url,file, TicketEnterpriceDto.class);
        if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
        else return null;
    }
}

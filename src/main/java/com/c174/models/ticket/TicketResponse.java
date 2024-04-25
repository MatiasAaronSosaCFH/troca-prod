package com.c174.models.ticket;

import lombok.Builder;
import com.c174.models.embed.Audit;
import com.c174.models.event.EventResponse;
import com.c174.models.profile.ProfileResponse;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private String meta;
    private EventResponse event;
    private Audit audit;
    private ProfileResponse owner;
    private Double price;

}

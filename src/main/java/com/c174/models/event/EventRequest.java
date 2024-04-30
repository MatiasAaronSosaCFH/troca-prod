package com.c174.models.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class EventRequest {

    private Long id;
    @NotEmpty
    @Size(min = 1, max = 50)
    private String name;
    private Date dateStart;
    private Date dateEnd;
    private String address;

}

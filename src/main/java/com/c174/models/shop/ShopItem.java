package com.c174.models.shop;

import com.c174.models.ticket.TicketShop;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Data
public class ShopItem {
    private TicketShop items;
    private UserShop payer;
}

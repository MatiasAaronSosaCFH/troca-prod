package com.c174.services.implementation;

import com.c174.exception.AlreadyExistsException;
import com.c174.models.mpuser.CredentialMPUser;
import com.c174.models.profile.ProfileEntity;
import com.c174.models.shop.ShopItem;
import com.c174.models.shop.UserShop;
import com.c174.models.ticket.TicketEntity;
import com.c174.models.ticket.TicketShop;
import com.c174.models.user.UserEntity;
import com.c174.repositorys.MpUserRepository;
import com.c174.repositorys.ProfileRepository;
import com.c174.repositorys.TicketRepository;
import com.c174.repositorys.UserRepository;
import com.c174.services.abstraccion.ShopService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ShopServiceImplementation implements ShopService {
    private final TicketRepository ticketRepository;
    private final ProfileRepository profileRepository;
    private final MpUserRepository mpUserRepository;
    private final UserRepository userRepository;
    private final String baseUrl = "https://c17-34-m-java.vercel.app/";
    @Value("${mercadopago.access_token}")
    private String accessToken;
    public ShopServiceImplementation(TicketRepository ticketRepository, ProfileRepository profileRepository, MpUserRepository mpUserRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.profileRepository = profileRepository;
        this.mpUserRepository = mpUserRepository;
        this.userRepository = userRepository;
    }
    @Override
    public String buyTickets(ShopItem ticket) throws AlreadyExistsException, MPException, MPApiException {
        TicketShop ticketsShop = ticket.getItems();
        UserShop user = ticket.getPayer();

        UserEntity userEntity = userRepository.findByEmail(user.getEmail()).get();
        TicketEntity ticketsEntities= ticketRepository.findById(ticketsShop.getId()).get();
        ProfileEntity profile = profileRepository.findById(userEntity.getProfile().getId()).get();
        
        String sandboxInit = createPreference(ticketsEntities, user,findAccessToken(ticketsEntities.getId()));

        ticketsEntities.setOwner(profile);
        ticketRepository.save(ticketsEntities);

        return sandboxInit;
    }
    //Crea la preferencia de MercadoPago devolviendo el link hacia donde va a ser redirigido el comprador
    private String  createPreference(TicketEntity ticket, UserShop userBuyer, String accessTokenVendedor) throws MPException, MPApiException {

        UserEntity user = userRepository.findByEmail(userBuyer.getEmail()).get();
        Long idRef = user.getProfile().getId();

        try {
            MercadoPagoConfig.setAccessToken(accessTokenVendedor);

            List<PreferenceItemRequest> items = new ArrayList<>();

            items.add(createItemRequest(ticket));

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(baseUrl + "wallet/upcoming/" + idRef)
                    .pending("")
                    .failure(baseUrl)
                    .build();

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .name(userBuyer.getName())
                    .email(userBuyer.getEmail())
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .payer(payer)
                    .marketplaceFee(new BigDecimal(500))
                    .purpose("wallet_purchase")
                    .build();

            PreferenceClient client = new PreferenceClient();

            Map<String, String> customHeaders = new HashMap<>();
            customHeaders.put("Content-Type", "application/json");
            customHeaders.put("Authorization", "Bearer " + accessTokenVendedor);

            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders( customHeaders)
                    .build();

            Preference preference = client.create(preferenceRequest, requestOptions);

            return preference.getInitPoint();
        }
        catch (MPApiException e){
            throw e;
        }
        catch (MPException e){
            throw e;
        }
        catch (Exception e){
            throw e;
        }
    }
    // Crea un ItemRequest para la preferencia de MercadoPago
    private PreferenceItemRequest createItemRequest(TicketEntity ticket) {
        return PreferenceItemRequest.builder()
                .id(ticket.getId().toString())
                .title(ticket.getEvent().getName())
                .description(ticket.getEvent().getName())
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(new BigDecimal(ticket.getPrice().toString())) // TODO: cambiar esto
                .build();
    }

    // Busca el access token del vendedor atravez de la entidad profile hasta llegar al user
    private String findAccessToken(Long ticketId) {
            TicketEntity ticket = ticketRepository.findById(ticketId).get();
            Long userAppId = ticket.getOwner().getUser().getId();
            Optional<CredentialMPUser> credentialMPUser = mpUserRepository.findByUserApp_Id(userAppId);
            return credentialMPUser.get().getAccess_token();
    }

}

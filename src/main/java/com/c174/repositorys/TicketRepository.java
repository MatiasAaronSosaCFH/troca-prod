package com.c174.repositorys;

import com.c174.models.ticket.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    @Query("SELECT t FROM TicketEntity t WHERE t.owner.id = ?1")
    List<TicketEntity> findByProfileId(Long id);

    @Query("SELECT t FROM TicketEntity t WHERE t.event.id = ?1")
    List<TicketEntity> findByEventId(Long id);

    @Query("SELECT t FROM TicketEntity t WHERE t.isLock = :lock")
    List<TicketEntity> findAllLock(@Param("lock") Boolean lock);

    /*@Transactional
    @Modifying
    @Query("UPDATE TicketEntity t SET t.onService = :onService WHERE t.id = :id")
    void changeServiceTicket(@Param("onService")Boolean onService, @Param("id")Long id);

    @Modifying
    @Query("UPDATE TicketEntity t SET t.event = :event," +
            " t.price = :price," +
            " t.owner = :owner," +
            " t.isPresent = :isPresent," +
            " t.isLock = :isLock," +
            "t.meta = :meta" +
            "WHERE t.id = :id")

    TicketEntity updateTicket(@Param("event")Long event,
                                        @Param("price")Double price,
                                        @Param("owner")Long owner,
                                        @Param("meta")String meta,
                                        @Param("id")Long id);*/
}

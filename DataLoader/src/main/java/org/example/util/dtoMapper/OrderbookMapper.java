package org.example.util.dtoMapper;

import org.example.postgres.entity.OrderbookEntity;

import java.time.OffsetDateTime;
import java.util.List;

public class OrderbookMapper {

    public static OrderbookEntity mapApiOrderbookToEntity(ru.tinkoff.piapi.contract.v1.OrderBook apiOrderbook) {
        OrderbookEntity orderbookEntity = new OrderbookEntity();

        orderbookEntity.setFigi(apiOrderbook.getFigi());
        orderbookEntity.setInstrumentUid(apiOrderbook.getInstrumentUid());
        orderbookEntity.setDepth(apiOrderbook.getDepth());
        orderbookEntity.setTime(OffsetDateTime.ofInstant(
                java.time.Instant.ofEpochSecond(apiOrderbook.getTime().getSeconds(), apiOrderbook.getTime().getNanos()),
                java.time.ZoneOffset.UTC));

        List<OrderbookEntity.Order> bids = apiOrderbook.getBidsList().stream()
                .map(apiOrder -> new OrderbookEntity.Order(
                        new OrderbookEntity.Quotation(apiOrder.getPrice().getUnits(), apiOrder.getPrice().getNano()),
                        apiOrder.getQuantity()
                ))
                .toList();

        List<OrderbookEntity.Order> asks = apiOrderbook.getAsksList().stream()
                .map(apiOrder -> new OrderbookEntity.Order(
                        new OrderbookEntity.Quotation(apiOrder.getPrice().getUnits(), apiOrder.getPrice().getNano()),
                        apiOrder.getQuantity()
                ))
                .toList();

        orderbookEntity.setBids(bids);
        orderbookEntity.setAsks(asks);

        return orderbookEntity;
    }
}

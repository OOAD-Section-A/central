package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.PriceListDao;
import com.jackfruit.scm.database.model.PriceList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PricingServiceTest {

    @Test
    void shouldRejectPriceFloorAboveBasePrice() {
        PricingService pricingService = new PricingService(new NoOpPriceListDao());

        PriceList invalid = new PriceList(
                "PRICE-INVALID",
                "SKU-1",
                "SOUTH",
                "RETAIL",
                "RETAIL",
                new BigDecimal("100"),
                new BigDecimal("110"),
                "INR",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "ACTIVE");

        Assertions.assertThrows(IllegalArgumentException.class, () -> pricingService.createPrice(invalid));
    }

    private static final class NoOpPriceListDao implements PriceListDao {

        @Override
        public void save(PriceList entity) {
        }

        @Override
        public void update(PriceList entity) {
        }

        @Override
        public Optional<PriceList> findById(String s) {
            return Optional.empty();
        }

        @Override
        public List<PriceList> findAll() {
            return List.of();
        }
    }
}

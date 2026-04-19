package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.PriceListDao;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class PricingService {

    private final PriceListDao priceListDao;

    public PricingService(PriceListDao priceListDao) {
        this.priceListDao = priceListDao;
    }

    public void createPrice(PriceList priceList) {
        validatePriceList(priceList);
        priceListDao.save(priceList);
    }

    public void updatePrice(PriceList priceList) {
        validatePriceList(priceList);
        priceListDao.update(priceList);
    }

    public Optional<PriceList> getPrice(String priceId) {
        ValidationUtils.requireText(priceId, "priceId");
        return priceListDao.findById(priceId);
    }

    public List<PriceList> getAllPrices() {
        return priceListDao.findAll();
    }

    private void validatePriceList(PriceList priceList) {
        ValidationUtils.requireText(priceList.getPriceId(), "priceId");
        ValidationUtils.requireText(priceList.getSkuId(), "skuId");
        ValidationUtils.requireText(priceList.getRegionCode(), "regionCode");
        ValidationUtils.requireText(priceList.getChannel(), "channel");
        ValidationUtils.requireText(priceList.getPriceType(), "priceType");
        ValidationUtils.requirePositive(priceList.getBasePrice(), "basePrice");
        ValidationUtils.requirePositive(priceList.getPriceFloor(), "priceFloor");
        if (priceList.getPriceFloor().compareTo(priceList.getBasePrice()) > 0) {
            throw new IllegalArgumentException("priceFloor cannot be greater than basePrice");
        }
        if (priceList.getEffectiveFrom() == null || priceList.getEffectiveTo() == null
                || !priceList.getEffectiveTo().isAfter(priceList.getEffectiveFrom())) {
            throw new IllegalArgumentException("effectiveTo must be after effectiveFrom");
        }
    }
}

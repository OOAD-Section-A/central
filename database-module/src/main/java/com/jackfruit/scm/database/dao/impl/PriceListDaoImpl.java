package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.PriceListDao;
import com.jackfruit.scm.database.model.PriceList;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class PriceListDaoImpl extends AbstractJdbcDao implements PriceListDao {

    @Override
    public void save(PriceList priceList) {
        executeUpdate(
                """
                INSERT INTO price_list
                (price_id, sku_id, region_code, channel, price_type, base_price, price_floor,
                 currency_code, effective_from, effective_to, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, priceList.getPriceId());
                    statement.setString(2, priceList.getSkuId());
                    statement.setString(3, priceList.getRegionCode());
                    statement.setString(4, priceList.getChannel());
                    statement.setString(5, priceList.getPriceType());
                    statement.setBigDecimal(6, priceList.getBasePrice());
                    statement.setBigDecimal(7, priceList.getPriceFloor());
                    statement.setString(8, priceList.getCurrencyCode());
                    statement.setTimestamp(9, Timestamp.valueOf(priceList.getEffectiveFrom()));
                    statement.setTimestamp(10, Timestamp.valueOf(priceList.getEffectiveTo()));
                    statement.setString(11, priceList.getStatus());
                });
    }

    @Override
    public void update(PriceList priceList) {
        executeUpdate(
                """
                UPDATE price_list
                SET base_price = ?, price_floor = ?, effective_to = ?, status = ?
                WHERE price_id = ?
                """,
                statement -> {
                    statement.setBigDecimal(1, priceList.getBasePrice());
                    statement.setBigDecimal(2, priceList.getPriceFloor());
                    statement.setTimestamp(3, Timestamp.valueOf(priceList.getEffectiveTo()));
                    statement.setString(4, priceList.getStatus());
                    statement.setString(5, priceList.getPriceId());
                });
    }

    @Override
    public Optional<PriceList> findById(String priceId) {
        return queryForObject(
                "SELECT * FROM price_list WHERE price_id = ?",
                resultSet -> new PriceList(
                        resultSet.getString("price_id"),
                        resultSet.getString("sku_id"),
                        resultSet.getString("region_code"),
                        resultSet.getString("channel"),
                        resultSet.getString("price_type"),
                        resultSet.getBigDecimal("base_price"),
                        resultSet.getBigDecimal("price_floor"),
                        resultSet.getString("currency_code"),
                        resultSet.getTimestamp("effective_from").toLocalDateTime(),
                        resultSet.getTimestamp("effective_to").toLocalDateTime(),
                        resultSet.getString("status")),
                statement -> statement.setString(1, priceId));
    }

    @Override
    public List<PriceList> findAll() {
        return queryForList(
                "SELECT * FROM price_list",
                resultSet -> new PriceList(
                        resultSet.getString("price_id"),
                        resultSet.getString("sku_id"),
                        resultSet.getString("region_code"),
                        resultSet.getString("channel"),
                        resultSet.getString("price_type"),
                        resultSet.getBigDecimal("base_price"),
                        resultSet.getBigDecimal("price_floor"),
                        resultSet.getString("currency_code"),
                        resultSet.getTimestamp("effective_from").toLocalDateTime(),
                        resultSet.getTimestamp("effective_to").toLocalDateTime(),
                        resultSet.getString("status")));
    }
}

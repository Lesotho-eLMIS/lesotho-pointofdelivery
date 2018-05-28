/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.stockmanagement.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.openlmis.stockmanagement.domain.card.StockCardLineItemComparators.byOccurredDate;
import static org.openlmis.stockmanagement.domain.card.StockCardLineItemComparators.byProcessedDate;
import static org.openlmis.stockmanagement.domain.card.StockCardLineItemComparators.byReasonPriority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openlmis.stockmanagement.domain.card.StockCard;
import org.openlmis.stockmanagement.domain.card.StockCardLineItem;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class StockCardAggregate {

  @Getter
  @Setter
  private List<StockCard> stockCards;

  /**
   * Returns amount of products assigned to reasons that have given tag.
   * It takes into consideration reason type.
   *
   * @param tag used for filtering stock card line items by reason tag
   * @param startDate used for filtering stock card line items by occurred date
   * @param endDate used for filtering stock card line items by occurred date
   * @return quantity value, is negative for Debit reason
   */
  public Integer getAmount(String tag, LocalDate startDate, LocalDate endDate) {
    return getFilteredLineItems(startDate, endDate, tag).stream()
        .mapToInt(StockCardLineItem::getValue)
        .sum();
  }

  /**
   * Returns map of tags found in reasons from all stock card line items
   * and accumulated value of line items that have reason with given tag.
   *
   * @param startDate used for filtering stock card line items by occurred date
   * @param endDate used for filtering stock card line items by occurred date
   * @return map of tags and amounts from connected line items
   */
  public Map<String, Integer> getAmounts(LocalDate startDate, LocalDate endDate) {
    return getFilteredLineItems(startDate, endDate, null).stream()
        .map(lineItem -> {
          int value = lineItem.getValue();
          return lineItem.getReason().getTags().stream()
              .map(tag -> new ImmutablePair<>(tag, value))
              .collect(toList());
        })
        .flatMap(Collection::stream)
        .collect(toMap(
            ImmutablePair::getLeft,
            ImmutablePair::getRight,
            Integer::sum));
  }

  /**
   * Returns a number of days where stock was below zero in range of dates.
   *
   * @param startDate used for filtering stock card line items by occurred date
   * @param endDate used for filtering stock card line items by occurred date
   * @return number of days without stock available
   */
  public Long getStockoutDays(LocalDate startDate, LocalDate endDate) {
    List<StockCardLineItem> lineItems = stockCards.stream()
        .flatMap(stockCard -> stockCard.getLineItems().stream())
        .sorted(getComparator())
        .collect(toList());

    return calculateStockoutDays(getStockoutPeriods(lineItems), startDate, endDate);
  }

  private Comparator<StockCardLineItem> getComparator() {
    return byOccurredDate()
        .thenComparing(byProcessedDate())
        .thenComparing(byReasonPriority());
  }

  private List<StockCardLineItem> getFilteredLineItems(LocalDate startDate,
      LocalDate endDate, String tag) {

    return stockCards.stream()
        .flatMap(stockCard -> stockCard.getLineItems().stream())
        .filter(lineItem ->
            (null == startDate || lineItem.getOccurredDate().isAfter(startDate))
                && (null == endDate || lineItem.getOccurredDate().isBefore(endDate))
                && (null == tag || lineItem.getReason().getTags().contains(tag)))
        .collect(toList());
  }

  private Map<LocalDate, LocalDate> getStockoutPeriods(List<StockCardLineItem> lineItems) {

    int recentSoh = 0;
    LocalDate stockOutDateStart = null;
    Map<LocalDate, LocalDate> stockOutDaysMap = new TreeMap<>();

    for (StockCardLineItem lineItem : lineItems) {
      lineItem.calculateStockOnHand(recentSoh);
      recentSoh = lineItem.getStockOnHand();
      if (recentSoh <= 0) {
        stockOutDateStart = lineItem.getOccurredDate();
      } else if (null != stockOutDateStart) {
        stockOutDaysMap.put(stockOutDateStart, lineItem.getOccurredDate());
        stockOutDateStart = null;
      }
    }

    return stockOutDaysMap;
  }

  private Long calculateStockoutDays(Map<LocalDate, LocalDate> stockOutDaysMap,
      LocalDate startDate, LocalDate endDate) {

    return stockOutDaysMap.keySet().stream()
        .filter(key -> null == endDate || !key.isAfter(endDate))
        .filter(key -> null == startDate || !stockOutDaysMap.get(key).isBefore(startDate))
        .mapToLong(key -> DAYS.between(
            startDate.isAfter(key) ? startDate : key,
            endDate.isBefore(stockOutDaysMap.get(key)) ? endDate : stockOutDaysMap.get(key)))
        .sum();
  }
}

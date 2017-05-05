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

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.openlmis.stockmanagement.service.StockCardSummariesService.SearchOptions.ExistingStockCardsOnly;
import static org.openlmis.stockmanagement.service.StockCardSummariesService.SearchOptions.IncludeApprovedOrderables;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.stockmanagement.domain.card.StockCard;
import org.openlmis.stockmanagement.domain.card.StockCardLineItem;
import org.openlmis.stockmanagement.dto.StockCardDto;
import org.openlmis.stockmanagement.dto.referencedata.LotDto;
import org.openlmis.stockmanagement.dto.referencedata.OrderableDto;
import org.openlmis.stockmanagement.repository.StockCardRepository;
import org.openlmis.stockmanagement.service.referencedata.ApprovedProductReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.FacilityReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.LotReferenceDataService;
import org.openlmis.stockmanagement.service.referencedata.ProgramReferenceDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class StockCardSummariesServiceTest {

  @Mock
  private ApprovedProductReferenceDataService approvedProductReferenceDataService;

  @Mock
  @SuppressWarnings("PMD")
  private FacilityReferenceDataService facilityRefDataService;

  @Mock
  @SuppressWarnings("PMD")
  private ProgramReferenceDataService programRefDataService;

  @Mock
  private LotReferenceDataService lotReferenceDataService;

  @Mock
  private StockCardRepository cardRepository;

  @InjectMocks
  private StockCardSummariesService stockCardSummariesService;

  @Test
  public void should_contain_existing_stock_cards_and_approved_orderables_and_lots()
      throws Exception {
    //given
    UUID orderable1Id = randomUUID();
    UUID orderable2Id = randomUUID();
    UUID orderable3Id = randomUUID();
    UUID orderable4Id = randomUUID();

    OrderableDto orderable1 = createOrderableDto(orderable1Id);
    OrderableDto orderable2 = createOrderableDto(orderable2Id);
    OrderableDto orderable3 = createOrderableDto(orderable3Id);
    OrderableDto orderable4 = createOrderableDto(orderable4Id);

    UUID programId = randomUUID();
    UUID facilityId = randomUUID();

    when(approvedProductReferenceDataService
        .getAllApprovedProducts(programId, facilityId))
        .thenReturn(asList(orderable1, orderable2, orderable3, orderable4));

    when(cardRepository.findByProgramIdAndFacilityId(programId, facilityId))
        .thenReturn(asList(
            createStockCard(orderable1Id, randomUUID()),
            createStockCard(orderable3Id, randomUUID())));

    LotDto lotDto = new LotDto();
    lotDto.setId(randomUUID());
    when(lotReferenceDataService.getAllLotsOf(orderable1Id))
        .thenReturn(singletonList(lotDto));
    when(lotReferenceDataService.getAllLotsOf(orderable2Id))
        .thenReturn(emptyList());
    when(lotReferenceDataService.getAllLotsOf(orderable3Id))
        .thenReturn(emptyList());
    when(lotReferenceDataService.getAllLotsOf(orderable4Id))
        .thenReturn(emptyList());

    //when
    List<StockCardDto> cardDtos = stockCardSummariesService
        .findStockCards(programId, facilityId, IncludeApprovedOrderables);

    //then
    assertThat(cardDtos.size(), is(5));

    String orderablePropertyName = "orderable";
    String lotPropertyName = "lot";
    String idPropertyName = "id";
    String lineItemsPropertyName = "lineItems";
    String stockOnHandPropertyName = "stockOnHand";
    String lastUpdatePropertyName = "lastUpdate";

    assertThat(cardDtos, hasItem(allOf(
        hasProperty(orderablePropertyName, is(orderable1)),
        hasProperty(lotPropertyName, is(nullValue())),
        hasProperty(idPropertyName, notNullValue()),
        hasProperty(stockOnHandPropertyName, notNullValue()),
        hasProperty(lastUpdatePropertyName, is(of(2017, 3, 18, 15, 10, 31, 100, UTC))),
        hasProperty(lineItemsPropertyName, nullValue()))));
    assertThat(cardDtos, hasItem(allOf(
        hasProperty(orderablePropertyName, is(orderable3)),
        hasProperty(lotPropertyName, is(nullValue())),
        hasProperty(idPropertyName, notNullValue()),
        hasProperty(stockOnHandPropertyName, notNullValue()),
        hasProperty(lastUpdatePropertyName, is(of(2017, 3, 18, 15, 10, 31, 100, UTC))),
        hasProperty(lineItemsPropertyName, nullValue()))));

    assertThat(cardDtos, hasItem(allOf(
        hasProperty(orderablePropertyName, is(orderable2)),
        hasProperty(lotPropertyName, is(nullValue())),
        hasProperty(idPropertyName, nullValue()),
        hasProperty(stockOnHandPropertyName, nullValue()),
        hasProperty(lastUpdatePropertyName, nullValue()),
        hasProperty(lineItemsPropertyName, nullValue()))));
    assertThat(cardDtos, hasItem(allOf(
        hasProperty(orderablePropertyName, is(orderable4)),
        hasProperty(lotPropertyName, is(nullValue())),
        hasProperty(idPropertyName, nullValue()),
        hasProperty(stockOnHandPropertyName, nullValue()),
        hasProperty(lastUpdatePropertyName, nullValue()),
        hasProperty(lineItemsPropertyName, nullValue()))));

    assertThat(cardDtos, hasItem(allOf(
        hasProperty(orderablePropertyName, is(orderable1)),
        hasProperty(lotPropertyName, is(lotDto)),
        hasProperty(idPropertyName, nullValue()),
        hasProperty(stockOnHandPropertyName, nullValue()),
        hasProperty(lastUpdatePropertyName, nullValue()),
        hasProperty(lineItemsPropertyName, nullValue()))));
  }

  @Test
  public void should_contain_existing_stock_cards_only_when_indicated_by_parameter()
      throws Exception {
    //given
    UUID orderable1Id = randomUUID();
    UUID orderable2Id = randomUUID();
    UUID orderable3Id = randomUUID();
    UUID orderable4Id = randomUUID();

    OrderableDto orderable1 = createOrderableDto(orderable1Id);
    OrderableDto orderable2 = createOrderableDto(orderable2Id);
    OrderableDto orderable3 = createOrderableDto(orderable3Id);
    OrderableDto orderable4 = createOrderableDto(orderable4Id);

    UUID programId = randomUUID();
    UUID facilityId = randomUUID();
    when(approvedProductReferenceDataService
        .getAllApprovedProducts(programId, facilityId))
        .thenReturn(asList(orderable1, orderable2, orderable3, orderable4));

    when(cardRepository.findByProgramIdAndFacilityId(programId, facilityId))
        .thenReturn(asList(
            createStockCard(orderable1Id, randomUUID()),
            createStockCard(orderable3Id, randomUUID())));

    when(lotReferenceDataService.getAllLotsOf(any(UUID.class)))
        .thenReturn(emptyList());

    //when
    List<StockCardDto> cardDtos = stockCardSummariesService
        .findStockCards(programId, facilityId, ExistingStockCardsOnly);

    //then
    assertThat(cardDtos.size(), is(2));

    String orderablePropertyName = "orderable";
    String idPropertyName = "id";
    String lineItemsPropertyName = "lineItems";
    String stockOnHandPropertyName = "stockOnHand";
    String lastUpdatePropertyName = "lastUpdate";

    assertThat(cardDtos, hasItem(allOf(
        hasProperty(orderablePropertyName, is(orderable1)),
        hasProperty(idPropertyName, notNullValue()),
        hasProperty(stockOnHandPropertyName, notNullValue()),
        hasProperty(lastUpdatePropertyName, is(of(2017, 3, 18, 15, 10, 31, 100, UTC))),
        hasProperty(lineItemsPropertyName, nullValue()))));
    assertThat(cardDtos, hasItem(allOf(
        hasProperty(orderablePropertyName, is(orderable3)),
        hasProperty(idPropertyName, notNullValue()),
        hasProperty(stockOnHandPropertyName, notNullValue()),
        hasProperty(lastUpdatePropertyName, is(of(2017, 3, 18, 15, 10, 31, 100, UTC))),
        hasProperty(lineItemsPropertyName, nullValue()))));
  }

  @Test
  public void should_return_page_of_stock_cards() throws Exception {
    //given
    UUID programId = randomUUID();
    UUID facilityId = randomUUID();
    PageRequest pageRequest = new PageRequest(0, 1);

    StockCard card = new StockCard();
    card.setLineItems(emptyList());
    when(cardRepository.findByProgramIdAndFacilityId(programId, facilityId, pageRequest))
        .thenReturn(new PageImpl<>(singletonList(card), pageRequest, 10));

    when(approvedProductReferenceDataService.getAllApprovedProducts(programId, facilityId))
        .thenReturn(singletonList(new OrderableDto()));

    when(lotReferenceDataService.getAllLotsOf(any(UUID.class)))
        .thenReturn(emptyList());

    //when
    Page<StockCardDto> stockCards = stockCardSummariesService
        .findStockCards(programId, facilityId, pageRequest);

    //then
    assertThat(stockCards.getContent().size(), is(1));
    assertThat(stockCards.getTotalElements(), is(10L));
  }

  private StockCard createStockCard(UUID orderableId, UUID cardId) {
    StockCard stockCard = new StockCard();
    stockCard.setOrderableId(orderableId);
    stockCard.setId(cardId);
    StockCardLineItem lineItem1 = StockCardLineItem.builder()
        .occurredDate(of(2017, 3, 17, 15, 10, 31, 100, UTC))
        .processedDate(of(2017, 3, 17, 15, 10, 31, 100, UTC))
        .quantity(1)
        .build();
    StockCardLineItem lineItem2 = StockCardLineItem.builder()
        .occurredDate(of(2017, 3, 18, 15, 10, 31, 100, UTC))
        .processedDate(of(2017, 3, 18, 15, 10, 31, 100, UTC))
        .quantity(1)
        .build();
    stockCard.setLineItems(asList(lineItem1, lineItem2, lineItem1));
    return stockCard;
  }

  private OrderableDto createOrderableDto(UUID orderableId) {
    return OrderableDto.builder().id(orderableId).build();
  }
}
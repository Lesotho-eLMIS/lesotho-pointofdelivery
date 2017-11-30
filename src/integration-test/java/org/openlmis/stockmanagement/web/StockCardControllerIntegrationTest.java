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

package org.openlmis.stockmanagement.web;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.openlmis.stockmanagement.testutils.StockCardDtoDataBuilder.createStockCardDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.openlmis.stockmanagement.dto.StockCardDto;
import org.openlmis.stockmanagement.exception.PermissionMessageException;
import org.openlmis.stockmanagement.service.PermissionService;
import org.openlmis.stockmanagement.service.StockCardService;
import org.openlmis.stockmanagement.service.StockCardSummariesService;
import org.openlmis.stockmanagement.testutils.StockCardDtoDataBuilder;
import org.openlmis.stockmanagement.util.Message;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.ResultActions;
import java.util.UUID;

//the name of this controller test is intentional wrong: cardz insteads of cards
//because there is a problem with "spring security test" that seems to be relates
//with test execution order, naming it cardz will put it behind and solve the problem
//not sure what the root cause is yet
public class StockCardControllerIntegrationTest extends BaseWebTest {

  private static final String API_STOCK_CARDS = "/api/stockCards/";
  private static final String API_STOCK_CARD_SUMMARIES = "/api/stockCardSummaries";

  @MockBean
  private StockCardService stockCardService;

  @MockBean
  private StockCardSummariesService stockCardSummariesService;

  @MockBean
  private PermissionService permissionService;

  @Test
  public void should404WhenStockCardNotFoundById() throws Exception {
    //given
    when(stockCardService.findStockCardById(any(UUID.class))).thenReturn(null);

    //when
    ResultActions resultActions = mvc.perform(
        get(API_STOCK_CARDS + UUID.randomUUID().toString())
            .param(ACCESS_TOKEN, ACCESS_TOKEN_VALUE));

    //then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  public void shouldGetStockCardById() throws Exception {
    //given
    UUID stockCardId = UUID.randomUUID();
    StockCardDto stockCardDto = createStockCardDto();

    when(stockCardService.findStockCardById(stockCardId)).thenReturn(stockCardDto);

    //when
    ResultActions resultActions = mvc.perform(
        get(API_STOCK_CARDS + stockCardId.toString())
            .param(ACCESS_TOKEN, ACCESS_TOKEN_VALUE));

    //then
    resultActions
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{"
                + "'stockOnHand':1,"
                + "'facility':{'name':'HC01'},"
                + "'program':{'name':'HIV'},"
                + "'orderable':{'productCode':'ABC01'},"
                + "'lineItems':["
                + "{'occurredDate':'2017-02-13',"
                + "'source':{'name':'HF1'},"
                + "'destination':null,"
                + "'reason':{'name':'Transfer In','reasonCategory':'ADJUSTMENT',"
                + "'reasonType':'CREDIT'},"
                + "'quantity':1, 'stockOnHand':1}]}"));
  }

  @Test
  public void shouldReturn403WhenUserDoesNotHavePermissionToViewCardSummaries()
      throws Exception {
    //given
    UUID programId = UUID.randomUUID();
    UUID facilityId = UUID.randomUUID();
    doThrow(new
        PermissionMessageException(new Message("no permission"))).when(permissionService)
        .canViewStockCard(programId, facilityId);

    //when
    ResultActions resultActions = mvc.perform(
        get(API_STOCK_CARD_SUMMARIES)
            .param(ACCESS_TOKEN, ACCESS_TOKEN_VALUE)
            .param("program", programId.toString())
            .param("facility", facilityId.toString()));

    //then
    resultActions.andExpect(status().isForbidden());
  }

  @Test
  public void shouldGetPagedStockCardSummariesWhenPermissionIsGranted() throws Exception {
    //given
    UUID programId = UUID.randomUUID();
    UUID facilityId = UUID.randomUUID();

    PageRequest pageable = new PageRequest(0, 20);
    when(stockCardSummariesService
        .findStockCards(programId, facilityId, pageable))
        .thenReturn(new PageImpl<>(singletonList(StockCardDtoDataBuilder.createStockCardDto())));

    ResultActions resultActions = mvc.perform(
        get(API_STOCK_CARD_SUMMARIES)
            .param(ACCESS_TOKEN, ACCESS_TOKEN_VALUE)
            .param("page", "0")
            .param("size", "20")
            .param("program", programId.toString())
            .param("facility", facilityId.toString()));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.content", hasSize(1)));
  }
}
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

package org.openlmis.stockmanagement.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openlmis.stockmanagement.domain.physicalinventory.PhysicalInventory;
import org.openlmis.stockmanagement.domain.physicalinventory.PhysicalInventoryLineItem;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PhysicalInventoryDtoTest {
  @Test
  public void should_convert_into_stock_event_dtos() throws Exception {
    //given
    PhysicalInventoryDto piDto = createInventoryDto();

    //when
    List<StockEventDto> eventDtos = piDto.toEventDtos();

    //then
    assertThat(eventDtos.size(), is(1));

    StockEventDto eventDto = eventDtos.get(0);
    assertThat(eventDto.getProgramId(), is(piDto.getProgramId()));
    assertThat(eventDto.getFacilityId(), is(piDto.getFacilityId()));
    assertThat(eventDto.getSignature(), is(piDto.getSignature()));
    assertThat(eventDto.getDocumentNumber(), is(piDto.getDocumentNumber()));
    assertThat(eventDto.getOccurredDate(), is(piDto.getOccurredDate()));

    PhysicalInventoryLineItemDto piLineItemDto = piDto.getLineItems().get(0);
    assertThat(eventDto.getOrderableId(), is(piLineItemDto.getOrderable().getId()));
    assertThat(eventDto.getQuantity(), is(piLineItemDto.getQuantity()));
  }

  @Test
  public void should_convert_into_inventory_jpa_model_for_submit() throws Exception {
    //given
    PhysicalInventoryDto piDto = createInventoryDto();

    //when
    PhysicalInventory inventory = piDto.toPhysicalInventoryForSubmit();

    //then
    assertThat(inventory.getProgramId(), is(piDto.getProgramId()));
    assertThat(inventory.getFacilityId(), is(piDto.getFacilityId()));
    assertThat(inventory.getOccurredDate(), is(piDto.getOccurredDate()));
    assertThat(inventory.getSignature(), is(piDto.getSignature()));
    assertThat(inventory.getDocumentNumber(), is(piDto.getDocumentNumber()));
    assertThat(inventory.getIsDraft(), is(false));
    assertThat(inventory.getLineItems(), nullValue());
  }

  @Test
  public void should_convert_into_inventory_jpa_model_for_draft() throws Exception {
    //given
    PhysicalInventoryDto piDto = createInventoryDto();

    //when
    PhysicalInventory inventory = piDto.toPhysicalInventoryForDraft();

    //then
    assertThat(inventory.getProgramId(), is(piDto.getProgramId()));
    assertThat(inventory.getFacilityId(), is(piDto.getFacilityId()));
    assertThat(inventory.getOccurredDate(), is(piDto.getOccurredDate()));
    assertThat(inventory.getSignature(), is(piDto.getSignature()));
    assertThat(inventory.getDocumentNumber(), is(piDto.getDocumentNumber()));

    assertThat(inventory.getIsDraft(), is(true));

    assertThat(inventory.getLineItems().size(), is(1));
    PhysicalInventoryLineItemDto piLineItemDto = piDto.getLineItems().get(0);
    PhysicalInventoryLineItem piLineItem = inventory.getLineItems().get(0);
    assertThat(piLineItem.getQuantity(), is(piLineItemDto.getQuantity()));
    assertThat(piLineItem.getOrderableId(), is(piLineItemDto.getOrderable().getId()));
  }

  @Test
  public void should_create_dto_from_jpa_model() throws Exception {
    //given
    PhysicalInventory inventory = createInventoryDto().toPhysicalInventoryForDraft();

    //when
    PhysicalInventoryDto dto = PhysicalInventoryDto.from(inventory);

    //then
    assertThat(dto.getProgramId(), is(inventory.getProgramId()));
    assertThat(dto.getFacilityId(), is(inventory.getFacilityId()));
    assertThat(dto.getOccurredDate(), is(inventory.getOccurredDate()));
    assertThat(dto.getDocumentNumber(), is(inventory.getDocumentNumber()));
    assertThat(dto.getSignature(), is(inventory.getSignature()));
    assertThat(dto.getIsStarter(), is(false));

    assertThat(dto.getLineItems().size(), is(1));
  }

  private PhysicalInventoryDto createInventoryDto() {
    PhysicalInventoryDto piDto = new PhysicalInventoryDto();
    piDto.setOccurredDate(ZonedDateTime.now());
    piDto.setFacilityId(UUID.randomUUID());
    piDto.setProgramId(UUID.randomUUID());
    piDto.setSignature("test");
    piDto.setDocumentNumber(null);

    PhysicalInventoryLineItemDto piLineItemDto1 = new PhysicalInventoryLineItemDto();
    piLineItemDto1.setOrderable(OrderableDto.builder().id(UUID.randomUUID()).build());
    piLineItemDto1.setQuantity(123);

    piDto.setLineItems(Arrays.asList(piLineItemDto1));
    return piDto;
  }
}
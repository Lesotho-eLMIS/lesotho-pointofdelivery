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

package org.openlmis.stockmanagement.validators;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.stockmanagement.domain.event.StockEventLineItem;
import org.openlmis.stockmanagement.domain.physicalinventory.PhysicalInventoryLineItemAdjustment;
import org.openlmis.stockmanagement.domain.reason.StockCardLineItemReason;
import org.openlmis.stockmanagement.domain.reason.ValidReasonAssignment;
import org.openlmis.stockmanagement.dto.StockEventDto;
import org.openlmis.stockmanagement.dto.referencedata.FacilityDto;
import org.openlmis.stockmanagement.dto.referencedata.FacilityTypeDto;
import org.openlmis.stockmanagement.exception.ValidationMessageException;
import org.openlmis.stockmanagement.repository.ValidReasonAssignmentRepository;

import java.util.Collections;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class PhysicalInventoryAdjustmentReasonsValidatorTest extends BaseValidatorTest  {

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  private ValidReasonAssignmentRepository validReasonRepository;

  @InjectMocks
  private PhysicalInventoryAdjustmentReasonsValidator validator;
  private UUID reasonId = UUID.randomUUID();
  private FacilityDto facility = mock(FacilityDto.class);
  private UUID facilityTypeId = UUID.randomUUID();
  private StockEventDto stockEventDto = spy(new StockEventDto());

  @Before
  public void setUp() throws Exception {
    super.setUp();
    setContext(stockEventDto);

    when(validReasonRepository
        .findByProgramIdAndFacilityTypeIdAndReasonId(
            any(UUID.class), any(UUID.class), any(UUID.class)))
        .thenReturn(new ValidReasonAssignment());
    stubFacilityType();

    when(stockEventDto.isPhysicalInventory()).thenReturn(true);
  }

  @Test
  public void shouldByPassWhenStockEventIsNotPhysicalInventory() {
    when(stockEventDto.isPhysicalInventory()).thenReturn(false);

    validator.validate(stockEventDto);
  }

  @Test
  public void shouldPassWhenReasonIsValid() {
    stockEventDto.setProgramId(UUID.randomUUID());
    stockEventDto.setFacilityId(UUID.randomUUID());
    stockEventDto.setLineItems(
        Collections.singletonList(
            generateLineItem(5, generateReason())));

    validator.validate(stockEventDto);

    verify(validReasonRepository)
        .findByProgramIdAndFacilityTypeIdAndReasonId(
            stockEventDto.getProgramId(), facilityTypeId, reasonId);
  }

  @Test
  public void shouldPassWhenNoStockAdjustments() {
    stockEventDto.setLineItems(
        Collections.singletonList(new StockEventLineItem()));

    validator.validate(stockEventDto);
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldNotPassWhenReasonIsNotValid() {
    stockEventDto.setLineItems(
        Collections.singletonList(
            generateLineItem(5, generateReason())));

    when(validReasonRepository
        .findByProgramIdAndFacilityTypeIdAndReasonId(
            any(UUID.class), any(UUID.class), any(UUID.class)))
        .thenReturn(null);

    validator.validate(stockEventDto);
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldNotPassWhenNoReason() {
    stockEventDto.setLineItems(
        Collections.singletonList(
            generateLineItem(5, null)));

    validator.validate(stockEventDto);
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldNotPassWhenNoQuantity() {
    stockEventDto.setLineItems(
        Collections.singletonList(
            generateLineItem(null, generateReason())));

    validator.validate(stockEventDto);
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldNotPassWhenFacilityIdIsInvalid() {
    stockEventDto.setLineItems(
        Collections.singletonList(
            generateLineItem(5, generateReason())));

    when(facilityService.findOne(stockEventDto.getFacilityId())).thenReturn(null);

    setContext(stockEventDto);

    validator.validate(stockEventDto);
  }

  private void stubFacilityType() {
    when(facilityService.findOne(stockEventDto.getFacilityId())).thenReturn(facility);

    FacilityTypeDto facilityType = new FacilityTypeDto();
    facilityType.setId(facilityTypeId);
    when(facility.getType()).thenReturn(facilityType);
  }

  private StockCardLineItemReason generateReason() {
    StockCardLineItemReason stockCardLineItemReason = new StockCardLineItemReason();
    stockCardLineItemReason.setId(reasonId);
    return stockCardLineItemReason;
  }

  private StockEventLineItem generateLineItem(Integer quantity, StockCardLineItemReason reason) {
    PhysicalInventoryLineItemAdjustment adjustment = PhysicalInventoryLineItemAdjustment.builder()
        .quantity(quantity)
        .reason(reason)
        .build();

    return StockEventLineItem.builder()
        .stockAdjustments(Collections.singletonList(adjustment))
        .build();
  }

}

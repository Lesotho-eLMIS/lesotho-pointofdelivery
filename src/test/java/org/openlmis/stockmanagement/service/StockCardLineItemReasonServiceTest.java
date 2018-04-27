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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.stockmanagement.domain.reason.StockCardLineItemReason;
import org.openlmis.stockmanagement.exception.ValidationMessageException;
import org.openlmis.stockmanagement.repository.StockCardLineItemReasonRepository;
import org.openlmis.stockmanagement.testutils.StockCardLineItemReasonDataBuilder;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class StockCardLineItemReasonServiceTest {
  @InjectMocks
  private StockCardLineItemReasonService reasonService;

  @Mock
  private StockCardLineItemReasonRepository reasonRepository;

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowValidationExceptionWithUnavailableReasonDto() throws Exception {
    //given
    StockCardLineItemReason reason = new StockCardLineItemReason();

    //when
    reasonService.saveOrUpdate(reason);
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowValidationExceptionWhenReasonIdNotFoundInDb() throws Exception {
    //given
    UUID reasonId = UUID.randomUUID();
    when(reasonRepository.findOne(reasonId)).thenReturn(null);
    reasonService.checkUpdateReasonIdExists(reasonId);
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowExceptionWhenCreatingReasonNameIsDuplicateWithOtherOne()
      throws Exception {
    //given
    StockCardLineItemReason creatingReason = new StockCardLineItemReasonDataBuilder()
        .withoutId()
        .build();
    StockCardLineItemReason existingReason = new StockCardLineItemReasonDataBuilder().build();
    when(reasonRepository.findByName(creatingReason.getName())).thenReturn(existingReason);

    //when
    reasonService.saveOrUpdate(creatingReason);
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowExceptionWhenUpdatingReasonNameIsDuplicateWithOtherOne()
      throws Exception {
    //given
    StockCardLineItemReason updatingReason = new StockCardLineItemReasonDataBuilder().build();
    StockCardLineItemReason existingReason = new StockCardLineItemReasonDataBuilder().build();
    when(reasonRepository.findByName(updatingReason.getName())).thenReturn(existingReason);

    //when
    reasonService.saveOrUpdate(updatingReason);
  }

  @Test
  public void shouldSaveReasonWhenPassNullValueValidation() throws Exception {
    //when
    StockCardLineItemReason reason = new StockCardLineItemReasonDataBuilder().withoutId().build();
    reasonService.saveOrUpdate(reason);

    //then
    verify(reasonRepository, times(1)).save(reason);
  }

}

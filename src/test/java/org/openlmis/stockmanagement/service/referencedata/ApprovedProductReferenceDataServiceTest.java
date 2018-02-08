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

package org.openlmis.stockmanagement.service.referencedata;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.stockmanagement.dto.referencedata.ApprovedProductDto;
import org.openlmis.stockmanagement.dto.referencedata.OrderableDto;
import org.openlmis.stockmanagement.testutils.ApprovedProductDtoDataBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApprovedProductReferenceDataServiceTest {

  private ApprovedProductReferenceDataService spy;

  @Before
  public void setUp() {
    spy = spy(new ApprovedProductReferenceDataService());
  }

  @Test
  public void shouldReturnMapOfOrderableFulfills() {
    UUID programId = randomUUID();
    Collection<UUID> orderableIds = asList(randomUUID(), randomUUID());
    Pageable pageable = new PageRequest(0, 10);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("programId", programId);
    parameters.put("size", pageable.getPageSize());
    parameters.put("page", pageable.getPageNumber());
    parameters.put("orderableId", orderableIds);

    Page<ApprovedProductDto> productPage = new PageImpl<>(
        Collections.singletonList(new ApprovedProductDtoDataBuilder().build()), pageable, 1);

    UUID facilityId = randomUUID();

    doReturn(productPage)
        .when(spy)
        .getPage(facilityId + "/approvedProducts", parameters);

    Page<OrderableDto> result = spy
        .getApprovedProducts(facilityId, programId, orderableIds, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(productPage.getContent().get(0).getOrderable(), result.getContent().get(0));
  }
}

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


package org.openlmis.pointofdelivery.dto;

import static java.time.ZonedDateTime.now;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.openlmis.pointofdelivery.domain.event.PointOfDeliveryEvent;
import org.openlmis.pointofdelivery.dto.requisition.RejectionReasonDto;
import org.openlmis.pointofdelivery.service.requisition.RejectionReasonService;
import org.openlmis.pointofdelivery.util.PointOfDeliveryEventProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointOfDeliveryEventDto {

  private static final Logger LOGGER = LoggerFactory.getLogger(PointOfDeliveryEventDto.class);
  private static RejectionReasonService rejectionReasonService = new RejectionReasonService();

  private UUID id;

  private UUID sourceId;
  private String sourceFreeText;

  private UUID destinationId;
  private String destinationFreeText;

  private UUID receivedByUserId;
  private String receivedByUserNames;

  private ZonedDateTime receivingDate;

  private String referenceNumber;

  private LocalDate packingDate;

  private String packedBy;

  private Integer numberOfCartons;

  private Integer numberOfContainers;

  private Integer numberOfCartonsRejected;

  private Integer numberOfContainersRejected;

  private String remarks;

  //private List<UUID> rejectionReasonIds;
  private List<RejectionReasonDto> rejectionReasons;

  private PointOfDeliveryEventProcessContext context;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */
  public PointOfDeliveryEvent toPointOfDeliveryEvent() {
    Set<UUID> rejectionReasonIds = new HashSet<>();
    for (RejectionReasonDto rejectionReasonDto : rejectionReasons) {
      rejectionReasonIds.add(rejectionReasonDto.getId());
    }
    PointOfDeliveryEvent pointOfDeliveryEvent = new PointOfDeliveryEvent(
        sourceId, sourceFreeText, destinationId, destinationFreeText, 
        context.getCurrentUserId(), context.getCurrentUserNames(), now(), 
        referenceNumber, packingDate, packedBy, numberOfCartons, 
        numberOfContainers, numberOfCartonsRejected,
        numberOfContainersRejected, remarks, rejectionReasonIds);
    return pointOfDeliveryEvent;
  }

  public boolean hasSourceId() {
    return this.sourceId != null;
  }

  public boolean hasDestinationId() {
    return this.destinationId != null;
  }

}

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

package org.openlmis.pointofdelivery.service.requisition;

import java.util.UUID;
import org.openlmis.pointofdelivery.dto.requisition.RejectionReasonDto;
import org.springframework.stereotype.Service;

@Service
public class RejectionReasonService extends BaseRequisitionService<RejectionReasonDto> {  


  @Override
  protected String getUrl() {
    return "/api/rejectionReasons/";
  }
  
  @Override
  protected Class<RejectionReasonDto> getResultClass() {
    return RejectionReasonDto.class;
  }
  
  @Override
  protected Class<RejectionReasonDto[]> getArrayResultClass() {
    return RejectionReasonDto[].class;
  }

  /**
   * Return one object from service.
   *
   * @param id UUID of requesting object.
   * @return Requesting reference data object.
   */
  public RejectionReasonDto getRejectionReason(UUID id) {
    return findOne(id);
  }

  // private RejectionReasonDto getResult(String resourceUrl, RequestParameters parameters) {
  //   String url = getServiceUrl() + getUrl() + resourceUrl;

  //   ResponseEntity<RejectionReasonDto> response = runWithTokenRetry(() -> restTemplate.exchange(
  //       RequestHelper.createUri(url, parameters),
  //       HttpMethod.GET,
  //       RequestHelper.createEntity(
  //             RequestHeaders.init().setAuth(authService.obtainAccessToken())),
  //       RejectionReasonDto.class
  //   ));

  //   return response.getBody();
  // }

  // /**
  //  * Return one object from service.
  //  *
  //  * @param resourceUrl UUID of requesting object.
  //  *  @param parameters UUID of requesting object.
  //  */
  // public void fetch(String resourceUrl,
  //      RequestParameters parameters) {
  //   String url = getServiceUrl() + getUrl() + StringUtils.defaultIfBlank(resourceUrl, "");
  //   //String url = "http://192.168.8.104/api/rejectionReasons/" + resourceUrl;
  //   RequestParameters params = RequestParameters
  //       .init()
  //       .setAll(parameters);

  //   try {
  //     LOGGER.error(runWithTokenRetry(() -> restTemplate.exchange(
  //         RequestHelper.createUri(url, params),
  //         HttpMethod.GET,
  //         RequestHelper.createEntity(
  //             RequestHeaders.init().setAuth(authService.obtainAccessToken())),
  //         RejectionReasonDto.class)).getBody().toString());
  //   } catch (HttpStatusCodeException ex) {
  //     // rest template will handle 404 as an exception, instead of returning null
  //     if (HttpStatus.NOT_FOUND == ex.getStatusCode()) {
  //       logger.warn(
  //           "{} matching params does not exist. Params: {}",
  //           getResultClass().getSimpleName(), parameters
  //       );

  //       LOGGER.error("null value");
  //     } else {
  //       throw buildDataRetrievalException(ex);
  //     }
  //   }
  // }

  // private DataRetrievalException buildDataRetrievalException(HttpStatusCodeException ex) {
  //   return new DataRetrievalException(getResultClass().getSimpleName(), ex);
  // }

  // /**
  //  * Retrieves a list of rejection reasons based on the provided rejection reason IDs.
  //  *
  //  * @param rejectionReasonIds The list of rejection reason IDs to retrieve.
  //  * @return A list of RejectionReasonDto objects representing rejection reasons.
  //  */
  // public List<RejectionReasonDto> getRejectionReasons(Set<UUID> rejectionReasonIds) {
  //   String rejectionReasonsEndpoint = "http://192.168.8.104" + "/api/rejectionReasons";

  //   List<RejectionReasonDto> rejectionReasonList = new ArrayList<>();

  //   for (UUID rejectionReasonId : rejectionReasonIds) {
  //     String individualRejectionReasonEndpoint = 
  //         rejectionReasonsEndpoint + "/" + rejectionReasonId;

  //     ResponseEntity<RejectionReasonDto> responseEntity = restTemplate.exchange(
  //         individualRejectionReasonEndpoint,
  //         HttpMethod.GET,
  //         RequestHelper.createEntity(
  //             RequestHeaders.init().setAuth(authService.obtainAccessToken())),
  //         RejectionReasonDto.class
  //     );

  //     rejectionReasonList.add(responseEntity.getBody());
  //   }

  //   return rejectionReasonList;
  // }

}

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

import java.util.Collection;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.openlmis.stockmanagement.exception.ValidationMessageException;
import org.openlmis.stockmanagement.util.UuidUtil;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor
public class SearchParams {

  private static final String PAGE = "page";
  private static final String SIZE = "size";
  private static final String SORT = "sort";
  private static final String ACCESS_TOKEN = "access_token";

  private MultiValueMap<String, String> params;

  /**
   * Constructs new SearchParams object from {@code MultiValueMap}.
   */
  public SearchParams(MultiValueMap<String, String> queryMap) {
    if (queryMap != null) {
      params = new LinkedMultiValueMap<>(queryMap);
      params.remove(PAGE);
      params.remove(SIZE);
      params.remove(SORT);
      params.remove(ACCESS_TOKEN);
    } else {
      params = new LinkedMultiValueMap<>();
    }
  }

  public boolean containsKey(String key) {
    return params.containsKey(key);
  }

  public String getFirst(String key) {
    return params.getFirst(key);
  }

  public Collection<String> get(String key) {
    return params.get(key);
  }

  public LinkedMultiValueMap<String, String> asMultiValueMap() {
    return new LinkedMultiValueMap<>(params);
  }

  public Collection<String> keySet() {
    return params.keySet();
  }

  public boolean isEmpty() {
    return MapUtils.isEmpty(params);
  }

  public UUID getUuid(String value) {
    return UuidUtil.fromString(value)
        .orElseThrow(() -> new ValidationMessageException(""));
  }
}

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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.openlmis.stockmanagement.i18n.MessageKeys.ERROR_NO_FOLLOWING_PERMISSION;
import static org.openlmis.stockmanagement.service.OAuth2AuthenticationDataBuilder.SERVICE_CLIENT_ID;
import static org.openlmis.stockmanagement.service.PermissionService.REASONS_MANAGE;
import static org.openlmis.stockmanagement.service.PermissionService.STOCK_ADJUST;
import static org.openlmis.stockmanagement.service.PermissionService.STOCK_CARDS_VIEW;
import static org.openlmis.stockmanagement.service.PermissionService.STOCK_CARD_TEMPLATES_MANAGE;
import static org.openlmis.stockmanagement.service.PermissionService.STOCK_DESTINATIONS_MANAGE;
import static org.openlmis.stockmanagement.service.PermissionService.STOCK_INVENTORIES_EDIT;
import static org.openlmis.stockmanagement.service.PermissionService.STOCK_ORGANIZATIONS_MANAGE;
import static org.openlmis.stockmanagement.service.PermissionService.STOCK_SOURCES_MANAGE;
import static org.openlmis.stockmanagement.service.PermissionService.SYSTEM_SETTINGS_MANAGE;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.stockmanagement.dto.referencedata.ResultDto;
import org.openlmis.stockmanagement.dto.referencedata.RightDto;
import org.openlmis.stockmanagement.dto.referencedata.UserDto;
import org.openlmis.stockmanagement.exception.PermissionMessageException;
import org.openlmis.stockmanagement.service.referencedata.UserReferenceDataService;
import org.openlmis.stockmanagement.util.AuthenticationHelper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.UUID;

@SuppressWarnings("PMD.TooManyMethods")
@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @Mock
  private AuthenticationHelper authenticationHelper;

  @InjectMocks
  private PermissionService permissionService;

  @Mock
  private UserDto user;

  @Mock
  private RightDto right;

  private UUID userId = UUID.randomUUID();
  private UUID programId = UUID.randomUUID();
  private UUID facilityId = UUID.randomUUID();
  private UUID rightId = UUID.randomUUID();
  private SecurityContext securityContext;
  private OAuth2Authentication serviceClient;
  private OAuth2Authentication userClient;

  @Before
  public void setUp() {
    userClient = new OAuth2AuthenticationDataBuilder().buildUserAuthentication();
    serviceClient = new OAuth2AuthenticationDataBuilder().buildServiceAuthentication();

    when(user.getId()).thenReturn(userId);

    when(right.getId()).thenReturn(rightId);

    when(authenticationHelper.getCurrentUser()).thenReturn(user);

    when(authenticationHelper.getRight(anyString()))
        .thenReturn(right);

    securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    when(securityContext.getAuthentication()).thenReturn(userClient);

    ReflectionTestUtils.setField(permissionService, "serviceTokenClientId", SERVICE_CLIENT_ID);
  }

  @Test
  public void canCreateStockCardTemplates() throws Exception {
    hasRight(rightId, true);

    permissionService.canCreateStockCardTemplate();

    verifyUserRight(STOCK_CARD_TEMPLATES_MANAGE, rightId);
  }

  @Test
  public void cannotCreateStockCardTemplates() throws Exception {
    expectException(STOCK_CARD_TEMPLATES_MANAGE);

    permissionService.canCreateStockCardTemplate();
  }

  @Test
  public void canEditPhysicalInventory() throws Exception {
    hasRight(rightId, programId, facilityId, true);

    permissionService.canEditPhysicalInventory(programId, facilityId);

    verifyUserRightWithProgramFacility(STOCK_INVENTORIES_EDIT, rightId);
  }

  @Test
  public void cannotEditPhysicalInventory() throws Exception {
    expectException(STOCK_INVENTORIES_EDIT);
    hasRight(rightId, programId, facilityId, false);

    permissionService.canEditPhysicalInventory(programId, facilityId);
  }

  @Test
  public void canAdjustStock() throws Exception {
    hasRight(rightId, programId, facilityId, true);

    permissionService.canAdjustStock(programId, facilityId);

    verifyUserRightWithProgramFacility(STOCK_ADJUST, rightId);
  }

  @Test
  public void cannotAdjustStock() throws Exception {
    expectException(STOCK_ADJUST);
    hasRight(rightId, programId, facilityId, false);

    permissionService.canAdjustStock(programId, facilityId);
  }

  @Test
  public void canViewStockCard() throws Exception {
    hasRight(rightId, programId, facilityId, true);

    permissionService.canViewStockCard(programId, facilityId);

    verifyUserRightWithProgramFacility(STOCK_CARDS_VIEW, rightId);
  }

  @Test
  public void cannotViewStockCard() throws Exception {
    expectException(STOCK_CARDS_VIEW);
    hasRight(rightId, programId, facilityId, false);

    permissionService.canViewStockCard(programId, facilityId);
  }

  @Test
  public void canManageStockSources() throws Exception {
    hasRight(rightId, true);

    permissionService.canManageStockSources();

    verifyUserRight(STOCK_SOURCES_MANAGE, rightId);
  }

  @Test
  public void cannotManageStockSources() throws Exception {
    expectException(STOCK_SOURCES_MANAGE);
    hasRight(rightId, false);

    permissionService.canManageStockSources();
  }

  @Test
  public void canManageStockDestinations() throws Exception {
    hasRight(rightId, true);

    permissionService.canManageStockDestinations();

    verifyUserRight(STOCK_DESTINATIONS_MANAGE, rightId);
  }

  @Test
  public void cannotManageStockDestinations() throws Exception {
    expectException(STOCK_DESTINATIONS_MANAGE);
    hasRight(rightId, false);

    permissionService.canManageStockDestinations();
  }

  @Test
  public void canManageReasons() throws Exception {
    hasRight(rightId, true);

    permissionService.canManageReasons();

    verifyUserRight(REASONS_MANAGE, rightId);
  }

  @Test
  public void cannotManageReasons() throws Exception {
    expectException(REASONS_MANAGE);
    hasRight(rightId, false);

    permissionService.canManageReasons();
  }

  @Test
  public void canManageOrganizations() throws Exception {
    hasRight(rightId, true);

    permissionService.canManageOrganizations();

    verifyUserRight(STOCK_ORGANIZATIONS_MANAGE, rightId);
  }

  @Test
  public void cannotManageOrganizations() throws Exception {
    expectException(STOCK_ORGANIZATIONS_MANAGE);
    hasRight(rightId, false);

    permissionService.canManageOrganizations();
  }

  @Test
  public void canManageSystemSettings() throws Exception {
    hasRight(rightId, true);

    permissionService.canManageSystemSettings();

    verifyUserRight(SYSTEM_SETTINGS_MANAGE, rightId);
  }

  @Test
  public void cannotManageSystemSettings() throws Exception {
    expectException(SYSTEM_SETTINGS_MANAGE);
    hasRight(rightId, false);

    permissionService.canManageSystemSettings();
  }

  @Test
  public void adminCanViewAllValidReasonsSourcesDestinations() throws Exception {
    //given
    hasRight(rightId, true);

    //when
    permissionService.canViewValidReasons(UUID.randomUUID(), UUID.randomUUID());
    permissionService.canViewValidSources(UUID.randomUUID(), UUID.randomUUID());
    permissionService.canViewValidDestinations(UUID.randomUUID(), UUID.randomUUID());
  }

  @Test
  public void serviceTokensCanDoAnything() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(serviceClient);

    permissionService.canCreateStockCardTemplate();
    permissionService.canEditPhysicalInventory(programId, facilityId);
    permissionService.canAdjustStock(programId, facilityId);
    permissionService.canViewStockCard(programId, facilityId);
    permissionService.canManageStockSources();
    permissionService.canManageStockDestinations();
    permissionService.canManageReasons();
    permissionService.canManageOrganizations();
    permissionService.canManageSystemSettings();
  }

  private void hasRight(UUID rightId, boolean hasRight) {
    hasRight(rightId, null, null, hasRight);
  }

  private void hasRight(UUID rightId, UUID program, UUID facility, boolean hasRight) {
    ResultDto<Boolean> resultDto = new ResultDto<>(hasRight);
    when(userReferenceDataService
        .hasRight(userId, rightId, program, facility, null)
    ).thenReturn(resultDto);
  }

  private void expectException(String rightName) {
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(ERROR_NO_FOLLOWING_PERMISSION + ": " + rightName);
  }

  private void verifyUserRight(String rightName, UUID rightId) {
    InOrder order = inOrder(authenticationHelper, userReferenceDataService);
    verifyRight(order, rightName, rightId);
  }

  private void verifyUserRightWithProgramFacility(String rightName, UUID rightId) {
    InOrder order = inOrder(authenticationHelper, userReferenceDataService);
    verifyRight(order, rightName, rightId, programId, facilityId);
  }

  private void verifyRight(InOrder order, String rightName, UUID rightId) {
    verifyRight(order, rightName, rightId, null, null);
  }

  private void verifyRight(InOrder order, String rightName, UUID rightId,
                           UUID program, UUID facility) {
    order.verify(authenticationHelper, times(1)).getCurrentUser();
    order.verify(authenticationHelper, times(1)).getRight(rightName);
    order.verify(userReferenceDataService, times(1)).hasRight(userId, rightId, program, facility,
        null);
  }

}

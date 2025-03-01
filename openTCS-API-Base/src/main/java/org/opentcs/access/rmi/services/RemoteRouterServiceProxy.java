/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.access.rmi.services;

import java.rmi.RemoteException;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.components.kernel.services.RouterService;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Path;

/**
 * The default implementation of the router service.
 * Delegates method invocations to the corresponding remote service.
 */
class RemoteRouterServiceProxy
    extends AbstractRemoteServiceProxy<RemoteRouterService>
    implements RouterService {

  /**
   * Creates a new instance.
   */
  RemoteRouterServiceProxy() {
  }

  @Override
  public void updatePathLock(TCSObjectReference<Path> ref, boolean locked)
      throws ObjectUnknownException, KernelRuntimeException {
    checkServiceAvailability();

    try {
      getRemoteService().updatePathLock(getClientId(), ref, locked);
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }

  @Override
  public void updateRoutingTopology()
      throws KernelRuntimeException {
    checkServiceAvailability();

    try {
      getRemoteService().updateRoutingTopology(getClientId());
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }
}

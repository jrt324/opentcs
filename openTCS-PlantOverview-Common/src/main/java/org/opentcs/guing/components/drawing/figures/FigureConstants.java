/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.components.drawing.figures;

import org.jhotdraw.draw.AttributeKey;
import org.opentcs.guing.components.drawing.course.Origin;
import org.opentcs.guing.model.ModelComponent;

/**
 * Allgemeine Konstanten, die insbesondere Figures betreffen.
 *
 * @author Sebastian Naumann (ifak e.V. Magdeburg)
 */
public interface FigureConstants {

  /**
   * Über diesen Schlüssel greifen Figures auf ihr Model zu.
   */
  AttributeKey<ModelComponent> MODEL = new AttributeKey<>("Model", ModelComponent.class);
  /**
   * Über dieses Attribut erhalten Figures Zugriff auf den Referenzpunkt.
   */
  AttributeKey<Origin> ORIGIN = new AttributeKey<>("Origin", Origin.class);
}

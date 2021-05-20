/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.application.action.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import org.opentcs.guing.application.GuiManager;
import org.opentcs.guing.model.elements.LocationTypeModel;
import static org.opentcs.guing.util.I18nPlantOverviewModeling.TOOLBAR_PATH;
import org.opentcs.guing.util.ImageDirectory;
import org.opentcs.thirdparty.jhotdraw.util.ResourceBundleUtil;

/**
 * An action to trigger the creation of a location type.
 *
 * @author Heinz Huber (Fraunhofer IML)
 */
public class CreateLocationTypeAction
    extends AbstractAction {

  /**
   * This action class's ID.
   */
  public static final String ID = "openTCS.createLocationType";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(TOOLBAR_PATH);
  /**
   * The GUI manager instance we're working with.
   */
  private final GuiManager guiManager;

  /**
   * Creates a new instance.
   *
   * @param guiManager The GUI manager instance we're working with.
   */
  public CreateLocationTypeAction(GuiManager guiManager) {
    this.guiManager = guiManager;

    putValue(NAME, BUNDLE.getString("createLocationTypeAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("createLocationTypeAction.shortDescription"));

    ImageIcon icon = ImageDirectory.getImageIcon("/toolbar/locationType.22.png");
    putValue(SMALL_ICON, icon);
    putValue(LARGE_ICON_KEY, icon);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    guiManager.createModelComponent(LocationTypeModel.class);
  }
}

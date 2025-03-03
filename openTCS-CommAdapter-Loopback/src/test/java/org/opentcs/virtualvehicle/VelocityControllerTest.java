/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.virtualvehicle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.opentcs.data.model.Vehicle;

/**
 */
public class VelocityControllerTest {

  private static final int MAX_DECEL = -1000;
  private static final int MAX_ACCEL = 1000;
  private static final int MAX_REV_VELO = -500;
  private static final int MAX_FWD_VELO = 500;

  private static final long WAY_LENGTH = 5000;
  private static final int MAX_VELO = 500;
  private static final String POINT_NAME = "Destination_Point";

  @Test
  public void testThatInitialControllerHasNoWayEntries() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    assertFalse(controller.hasWayEntries());
  }

  @Test
  public void testThatInitialControllerHasNoCurrentWayEntry() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    assertNull(controller.getCurrentWayEntry());
  }

  @Test
  public void testAddNullVelocityListenerShouldThrowException() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    assertThrows(NullPointerException.class, () -> controller.addVelocityListener(null));
  }

  @Test
  public void testAddNullWayEntryShouldThrowException() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    assertThrows(NullPointerException.class, () -> controller.addWayEntry(null));
  }

  @Test
  public void testNegativeAdvanceTimeShouldThrowException() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    assertThrows(IllegalArgumentException.class, () -> controller.advanceTime(-1));
  }

  @Test
  public void testAdvanceTimeAdvancesTime() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    final int timeAdvancement = 5;
    long timeBefore = controller.getCurrentTime();
    controller.advanceTime(timeAdvancement);
    long timeAfter = controller.getCurrentTime();
    assertEquals(timeAfter, timeBefore + timeAdvancement);
  }

  @Test
  public void testPausedVehicleDoesNotChangePosition() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    VelocityController.WayEntry wayEntry
        = new VelocityController.WayEntry(WAY_LENGTH, MAX_VELO,
                                          POINT_NAME, Vehicle.Orientation.FORWARD);
    controller.addWayEntry(wayEntry);
    controller.setVehiclePaused(true);
    int posBefore = controller.getCurrentPosition();
    controller.advanceTime(5);
    int posAfter = controller.getCurrentPosition();
    assertEquals(posBefore, posAfter);
  }

  @Test
  public void testVelocityListenerGetsNotifiedOnAdvanceTime() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    VelocityListener veloListener = mock(VelocityListener.class);
    controller.addVelocityListener(veloListener);
    controller.advanceTime(5);
    verify(veloListener, times(1)).addVelocityValue(anyInt());
  }

  @Test
  public void testCurrentWayEntryIsFirstEntryInQueue() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, MAX_ACCEL,
                                 MAX_REV_VELO, MAX_FWD_VELO);
    VelocityController.WayEntry firstEntry
        = new VelocityController.WayEntry(WAY_LENGTH, MAX_VELO,
                                          POINT_NAME, Vehicle.Orientation.FORWARD);
    VelocityController.WayEntry secondEntry
        = new VelocityController.WayEntry(WAY_LENGTH, MAX_VELO,
                                          POINT_NAME, Vehicle.Orientation.FORWARD);
    controller.addWayEntry(firstEntry);
    controller.addWayEntry(secondEntry);
    assertSame(firstEntry, controller.getCurrentWayEntry());
    assertNotSame(secondEntry, controller.getCurrentWayEntry());
  }

  @Test
  public void testCurrentWayEntryChangesOnAdvance() {
    VelocityController controller
        = new VelocityController(MAX_DECEL, 1000,
                                 MAX_REV_VELO, 500);
    VelocityController.WayEntry firstEntry
        = new VelocityController.WayEntry(1, MAX_VELO,
                                          POINT_NAME, Vehicle.Orientation.FORWARD);
    VelocityController.WayEntry secondEntry
        = new VelocityController.WayEntry(10000, MAX_VELO,
                                          POINT_NAME, Vehicle.Orientation.FORWARD);
    controller.addWayEntry(firstEntry);
    controller.addWayEntry(secondEntry);
    assertSame(firstEntry, controller.getCurrentWayEntry());
    controller.advanceTime(100);
    assertSame(secondEntry, controller.getCurrentWayEntry());
  }

  @Test
  public void testAccelerationToMaxVelocity() {
    final int maxFwdVelocity = 500; // mm/s
    final int maxAcceleration = 250; // mm/s^2
    VelocityController controller
        = new VelocityController(MAX_DECEL, maxAcceleration,
                                 MAX_REV_VELO, maxFwdVelocity);
    // Way point with enough length to reach the max. velocity and with
    // a higher velocity limit than the vehicle's max velocity:
    VelocityController.WayEntry wayEntry
        = new VelocityController.WayEntry(100000, 2 * maxFwdVelocity,
                                          POINT_NAME, Vehicle.Orientation.FORWARD);
    controller.addWayEntry(wayEntry);

    controller.advanceTime(1000);
    // Reach 250 mm/s in 1s:
    assertEquals(250, controller.getCurrentVelocity());

    controller.advanceTime(1000);
    // Reach max. velocity of 500 mm/s in 2s:
    assertEquals(500, controller.getCurrentVelocity());

    controller.advanceTime(1000);
    // Stay at max velocity:
    assertEquals(500, controller.getCurrentVelocity());
  }

  @Test
  public void testVelocityLimitOnWayEntryRestrictsMaxVelocityOfVehicle() {
    final int maxFwdVelocity = 500; // mm/s
    final int maxAcceleration = 500; // mm/s^2
    VelocityController controller
        = new VelocityController(MAX_DECEL, maxAcceleration,
                                 MAX_REV_VELO, maxFwdVelocity);
    // Way point with a velocity limit less than the vehicle's max velocity:
    VelocityController.WayEntry wayEntry
        = new VelocityController.WayEntry(10000, maxFwdVelocity - 250,
                                          POINT_NAME, Vehicle.Orientation.FORWARD);
    controller.addWayEntry(wayEntry);

    for (int i = 0; i < 10; i++) {
      controller.advanceTime(100);
    }

    // Velocity could be 500 mm/s after 1s, but should be limited to 250 mm/s
    assertEquals(250, controller.getCurrentVelocity());
  }
}

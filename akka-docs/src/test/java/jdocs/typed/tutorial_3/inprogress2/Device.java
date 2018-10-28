/*
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package jdocs.typed.tutorial_3.inprogress2;

//#device-with-read


import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

import static jdocs.typed.tutorial_3.inprogress2.DeviceProtocol.*;

public class Device extends AbstractBehavior<DeviceMessage> {

  private final ActorContext<DeviceMessage> context;
  private final String groupId;
  private final String deviceId;

  public Device(ActorContext<DeviceMessage> context, String groupId, String deviceId) {
    this.context = context;
    this.groupId = groupId;
    this.deviceId = deviceId;

    context.getLog().info("Device actor {}-{} started", groupId, deviceId);
  }

  private Optional<Double> lastTemperatureReading = Optional.empty();

  @Override
  public Receive<DeviceMessage> createReceive() {
    return receiveBuilder()
      .onMessage(ReadTemperature.class, this::readTemperature)
      .build();
  }

  private Behavior<DeviceMessage> readTemperature(ReadTemperature r) {
    r.replyTo.tell(new RespondTemperature(r.requestId, lastTemperatureReading));
    return this;
  }

  private Device postStop() {
    context.getLog().info("Device actor {}-{} stopped", groupId, deviceId);
    return this;
  }

}

//#device-with-read

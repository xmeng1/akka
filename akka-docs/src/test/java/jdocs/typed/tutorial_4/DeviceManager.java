/**
 * Copyright (C) 2009-2018 Lightbend Inc. <https://www.lightbend.com>
 */

package jdocs.typed.tutorial_4;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.HashMap;
import java.util.Map;

import static jdocs.typed.tutorial_4.DeviceManagerProtocol.*;

//#device-manager-full
public class DeviceManager extends AbstractBehavior<DeviceManagerMessage> {

  public static Behavior<DeviceManagerMessage> behavior() {
    return Behaviors.setup(DeviceManager::new);
  }

  private final ActorContext<DeviceManagerMessage> context;
  private final Map<String, ActorRef<DeviceManagerMessage>> groupIdToActor = new HashMap<>();
  private final Map<ActorRef<DeviceManagerMessage>, String> actorToGroupId = new HashMap<>();

  public DeviceManager(ActorContext<DeviceManagerMessage> context) {
    this.context = context;
    context.getLog().info("DeviceManager started");
  }

  private DeviceManager onTrackDevice(RequestTrackDevice trackMsg) {
    String groupId = trackMsg.groupId;
    ActorRef<DeviceManagerMessage> ref = groupIdToActor.get(groupId);
    if (ref != null) {
      ref.tell(trackMsg);
    } else {
      context.getLog().info("Creating device group actor for {}", groupId);
      ActorRef<DeviceManagerMessage> groupActor =
        context.spawn(DeviceGroup.behavior(groupId), "group-" + groupId);
      context.watch(groupActor);
      groupActor.tell(trackMsg);
      groupIdToActor.put(groupId, groupActor);
      actorToGroupId.put(groupActor, groupId);
    }
    return this;
  }

  private DeviceManager onTerminated(Terminated t) {
    ActorRef<Void> groupActor = t.getRef();
    String groupId = actorToGroupId.get(groupActor);
    context.getLog().info("Device group actor for {} has been terminated", groupId);
    actorToGroupId.remove(groupActor);
    groupIdToActor.remove(groupId);
    return this;
  }

  public Receive<DeviceManagerMessage> createReceive() {
    return receiveBuilder()
        .onMessage(RequestTrackDevice.class, this::onTrackDevice)
        .onSignal(Terminated.class, this::onTerminated)
        .build();
  }

  private DeviceManager postStop() {
    context.getLog().info("DeviceManager stopped");
    return this;
  }

}
//#device-manager-full

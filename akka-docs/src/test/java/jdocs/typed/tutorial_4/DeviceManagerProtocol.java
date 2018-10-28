/**
 * Copyright (C) 2009-2018 Lightbend Inc. <https://www.lightbend.com>
 */

package jdocs.typed.tutorial_4;

import akka.actor.typed.ActorRef;

import java.util.Set;

//#device-registration-msgs
abstract class DeviceManagerProtocol {
  // no instances of DeviceManagerProtocol class
  private DeviceManagerProtocol() {}

  interface DeviceManagerMessage {}

  public static final class RequestTrackDevice implements DeviceManagerMessage {
    public final String groupId;
    public final String deviceId;
    public final ActorRef<DeviceRegistered> replyTo;

    public RequestTrackDevice(String groupId, String deviceId, ActorRef<DeviceRegistered> replyTo) {
      this.groupId = groupId;
      this.deviceId = deviceId;
      this.replyTo = replyTo;
    }
  }

  public static final class DeviceRegistered {
    public final ActorRef<DeviceProtocol.DeviceMessage> device;

    public DeviceRegistered(ActorRef<DeviceProtocol.DeviceMessage> device) {
      this.device = device;
    }
  }
  //#device-registration-msgs

  //#device-terminated
  public static class DeviceTerminated implements DeviceManagerMessage{
    public final ActorRef<DeviceProtocol.DeviceMessage> device;
    public final String groupId;
    public final String deviceId;

    public DeviceTerminated(ActorRef<DeviceProtocol.DeviceMessage> device, String groupId, String deviceId) {
      this.device = device;
      this.groupId = groupId;
      this.deviceId = deviceId;
    }
  }
  //#device-terminated

  public static final class RequestDeviceList implements DeviceManagerMessage {
    final long requestId;
    final ActorRef<ReplyDeviceList> replyTo;

    public RequestDeviceList(long requestId, ActorRef<ReplyDeviceList> replyTo) {
      this.requestId = requestId;
      this.replyTo = replyTo;
    }
  }

  public static final class ReplyDeviceList {
    final long requestId;
    final Set<String> ids;

    public ReplyDeviceList(long requestId, Set<String> ids) {
      this.requestId = requestId;
      this.ids = ids;
    }
  }

  //#device-registration-msgs
}
//#device-registration-msgs

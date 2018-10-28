/**
 * Copyright (C) 2009-2018 Lightbend Inc. <https://www.lightbend.com>
 */

//#iot-app
package jdocs.typed.tutorial_2;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class IotMain {

  public static void main(String[] args) throws IOException {
    // Create ActorSystem and top level supervisor
    ActorSystem.create(IotSupervisor.behavior(), "iot-system");
  }

}
//#iot-app

/*
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

//#print-refs
package jdocs.typed.tutorial_1;

//#print-refs

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.junit.ClassRule;
import org.junit.Test;
import org.scalatest.junit.JUnitSuite;

//#print-refs

class PrintMyActorRefActor extends AbstractBehavior<String> {

  private final ActorContext<String> context;

  PrintMyActorRefActor(ActorContext<String> context) {
    this.context = context;
  }

  @Override
  public Receive<String> createReceive() {
    return receiveBuilder()
      .onMessageEquals("printit", this::printIt)
      .build();
  }

  private Behavior<String> printIt() {
    ActorRef<String> secondRef = context.spawn(Behaviors.empty(), "second-actor");
    System.out.println("Second: " + secondRef);
    return this;
  }

}
//#print-refs

//#start-stop
class StartStopActor1 extends AbstractBehavior<String> {

  StartStopActor1() {
    System.out.println("first started");
  }

  @Override
  public Receive<String> createReceive() {
      return receiveBuilder()
      .onMessageEquals("stop", Behaviors::stopped)
      .onSignal(PostStop.class, signal -> postStop())
      .build();
  }

  private Behavior<String> postStop() {
    System.out.println("first stopped");
    return this;
  }

}

class StartStopActor2 extends AbstractBehavior<String> {

  StartStopActor2() {
    System.out.println("second started");
  }

  @Override
  public Receive<String> createReceive() {
    return receiveBuilder()
      .onSignal(PostStop.class, signal -> postStop())
      .build();
  }

  private Behavior<String> postStop() {
    System.out.println("second stopped");
    return this;
  }
}
//#start-stop

//#supervise
class SupervisingActor extends AbstractBehavior<String> {

  private final ActorRef<String> child;

  SupervisingActor(ActorContext<String> context) {
    child = context.spawn(new SupervisedActor(), "supervised-actor");
  }

  @Override
  public Receive<String> createReceive() {
    return receiveBuilder()
      .onMessageEquals("failChild", this::failChild)
      .build();
  }

  private Behavior<String> failChild() {
    child.tell("fail");
    return this;
  }
}

class SupervisedActor extends AbstractBehavior<String> {

  SupervisedActor() {
    System.out.println("supervised actor started");
  }

  @Override
  public Receive<String> createReceive() {
    return receiveBuilder()
        .onMessageEquals("fail", this::fail)
      .onSignal(PostStop.class, signal -> postStop())
        .build();
  }

  private Behavior<String> fail() {
    System.out.println("supervised actor fails now");
    throw new RuntimeException("I failed!");
  }

  private Behavior<String> postStop() {
    System.out.println("second stopped");
    return this;
  }
}
//#supervise


//#print-refs

class Main extends AbstractBehavior<String> {

  private final ActorContext<String> context;

  Main(ActorContext<String> context) {
    this.context = context;
  }

  @Override
  public Receive<String> createReceive() {
    return receiveBuilder()
      .onMessageEquals("start", this::start)
      .build();
  }

  private Behavior<String> start() {
    ActorRef<String> firstRef = context.spawn(Behaviors.setup(PrintMyActorRefActor::new), "first-actor");

    System.out.println("First: " + firstRef);
    firstRef.tell("printit");
    return Behaviors.same();
  }
}

public class ActorHierarchyExperiments {
  public static void main(String[] args) {
    ActorSystem.create(
      Behaviors.setup(PrintMyActorRefActor::new), "testSystem");
  }
}
//#print-refs


class ActorHierarchyExperimentsTest extends JUnitSuite {

  @ClassRule
  public static final TestKitJunitResource testKit = new TestKitJunitResource();

  @Test
  public void testStartAndStopActors() {
    //#start-stop-main
    ActorRef<String> first = testKit.spawn(Behaviors.setup(ctx -> new StartStopActor1()), "first");
    first.tell("stop");
    //#start-stop-main
  }

  @Test
  public void testSuperviseActors() {
    //#supervise-main
    ActorRef<String> supervisingActor = testKit.spawn(Behaviors.setup(SupervisingActor::new), "supervising-actor");
    supervisingActor.tell("failChild");
    //#supervise-main
  }
}

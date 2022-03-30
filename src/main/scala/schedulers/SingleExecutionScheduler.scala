package schedulers

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import scala.concurrent.duration._
import scala.language.postfixOps

object SingleExecutionScheduler extends App {
  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SingleExecutionScheduler")    // Creating ActorSystem
  val simpleActor = system.actorOf(Props[SimpleActor])    //Creating actor

  system.log.info("Scheduling reminder for simple Actor")

   import system.dispatcher
 // implicit val executionContext = system.dispatcher
  system.scheduler.scheduleOnce(3 second) {
    simpleActor ! "reminder"                             // Sending messages by using tell
  }//(system.dispatcher)
}

package schedulers

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}
import scala.concurrent.duration._
import scala.language.postfixOps

object PeriodicExecutionScheduler extends App {
  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SingleExecutionScheduler")
  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("Scheduling reminder for simple Actor")

   import system.dispatcher

 /* /***
   * runs endlessly as no stop provided
   */
  val routine = system.scheduler.schedule(1 second, 2 seconds){
    simpleActor ! "heartbeat"
  }*/

  /***
   * stops as it uses cancellable
   */
  val routine: Cancellable = system.scheduler.scheduleWithFixedDelay(1 second, 2 seconds, simpleActor, "hello")

  system.scheduler.scheduleOnce(5 seconds) {
    routine.cancel()
  }
}
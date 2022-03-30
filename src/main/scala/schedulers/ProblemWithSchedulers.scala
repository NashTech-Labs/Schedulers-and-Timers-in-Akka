package schedulers

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}
import scala.concurrent.duration._
import scala.language.postfixOps

object ProblemWithSchedulers extends App {
  val system = ActorSystem("ProblemWithSchedulers")

  import system.dispatcher

  class SelfActor extends Actor with ActorLogging {
    var schedule: Cancellable = createTimeoutWindow()

    def createTimeoutWindow(): Cancellable = {
      context.system.scheduler.scheduleOnce(1 second) {
        self ! "timeout"
      }
    }

    override def receive: Receive = {
      case "timeout" => log.info("Stopping myself")
        context.stop(self)
      case message =>
        log.info(s"Received $message, staying alive")
        schedule.cancel()
        schedule = createTimeoutWindow()
    }
  }

  val selfActor = system.actorOf(Props[SelfActor], "SelfActor")

  system.scheduler.scheduleOnce(250 millis) {
    selfActor ! "ping"
  }

  system.scheduler.scheduleOnce(2 seconds) {
    system.log.info("sending pong to the self actor")
    selfActor ! "pong"
  }
}
package timers

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Timers}

import scala.concurrent.duration._
import scala.language.postfixOps

object SchedulingUsingTimers extends App {
  val system = ActorSystem("SchedulingUsingTimers")

  import system.dispatcher

  case object TimerKey     //Timer key
  case object Start        //Message
  case object Stop
  case object Reminder

  class TimerBasedActor extends Actor with ActorLogging with Timers {
    timers.startSingleTimer(TimerKey, Start, 500 millis)     //initial delay

    override def receive: Receive = {
      case Start =>
        log.info("Bootstrapping")
        timers.startPeriodicTimer(TimerKey, Reminder, 1 second)
      //  timers.startTimerWithFixedDelay(TimerKey, Reminder, 1 second)
      case Reminder =>
        log.info("I am alive")
      case Stop =>
        log.warning("Stopping!")
        timers.cancel(TimerKey)
        context.stop(self)
    }
  }

  val timerBasedActor = system.actorOf(Props[TimerBasedActor], "timerBasedActor")

  system.scheduler.scheduleOnce(5 seconds) {
    timerBasedActor ! Stop
  }
}
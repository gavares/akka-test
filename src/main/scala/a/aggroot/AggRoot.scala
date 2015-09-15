package a.aggroot

import a.commands.Command
import a.events.DomainEvent
import a.message.{Message, DomainEventMessage, EventMessage, CommandMessage}
import akka.actor.{DiagnosticActorLogging, ActorRef}
import akka.event.Logging.MDC
import akka.persistence.PersistentActor

import scala.util.{Try, Success}

/**
 *
 * Created by gavares on 9/13/15.
 */
trait AggregateState[DE <: DomainEvent, S <: AggregateState[DE,S]] {
  def apply(evt: DE): S
}

trait AggregateStateFactory[DE <: DomainEvent, S <: AggregateState[DE, S]] {
  def apply: PartialFunction[DE, S]
}

trait AggRoot[C <: Command, DE <: DomainEvent, S <: AggregateState[DE, S]]
  extends PersistentActor
  with DedupMsgSupport
  with DiagnosticActorLogging
{

  def handleCommand(cmd: C): Unit

  override def persistenceId: String = id
  def id = self.path.name

  private var _lastCommand: Option[CommandMessage[C]] = None
  private var _sender: ActorRef = null
  private var _msgNum: Long = 0

  def commandSender = _sender
  def commandMessage = _lastCommand.get

  val stateFactory: AggregateStateFactory[DE, S]
  private var stateOpt: Option[S] = None
  def state = stateOpt.get
  def initialized = stateOpt.isDefined

  override def receive: Receive = dedup(handleDuplicate).orElse(receiveCommand)

  override def receiveRecover: Receive = {
    case em: EventMessage[DE] => updateState(em)
  }

  override def receiveCommand: Receive = {
    case cmdMsg: CommandMessage[C] =>
      log.debug(s"Received: $cmdMsg")
      _lastCommand = Some(cmdMsg)
      _sender = sender()
      handleCommand(cmdMsg.cmd)
    case other => unhandled(other)
  }


  override def unhandled(msg: Any) = {
    log.warning(s"Received unhandled msg: $msg")
    super.unhandled(msg)
  }

  def doPersist(evt: DE) = {
    persist(new EventMessage(evt).causedBy(commandMessage)) { persisted =>
      log.debug("EventMessage persisted: {} {}", persisted.id, persisted.domainEvent)
      updateState(persisted)
      handle(_sender, DomainEventMessage.apply[DE](persistenceId, persisted))
    }
  }


  def updateState(em: EventMessage[DE]): Unit = {
    val evt = em.domainEvent
    val nextState: S = if(initialized) state.apply(evt) else stateFactory.apply(evt)
    stateOpt = Some(nextState)
    messageProcessed(em)
  }

  def handle(sndrRef: ActorRef, event: DomainEventMessage[DE]) = {
    acknowledgeCommandProcessed(commandMessage, Success(event))
  }

  def handleDuplicate(msg: Message) = acknowledgeCommandProcessed(msg, Success("OK"))

  def acknowledgeCommand(result: Try[Any]) = acknowledgeCommandProcessed(commandMessage, result)
  def acknowledgeCommand(result: Any) = acknowledgeCommandProcessed(commandMessage, Success(result))

  def acknowledgeCommandProcessed(msg: Message, result: Try[Any]) = {
    val deliveryReceipt = msg.deliveryReceipt(commandMessage.id, result)
    log.debug(s"Acknowledging with deliveryReceipt: $deliveryReceipt")
    _sender ! deliveryReceipt
  }

  override def mdc(currentMessage: Any): MDC = {
    _msgNum += 1
    val always = Map("msgNum" -> _msgNum)
    val perMessage = currentMessage match {
      case m: Message => Map("msgId" -> m.id)
      case other => Map.empty[String,Object]
    }

    always ++ perMessage
  }

}

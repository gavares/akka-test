package a.commands

import a.message.CommandMessage
import a._
import org.joda.time.DateTime

/**
 * Represents a command. All a.commands must extend this trait
 *
 * Created by gavares on 9/13/15.
 */
trait Command extends Timestamped {
  def aggId: String

  def cmdName = this.getClass.getSimpleName.replaceAll("[$]", "")

  def asCommandMessage: CommandMessage[this.type] = asCommandMessage(uuid, now)
  def asCommandMessage(uuid: String = uuid, ts: DateTime = now): CommandMessage[this.type] =
    new CommandMessage[this.type](this, uuid, ts)
}

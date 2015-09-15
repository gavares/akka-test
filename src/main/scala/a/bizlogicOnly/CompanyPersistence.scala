package a.bizlogicOnly

import a.commands.CompanyCommand
import a.commands.CompanyCommands.{AddUser, SetName}
import a.events.CompanyEvent
import a.events.CompanyEvents.{CompanyNameUpdated, UserAdded}
import akka.persistence.PersistentActor
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

/**
 *
 * Created by gavares on 9/12/15.
 */
case class CompanyPersistence(var state: Company) extends PersistentActor with LazyLogging {

  override def persistenceId = id
  def id = self.path.name

  override def receiveRecover: Receive = {
    case e: CompanyEvent => handleEvent(e)
  }

  private def handleEvent(e: CompanyEvent) = e match {
    case CompanyNameUpdated(name, timestamp) =>
      state.setName(name)

    case UserAdded(name, timestamp) =>
      state.addUser(name)
  }

  override def receiveCommand: Receive = {
    case c: CompanyCommand => handleCmd(c)
  }

  private def handleCmd(cmd: CompanyCommand) = cmd match {
    case c: SetName =>
      val evt = CompanyNameUpdated(c.companyId, c.name)
      val blk = { state.setName(c.name) }
      updateAndSave(evt)(blk)

    case c: AddUser =>
      val evt = UserAdded(c.companyId, c.userId)
      val blk = { state }
      updateAndSave(evt)(blk)
  }


  private def updateAndSave(evt: CompanyEvent)(blk: => Company) = Try( blk ) match {
    case Success(co) => persist(evt) { persisted =>
      logger.debug(s"Persisted: $persisted")
      this.state = co
    }

    case Failure(ex) => sender() ! ex
  }

}

import a._
import a.aggroot._
import a.commands.CompanyCommands
import a.commands.CompanyCommands.CreateCompany
import a.commands.ProductCommands.{Activate, CreateProduct, SetName}
import a.events.CompanyEvents.CompanyCreated
import a.message.DomainEventMessage
import a.message.amod.Processed
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.concurrent.stm.Ref
import scala.util.{Success, Random}

/**
 *
 * Created by gavares on 9/11/15.
 */
class ActorSpec(_system: ActorSystem) extends TestKit(_system)
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll
  with ImplicitSender
  with ScalaFutures
  with LazyLogging {

  implicit val timeout = Timeout(10.seconds)
  implicit val akkaTimeout = akka.util.Timeout(10.seconds)

  val companyMgrRef = Ref(new CompanyMgr())

  def this() = this(ActorSystem("ActorSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }


  "Actors" should {

    var companyId = ""
    val userId = randomId

    val companyView = system.actorOf(Props(CompanyView(companyMgrRef)))

    "define a company" in {
      var cMgrActor = system.actorOf(Props(new CompanyMgrAggRoot), CompanyMgrAggRoot.PersistenceId)
      val msgId1 = uuid
      cMgrActor ! CreateCompany("", "test_co", userId).asCommandMessage(msgId1)
      expectMsgPF(hint = "expect receipt for Company creation") {
        case Processed(msgId, result) if msgId == msgId1 =>
          companyId = result.get.asInstanceOf[DomainEventMessage[CompanyCreated]].event.id
          true
      }


      val msgId2 = uuid
      cMgrActor ! CompanyCommands.SetName(companyId, "test_co", userId).asCommandMessage(msgId2)
      expectMsgPF(hint = "expect receipt for company SetName") {
        case Processed(msgId, result) if msgId == msgId2 => true
      }

      Thread.sleep(2000)
      companyMgrRef.single.get.exists(companyId) mustBe true
    }

    "define some products" in {
      val sku = randomId
      var pActor = system.actorOf(Props(new ProductCatalogAggRoot(companyView)), s"catalog-$companyId")
      val cmds = Seq(
        CreateProduct(companyId, sku, "product1", "desc", "internal", userId),
        Activate(companyId, sku, userId),
        SetName(companyId, sku, "updated name", userId)
      ) map (_.asCommandMessage )

      cmds.foreach { cmd => pActor ! cmd }

      fishForMessage(hint = "expect receipt for product creation") {
        case Processed(msgId, result) if msgId == cmds.last.id && result.isInstanceOf[Success[_]] => true
        case _ => false
      }
    }
  }

  def randomId = Random.alphanumeric.take(8).mkString
}

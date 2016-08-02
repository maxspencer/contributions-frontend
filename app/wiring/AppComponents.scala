package wiring

import com.github.nscala_time.time.Imports._
import com.gu.identity.cookie.{PreProductionKeys, ProductionKeys}
import com.gu.identity.play.AccessCredentials.{Cookies, Token}
import com.gu.identity.testing.usernames.TestUsernames
import com.softwaremill.macwire._
import com.typesafe.config.ConfigFactory
import controllers._
import filters.CheckCacheHeadersFilter
import play.api.BuiltInComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.filters.headers.{SecurityHeadersConfig, SecurityHeadersFilter}
import services.PaymentServices
import router.Routes

//Sometimes intellij deletes this -> (import router.Routes)

/* https://www.playframework.com/documentation/2.5.x/ScalaCompileTimeDependencyInjection
 * https://github.com/adamw/macwire/blob/master/README.md#play24x
 */
trait AppComponents extends BuiltInComponents with PlayComponents {


  lazy val config = ConfigFactory.load()

  private val idConfig = config.getConfig("identity")

  lazy val identityKeys = if (idConfig.getBoolean("production.keys")) new ProductionKeys else new PreProductionKeys

  lazy val testUsernames = TestUsernames(
    com.gu.identity.testing.usernames.Encoder.withSecret(idConfig.getString("test.users.secret")),
    recency = 2.days.standardDuration
  )

  lazy val identityAuthProvider =
    Cookies.authProvider(identityKeys).withDisplayNameProvider(Token.authProvider(identityKeys, "membership"))

  lazy val paymentServices = PaymentServices(
    identityAuthProvider,
    testUsernames,
    PaymentServices.stripeServicesFor(config.getConfig("stripe"))
  )

  lazy val giraffeController = wire[Giraffe]
  lazy val healthcheckController = wire[Healthcheck]
  lazy val assetController = wire[Assets]


  override lazy val httpFilters: Seq[EssentialFilter] = Seq(
    wire[CheckCacheHeadersFilter],
    SecurityHeadersFilter(SecurityHeadersConfig(
      contentSecurityPolicy = None
    ))
  )

  val prefix: String = "/"
  lazy val router: Router = wire[Routes]
}

package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
import org.apache.log4j.BasicConfigurator
import com.geishatokyo.model._
import com.geishatokyo.dispatch.ImageDispatch


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {

    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:data/lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // where to search snippet
    LiftRules.addToPackages("com.geishatokyo")
    Schemifier.schemify(true, Schemifier.infoF _, User,UserPhoto , FriendLink,Detection)


    def loggedIn = If(() => S.loggedIn_?,"Need login")

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index" >> User.AddUserMenusAfter, // Simple menu form
      Menu("画像アップロード") / "facedetection" >> loggedIn,
      Menu("投稿画像一覧") / "album" >> loggedIn,
      Menu("画像") / "facetrain" >> Hidden,
      Menu("データベース") / "database" >> loggedIn submenus(
         UserPhoto.menus ::: Detection.menus :_*
      ) )


    LiftRules.setSiteMapFunc(() => User.sitemapMutator(sitemap()))
    LiftRules.statelessDispatchTable.append(new ImageDispatch)

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}

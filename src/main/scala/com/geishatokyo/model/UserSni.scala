package com.geishatokyo.model

import xml.NodeSeq
import net.liftweb.http.{S, StatefulSnippet}
import net.liftweb.util.Helpers._

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/26
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */

class UserSni extends StatefulSnippet {


  lazy val  user : User = {
    User.findByKey(S.param("user").map(_.toLong).open_!).open_!
  }


  def dispatch : DispatchIt = {
    case "render" => render _
  }

  def render(node : NodeSeq) : NodeSeq = {
    bind("e",node,
    "name" -> user.niceName)
  }



}
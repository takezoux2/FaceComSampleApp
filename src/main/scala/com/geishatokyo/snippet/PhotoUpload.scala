package com.geishatokyo.snippet

import net.liftweb.util.Helpers._
import com.geishatokyo.facecom.FaceComUtil
import net.liftweb.http.SHtml._
import xml.{Text, NodeSeq}
import face4j.model.Face
import com.geishatokyo.model.{UserPhoto, User}
import net.liftweb.http.{S, StatefulSnippet, SHtml}
import net.liftweb.common.Empty
import net.liftweb.http.js.{JsCmd, JsCmds}

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/25
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */

class PhotoUpload extends StatefulSnippet {

  def dispatch : DispatchIt = {
    case "faceDetection" => faceDetection _
  }


  def faceDetection(node : NodeSeq) : NodeSeq = {
    bind("f",node,
    "file" -> SHtml.fileUpload( fileParamHolder => {
      println("uploaded = " + fileParamHolder.fileName)
      if(isImage(fileParamHolder.fileName)){
        val photo = FaceComUtil.recognize(User.currentUser.open_!, fileParamHolder.file)
        S.redirectTo("/facetrain.html?photo=" + photo.id.is)
      }
    }),
    "submit" -> SHtml.ajaxSubmit("アップ",() => {
      JsCmds.SetHtml("status", <div>OK</div> )
    }))
  }


  def isImage(filename : String) = {
    filename.endsWith(".jpg") ||
    filename.endsWith(".gif") ||
    filename.endsWith(".jpeg") ||
    filename.endsWith(".png")
  }



}

class PhotoTraining extends StatefulSnippet{

  lazy val photo : UserPhoto = {
    UserPhoto.findByKey(S.param("photo").map(_.toLong).open_!).open_!
  }

  def dispatch : DispatchIt = {
    case "img" => img _
    case "faces" => faces _
  }

  def img(node : NodeSeq) : NodeSeq = {
    <img src={"/photos/" + photo.imageName} />
  }

  def faces(node : NodeSeq) : NodeSeq = {

    val users = User.findAll.map(u => (u.id.is.toString,u.shortName))

    photo.detections.all flatMap(d => {
      bind("e",node,
      "pos" -> "x:%s y:%s".format(d.x.is,d.y.is),
      "detected" -> User.findByKey(d.user.is).map(_.shortName).openOr("Not detected"),
      "train" -> {(n : NodeSeq) => {
        var detected : Long = 0
        bind("f",n,
          "select" -> select(users,Empty,u => detected = u.toLong),
          "train" -> ajaxSubmit("変更",() => {
            FaceComUtil.train(d,User.findByKey(detected).open_!)
            JsCmds.SetHtml("status", <div style="color=red;">変更</div>)
          })
        )

      }            }
      )
    })
  }
}
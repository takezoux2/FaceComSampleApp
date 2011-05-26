package com.geishatokyo.snippet

import net.liftweb.http.StatefulSnippet
import xml.NodeSeq
import com.geishatokyo.model.{UserPhoto, User}
import net.liftweb.mapper.{Descending, OrderBy, CRUDify, By}

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/26
 * Time: 1:00
 * To change this template use File | Settings | File Templates.
 */

class Album extends StatefulSnippet{


  def dispatch : DispatchIt = {

    case "list" => list _

  }

  def list(node : NodeSeq) : NodeSeq = {

    val photos = UserPhoto.findAll(By(UserPhoto.user,User.currentUser.open_!),OrderBy(UserPhoto.id,Descending))

    def genImg(userPhoto : UserPhoto) = {
      <a href={"/facetrain?photo=" + userPhoto.id.is}>
        <img width="200" height="200" src={"/photos/" + userPhoto.imageName.is} />
      </a>
    }

    def makeTable( photos : List[UserPhoto]) : NodeSeq = {
      val split = photos.splitAt(3)
      if(split._2.size > 0){
        <tr>{
        split._1.flatMap( p => {
          <td>{genImg(p)}</td>
        } ) }</tr> ++ makeTable(split._2)
      }else{
        <tr>{
        split._1.flatMap( p => {
          <td>{genImg(p)}</td>
        } ) }</tr>
      }
    }

    makeTable(photos)

  }

}
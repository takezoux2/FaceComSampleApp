package com.geishatokyo.model

import net.liftweb.mapper._

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/26
 * Time: 0:40
 * To change this template use File | Settings | File Templates.
 */

object UserPhoto extends UserPhoto with LongKeyedMetaMapper[UserPhoto] with CRUDify[Long,UserPhoto]

class UserPhoto extends LongKeyedMapper[UserPhoto] with IdPK with OneToMany[Long,UserPhoto]{


  def getSingleton = UserPhoto

  object user extends MappedLongForeignKey(this,User)
  object imageName extends MappedString(this,100)

  object detections extends MappedOneToMany(Detection,Detection.userPhoto)


}
package com.geishatokyo.model

import net.liftweb.mapper._

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/26
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */

object Detection extends Detection with LongKeyedMetaMapper[Detection] with CRUDify[Long,Detection]{

}

class Detection extends LongKeyedMapper[Detection] with IdPK{


  def getSingleton = Detection

  object userPhoto extends MappedLongForeignKey(this,UserPhoto)
  object tagId extends MappedString(this,100)
  object x extends MappedDouble(this)
  object y extends MappedDouble(this)
  object width extends MappedDouble(this)
  object height extends MappedDouble(this)
  object confidential extends MappedInt(this)
  object user extends MappedLongForeignKey(this,User)


}
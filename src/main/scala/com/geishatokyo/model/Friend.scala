package com.geishatokyo.model

import net.liftweb.mapper._

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/26
 * Time: 0:09
 * To change this template use File | Settings | File Templates.
 */

object FriendLink extends FriendLink with MetaMapper[FriendLink]

class FriendLink extends Mapper[FriendLink] {
  def getSingleton = FriendLink

  object user extends LongMappedMapper(this,User)
  object friend extends LongMappedMapper(this,User)


}
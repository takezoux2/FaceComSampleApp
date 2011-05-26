package com.geishatokyo.dispatch

import net.liftweb.http.rest.RestHelper
import com.geishatokyo.facecom.FaceComUtil
import net.liftweb.common.{Full, Box}
import net.liftweb.http.{InMemoryResponse, LiftResponse, Req}

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/26
 * Time: 1:59
 * To change this template use File | Settings | File Templates.
 */

class ImageDispatch extends RestHelper{

  serve {
    case Req( List("photos",image)  , _ , _) => {

      val i = FaceComUtil.readImage(image)

      Full(new InMemoryResponse(i,Nil,Nil,200))

    }
  }

}
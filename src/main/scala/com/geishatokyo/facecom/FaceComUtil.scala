package com.geishatokyo.facecom

import face4j.DefaultFaceClient
import net.liftweb.http.auth.MD5
import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory
import java.io.{FileInputStream, FileOutputStream, File}
import face4j.model.{Face, Photo}
import com.geishatokyo.model.{Detection, UserPhoto, User}
import java.security.MessageDigest
import net.liftweb.mapper.By

/**
 * Created by IntelliJ IDEA.
 * User: takeshita
 * Date: 11/05/25
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */

object FaceComUtil{

  val logger = LoggerFactory.getLogger(getClass)

  val client = new DefaultFaceClient("369f179ce36c1288f8ddac8852159184","b9491e5f77ee1998affa5ef8ce099d19")

  val dir = "data/photos"

  def readImage(image : String) : Array[Byte] = {
    val f = new File(dir,image)
    val input = new FileInputStream(f)
    val b : Array[Byte] = new Array(input.available)
    input.read(b)
    input.close
    b
  }

  def detect(user : User,image : Array[Byte]) = {

    val (f,userPhoto) = save(user,image)

    val p = client.detect(f)

    val faces = p.getFaces().asScala.toList

    saveFaces(userPhoto,faces)
    faces
  }


  def toKey(data : Array[Byte]) : String = {

    val md = MessageDigest.getInstance("MD5")
    val hash = md.digest(data)
    toHex(hash.take(12))
  }


  def toHex( data : Array[Byte]) = {
    val sb = new StringBuilder
    for(d <- data){
      sb.append("%02X".format(d))
    }
    sb.toString
  }

  def save(user : User,image : Array[Byte]) : (File,UserPhoto) = {

    val imageFile = toKey(image)

    val p = UserPhoto.find(By(UserPhoto.imageName,imageFile))
    if(p.isDefined){
      return ( new File(dir, imageFile) , p.open_!)
    }

    val forDir = new File(dir)
    forDir.mkdirs

    logger.info("Upload:" + imageFile)

    //save to DB
    val i = UserPhoto.createInstance
    i.user(user.id.is)
    i.imageName(imageFile)
    i.save

    val f = new File(dir, imageFile)
    val output = new FileOutputStream(f)
    output.write(image)
    output.flush
    output.close


    (f,i)
  }

  def saveFaces( userPhoto : UserPhoto , faces : List[Face]) = {

    def uidToLongId(uid : String) = {
      val i = uid.indexOf("@")
      if(i > 0){
        uid.substring(0,i).toLong
      }else{
        uid.toLong
      }
    }

    faces.foreach(v => logger.info( v.toString))

    faces.foreach(f => {
      val d = Detection.createInstance
      d.userPhoto(userPhoto)
      d.x(f.getCenter.x)
      d.y(f.getCenter.y)
      d.width(f.getWidth)
      d.height(f.getHeight)
      d.tagId(f.getTID)

      val guess = f.getGuess
      if(guess != null){
        val s = guess.getFirst
        try{
          User.findByKey(uidToLongId(s)).foreach(u => {
            d.user(u.id.is)
          })
        }catch{
          case e : Exception => logger.info("Error",e)
        }
      }

      d.save
    })

  }

  def recognize(user : User, image : Array[Byte]) : UserPhoto = {

    val ids = User.findAll.map(_.uidWithNameSpace)

    val (f,userPhoto) = save(user,image)
    val photo : Photo = client.recognize(f, ids.mkString(","))

    val faces = photo.getFaces.asScala.toList

    saveFaces(userPhoto,faces)
    userPhoto
  }

  def train(detection : Detection , user : User) = {

    logger.info("Train User:%s target:%s@%s".format(user.id.is,detection.id.is,detection.userPhoto.is))

    client.saveTags(detection.tagId,user.uidWithNameSpace,user.niceName)
    client.train(user.uidWithNameSpace)

    detection.user(user.id.is)
    detection.save

  }


}
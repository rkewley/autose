
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import persistence._
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

object CourseTopicsController extends Base {

  def listCourseTopics = Action {
    Ok(viewlist.html.listCourseTopics(SqlCourseTopics.all))
  }
  
   def showCourseTopics(id: Long) = Action {
    Ok(viewshow.html.showCourseTopics(SqlCourses.select(id)))
  }

   def showTopicsCourses(id: Long) = Action {
    Ok(viewshow.html.showTopicsCourses(SqlTopics.select(id)))
  }


}
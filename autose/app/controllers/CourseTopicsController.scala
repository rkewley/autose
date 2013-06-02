
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import persistence._
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.templates.Html
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

  def getLessonLink(lessonTopic: MdlCourseTopics): Html = {
    Html("<a href=" + routes.LessonsController.showLessons(lessonTopic.vLessonIndex) + ">" + lessonTopic.vLessonNumber + "</a>")
  }
  
  def commaSeparateHtml(html1:Html, html2: Html): Html = {
    html1 += Html(", ") += html2
  }
  
  def accumulateLessonLinks(accumulatedHtml: Html, lessonTopic: MdlCourseTopics): Html = {
    commaSeparateHtml(accumulatedHtml, getLessonLink(lessonTopic))
  }

}

package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import models.NormalUser
import views._
import slick.AppDB
//import util.pdf.PDF
import scala.slick.driver.MySQLDriver.simple._
import play.api.mvc._
import persistence._
import jp.t2v.lab.play2.auth._

object CoursesController extends ControllerTrait[Long, MdlCourses, Long] with Base with OptionalAuthElement {

  val form = Form[MdlCourses](
    mapping(
      "fidCourse" -> optional(of[Long]),
      "fCourseIDNumber" -> text,
      "fAcademicYear" -> of[Long],
      "fAcademicTerm" -> of[Long],
      "fCourseName" -> text,
      "fCourseDirector" -> of[Long],
      "fProgramDirector" -> of[Long],
      "fCourseDescriptionRedbook" -> text,
      "fCreditHours" -> of[Double],
      "fPrerequisites" -> text,
      "fCorequisites" -> text,
      "fETCredits" -> of[Double],
      "fCourseStrategy" -> text,
      "fCriteriaForPassing" -> text,
      "fAdminInstructions" -> text,
      "fDepartmentID" -> of[Long],
      "fCourseWebsite" -> of[Boolean],
      "fCourseDescriptionWebsite" -> text)(MdlCourses.apply)(MdlCourses.unapply))

  override def listFunction(ffk: Long)(implicit user: MdlUser): Html = {
    views.html.viewlist.listCourses(getAll(ffk))
  }

  override def listFunction(item: MdlCourses)(implicit user: MdlUser): Html = {
    views.html.viewlist.listCourses(getAll(item))
  }

  override def showFunction(vCourses: MdlCourses): Html =
    views.html.viewshow.showCourses(vCourses)

  override def editFunction(mdlCoursesForm: Form[MdlCourses]): Html =
    views.html.viewforms.formCourses(mdlCoursesForm, 0, 0, 0)

  override def createFunction(mdlCoursesForm: Form[MdlCourses]): Html =
    views.html.viewforms.formCourses(mdlCoursesForm, 1, 0, 0)

  def crud = slick.AppDB.dal.Courses

  override def redirect(item: MdlCourses) = routes.CoursesController.listCourses()

  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }


  def listAllCourses = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    implicit val userOption = Some(user)
    Ok(views.html.viewlist.listCourses(crud.all.sortWith(Courses.compare)))
  }

  def listCourses = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    implicit val userOption = Some(user)
    Ok(views.html.viewlist.listCourses(crud.all.filter(vCourses => vCourses.vAcademicYear == Globals.currentYear && vCourses.vAcademicTerm == Globals.currentTerm).sortWith(Courses.compare)))
  }

  def home = listCourses
  
  def publish(idCourse: Long, idProgram: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    //OK(views.html.viewpdf.publishCourses(AppDB.dal.Courses.select(idCourse).get, AppDB.dal.Programs.select(idProgram).get))
    Ok(views.html.viewpdf.publishCourses(AppDB.dal.Courses.select(idCourse).get, AppDB.dal.Programs.select(idProgram).get))
  }

  def abet(idCourse: Long, idProgram: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(views.html.viewpdf.publishCourses(AppDB.dal.Courses.select(idCourse).get, AppDB.dal.Programs.select(idProgram).get))
  } 
  
  def copyCoursesToStap(id: Long) = compositeAction(Faculty) { user =>
    implicit template => implicit request =>
      val course = AppDB.dal.Courses.select(id).get
      val yearTerm = (course.vAcademicYear, 3)
      println("Copying " + course.vCourseIDNumber + " " + yearTerm._1 + "-" + yearTerm._2 + " to STAP")
      val newCourse = course.newCourse(yearTerm._1, yearTerm._2)
      Ok(views.html.viewforms.formCourses(form.fill(newCourse), 1, 1, id))
  }

  def copyCourses(id: Long) = compositeAction(Faculty) { user =>
    implicit template => implicit request =>
      val course = AppDB.dal.Courses.select(id).get
      val yearTerm = course.vAcademicTerm match {
        case 1 => (course.vAcademicYear, 2)
        case _ => (course.vAcademicYear + 1, 1)
      }
      val newCourse = course.newCourse(yearTerm._1, yearTerm._2)
      Ok(views.html.viewforms.formCourses(form.fill(newCourse), 1, 1, id))
  }

  def copyCourseData(orignalCourseId: Long, newCourseId: Long) {
	  // Copy course references
	  val references = SqlCourseReferences.selectWhere("Course = " + orignalCourseId)
	  references.foreach(reference => {
	    val newReference = new MdlCourseReferences(0, newCourseId, reference.vReference)
	    SqlCourseReferences.insert(newReference)
	  })
	  
	  // Copy course links
	  val courseLinks = SqlCourseLinks.selectWhere("Course = " + orignalCourseId)
	  courseLinks.foreach(courseLink => {
	    val newCourseLink = new MdlCourseLinks(0, newCourseId, 	courseLink.vLink, courseLink.vDisplayDescription, courseLink.vIsFileLink, courseLink.vFaculty)
	    SqlCourseLinks.insert(newCourseLink)
	  })
	  
	  // Copy lessons
	  val lessons = AppDB.dal.Lessons.all.filter(lsn => lsn.vidCourse == orignalCourseId)
	  lessons.foreach(lesson =>{
	    val newLesson = new MdlLessonsSlick(Option(0), lesson.vLessonNumber, lesson.vLessonName, lesson.vAssignment, lesson.vLocation,
	    		newCourseId, lesson.vDuration, lesson.vLab, lesson.vLessonSummary)
	    val newLessonId = AppDB.dal.Lessons.insert(newLesson)
	    
	    // Copy lesson links
	    val lessonLinks =  AppDB.dal.LessonLinks.all.filter(lsnLink => lsnLink.vLesson == lesson.vLessonIndex.get)        
	    lessonLinks.foreach(lessonLink => {
	      val newLessonLink = new MdlLessonLinks(Option(0), lessonLink.vLink, lessonLink.vDescription, lessonLink.vIsFileLiink, newLessonId, lessonLink.vFaculty)
	      val newLessonLinkId = AppDB.dal.LessonLinks.insert(newLessonLink)
	      
	      // Copy knowledge, skills and behaviors associated with each link
	      val lessonLinkKSAs = SqlLessonLinkTopicObjectives.selectWhere("LessonLink = " + lessonLink.vLessonLinkNumber.get)
	      lessonLinkKSAs.foreach(lessonLinkKSA => {
	        val newLessonLinkKSA = new MdlLessonLinkTopicObjectives(0, newLessonLinkId, lessonLinkKSA.vTopicObjective)
	        SqlLessonLinkTopicObjectives.insert(newLessonLinkKSA)
	      })
	    })
	    
	    // Copy Knowledge Skills and Behaviors for this lesson
	    val lessonKSAs = AppDB.dal.LessonKSA.selectByLesson(lesson.vLessonIndex.get)
	    lessonKSAs.foreach(lessonKSA => {
	      val newLessonKSA = new MdlLessonKSA(Option(0), newLessonId, lessonKSA.vTopicObjective)
	      AppDB.dal.LessonKSA.insert(newLessonKSA)
	    })	    
	  })
	  
	  // Copy Graded Events
	  val gradedEvents = AppDB.dal.GradedRequirements.selectByCourse(orignalCourseId)
	  gradedEvents.foreach(gradedEvent => {
	    val newGradedEvent = new MdlGradedRequirements(Option(0), newCourseId, gradedEvent.vGradedEventName, gradedEvent.vEventDescription, gradedEvent.vTypeOfEvent,
	    		gradedEvent.vPoints, gradedEvent.vLessonassigned, gradedEvent.vLessoncompleted)	
	    val newGradedEventId = AppDB.dal.GradedRequirements.insert(newGradedEvent)
	    
	    // Copy graded event links
	    val gradedEventLinks = AppDB.dal.GradedRequirementLinks.selectByGradedRequirement(gradedEvent.vGradedEventIndex.get)
	    gradedEventLinks.foreach(gradedEventLink=> {
	      val newGradedEventLink = new MdlGradedRequirementLinks(Option(0), gradedEventLink.vLink, gradedEventLink.vDescription, 
	          gradedEventLink.vIsFileLink, newGradedEventId)
	      AppDB.dal.GradedRequirementLinks.insert(newGradedEventLink)
	    })
	    
	    // Copy Knowledge, Skills, and Behaviors for this Graded Event
	    val gradedEventKSAs = AppDB.dal.KSAGradedEvent.selectByGradedEvent(gradedEvent.vGradedEventIndex.get)
	    gradedEventKSAs.foreach(gradedEventKSA => {
	      val newGradedEventKSA = new MdlKSAGradedEvent(Option(0), gradedEventKSA.vKSA, newGradedEventId)
	      AppDB.dal.KSAGradedEvent.insert(newGradedEventKSA)
	    })
	    
	    // Copy Sub-Events
	    val subEvents = AppDB.dal.SubGradedEvent.selectByGradedEvent(gradedEvent.vGradedEventIndex.get)
	    subEvents.foreach(subEvent=> {
	      val newSubEvent = new MdlSubGradedEvent(Option(0), newGradedEventId, subEvent.vDescription, subEvent.vPoints)
	      val newSubEventId = AppDB.dal.SubGradedEvent.insert(newSubEvent)
	      
	      // Copy Knowledge Skill, and Behaviors for Sub Event
	      val subEventKSAs = AppDB.dal.KSASubGradedEvent.selectBySubGradedEvent(subEvent.vidSubGradedEvent.get)
	      subEventKSAs.foreach(subEventKSA => {
	        val newSubEventKSA = new MdlKSASubGradedEvent(Option(0), subEventKSA.vKSA, newSubEventId)
	        AppDB.dal.KSASubGradedEvent.insert(newSubEventKSA)
	      })
	    })
	  })
	  
	 
  }

  def homeCourses(id: Long) = compositeAction(NormalUser) {implicit user => implicit template => implicit request =>
    Ok(viewhome.html.homeCourses(AppDB.dal.Courses.select(id).get))
  }
  


  def saveCourses(newEntry: Int, copyEntry: Int, originalCourse: Long) = Action { implicit request =>
    form.bindFromRequest.fold(
      form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vCourses => {
        if (vCourses.validate) {
          val newCourseId = newEntry match {
            case 0 => {
              AppDB.dal.Courses.update(vCourses)
              vCourses.vidCourse.get
            }
            case _ => {
              println("Copying new course")
              val newId = AppDB.dal.Courses.insert(vCourses)
              if (copyEntry != 0) {
                copyCourseData(originalCourse, newId)
              }
              newId
            }
          }
          Redirect(routes.CoursesController.homeCourses(newCourseId))
        } else {
          val validationErrors = vCourses.validationErrors
          Logger.debug(validationErrors)
          BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }

  def newItem(fkId: Long) = new MdlCourses

}
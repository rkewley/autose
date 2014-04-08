
package slick

import models.MdlSubEventAMS
import models.Mdl

trait SubEventAMSComponent  {
	this: Profile =>
	  
	import profile.simple._

	object SubEventAMS extends Table[MdlSubEventAMS]("SubEventAMS") with Crud[MdlSubEventAMS, Long]  {

      def vidSubEventAMS = column[Long]("idSubEventAMS", O.PrimaryKey, O.AutoInc)
      def vGradedEventNumberAMS = column[Long]("GradedEventNumberAMS")
      def vSubEventNumberAMS = column[Long]("SubEventNumberAMS")
      def vGradedEvent = column[Long]("GradedEvent")
      def vName = column[String]("Name")
      def vDescription = column[String]("Description")
      def vPoints = column[Double]("Points")
      def * = vidSubEventAMS.? ~ vGradedEventNumberAMS ~ vSubEventNumberAMS ~ vGradedEvent ~ vName ~ vDescription ~ vPoints<> (MdlSubEventAMS.apply _, MdlSubEventAMS.unapply _)
      def forInsert = vGradedEventNumberAMS ~ vSubEventNumberAMS ~ vGradedEvent ~ vName ~ vDescription ~ vPoints <> 
      ({t => MdlSubEventAMS(None , t._1, t._2, t._3, t._4, t._5, t._6)},
      {(vSubEventAMS: MdlSubEventAMS) => Some(vSubEventAMS.vGradedEventNumberAMS, vSubEventAMS.vSubEventNumberAMS, vSubEventAMS.vGradedEvent, vSubEventAMS.vName, vSubEventAMS.vDescription, vSubEventAMS.vPoints)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(SubEventAMS)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(SubEventAMS)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(SubEventAMS)
	      q.filter(p => p.vidSubEventAMS === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
      def selectByGradedEventAMS(gradedEventAMS: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(SubEventAMS)
	      q.filter(p => p.vGradedEvent === gradedEventAMS).elements.toList
	    }
	  }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vSubEventAMS: MdlSubEventAMS): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          SubEventAMS.forInsert returning SubEventAMS.vidSubEventAMS insert MdlSubEventAMS(None, vSubEventAMS.vGradedEventNumberAMS, vSubEventAMS.vSubEventNumberAMS, vSubEventAMS.vGradedEvent, vSubEventAMS.vName, vSubEventAMS.vDescription, vSubEventAMS.vPoints)

	    }
	  }
    

	  def update(vSubEventAMS: MdlSubEventAMS) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vSubEventAMS.vidSubEventAMS.get)
	      val q2 = q.map(vSubEventAMS => vSubEventAMS.vGradedEventNumberAMS ~ vSubEventAMS.vSubEventNumberAMS ~ vSubEventAMS.vGradedEvent ~ vSubEventAMS.vName ~ vSubEventAMS.vDescription ~ vSubEventAMS.vPoints)
	      q2.update(vSubEventAMS.vGradedEventNumberAMS, vSubEventAMS.vSubEventNumberAMS, vSubEventAMS.vGradedEvent, vSubEventAMS.vName, vSubEventAMS.vDescription, vSubEventAMS.vPoints)
	    }
	  }

    def getAverage(id: Long) = {
	  AppDB.database.withSession { implicit session: Session =>
	      val event = select(id).get
	      val grades = AppDB.dal.GradesSubEventAMS.getGradesForSubEvent(id)
	      val total: Double = grades.foldLeft(0.0) { (total, event) =>
	        total + event.vPoints
	      }
	      total / grades.length
	  }
    }
    
    def getAverageForEventandProgram(eventId: Long, programId: Long) = {
	  AppDB.database.withSession { implicit session: Session =>
	      val cadets = AppDB.dal.Students.all
	      val grades = AppDB.dal.GradesSubEventAMS.getGradesForSubEvent(eventId).filter{grade => 
	        val cadet = cadets.find{cadet => cadet.vStudentId == grade.vStudentID}
	        cadet match {
	          case Some(c) => c.vProgram == programId
	          case None => false
	        }
	      }
	      val total: Double = grades.foldLeft(0.0) { (total, event) =>
	        total + event.vPoints
	      }
	      total / grades.length
		  }
    }
    
    def getPercentageForEventAndProgram(eventId: Long, programId: Long) = {
	  AppDB.database.withSession { implicit session: Session =>
	      val event = AppDB.dal.SubEventAMS.select(eventId).get
	      (getAverageForEventandProgram(eventId, programId) / event.vPoints) * 100    
	  }
    }

    def getPercentage(id: Long) = {
	  AppDB.database.withSession { implicit session: Session =>
	      val event = AppDB.dal.SubEventAMS.select(id).get
	      (getAverage(id) / event.vPoints) * 100
	  }
    }
    
    def selectByCourse(idCourse: Long) = {

	    AppDB.database.withSession { implicit session: Session =>
	      val result1 = for {
	        (subEvent, gradedEvent) <- AppDB.dal.SubEventAMS innerJoin AppDB.dal.GradedEventAMS on (_.vGradedEvent === _.vidGradedEventAMS)
	           if gradedEvent.vCourse === idCourse
	      } yield (subEvent.vidSubEventAMS, subEvent.vGradedEventNumberAMS, subEvent.vSubEventNumberAMS, subEvent.vGradedEvent, subEvent.vName, subEvent.vDescription, subEvent.vPoints)
	      result1.elements.toList.map { element =>
	        MdlSubEventAMS(Some(element._1), element._2, element._3, element._4, element._5, element._6, element._7)
	      }
 
      }
      
    }
    
    def getSelectionString(idSubEvent: Long) = {
	  AppDB.database.withSession { implicit session: Session =>
	      val subEvent = select(idSubEvent).get
	      val gradedEvent = AppDB.dal.GradedEventAMS.select(subEvent.vGradedEvent).get
	      val course = AppDB.dal.Courses.select(gradedEvent.vCourse).get
	      (course.selectIdentifier._2 + ", " + gradedEvent.vName + ", " + subEvent.vDescription)
	  }
    }
	  

	}
}

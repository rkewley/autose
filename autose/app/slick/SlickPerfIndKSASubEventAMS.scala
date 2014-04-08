
package slick

import models.MdlPerfIndKSASubEventAMS
import models._

trait PerfIndKSASubEventAMSComponent  {
	this: Profile =>
	  
	import profile.simple._

	object PerfIndKSASubEventAMS extends Table[MdlPerfIndKSASubEventAMS]("PerfIndKSASubEventAMS") with Crud[MdlPerfIndKSASubEventAMS, Long]  {

      def vidPerfIndKSASubEventAMS = column[Long]("idPerfIndKSASubEventAMS", O.PrimaryKey, O.AutoInc)
      def vPerformanceIndicator = column[Long]("PerformanceIndicator")
      def vKSASubEventAMS = column[Long]("KSASubEventAMS")
      def * = vidPerfIndKSASubEventAMS.? ~ vPerformanceIndicator ~ vKSASubEventAMS<> (MdlPerfIndKSASubEventAMS.apply _, MdlPerfIndKSASubEventAMS.unapply _)
      def forInsert = vPerformanceIndicator ~ vKSASubEventAMS <> 
      ({t => MdlPerfIndKSASubEventAMS(None , t._1, t._2)},
      {(vPerfIndKSASubEventAMS: MdlPerfIndKSASubEventAMS) => Some(vPerfIndKSASubEventAMS.vPerformanceIndicator, vPerfIndKSASubEventAMS.vKSASubEventAMS)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(PerfIndKSASubEventAMS)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(PerfIndKSASubEventAMS)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(PerfIndKSASubEventAMS)
	      q.filter(p => p.vidPerfIndKSASubEventAMS === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
      
      case class SubEventData(val idPerfIndKSASubEventAMS: Long, val subEventId: Long, val courseId: Long, val courseName: String, val gradedEventId: Long, val gradedEventName: String, val subEventDescription: String)
      
      def selectByKSBandPI(ksbId: Long, perfIndId: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
        val subEvents = for {
	      (piKsaSubEvent, ksaSubEventAMS) <- AppDB.dal.PerfIndKSASubEventAMS innerJoin AppDB.dal.KSASubEventAMS on (_.vKSASubEventAMS === _.vidKSASubEventAMS)
	      	if(ksaSubEventAMS.vKSA === ksbId && piKsaSubEvent.vPerformanceIndicator === perfIndId)
	      } yield (ksaSubEventAMS.vSubEventAMS, piKsaSubEvent.vidPerfIndKSASubEventAMS)
	      //println("Sub-events for KSA = " + ksbId + ": ")
	      //subEvents.foreach(println)
	      val subEventsList = subEvents.elements.toList
	      subEventsList.map { subEventId =>
	        val subEvent: MdlSubEventAMS = AppDB.dal.SubEventAMS.select(subEventId._1).get
	        val gradedEvent = AppDB.dal.GradedEventAMS.select(subEvent.vGradedEvent).get
	        val course = AppDB.dal.Courses.select(gradedEvent.vCourse).get
	        SubEventData(subEventId._2, subEventId._1, course.vidCourse.get, course.selectIdentifier._2, gradedEvent.vidGradedEventAMS.get, gradedEvent.vName, subEvent.vDescription)
	      }
	    }
      }
      
      def averageByPI(perfIndId: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
        val ksbQuery = for {
	      (piKsaSubEvent, ksaSubEventAMS) <- AppDB.dal.PerfIndKSASubEventAMS innerJoin AppDB.dal.KSASubEventAMS on (_.vKSASubEventAMS === _.vidKSASubEventAMS)
	      	if(piKsaSubEvent.vPerformanceIndicator === perfIndId)
	    } yield (ksaSubEventAMS.vKSA)
	    val ksbList = ksbQuery.elements.toList
	    val total: Double = ksbList.foldLeft(0.0) {(total, ksb) =>
	      total + averageByKSBandPerfIndicator(ksb, perfIndId)
	    }
	    total / ksbList.length
	    }
      }
      
      def averageByKSBandPerfIndicator(ksbId: Long, perfIndId: Long) = {
        val subEvents = selectByKSBandPI(ksbId, perfIndId)
        val pi = AppDB.dal.PerformanceIndicator.select(perfIndId).get
        val programId = AppDB.dal.ProgramOutcomes.select(pi.vProgramOutcome).get.vProgram
        val total: Double = subEvents.map(subEvent => AppDB.dal.SubEventAMS.getPercentageForEventAndProgram(subEvent.subEventId, programId)).foldLeft(0.0) { (total, avgScore) =>
          total + avgScore
        }
        total / subEvents.length       
      }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vPerfIndKSASubEventAMS: MdlPerfIndKSASubEventAMS): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      PerfIndKSASubEventAMS.forInsert returning PerfIndKSASubEventAMS.vidPerfIndKSASubEventAMS insert MdlPerfIndKSASubEventAMS(None, vPerfIndKSASubEventAMS.vPerformanceIndicator, vPerfIndKSASubEventAMS.vKSASubEventAMS)

	    }
	  }
    

	  def update(vPerfIndKSASubEventAMS: MdlPerfIndKSASubEventAMS) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vPerfIndKSASubEventAMS.vidPerfIndKSASubEventAMS.get)
	      val q2 = q.map(vPerfIndKSASubEventAMS => vPerfIndKSASubEventAMS.vPerformanceIndicator ~ vPerfIndKSASubEventAMS.vKSASubEventAMS)
	      q2.update(vPerfIndKSASubEventAMS.vPerformanceIndicator, vPerfIndKSASubEventAMS.vKSASubEventAMS)
	    }
	  }

	}
}

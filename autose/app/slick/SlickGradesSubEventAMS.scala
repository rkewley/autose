
package slick

import models.MdlGradesSubEventAMS
import models.Mdl

trait GradesSubEventAMSComponent {
  this: Profile =>

  import profile.simple._

  object GradesSubEventAMS extends Table[MdlGradesSubEventAMS]("GradesSubEventAMS") with Crud[MdlGradesSubEventAMS, Long] {

    def vidGradesSubEventAMS = column[Long]("idGradesSubEventAMS", O.PrimaryKey, O.AutoInc)
    def vStudent = column[Long]("Student")
    def vSubEventAMS = column[Long]("SubEventAMS")
    def vPoints = column[Double]("Points")
    def vStudentID = column[String]("StudentID")
    def * = vidGradesSubEventAMS.? ~ vStudent ~ vSubEventAMS ~ vPoints ~ vStudentID <> (MdlGradesSubEventAMS.apply _, MdlGradesSubEventAMS.unapply _)
    def forInsert = vStudent ~ vSubEventAMS ~ vPoints ~ vStudentID <>
      ({ t => MdlGradesSubEventAMS(None, t._1, t._2, t._3, t._4) },
        { (vGradesSubEventAMS: MdlGradesSubEventAMS) => Some(vGradesSubEventAMS.vStudent, vGradesSubEventAMS.vSubEventAMS, vGradesSubEventAMS.vPoints, vGradesSubEventAMS.vStudentID) })

    def allQuery = {
      AppDB.database.withSession { implicit session: Session =>
        Query(GradesSubEventAMS)
      }
    }

    def all = {
      AppDB.database.withSession { implicit session: Session =>
        val q = Query(GradesSubEventAMS)
        q.elements.toList
      }
    }

    def selectQuery(pk: Long)(implicit session: Session) = {
      val q = Query(GradesSubEventAMS)
      q.filter(p => p.vidGradesSubEventAMS === pk)
    }

    def select(pk: Long) = {
      AppDB.database.withSession { implicit session: Session =>
        selectQuery(pk).elements.toList.headOption
      }
    }

    def delete(pk: Long) {
      AppDB.database.withSession { implicit session: Session =>
        selectQuery(pk).delete
      }
    }

    def insert(vGradesSubEventAMS: MdlGradesSubEventAMS): Long = {
      AppDB.database.withSession { implicit session: Session =>
        GradesSubEventAMS.forInsert returning GradesSubEventAMS.vidGradesSubEventAMS insert MdlGradesSubEventAMS(None, vGradesSubEventAMS.vStudent, vGradesSubEventAMS.vSubEventAMS, vGradesSubEventAMS.vPoints, vGradesSubEventAMS.vStudentID)

      }
    }

    def update(vGradesSubEventAMS: MdlGradesSubEventAMS) {
      AppDB.database.withSession { implicit session: Session =>
        val q = selectQuery(vGradesSubEventAMS.vidGradesSubEventAMS.get)
        val q2 = q.map(vGradesSubEventAMS => vGradesSubEventAMS.vStudent ~ vGradesSubEventAMS.vSubEventAMS ~ vGradesSubEventAMS.vPoints ~ vGradesSubEventAMS.vStudentID)
        q2.update(vGradesSubEventAMS.vStudent, vGradesSubEventAMS.vSubEventAMS, vGradesSubEventAMS.vPoints, vGradesSubEventAMS.vStudentID)
      }
    }

    def getGradesForSubEvent(id: Long): List[MdlGradesSubEventAMS] = {
      AppDB.database.withSession { implicit session: Session =>
        val q = Query(GradesSubEventAMS)
        q.filter(p => p.vSubEventAMS === id).elements.toList
      }
    }
  }

}

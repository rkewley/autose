
package slick

import models.MdlElectiveGroup
import models.Mdl

trait ElectiveGroupComponent  {
	this: Profile =>
	  
	import profile.simple._

	object ElectiveGroup extends Table[MdlElectiveGroup]("ElectiveGroup") with Crud[MdlElectiveGroup, Long]  {

      def vidElectiveGroup = column[Long]("idElectiveGroup", O.PrimaryKey, O.AutoInc)
      def vElectiveGroupName = column[String]("ElectiveGroupName")
      def vNumberToChoose = column[Long]("NumberToChoose")
      def vProgram = column[Long]("Program")
      def vSubDiscipline = column[Boolean]("SubDiscipline")
      def * = vidElectiveGroup.? ~ vElectiveGroupName ~ vNumberToChoose ~ vProgram ~ vSubDiscipline<> (MdlElectiveGroup.apply _, MdlElectiveGroup.unapply _)
      def forInsert = vElectiveGroupName ~ vNumberToChoose ~ vProgram ~ vSubDiscipline <> 
      ({t => MdlElectiveGroup(None , t._1, t._2, t._3, t._4)},
      {(vElectiveGroup: MdlElectiveGroup) => Some(vElectiveGroup.vElectiveGroupName, vElectiveGroup.vNumberToChoose, vElectiveGroup.vProgram, vElectiveGroup.vSubDiscipline)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(ElectiveGroup)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(ElectiveGroup)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(ElectiveGroup)
	      q.filter(p => p.vidElectiveGroup === pk)
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

	  def insert(vElectiveGroup: MdlElectiveGroup): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      ElectiveGroup.forInsert returning ElectiveGroup.vidElectiveGroup insert MdlElectiveGroup(None, vElectiveGroup.vElectiveGroupName, vElectiveGroup.vNumberToChoose, vElectiveGroup.vProgram, vElectiveGroup.vSubDiscipline)

	    }
	  }
    

	  def update(vElectiveGroup: MdlElectiveGroup) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vElectiveGroup.vidElectiveGroup.get)
	      val q2 = q.map(vElectiveGroup => vElectiveGroup.vElectiveGroupName ~ vElectiveGroup.vNumberToChoose ~ vElectiveGroup.vProgram ~ vElectiveGroup.vSubDiscipline)
	      q2.update(vElectiveGroup.vElectiveGroupName, vElectiveGroup.vNumberToChoose, vElectiveGroup.vProgram, vElectiveGroup.vSubDiscipline)
	    }
	  }

	}
}

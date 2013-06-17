
package slick

import models.MdlProgramEducationalObjectives
import models.Mdl

trait ProgramEducationalObjectivesComponent  {
	this: Profile =>
	  
	import profile.simple._

	object ProgramEducationalObjectives extends Table[MdlProgramEducationalObjectives]("ProgramEducationalObjectives") with Crud[MdlProgramEducationalObjectives, Long]  {

      def vProgObjNumber = column[Long]("ProgObjNumber", O.PrimaryKey)
      def vProgram = column[Long]("Program")
      def vProgramEducationalObjective = column[String]("ProgramEducationalObjective")
      def * = vProgObjNumber.? ~ vProgram ~ vProgramEducationalObjective<> (MdlProgramEducationalObjectives.apply _, MdlProgramEducationalObjectives.unapply _)
      def forInsert = vProgram ~ vProgramEducationalObjective <> 
      ({t => MdlProgramEducationalObjectives(None , t._1, t._2)},
      {(vProgramEducationalObjectives: MdlProgramEducationalObjectives) => Some(vProgramEducationalObjectives.vProgram, vProgramEducationalObjectives.vProgramEducationalObjective)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(ProgramEducationalObjectives)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(ProgramEducationalObjectives)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(ProgramEducationalObjectives)
	      q.filter(p => p.vProgObjNumber === pk)
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

	  def insert(vProgramEducationalObjectives: MdlProgramEducationalObjectives) {
	    AppDB.database.withSession { implicit session: Session =>
	      ProgramEducationalObjectives.forInsert insert MdlProgramEducationalObjectives(None, vProgramEducationalObjectives.vProgram, vProgramEducationalObjectives.vProgramEducationalObjective)

	    }
	  }
    

	  def update(vProgramEducationalObjectives: MdlProgramEducationalObjectives) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vProgramEducationalObjectives.vProgObjNumber.get)
	      val q2 = q.map(vProgramEducationalObjectives => vProgramEducationalObjectives.vProgram ~ vProgramEducationalObjectives.vProgramEducationalObjective)
	      q2.update(vProgramEducationalObjectives.vProgram, vProgramEducationalObjectives.vProgramEducationalObjective)
	    }
	  }

	}
}

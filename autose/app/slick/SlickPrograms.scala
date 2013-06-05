package slick


import models.MdlPrograms
import models.Mdl

trait ProgramsComponent  {
	this: Profile =>
	  
	import profile.simple._

	object Programs extends Table[MdlPrograms]("Programs") with Crud[MdlPrograms, Long]  {
	  def vidPrograms = column[Long]("idPrograms", O.PrimaryKey)
	  def vProgram = column[String]("Program")
	  def vName = column[String]("Name")
	  def vSlogan = column[String]("Slogan")
	  def vInformation = column[String]("Information")
	  def vProgramDirector = column[Long]("ProgramDirector")
	  def vDepartment = column[Long]("Department")
	  
	  def * = vidPrograms.? ~ vProgram ~ vName ~ vSlogan ~ vInformation ~ vProgramDirector ~ vDepartment <> (MdlPrograms.apply _, MdlPrograms.unapply _)
	  def forInsert = vProgram ~ vName ~ vSlogan ~ vInformation ~ vProgramDirector ~ vDepartment <> 
	  ({t => MdlPrograms(None, t._1, t._2, t._3, t._4, t._5, t._6)}, 
	      {(vPrograms: MdlPrograms) => Some(vPrograms.vProgram, vPrograms.vName, vPrograms.vSlogan, vPrograms.vInformation, vPrograms.vProgramDirector, vPrograms.vDepartment)})
	  
	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(Programs)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(Programs)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(Programs)
	      q.filter(p => p.vidPrograms === pk)
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
	  
	  def insert(vPrograms: MdlPrograms) {
	    println("Inserting " + vPrograms.toString)
	    AppDB.database.withSession { implicit session: Session =>
	      Programs.forInsert insert MdlPrograms(None, vPrograms.vProgram, vPrograms.vName, vPrograms.vSlogan, vPrograms.vInformation, vPrograms.vProgramDirector, vPrograms.vDepartment)
	    }
	  }
	  
	  def update(vPrograms: MdlPrograms) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vPrograms.vidPrograms.get)
	      val q2 = q.map(v => v.vProgram ~ v.vName ~ v.vSlogan ~ v.vInformation ~ v.vProgramDirector ~ v.vDepartment)
	      q2.update(vPrograms.vProgram, vPrograms.vName, vPrograms.vSlogan, vPrograms.vInformation, vPrograms.vProgramDirector, vPrograms.vDepartment)
	    }
	  }
	  
	}
}
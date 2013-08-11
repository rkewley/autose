
package slick

import models.MdlDefinitions
import models.Mdl

trait DefinitionsComponent  {
	this: Profile =>
	  
	import profile.simple._

	object Definitions extends Table[MdlDefinitions]("Definitions") with Crud[MdlDefinitions, Long]  {

      def vidDefinitions = column[Long]("idDefinitions", O.PrimaryKey, O.AutoInc)
      def vWord = column[String]("Word")
      def vDefinition = column[String]("Definition")
      def * = vidDefinitions.? ~ vWord ~ vDefinition<> (MdlDefinitions.apply _, MdlDefinitions.unapply _)
      def forInsert = vWord ~ vDefinition <> 
      ({t => MdlDefinitions(None , t._1, t._2)},
      {(vDefinitions: MdlDefinitions) => Some(vDefinitions.vWord, vDefinitions.vDefinition)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(Definitions)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(Definitions)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(Definitions)
	      q.filter(p => p.vidDefinitions === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
      def select(word: String) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(Definitions)
	      q.filter(p => p.vWord === word).elements.toList.headOption
	    }
	  }
      
      def getDefinition(word: String) = {
        select(word) match {
          case Some(definition) => definition.vDefinition
          case None => word + " not defined"
        }
      }
      
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vDefinitions: MdlDefinitions): Long =  {
	    AppDB.database.withSession { implicit session: Session =>
	      Definitions.forInsert returning Definitions.vidDefinitions insert MdlDefinitions(None, vDefinitions.vWord, vDefinitions.vDefinition)
	    }
	  }
    

	  def update(vDefinitions: MdlDefinitions) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vDefinitions.vidDefinitions.get)
	      val q2 = q.map(vDefinitions => vDefinitions.vWord ~ vDefinitions.vDefinition)
	      q2.update(vDefinitions.vWord, vDefinitions.vDefinition)
	    }
	  }

	}
}

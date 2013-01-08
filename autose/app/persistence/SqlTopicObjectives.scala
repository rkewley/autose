
package persistence

import models.MdlTopicObjectives
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.Logger

object SqlTopicObjectives {

  val vTopicObjectives = {
    get[Long]("TopicObjectiveNumber") ~
	get[String]("Objective") ~
	get[Long]("Topic") ~
	get[String]("KSAB") map { case
    vTopicObjectiveNumber ~
		vObjective ~
		vTopic ~
		vKSAB =>
    MdlTopicObjectives(vTopicObjectiveNumber,
		vObjective,
		vTopic,
		vKSAB)
    }
  }
      def fullTopicName(vTopicObjective: MdlTopicObjectives): String = {
        val topicName = SqlTopics.select(vTopicObjective.vTopic).vTopic
        topicName + ": " + vTopicObjective.vObjective
      }
      
      def shortenedTopicName(fullName: String, maxLength: Int): String = {
        fullName.size match {
          case x if x > maxLength => fullName.substring(0, maxLength-3) + "..."
          case (_) => fullName
        }        
      }

      def shortenedTopicNameFor(vTopicObjective: MdlTopicObjectives, maxLength: Int): String = {
        shortenedTopicName(fullTopicName(vTopicObjective), maxLength)
      }      
    def getOrderedTopicObjectiveListFor(topicObjectives: List[MdlTopicObjectives], maxSize: Option[Int]): Seq[(String, String)] = {
      def compareTopics(a: (String, String), b: (String, String)) = a._2 < b._2
      topicObjectives.map(vTopicObjective => 
        vTopicObjective.vTopicObjectiveNumber.toString-> {
          val fullName = fullTopicName(vTopicObjective)
          maxSize match {
            case Some(x) => shortenedTopicName(fullName, x)
            case None => fullName
          }	  
        }
      ).sortWith(compareTopics).toSeq
    }
    
    def getOrderedTopicObjectiveList(maxSize: Option[Int]): Seq[(String, String)]  = getOrderedTopicObjectiveListFor(all, maxSize)
    
  	def all: List[MdlTopicObjectives] = DB.withConnection { implicit c =>
  		SQL("select * from `TopicObjectives`").as(vTopicObjectives *)
	}

	def select(vTopicObjectiveNumber: Long): MdlTopicObjectives = DB.withConnection { implicit c =>
  		SQL("select * from `TopicObjectives` WHERE `TopicObjectiveNumber` = {sqlTopicObjectiveNumber}").on(
  			'sqlTopicObjectiveNumber -> vTopicObjectiveNumber).as(vTopicObjectives *).head
	}

  	def selectWhere(where: String): List[MdlTopicObjectives] = DB.withConnection { implicit c =>
  		SQL("select * from `TopicObjectives` WHERE " + where).as(vTopicObjectives *)
	}

	def delete(vTopicObjectiveNumber: Long) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `TopicObjectives` WHERE `TopicObjectiveNumber` = {sqlTopicObjectiveNumber}").on(
      'sqlTopicObjectiveNumber -> vTopicObjectiveNumber
  		).executeUpdate()
    }

	def insert(vTopicObjectives: MdlTopicObjectives) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `TopicObjectives` (`Objective`, `Topic`, `KSAB`) VALUES ({sqlObjective}, {sqlTopic}, {sqlKSAB})").on('sqlObjective -> vTopicObjectives.vObjective, 'sqlTopic -> vTopicObjectives.vTopic, 'sqlKSAB -> vTopicObjectives.vKSAB).executeInsert()
	}

	def update(vTopicObjectives: MdlTopicObjectives) = DB.withConnection { implicit c =>
  		SQL("UPDATE `TopicObjectives` SET `Objective` = {sqlObjective}, `Topic` = {sqlTopic}, `KSAB` = {sqlKSAB} WHERE `TopicObjectiveNumber` = {sqlTopicObjectiveNumber}").on('sqlTopicObjectiveNumber -> vTopicObjectives.vTopicObjectiveNumber, 'sqlObjective -> vTopicObjectives.vObjective, 'sqlTopic -> vTopicObjectives.vTopic, 'sqlKSAB -> vTopicObjectives.vKSAB).executeUpdate()

  }

}
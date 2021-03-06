    
 package models
    
 case class MdlLessonLinkTopicObjectives (
 	vidLessonLinkTopicObjective : Long,
	vLessonLink : Long,
	vTopicObjective : Long
    )  {
    
      def this() = this(0, 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidLessonLinkTopicObjective.toString -> vidLessonLinkTopicObjective.toString
    
      def compare(a: MdlLessonLinkTopicObjectives, b: MdlLessonLinkTopicObjectives) = a.vidLessonLinkTopicObjective.compareTo(b.vidLessonLinkTopicObjective)
}
 
 case class MdlLessonLinkTopicObjectivesList (
 	vidLessonLinkTopicObjective : Long,
	vLessonLink : Long,
	vTopicObjective : List[Long]
    ) {
    
    def getList = {
      vTopicObjective.map(topicObjective => 
        new MdlLessonLinkTopicObjectives(vidLessonLinkTopicObjective, vLessonLink, topicObjective))
    }
 }
    
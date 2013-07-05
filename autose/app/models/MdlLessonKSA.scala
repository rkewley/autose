    
 package models
    
 case class MdlLessonKSA (
 	vidLessonTopicObjectives : Option[Long],
	vLesson : Long,
	vTopicObjective : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidLessonTopicObjectives.get.toString -> vidLessonTopicObjectives.get.toString
    
      def compare(a: MdlLessonKSA, b: MdlLessonKSA) = a.vidLessonTopicObjectives.get.compareTo(b.vidLessonTopicObjectives.get)
        
      def primaryKey = vidLessonTopicObjectives
}
    
    
 package models
 
 object GradedEventAMS {
   def compare(a: MdlGradedEventAMS, b: MdlGradedEventAMS): Boolean = {
     a.vLesson < b.vLesson
   }
 }
    
 case class MdlGradedEventAMS (
 	vidGradedEventAMS : Option[Long],
	vEventNumberAMS : Long,
	vCourse : Long,
	vName : String,
	vDescription : String,
	vDetailedDescription: String,
	vType : String,
	vMaxPoints : Double,
	vLesson : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0, "", "", "", "", 0.0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidGradedEventAMS.get.toString -> (vName + ", " + vDescription) 
    
      def compare(a: MdlGradedEventAMS, b: MdlGradedEventAMS) = a.vidGradedEventAMS.get.compareTo(b.vidGradedEventAMS.get)
        
      def primaryKey = vidGradedEventAMS
}
    
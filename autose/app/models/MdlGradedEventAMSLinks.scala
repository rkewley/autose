    
 package models
    
 case class MdlGradedEventAMSLinks (
 	vidGradedEventAMSLinks : Option[Long],
	vLink : String,
	vDescription : String,
	vIsFileLink : Boolean,
	vvGradedEventAMS : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", "", false, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidGradedEventAMSLinks.get.toString -> vidGradedEventAMSLinks.get.toString
    
      def compare(a: MdlGradedEventAMSLinks, b: MdlGradedEventAMSLinks) = a.vidGradedEventAMSLinks.get.compareTo(b.vidGradedEventAMSLinks.get)
        
      def primaryKey = vidGradedEventAMSLinks
}
    
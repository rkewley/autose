    
 package models
    
 case class MdlGradedRequirementLinks (
 	vidGradedRequirementLinks : Option[Long],
	vLink : String,
	vDescription : String,
	vIsFileLink : Boolean,
	vGradedRequirement : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", "", false, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidGradedRequirementLinks.get.toString -> vDescription
    
      def compare(a: MdlGradedRequirementLinks, b: MdlGradedRequirementLinks) = a.vidGradedRequirementLinks.get.compareTo(b.vidGradedRequirementLinks.get)
        
      def primaryKey = vidGradedRequirementLinks
}
    
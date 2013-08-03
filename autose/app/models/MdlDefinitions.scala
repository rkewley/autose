    
 package models
    
 case class MdlDefinitions (
 	vidDefinitions : Option[Long],
	vWord : String,
	vDefinition : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidDefinitions.get.toString -> vidDefinitions.get.toString
    
      def compare(a: MdlDefinitions, b: MdlDefinitions) = a.vidDefinitions.get.compareTo(b.vidDefinitions.get)
        
      def primaryKey = vidDefinitions
}
    
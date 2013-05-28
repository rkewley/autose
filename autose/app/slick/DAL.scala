package slick

import scala.slick.driver.ExtendedProfile
 
class DAL(override val profile: ExtendedProfile) extends ProgramsComponent with Profile {
 
  import profile.simple._
 
}
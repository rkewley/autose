package slick

import scala.slick.driver.ExtendedProfile
 
class DAL(override val profile: ExtendedProfile) extends ProgramsComponent 
	with ProgramOutcomesComponent 
	with GradedRequirementsComponent
	with EventTypeComponent
	with SubGradedEventComponent
	with KSAGradedEventComponent
	with KSASubGradedEventComponent
	with KSAPerfIndicatorComponent
	with PerfIndProgramOutcomesComponent
	with PerformanceIndicatorComponent
	with GradedRequirementLinksComponent
	with Profile {
 
  import profile.simple._
 
}
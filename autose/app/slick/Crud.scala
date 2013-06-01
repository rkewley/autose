package slick

trait Crud[Mdl, K] {
  def all: List[Mdl]
  def select(id: K): Option[Mdl]
  def delete(id: K): Mdl
  def insert(item: Mdl)
  def update(item: Mdl)
  def selectWhere(sql: String)
}
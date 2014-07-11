package model

case class Frequency(word: String) {
  private var _count: Long = 0
  def increment = _count = _count + 1 
  def count = _count
}

case class Group(label: String, values: Seq[String])

case class Histogram(groupings: Seq[Group]) {
  type Filter = ((String, Frequency)) => Boolean
  
  // Invert the stack to associate a value with a label
  private val values = groupings.flatMap(group => group.values.map(_ -> group.label)).toMap
  
  private val histogram = scala.collection.mutable.HashMap[String, Frequency]()
  
  
  /** Cast to lower case, get element out of values map, if exists then use the group label to increment
   */
  def increment(word: String) = values.get(word.toLowerCase).map {
      group => histogram.getOrElse(group, {val f = new Frequency(group); histogram.put(group, new Frequency(group)); f}).increment
    }
  
  def get = histogram.map(f => f._1 -> f._2).toMap
  
  private def filterOutOnes(in: (String, Frequency)) : Boolean = in._2.count > 1

  /** To string with a filter and string builder to help abbreviated output
   */
  
  def toString(builder: StringBuilder, filter : Filter) : StringBuilder = { histogram.filter(filter).keySet.toList.sorted.foreach {
    key =>
      builder.append(key)
      builder.append("\t\t")
      builder.append(histogram(key).count)
      builder.append("\n")
  }; builder; }
  
  override
  def toString: String = toString(StringBuilder.newBuilder, filterOutOnes).toString
  
}
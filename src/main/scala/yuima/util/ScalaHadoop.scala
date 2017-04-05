//package yuima.util
//
//import org.apache.hadoop.conf.{ Configuration, Configured }
//import org.apache.hadoop.fs.Path
//import org.apache.hadoop.io.{ IntWritable, LongWritable, Text }
//import org.apache.hadoop.mapreduce.{ Job, Mapper, Reducer }
//import org.apache.hadoop.mapreduce.lib.input.{ FileInputFormat, TextInputFormat }
//import org.apache.hadoop.mapreduce.lib.output.{ FileOutputFormat, TextOutputFormat }
//import org.apache.hadoop.util.{ Tool, ToolRunner }
//import org.apache.hadoop.io.Writable
//import scala.reflect.ClassTag
//import scala.collection.JavaConverters._
//
//object ScalaHadoop
//
//abstract class ScalaHadoop[Key <: Writable, Value <: Writable] extends Configured with Tool {
//  val key: ClassTag[Key]
//  val value: ClassTag[Value]
//
//  type Mpr = Mapper[LongWritable, Text, Key, Value]
//  type Rdr = Reducer[Key, Value, Key, Value]
//
//  val jobName = "No name"
//  def mapFun(v: Text, c: Mpr#Context): Unit
//  def reduceFun(k: Key, vs: Iterable[Value], c: Rdr#Context): Unit
//
//  class Map extends Mapper[LongWritable, Text, Key, Value] {
//    override def map(key: LongWritable, value: Text, context: Mpr#Context) {
//      mapFun(_: Text, _: Mpr#Context)
//    }
//  }
//
//  class Reduce extends Reducer[Key, Value, Key, Value] {
//    override def reduce(key: Key, values: java.lang.Iterable[Value], context: Rdr#Context) {
//      val vs = values.asScala
//      reduceFun(_: Key, vs: Iterable[Value], _: Rdr#Context)
//    }
//  }
//
//  def run(args: Array[String]): Int = {
//    val conf = getConf()
//    val job = new Job(conf, jobName)
//
//    job.setJarByClass(getClass)
//    job.setOutputKeyClass(key.runtimeClass)
//    job.setOutputValueClass(value.runtimeClass)
//    job.setMapperClass(classOf[Map])
//    job.setCombinerClass(classOf[Reduce])
//    job.setReducerClass(classOf[Reduce])
//
//    job.setInputFormatClass(classOf[TextInputFormat])
//    job.setOutputFormatClass(classOf[TextOutputFormat[Text, Text]])
//
//    FileInputFormat.setInputPaths(job, new Path(args(0)))
//    FileOutputFormat.setOutputPath(job, new Path(args(1)))
//
//    val success = job.waitForCompletion(true)
//    if (success) 0 else 1
//  }
//
////  def doRun(args: Array[String]) = System.exit(ToolRunner.run(this, args))
//}
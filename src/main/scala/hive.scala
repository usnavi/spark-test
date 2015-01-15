package com.rackspace.feeds


import org.apache.spark._
import org.apache.spark.sql.catalyst.expressions.Row
import org.apache.spark.sql.hive._
import org.codehaus.jackson.map.ObjectMapper

/**
 * Simple POC of how we can leverage spark to pull events from Hive and write them out by DC & feed.
 *
 * This POC assumes the following
 * - Assumes the preferences svc data has already been imported into hadoop as a Hive table.
 * - Does not actually interact with identity to get the admin user to impersonate;  comments explain how this might be done.
 * - Does not actually write to Cloud Files; comments explain how this might be done.
 */
object ArchiveTest {

  //////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Inputs into the job.  Might want to reference these from an external datasource
  //

  // list of DCs
  val IAD = "IAD"
  val DFW = "DFW"
  val ORD = "ORD"
  val LON = "LON"
  val HKG = "HKG"
  val SYD = "SYD"

  // list of Feeds to archive
  val FEED = List( "functest1/events", "usagetest1/events" )

  // feeds which use NastId
  val NASTY_FEED = List( )

  val date = "2014-12-01"

  // admin pw & apiKey to interact with identity & impersonate user.
  val admin = "TODO"
  val pw = "TODO"
  val apiKey = "TODO"

  //////////////////////////////////////////////////////////////////////////////////////////////////////

  def main( args: Array[String] ): Unit = {

    // initialize spark & hive interface
    val conf = new SparkConf().setAppName("Archive Test")
    val spark = new SparkContext( conf )
    val hive = new HiveContext( spark )

    // get map of all tenants and their DC containers from the hive preferences service
    // TODO:  foldLeft or foldRight?
    val tenants = hive.sql( "select * from preferences" )
      .collect()
      .foldLeft(  Map[String, Map[String, String]]() )( tenantContainers )

    // TODO:  get token
    // admin_token=`curl -sX POST $identity/v2.0/tokens -H "Content-type: application/json" -H "Accept: application/json" -d '{ "auth":{ "RAX-KSKEY:apiKeyCredentials":{ "username": "'$admin'", "apiKey":"'$apikey'"} } }' | jsawk 'return this.access.token.id'`


    tenants.foreach( tenant => {

      // TODO:  get user for tenant (auth v1.1, requires basic auth)
      // user_name=`curl -s --user $admin:$pw $identity/v1.1/mosso/842558 | jsawk 'return this.user.id'`

      // TODO:  get impersonation token (auth v2.0)
      // imp_token=`curl -sH "X-Auth-Token: $admin_token" -d '{"RAX-AUTH:impersonation":{"user":{"username":"'$user_name'"},"expire-in-seconds":10800}}' https://identity-internal.api.rackspacecloud.com/v2.0/RAX-AUTH/impersonation-tokens -H "Content-type: application/json" | jsawk 'return this.access.token.id'`

      println( tenant._1 )

      tenant._2.foreach(dc => {

        println( dc._1 )
        FEED.foreach(feed => {

          // TODO: use tenant vs NastID for appropriate feed (e.g., Nasty for Files)
          val sql = "select entrybody from entries where feed = '" +
            feed + "' and dc = '" +
            dc._1 + "' and tenantid = '" +
            tenant._1 + "' and date = '" +
            date + "'"

          val entrybody = hive.sql( sql ).collect()

          println( feed )

          // TODO:  verify that container exists
          // curl -X HEAD $container/container_1

          // TODO:  write to Cloud Files
          // curl -H "X-Auth-Token: $imp_token" --upload-file pom.xml $container/file2.xml

          if( !entrybody.isEmpty )
            println(entrybody.head.getString(0))
          else
            println( "no entries" )
        })
      })
    })

    // TODO:  oh, and report errors while you're at it! :-P
  }

  /**
   * For a given tenantId, returns a fully populated DC map containing every DC -> URL mapping.
   *
   * @param tenantMap sum object for foldLeft
   * @param row row from hive entries table
   * @return Map of (tenantId -> ( DC -> container URL))
   */
  def tenantContainers( tenantMap :  Map[ String, Map[String, String]], row : Row ) : Map[ String, Map[String, String]] = {

    val tenantId = row.getString(0)
    val prefs = (new ObjectMapper()).readTree(row.getString(1))
    val default_con = prefs.get("default_container_name").getTextValue()
    val formats = prefs.get("data_format")
    val urls = prefs.get("archive_container_urls")


    val DC = Map( IAD -> "",
      DFW -> "",
      ORD -> "",
      LON -> "",
      HKG -> "",
      SYD -> ""
    )

    val containers = DC.foldLeft( Map[String, String]() )((map, dc) => (

      if (urls.has(dc._1)) {

        map + ( dc._1 ->  dc._2 )
      }
      else {

        map + (dc._1 -> default_con )
      }
      ))

    tenantMap + (tenantId -> containers )
  }
}



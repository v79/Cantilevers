package org.liamjd.cantilevers.db

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.with
import java.net.URI

val dbModule = Kodein.Module {

	if (System.getenv("DATABASE_URL") != null) {
		// postgres://nsfwlpttpycjqw:9caa426fd424aa3c947562468cbeeb37fa7b340bfc846acaf17c379bcbc4a653@ec2-46-137-97-169.eu-west-1.compute.amazonaws.com:5432/d8o5ibnkc198og
		val dbUri = URI(System.getenv("DATABASE_URL"))

		val jdbcUsername = dbUri.userInfo.split(":")[0]
		val jdbcPassword = dbUri.userInfo.split(":")[1]
		val dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() // + dbUri.getPath()

		constant("dbDriverClass") with "org.postgresql.Driver"
		constant("dbConnectionString") with dbUrl
		constant("dbDatabase") with dbUri.path
		constant("dbPassword") with jdbcPassword
		constant("dbUser") with jdbcUsername
	} else {
		// use our locally configured MariaDB instance
		constant("dbDriverClass") with "org.mariadb.jdbc.Driver"
		constant("dbConnectionString") with "jdbc:mysql://127.0.0.1:3306/"
		constant("dbDatabase") with "cantilevers"
		constant("dbPassword") with "indy25tlx"
		constant("dbUser") with "liam"
	}
}


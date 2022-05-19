package tech.harmless.buckets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
@EnableMongoRepositories
class BucketsApplication(
//	@Autowired private val defaultItemRepo: DefaultItemRepo,
)

fun main(args: Array<String>) {
	runApplication<BucketsApplication>(*args)
}

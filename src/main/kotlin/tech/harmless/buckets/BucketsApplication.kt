package tech.harmless.buckets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BucketsApplication

fun main(args: Array<String>) {
	runApplication<BucketsApplication>(*args)
}

package org.demo.demoCheckedSafe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class DemoApplication {

    @Autowired
    lateinit var checkDataRepository: CheckDataRepository

    @GetMapping("/ping")
    fun ping(@RequestParam(value = "name", defaultValue = "World") name: String): String {
        return "Hello $name!"
    }

    @PostMapping("/demo")
    fun demoSave(@RequestBody request: CheckData): String {
        println("""
            Received JSON: 
            Fuel Level: ${request.fuelLevel}
            Warning Lights: ${request.warningLights}
            Wipers: ${request.wipers}
            Mirrors: ${request.mirrors}
            Tyres: ${request.tyres}
            Lights: ${request.lights}
            Brakes: ${request.brakes}
            Exhaust Smoke: ${request.exhaustSmoke}
            Selected Option: ${request.selectedOption}
        """.trimIndent())

        checkDataRepository.save(request)

        return "Data received"
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

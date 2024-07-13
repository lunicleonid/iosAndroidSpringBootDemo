package org.demo.demoCheckedSafe

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckDataRepository : JpaRepository<CheckData, Long>

@Entity
data class CheckData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val fuelLevel: Boolean,
    val warningLights: Boolean,
    val wipers: Boolean,
    val mirrors: Boolean,
    val tyres: Boolean,
    val lights: Boolean,
    val brakes: Boolean,
    val exhaustSmoke: Boolean,
    val selectedOption: String
)

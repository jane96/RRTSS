package lab.mars.MCRRTImp.base

open class OneTimeConfiguration(
        val timeTolerance : Long,
        val levelOneReplan : Boolean,
        val levelTwoReplan : Boolean,
        val levelTwoStartIdx : Int,
        val wayPointApproachDistance : Double,
        val firstLevelDeltaTime : Double,
        val secondLevelDeltaTime : Double
        )
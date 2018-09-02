package lab.mars.MCRRTImp.base

open class OneTimeConfiguration(
        val timeTolerance: Long,
        val levelOneReplan: Boolean,
        val levelTwoReplan: Boolean,
        val levelTwoFromIdx: Int = 0,
        val levelTwoToIdx: Int = 0,
        val wayPointApproachDistance: Double = 0.0,
        val firstLevelDeltaTime: Double,
        val secondLevelDeltaTime: Double = 0.0
)
package lab.mars.MCRRTImp.base

open class OneTimeConfiguration(
        val timeTolerance: Long,
        val levelOneReplan: Boolean,
        val levelOneReplanUseCache : Boolean = false,
        val levelOneReplanCacheIdx : Int = -1,
        val levelTwoReplan: Boolean,
        val levelTwoFromIdx: Int = 0,
        val levelTwoToIdx: Int = 0,
        val wayPointApproachDistance: Double = 0.0
)
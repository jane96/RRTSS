package lab.mars.MCRRTImp.base

open class OneTimeConfiguration<V : Vector<V>>(
        val timeTolerance: Long,
        val levelOneReplan: Boolean,
        val levelOneRotationLimit : Double = 30.0,
        val levelOneUseCache : Boolean = false,
        val levelOnePathCache : Path<WayPoint<V>>? = null,
        val levelOneReplanCacheIdx : Int = -1,
        val levelTwoReplan: Boolean,
        val levelTwoFromIdx: Int = 0,
        val levelTwoToIdx: Int = 0,
        val wayPointApproachDistance: Double = 0.0
)
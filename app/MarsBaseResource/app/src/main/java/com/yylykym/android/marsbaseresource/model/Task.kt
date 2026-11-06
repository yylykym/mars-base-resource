package com.yylykym.android.marsbaseresource.model

enum class TaskType { EXPLORE, MINE, REPAIR }

/**
 * 玩家当前拥有资源
 */
data class ResourceLevels(
    val oxygen: Int = 100,
    val electricity: Int = 100,
    val food: Int = 100
)

/**
 * 任务所消耗资源
 */
data class ResourceCost(
    val oxygen: Int = 0,
    val electricity: Int = 0,
    val food: Int = 0
)

/**
 * 任务历史记录
 */
data class TaskRecord(
    val sequence: Int,
    val taskType: TaskType,
    val spent: ResourceCost,
    val pointsEarned: Int
)

/**
 * 游戏任务
 */
data class GameTask(
    val id: Long,
    val type: TaskType,
    val name: String,
    val oxygenCost: Int,
    val powerCost: Int,
    val foodCost: Int,
    val scoreReward: Int
)

enum class ResourceType {
    OXYGEN,
    ELECTRICITY,
    FOOD
}

data class ResourceShortage(
    val type: ResourceType,
    val missing: Int
)

data class TaskFailure(
    val taskId: Long,
    val shortages: List<ResourceShortage>
)

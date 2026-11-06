package com.yylykym.android.marsbaseresource.ui

import androidx.lifecycle.ViewModel
import com.yylykym.android.marsbaseresource.model.GameTask
import com.yylykym.android.marsbaseresource.model.ResourceLevels
import com.yylykym.android.marsbaseresource.model.ResourceShortage
import com.yylykym.android.marsbaseresource.model.ResourceType
import com.yylykym.android.marsbaseresource.model.TaskFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())

    var uiState = _uiState.asStateFlow()

    fun executeTask(task: GameTask) {
        _uiState.update { state ->
            val failure = checkResource(state.resourceLevels, task)


            if (failure != null) {
                state.copy(taskFailure = failure)
            } else {
                val resources = state.resourceLevels

                val newResources = resources.copy(
                    oxygen = resources.oxygen - task.oxygenCost,
                    electricity = resources.electricity - task.powerCost,
                    food = resources.food - task.foodCost
                )

                val totalPoints = (state.points + task.scoreReward).coerceIn(0, 100)

                state.copy(
                    resourceLevels = newResources,
                    points = totalPoints,
                    gameOver = totalPoints >= 100,
                    taskFailure = null
                )
            }
        }
    }

    fun restartGame() {
        _uiState.update { GameUiState() }
    }

    fun useSupplyCounter() {
        // 如果资源是满的 不再消耗也不增加
        if (uiState.value.resourceLevels == ResourceLevels()) return
        if (_uiState.value.supplyCounter > 0) {
            _uiState.update {
                val newResource = it.resourceLevels.copy(
                    oxygen = (
                            it.resourceLevels.oxygen + 25
                            ).coerceAtMost(100),
                    electricity = (
                            it.resourceLevels.electricity + 25
                            ).coerceAtMost(100),
                    food = (
                            it.resourceLevels.food + 25
                            ).coerceAtMost(100),
                )
                it.copy(supplyCounter = it.supplyCounter - 1, resourceLevels = newResource)
            }
        }
    }


    private fun checkResource(
        resources: ResourceLevels,
        task: GameTask
    ): TaskFailure? {
        val shortages = buildList {
            if (resources.oxygen < task.oxygenCost) {
                add(
                    ResourceShortage(
                        ResourceType.OXYGEN,
                        task.oxygenCost - resources.oxygen
                    )
                )
            }

            if (resources.electricity < task.powerCost) {
                add(
                    ResourceShortage(
                        ResourceType.ELECTRICITY,
                        task.powerCost - resources.electricity
                    )
                )
            }

            if (resources.food < task.foodCost) {
                add(
                    ResourceShortage(
                        ResourceType.FOOD,
                        task.foodCost - resources.food
                    )
                )
            }
        }

        return if (shortages.isEmpty()) {
            null
        } else {
            TaskFailure(task.id, shortages)
        }
    }

}
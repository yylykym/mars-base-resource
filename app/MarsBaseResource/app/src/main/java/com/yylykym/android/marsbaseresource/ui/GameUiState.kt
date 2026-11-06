package com.yylykym.android.marsbaseresource.ui

import com.yylykym.android.marsbaseresource.model.ResourceLevels
import com.yylykym.android.marsbaseresource.model.ResourceShortage
import com.yylykym.android.marsbaseresource.model.TaskFailure

data class GameUiState(
    val resourceLevels: ResourceLevels = ResourceLevels(),
    val gameOver: Boolean = false,
    val points: Int = 0,
    val supplyCounter: Int = 2,
    val taskFailure: TaskFailure? = null
)

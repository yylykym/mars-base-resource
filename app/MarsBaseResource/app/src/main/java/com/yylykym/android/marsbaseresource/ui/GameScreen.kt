package com.yylykym.android.marsbaseresource.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yylykym.android.marsbaseresource.R
import com.yylykym.android.marsbaseresource.model.GameTask
import com.yylykym.android.marsbaseresource.model.ResourceLevels
import com.yylykym.android.marsbaseresource.model.ResourceShortage
import com.yylykym.android.marsbaseresource.model.ResourceType
import com.yylykym.android.marsbaseresource.model.TaskFailure
import com.yylykym.android.marsbaseresource.model.TaskType
import com.yylykym.android.marsbaseresource.ui.theme.MarsBaseResourceTheme

@Composable
fun GameScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {

    val uiState by gameViewModel.uiState.collectAsState()

    Column(
        modifier.padding(dimensionResource(R.dimen.padding_large))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "火星基地控制台",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = {}) {
                Text(text = "记录", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

        ResourceLayout(
            uiState.points,
            uiState.resourceLevels
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        TaskLayout(onExecuteTask = gameViewModel::executeTask, uiState.taskFailure)

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        Row() {
            Text(
                text = "紧急补给: 剩余${uiState.supplyCounter}次",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = gameViewModel::useSupplyCounter) {
                Text(text = "选择资源", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        Button(onClick = gameViewModel::restartGame, modifier = Modifier.align(Alignment.End)) {
            Text(text = "重新开始", style = MaterialTheme.typography.labelSmall)
        }

        if (uiState.gameOver) {
            GameOverDialog(
                onPlayAgain = gameViewModel::restartGame,
                onExit = onExit
            )
        }

    }
}

@Composable
fun ResourceLayout(
    points: Int,
    resourceLevels: ResourceLevels,
    modifier: Modifier = Modifier
) {
    Card(
        modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Row() {
                Text(
                    text = "氧气: ${resourceLevels.oxygen}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "电力: ${resourceLevels.electricity}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "食物: ${resourceLevels.food}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(text = "建筑积分: ${points}/${100}", style = MaterialTheme.typography.bodyMedium)



            LinearProgressIndicator(
                progress = { (points / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun TaskLayout(
    onExecuteTask: (GameTask) -> Unit,
    taskFailure: TaskFailure?,
    modifier: Modifier = Modifier
) {
    val tasks = listOf(
        GameTask(1, TaskType.EXPLORE, "火星观察", 20, 15, 0, 20),
        GameTask(2, TaskType.MINE, "矿物采集", 0, 30, 10, 35),
        GameTask(3, TaskType.REPAIR, "设备维修", 0, 15, 15, 25)
    )
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        Text(text = "可执行任务", style = MaterialTheme.typography.titleSmall)
        Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))) {
            tasks.forEach { task ->
                TaskCard(
                    task = task,
                    onExecuteTask = onExecuteTask,
                    shortages = taskFailure
                        ?.takeIf { it.taskId == task.id }
                        ?.shortages
                        .orEmpty()
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    onExecuteTask: (GameTask) -> Unit,
    shortages: List<ResourceShortage> = emptyList(),
    task: GameTask,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {

        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(text = task.name, style = MaterialTheme.typography.titleMedium)

            Row() {
                if (task.oxygenCost > 0)
                    Text(
                        text = "氧气: -${task.oxygenCost}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                if (task.powerCost > 0)
                    Text(
                        text = "电力: -${task.powerCost}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                if (task.foodCost > 0)
                    Text(
                        text = "食物: -${task.foodCost}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                Text(
                    text = "积分: +${task.scoreReward}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (shortages.isNotEmpty()) {
                val message = shortages.joinToString("，") { shortage ->
                    val name = when (shortage.type) {
                        ResourceType.OXYGEN -> "氧气"
                        ResourceType.ELECTRICITY -> "电力"
                        ResourceType.FOOD -> "食物"
                    }
                    "$name 还缺少 ${shortage.missing}"
                }

                Text(
                    text = "资源不足：$message",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(onClick = { onExecuteTask(task) }, modifier = Modifier.align(Alignment.End)) {
                Text(text = "执行任务", style = MaterialTheme.typography.labelSmall)
            }

        }
    }
}

@Composable
fun GameOverDialog(onPlayAgain: () -> Unit, onExit: () -> Unit, modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(text = "游戏结束") },
        text = { Text(text = "游戏已结束，你可以继续重玩或者退出") },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onExit
            ) {
                Text("退出")
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = "继续")
            }
        }
    )
}

@Preview
@Composable
fun PreviewGameScreen() {
    MarsBaseResourceTheme {
        GameScreen({})
    }
}
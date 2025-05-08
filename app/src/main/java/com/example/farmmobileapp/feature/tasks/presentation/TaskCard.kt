package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.farmmobileapp.R
import com.example.farmmobileapp.feature.navigation.NavigationRoutes
import com.example.farmmobileapp.ui.theme.PrimeColors
import com.example.farmmobileapp.util.CategoryIconUtils
import com.example.farmmobileapp.util.DateTimeUtils

@Composable
fun TaskCard(
    taskWithField: TaskWithField,
    navController: NavController
) {
    val task = taskWithField.task

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .clickable {
                navController.navigate(
                    NavigationRoutes.TaskDetail.createRoute(
                        task.id
                    )
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PriorityHigh,
                        contentDescription = "Priority",
                        tint = task.priority.getColor(),
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = task.title,
                        fontSize = 18.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    CategoryIconUtils.GetIconForCategory(task.categoryName)
                }
            }

            task.dueDate?.let { dueDate ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_clock_24px),
                        contentDescription = "Due Date",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(text = DateTimeUtils.formatBackendDateTimeToUserFriendly(dueDate))
                }
            }

            taskWithField.field?.let { field ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.location_on_24),
                        contentDescription = "Field",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(text = field.name)
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = DateTimeUtils.formatBackendDateTimeToUserFriendly(task.createdAt),
                    color = PrimeColors.Gray.color500,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
package com.example.vero.helpers

import com.example.vero.api.dtos.TaskDto
import com.example.vero.db.entity.TaskModel

val firstTaskModel = TaskModel(
    task = "100 Aufbau",
    title = "Gerüst montieren",
    description = "Gerüste montieren.",
    sort = "0",
    wageType = "100 Aufbau",
    BusinessUnitKey = "Gerüstbau",
    businessUnit = "Gerüstbau",
    parentTaskID = "",
    preplanningBoardQuickSelect = null,
    colorCode = "",
    workingTime = null,
    isAvailableInTimeTrackingKioskMode = false
)

val secondTaskModel = firstTaskModel.copy(
    task = "101 Abbau",
    title = "Gerüst abbauen",
    description = "Gerüste abbauen."
)

val apiTasks = listOf(
    TaskDto(
        task = "200 Aufbau",
        title = "Gerüst montieren",
        description = "Gerüste montieren.",
        sort = "0",
        wageType = "200 Aufbau",
        BusinessUnitKey = "Gerüstbau",
        businessUnit = "Gerüstbau",
        parentTaskID = "",
        preplanningBoardQuickSelect = null,
        colorCode = "",
        workingTime = null,
        isAvailableInTimeTrackingKioskMode = false
    ),
    TaskDto(
        task = "201 Abbau",
        title = "Gerüst abbauen",
        description = "Gerüste abbauen.",
        sort = "0",
        wageType = "201 Abbau",
        BusinessUnitKey = "Gerüstbau",
        businessUnit = "Gerüstbau",
        parentTaskID = "",
        preplanningBoardQuickSelect = null,
        colorCode = "",
        workingTime = null,
        isAvailableInTimeTrackingKioskMode = false
    )
)
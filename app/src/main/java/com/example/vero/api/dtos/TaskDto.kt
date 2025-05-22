package com.example.vero.api.dtos

data class TaskDto(
    val BusinessUnitKey: String?,
    val businessUnit: String?,
    val colorCode: String?,
    val description: String?,
    val isAvailableInTimeTrackingKioskMode: Boolean,
    val parentTaskID: String?,
    val preplanningBoardQuickSelect: Any?,
    val sort: String?,
    val task: String?,
    val title: String?,
    val wageType: String?,
    val workingTime: Any?
)

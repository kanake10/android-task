package com.example.vero.utils

import com.example.vero.api.dtos.TaskDto
import com.example.vero.db.entity.TaskModel
import retrofit2.HttpException
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException
import androidx.compose.ui.graphics.Color


suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (e: HttpException) {
        // You can parse error body here if needed
        val code = e.code()
        val errorMsg = e.response()?.errorBody()?.string() ?: e.localizedMessage ?: "HTTP error $code"
        Resource.Error("Network error $code: $errorMsg")
    } catch (e: IOException) {
        Resource.Error("Network failure: Please check your internet connection.")
    } catch (e: TimeoutCancellationException) {
        Resource.Error("Request timed out. Please try again.")
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
    }
}

fun TaskDto.toModel(): TaskModel {
    return TaskModel(
        task = this.task,
        title = this.title,
        description = this.description,
        sort = this.sort,
        wageType = this.wageType,
        BusinessUnitKey = this.BusinessUnitKey,
        businessUnit = this.businessUnit,
        parentTaskID = this.parentTaskID,
        preplanningBoardQuickSelect = this.preplanningBoardQuickSelect?.toString(),
        colorCode = this.colorCode,
        workingTime = this.workingTime?.toString(),
        isAvailableInTimeTrackingKioskMode = this.isAvailableInTimeTrackingKioskMode
    )
}

fun parseColorOrDefault(colorString: String?, defaultColor: Color = Color.Gray): Color {
    return try {
        if (colorString.isNullOrBlank()) {
            defaultColor
        } else {
            Color(android.graphics.Color.parseColor(colorString))
        }
    } catch (e: Exception) {
        defaultColor
    }
}

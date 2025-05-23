package io.github.zheniaregbl.zephyr.foundation.input.text_field

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.zheniaregbl.zephyr.core.theme.ZephyrDisableBackgroundColor
import io.github.zheniaregbl.zephyr.core.theme.ZephyrDisableColor
import io.github.zheniaregbl.zephyr.core.theme.ZephyrErrorColor
import io.github.zheniaregbl.zephyr.core.theme.ZephyrPrimaryColor
import io.github.zheniaregbl.zephyr.core.theme.ZephyrTertiaryOne
import io.github.zheniaregbl.zephyr.core.theme.ZephyrTertiaryTwo

/**
 * Text field component for user input. Supports hover, focus, error, and disabled states with smooth color animations.
 * @param modifier The modifier to be applied to the layout.
 * @param value The current text value of the text field.
 * @param onValueChange Callback invoked when the text value changes.
 * @param enabled Whether the text field is interactive. If `false`, the field is disabled.
 * @param readOnly Whether the text field is read-only. If `true`, input is prevented but selection is allowed.
 * @param isError Whether the text field is in an error state, affecting its appearance.
 * @param placeholder The placeholder text displayed when the field is empty.
 * @param textStyle The text style applied to the input text and placeholder.
 * @param colors Object defining colors for different states of the text field. See [ZephyrTextFieldColor].
 * @param cornerRadius The corner radius for the text field's background and border.
 *
 * @sample io.github.zheniaregbl.zephyr.foundation.sample.SimplyTextField
 */
@Composable
fun ZephyrTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    placeholder: String? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    colors: ZephyrTextFieldColor = ZephyrTextFieldColor(),
    cornerRadius: Dp = 8.dp
) {

    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    val backgroundColor by remember {
        derivedStateOf {
            when {
                !enabled -> colors.disabledBackgroundColor
                isError -> colors.errorBackgroundColor
                isFocused -> colors.focusedBackgroundColor
                isHovered -> colors.hoveredBackgroundColor
                else -> colors.unfocusedBackgroundColor
            }
        }
    }

    val borderColor by remember {
        derivedStateOf {
            when {
                !enabled -> colors.disabledBorderColor
                isError -> colors.errorBorderColor
                isFocused -> colors.focusedBorderColor
                isHovered -> colors.hoveredBorderColor
                else -> colors.unfocusedBorderColor
            }
        }
    }

    val textColor by remember {
        derivedStateOf {
            when {
                !enabled -> colors.disabledTextColor
                isError -> colors.errorTextColor
                isFocused -> colors.focusedTextColor
                isHovered -> colors.hoveredTextColor
                else -> colors.unfocusedTextColor
            }
        }
    }

    val placeholderColor by remember {
        derivedStateOf {
            when {
                !enabled -> colors.disabledPlaceholderColor
                isError -> colors.errorPlaceholderColor
                isFocused -> colors.focusedPlaceholderColor
                isHovered -> colors.hoveredPlaceholderColor
                else -> colors.unfocusedPlaceholderColor
            }
        }
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "Text field background color"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "Text field border color"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = textColor,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "Text field text color"
    )

    val animatedPlaceholderColor by animateColorAsState(
        targetValue = placeholderColor,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "Text field placeholder color"
    )

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is HoverInteraction.Enter -> isHovered = true
                is HoverInteraction.Exit -> isHovered = false
                is FocusInteraction.Focus -> isFocused = true
                is FocusInteraction.Unfocus -> isFocused = false
            }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .hoverable(interactionSource)
    ) {

        Canvas(modifier = Modifier.matchParentSize()) {

            drawRoundRect(
                color = animatedBackgroundColor,
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )

            drawRoundRect(
                color = animatedBorderColor,
                cornerRadius = CornerRadius(cornerRadius.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = if (isError) colors.textSelectionColors.errorHandleColor
                else colors.textSelectionColors.handleColor,
                backgroundColor = if (isError) colors.textSelectionColors.errorBackgroundColor
                else colors.textSelectionColors.backgroundColor
            )
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 13.5.dp),
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle.copy(color = animatedTextColor),
                interactionSource = interactionSource,
                cursorBrush = SolidColor(if (isError) colors.errorCursorColor else colors.cursorColor),
                singleLine = true,
                decorationBox =  { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = textStyle.copy(color = animatedPlaceholderColor)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

/**
 * Color configuration for [ZephyrTextField], defining colors for various states and elements.
 * @param hoveredTextColor Color of the input text when the text field is hovered.
 * @param focusedTextColor Color of the input text when the text field is focused.
 * @param unfocusedTextColor Color of the input text when the text field is not focused.
 * @param disabledTextColor Color of the input text when the text field is disabled.
 * @param errorTextColor Color of the input text when the text field is in an error state.
 * @param hoveredBackgroundColor Background color when the text field is hovered.
 * @param focusedBackgroundColor Background color when the text field is focused.
 * @param unfocusedBackgroundColor Background color when the text field is not focused.
 * @param disabledBackgroundColor Background color when the text field is disabled.
 * @param errorBackgroundColor Background color when the text field is in an error state.
 * @param hoveredBorderColor Border color when the text field is hovered.
 * @param focusedBorderColor Border color when the text field is focused.
 * @param unfocusedBorderColor Border color when the text field is not focused.
 * @param disabledBorderColor Border color when the text field is disabled.
 * @param errorBorderColor Border color when the text field is in an error state.
 * @param hoveredPlaceholderColor Color of the placeholder text when the text field is hovered.
 * @param focusedPlaceholderColor Color of the placeholder text when the text field is focused.
 * @param unfocusedPlaceholderColor Color of the placeholder text when the text field is not focused.
 * @param disabledPlaceholderColor Color of the placeholder text when the text field is disabled.
 * @param errorPlaceholderColor Color of the placeholder text when the text field is in an error state.
 * @param cursorColor Color of the cursor when the text field is not in an error state.
 * @param errorCursorColor Color of the cursor when the text field is in an error state.
 * @param textSelectionColors Configuration for text selection colors. See [ZephyrTextSelectionColors].
 * */
@Immutable
class ZephyrTextFieldColor(
    val hoveredTextColor: Color = ZephyrTertiaryOne,
    val focusedTextColor: Color = Color(0xFF424242),
    val unfocusedTextColor: Color = Color(0xFF424242),
    val disabledTextColor: Color = ZephyrDisableColor,
    val errorTextColor: Color = ZephyrErrorColor,
    val hoveredBackgroundColor: Color = Color.White,
    val focusedBackgroundColor: Color = Color.White,
    val unfocusedBackgroundColor: Color = Color.White,
    val disabledBackgroundColor: Color = ZephyrDisableBackgroundColor,
    val errorBackgroundColor: Color = Color.White,
    val hoveredBorderColor: Color = ZephyrTertiaryOne,
    val focusedBorderColor: Color = ZephyrPrimaryColor,
    val unfocusedBorderColor: Color = ZephyrTertiaryTwo,
    val disabledBorderColor: Color = ZephyrDisableColor,
    val errorBorderColor: Color = ZephyrErrorColor,
    val hoveredPlaceholderColor: Color = ZephyrTertiaryOne.copy(alpha = 0.4f),
    val focusedPlaceholderColor: Color = ZephyrPrimaryColor.copy(alpha = 0.4f),
    val unfocusedPlaceholderColor: Color = ZephyrTertiaryTwo.copy(alpha = 0.4f),
    val disabledPlaceholderColor: Color = ZephyrDisableColor.copy(alpha = 0.65f),
    val errorPlaceholderColor: Color = ZephyrErrorColor.copy(alpha = 0.4f),
    val cursorColor: Color = ZephyrPrimaryColor,
    val errorCursorColor: Color = ZephyrErrorColor,
    val textSelectionColors: ZephyrTextSelectionColors = ZephyrTextSelectionColors.Defaults
) {

    fun copy(
        hoveredTextColor: Color = this.hoveredTextColor,
        focusedTextColor: Color = this.focusedTextColor,
        unfocusedTextColor: Color = this.unfocusedTextColor,
        disabledTextColor: Color = this.disabledTextColor,
        errorTextColor: Color = this.errorTextColor,
        hoveredBackgroundColor: Color = this.hoveredBackgroundColor,
        focusedBackgroundColor: Color = this.focusedBackgroundColor,
        unfocusedBackgroundColor: Color = this.unfocusedBackgroundColor,
        disabledBackgroundColor: Color = this.disabledBackgroundColor,
        errorBackgroundColor: Color = this.errorBackgroundColor,
        hoveredBorderColor: Color = this.hoveredBorderColor,
        focusedBorderColor: Color = this.focusedBorderColor,
        unfocusedBorderColor: Color = this.unfocusedBorderColor,
        disabledBorderColor: Color = this.disabledBorderColor,
        errorBorderColor: Color = this.errorBorderColor,
        hoveredPlaceholderColor: Color = this.hoveredPlaceholderColor,
        focusedPlaceholderColor: Color = this.focusedPlaceholderColor,
        unfocusedPlaceholderColor: Color = this.unfocusedPlaceholderColor,
        disabledPlaceholderColor: Color = this.disabledPlaceholderColor,
        errorPlaceholderColor: Color = this.errorPlaceholderColor,
        cursorColor: Color = this.cursorColor,
        errorCursorColor: Color = this.errorCursorColor,
        textSelectionColors: ZephyrTextSelectionColors = this.textSelectionColors
    ) = ZephyrTextFieldColor(
        hoveredTextColor.takeOrElse { this.hoveredTextColor },
        focusedTextColor.takeOrElse { this.focusedTextColor },
        unfocusedTextColor.takeOrElse { this.unfocusedTextColor },
        disabledTextColor.takeOrElse { this.disabledTextColor },
        errorTextColor.takeOrElse { this.errorTextColor },
        hoveredBackgroundColor.takeOrElse { this.hoveredBackgroundColor },
        focusedBackgroundColor.takeOrElse { this.focusedBackgroundColor },
        unfocusedBackgroundColor.takeOrElse { this.unfocusedBackgroundColor },
        disabledBackgroundColor.takeOrElse { this.disabledBackgroundColor },
        errorBackgroundColor.takeOrElse { this.errorBackgroundColor },
        hoveredBorderColor.takeOrElse { this.hoveredBorderColor },
        focusedBorderColor.takeOrElse { this.focusedBorderColor },
        unfocusedBorderColor.takeOrElse { this.unfocusedBorderColor },
        disabledBorderColor.takeOrElse { this.disabledBorderColor },
        errorBorderColor.takeOrElse { this.errorBorderColor },
        hoveredPlaceholderColor.takeOrElse { this.hoveredPlaceholderColor },
        focusedPlaceholderColor.takeOrElse { this.focusedPlaceholderColor },
        unfocusedPlaceholderColor.takeOrElse { this.unfocusedPlaceholderColor },
        disabledPlaceholderColor.takeOrElse { this.disabledPlaceholderColor },
        errorPlaceholderColor.takeOrElse { this.errorPlaceholderColor },
        cursorColor.takeOrElse { this.cursorColor },
        errorCursorColor.takeOrElse { this.errorCursorColor },
        textSelectionColors.takeOrElse { this.textSelectionColors }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ZephyrTextFieldColor) return false

        if (hoveredTextColor != other.hoveredTextColor) return false
        if (focusedTextColor != other.focusedTextColor) return false
        if (unfocusedTextColor != other.unfocusedTextColor) return false
        if (disabledTextColor != other.disabledTextColor) return false
        if (errorTextColor != other.errorTextColor) return false
        if (hoveredBackgroundColor != other.hoveredBackgroundColor) return false
        if (focusedBackgroundColor != other.focusedBackgroundColor) return false
        if (unfocusedBackgroundColor != other.unfocusedBackgroundColor) return false
        if (disabledBackgroundColor != other.disabledBackgroundColor) return false
        if (errorBackgroundColor != other.errorBackgroundColor) return false
        if (hoveredBorderColor != other.hoveredBorderColor) return false
        if (focusedBorderColor != other.focusedBorderColor) return false
        if (unfocusedBorderColor != other.unfocusedBorderColor) return false
        if (disabledBorderColor != other.disabledBorderColor) return false
        if (errorBorderColor != other.errorBorderColor) return false
        if (hoveredPlaceholderColor != other.hoveredPlaceholderColor) return false
        if (focusedPlaceholderColor != other.focusedPlaceholderColor) return false
        if (unfocusedPlaceholderColor != other.unfocusedPlaceholderColor) return false
        if (disabledPlaceholderColor != other.disabledPlaceholderColor) return false
        if (errorPlaceholderColor != other.errorPlaceholderColor) return false
        if (cursorColor != other.cursorColor) return false
        if (errorCursorColor != other.errorCursorColor) return false
        if (textSelectionColors != other.textSelectionColors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hoveredTextColor.hashCode()
        result = 31 * result + focusedTextColor.hashCode()
        result = 31 * result + unfocusedTextColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()
        result = 31 * result + errorTextColor.hashCode()
        result = 31 * result + hoveredBackgroundColor.hashCode()
        result = 31 * result + focusedBackgroundColor.hashCode()
        result = 31 * result + unfocusedBackgroundColor.hashCode()
        result = 31 * result + disabledBackgroundColor.hashCode()
        result = 31 * result + errorBackgroundColor.hashCode()
        result = 31 * result + hoveredBorderColor.hashCode()
        result = 31 * result + focusedBorderColor.hashCode()
        result = 31 * result + unfocusedBorderColor.hashCode()
        result = 31 * result + disabledBorderColor.hashCode()
        result = 31 * result + errorBorderColor.hashCode()
        result = 31 * result + hoveredPlaceholderColor.hashCode()
        result = 31 * result + focusedPlaceholderColor.hashCode()
        result = 31 * result + unfocusedPlaceholderColor.hashCode()
        result = 31 * result + disabledPlaceholderColor.hashCode()
        result = 31 * result + errorPlaceholderColor.hashCode()
        result = 31 * result + cursorColor.hashCode()
        result = 31 * result + errorCursorColor.hashCode()
        result = 31 * result + textSelectionColors.hashCode()
        return result
    }
}

/**
 * Color configuration for text selection in [ZephyrTextField].
 * @param handleColor Color of the selection handles in normal state.
 * @param backgroundColor Background color of the selected text in normal state.
 * @param errorHandleColor Color of the selection handles in error state.
 * @param errorBackgroundColor Background color of the selected text in error state.
 */
@Immutable
class ZephyrTextSelectionColors(
    val handleColor: Color = ZephyrPrimaryColor,
    val backgroundColor: Color = ZephyrPrimaryColor.copy(alpha = 0.4f),
    val errorHandleColor: Color = ZephyrErrorColor,
    val errorBackgroundColor: Color = ZephyrErrorColor.copy(alpha = 0.4f)
) {

    @Stable
    val isSpecified: Boolean
        get() = handleColor != Color.Unspecified ||
                backgroundColor != Color.Unspecified ||
                errorHandleColor != Color.Unspecified ||
                errorBackgroundColor != Color.Unspecified

    @Stable
    val isUnspecified: Boolean
        get() = !isSpecified

    inline fun takeOrElse(block: () -> ZephyrTextSelectionColors): ZephyrTextSelectionColors =
        if (isSpecified) this else block()

    fun copy(
        handleColor: Color = this.handleColor,
        backgroundColor: Color = this.backgroundColor,
        errorHandleColor: Color = this.errorHandleColor,
        errorBackgroundColor: Color = this.errorBackgroundColor
    ) = ZephyrTextSelectionColors(
        handleColor.takeOrElse { this.handleColor },
        backgroundColor.takeOrElse { this.backgroundColor },
        errorHandleColor.takeOrElse { this.errorHandleColor },
        errorBackgroundColor.takeOrElse { this.errorBackgroundColor }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ZephyrTextSelectionColors) return false

        if (handleColor != other.handleColor) return false
        if (backgroundColor != other.backgroundColor) return false
        if (errorHandleColor != other.errorHandleColor) return false
        if (errorBackgroundColor != other.errorBackgroundColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = handleColor.hashCode()
        result = 31 * result + backgroundColor.hashCode()
        result = 31 * result + errorHandleColor.hashCode()
        result = 31 * result + errorBackgroundColor.hashCode()
        return result
    }

    companion object {
        val Defaults = ZephyrTextSelectionColors()
        val Unspecified = ZephyrTextSelectionColors(
            handleColor = Color.Unspecified,
            backgroundColor = Color.Unspecified,
            errorHandleColor = Color.Unspecified,
            errorBackgroundColor = Color.Unspecified
        )
    }
}
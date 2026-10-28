package com.kmpproject.feature.map.ui.components

import android.graphics.Bitmap
import android.graphics.PointF
import android.util.SparseArray
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.shadow.DropShadowPainter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kmpproject.uikit.theme.AppTheme
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
internal fun rememberPinImages(): PinImages {
  val density = LocalDensity.current
  val fontFamilyResolver = LocalFontFamilyResolver.current
  val style = PinStyle(
    background = AppTheme.colors.pins.primaryBackground,
    selectedBackground = AppTheme.colors.accent.brand,
    foreground = AppTheme.colors.pins.foreground,
    stroke = AppTheme.colors.pins.stroke,
    text = AppTheme.typography.label,
    shadows = AppTheme.shadows.pin,
  )
  return remember(density, fontFamilyResolver, style) {
    PinImages(density, fontFamilyResolver, style)
  }
}

@Immutable
internal data class PinStyle(
  val background: Color,
  val selectedBackground: Color,
  val foreground: Color,
  val stroke: Color,
  val text: TextStyle,
  val shadows: List<Shadow>,
)

/**
 * Icons of city pins and clusters, drawn with the theme tokens.
 *
 * An image is drawn only when MapKit first asks for it and is then cached by MapKit under its id,
 * so a city that stays inside a cluster costs nothing. The id carries the style, so a theme change
 * never reuses an image drawn with the old colors.
 *
 * Shadows are blurred once per style: a bubble's shadow is the round [pillShadow] stretched through
 * its middle column, and a pin adds the [tailShadow]. The bubble itself is drawn over them as one
 * outline, so its tail flows out of the pill without a seam.
 */
@Immutable
internal class PinImages(
  private val density: Density,
  fontFamilyResolver: FontFamily.Resolver,
  private val style: PinStyle,
) {
  private val textMeasurer = TextMeasurer(
    defaultFontFamilyResolver = fontFamilyResolver,
    defaultDensity = density,
    defaultLayoutDirection = LayoutDirection.Ltr,
    cacheSize = 0,
  )
  private val styleKey = style.hashCode()
  private val clusters = SparseArray<ImageProvider>()
  private val margin = with(density) {
    val shadowReach = style.shadows.maxOf { shadow ->
      (shadow.radius + shadow.spread).toPx() + max(abs(shadow.offset.x.toPx()), abs(shadow.offset.y.toPx()))
    }
    ceil(shadowReach)
  }
  private val pinHeight = with(density) { ceil(PIN_HEIGHT.toPx() + TAIL_HEIGHT.toPx() + 2 * margin).toInt() }

  // A tail hangs from the flat part of the pill's bottom, so a pin is never narrower than that
  private val tailStretch = with(density) { 2 * ceil(TAIL_START.toPx()).toInt() }
  private val pillShadow by lazy { drawPillShadow() }
  private val tailShadow by lazy { drawTailShadow() }

  /** Anchors a pin at the tip of its tail: every pin has the same height, so one style fits all. */
  val pinIconStyle: IconStyle = with(density) {
    val tipY = margin + PIN_HEIGHT.toPx() + TAIL_HEIGHT.toPx()
    IconStyle().setAnchor(PointF(0.5f, tipY / pinHeight))
  }

  fun pin(title: String, selected: Boolean): ImageProvider {
    return BubbleImage(
      id = "map_${if (selected) "selected_pin" else "pin"}_${styleKey}_$title",
      text = title,
      tail = true,
      background = if (selected) style.selectedBackground else style.background,
    )
  }

  // MapKit reports every cluster anew on each zoom step, so one image per size is enough
  fun cluster(size: Int): ImageProvider {
    return clusters[size] ?: BubbleImage(
      id = "map_cluster_${styleKey}_$size",
      text = size.toString(),
      tail = false,
      background = style.background,
    ).also { image -> clusters.put(size, image) }
  }

  // MapKit may ask for images off the main thread, and the text measurer is shared
  @Synchronized
  private fun drawBubble(text: String, tail: Boolean, background: Color): ImageBitmap = with(density) {
    val layout = textMeasurer.measure(text = text, style = style.text, maxLines = 1, softWrap = false)
    val height = PIN_HEIGHT.toPx()
    val strokeWidth = STROKE_WIDTH.toPx()
    // An even stretch moves the middle by whole pixels, so the tail shadow stays sharp
    val fitted = 2 * ((layout.size.width + 2 * PADDING.toPx() - height) / 2).roundToInt().coerceAtLeast(0)
    val stretch = if (tail) max(fitted, tailStretch) else fitted
    val middle = floor(margin + height / 2).toInt()
    renderBitmap(pillShadow.width + stretch, if (tail) pinHeight else pillShadow.height) {
      drawImage(
        image = pillShadow,
        srcSize = IntSize(middle, pillShadow.height),
      )
      drawImage(
        image = pillShadow,
        srcOffset = IntOffset(middle, 0),
        srcSize = IntSize(1, pillShadow.height),
        dstOffset = IntOffset(middle, 0),
        dstSize = IntSize(stretch, pillShadow.height),
        filterQuality = FilterQuality.None,
      )
      drawImage(
        image = pillShadow,
        srcOffset = IntOffset(middle, 0),
        srcSize = IntSize(pillShadow.width - middle, pillShadow.height),
        dstOffset = IntOffset(middle + stretch, 0),
      )
      if (tail) {
        drawImage(
          image = tailShadow,
          topLeft = Offset((stretch - tailStretch) / 2f, 0f),
        )
      }
      val outline = bubbleOutline(
        bounds = Rect(
          left = margin + strokeWidth,
          top = margin + strokeWidth,
          right = margin + height + stretch - strokeWidth,
          bottom = margin + height - strokeWidth,
        ),
        tail = tail,
      )
      // The fill covers the inner half of the stroke, so the stroke lies outside the fill as in the design
      drawPath(outline, style.stroke, style = Stroke(2 * strokeWidth))
      drawPath(outline, background)
      drawText(
        textLayoutResult = layout,
        color = style.foreground,
        topLeft = Offset(
          x = margin + (height + stretch - layout.size.width) / 2,
          y = margin + (height - layout.size.height) / 2,
        ),
      )
    }
  }

  private fun drawPillShadow(): ImageBitmap = with(density) {
    val height = PIN_HEIGHT.toPx()
    val body = Size(height, height)
    val outline = Path().apply { addOval(Rect(Offset.Zero, body)) }
    val side = ceil(height + 2 * margin).toInt()
    renderBitmap(side, side) {
      translate(margin, margin) {
        drawShadows(outline, body)
      }
    }
  }

  /**
   * The shadow of the tail below the pill's bottom, laid out as for the narrowest pin. Pill and tail
   * don't overlap there, so their shadows add up to the shadow of the whole bubble.
   */
  private fun drawTailShadow(): ImageBitmap = with(density) {
    val height = PIN_HEIGHT.toPx()
    val start = TAIL_START.toPx()
    val tail = Size(2 * start, TAIL_HEIGHT.toPx())
    renderBitmap(pillShadow.width + tailStretch, pinHeight) {
      translate(margin + (height + tailStretch) / 2 - start, margin + height) {
        drawShadows(shadowTailOutline(tail), tail)
      }
    }
  }

  /** Pill of [bounds] with the tail hanging from the middle of its bottom when [tail] is set. */
  private fun bubbleOutline(bounds: Rect, tail: Boolean): Path = with(density) {
    val radius = bounds.height / 2
    val center = bounds.center.x
    Path().apply {
      moveTo(bounds.left + radius, bounds.top)
      lineTo(bounds.right - radius, bounds.top)
      arcTo(
        rect = Rect(bounds.right - 2 * radius, bounds.top, bounds.right, bounds.bottom),
        startAngleDegrees = -90f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false,
      )
      if (tail) {
        val start = TAIL_START.toPx()
        val startHandle = TAIL_START_HANDLE.toPx()
        val tipHandle = TAIL_TIP_HANDLE.toPx()
        val tip = bounds.bottom + TAIL_HEIGHT.toPx()
        lineTo(center + start, bounds.bottom)
        cubicTo(center + startHandle, bounds.bottom, center + tipHandle, tip, center, tip)
        cubicTo(center - tipHandle, tip, center - startHandle, bounds.bottom, center - start, bounds.bottom)
      }
      lineTo(bounds.left + radius, bounds.bottom)
      arcTo(
        rect = Rect(bounds.left, bounds.top, bounds.left + 2 * radius, bounds.bottom),
        startAngleDegrees = 90f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false,
      )
      close()
    }
  }

  /** The tail of the outline widened by the stroke, the part that casts a shadow below the pill. */
  private fun shadowTailOutline(size: Size): Path = with(density) {
    val center = size.width / 2
    val startHandle = SHADOW_TAIL_START_HANDLE.toPx()
    val tipHandle = SHADOW_TAIL_TIP_HANDLE.toPx()
    Path().apply {
      moveTo(0f, 0f)
      lineTo(size.width, 0f)
      cubicTo(center + startHandle, 0f, center + tipHandle, size.height, center, size.height)
      cubicTo(center - tipHandle, size.height, center - startHandle, 0f, 0f, 0f)
      close()
    }
  }

  private fun DrawScope.drawShadows(outline: Path, size: Size) {
    val shape = GenericShape { _, _ -> addPath(outline) }
    style.shadows.forEach { shadow ->
      with(DropShadowPainter(shape, shadow)) { draw(size) }
    }
  }

  private fun renderBitmap(width: Int, height: Int, block: DrawScope.() -> Unit): ImageBitmap {
    val bitmap = ImageBitmap(width, height)
    val size = Size(width.toFloat(), height.toFloat())
    CanvasDrawScope().draw(density, LayoutDirection.Ltr, Canvas(bitmap), size, block)
    return bitmap
  }

  private inner class BubbleImage(
    private val id: String,
    private val text: String,
    private val tail: Boolean,
    private val background: Color,
  ) : ImageProvider() {
    override fun getId(): String = id

    override fun getImage(): Bitmap = drawBubble(text, tail, background).asAndroidBitmap()
  }
}

// The pill's height and padding include the stroke
private val PIN_HEIGHT = 32.dp
private val PADDING = 9.dp
private val STROKE_WIDTH = 1.dp
private val TAIL_HEIGHT = 6.dp

// A side of the tail inside the stroke is a cubic that leaves the pill flat at TAIL_START from the
// middle and reaches the tip flat; the handles are the distances of its control points from the middle
private val TAIL_START = 6.7.dp
private val TAIL_START_HANDLE = 0.6.dp
private val TAIL_TIP_HANDLE = 1.3.dp

// The same tail widened by the stroke; an offset of a cubic is no cubic, so it is fitted on its own
private val SHADOW_TAIL_START_HANDLE = 0.8.dp
private val SHADOW_TAIL_TIP_HANDLE = 2.8.dp

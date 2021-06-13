package uk.co.newagedev.animalcompare.ui.screens.swipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uk.co.newagedev.animalcompare.domain.model.AnimalType
import uk.co.newagedev.animalcompare.ui.R
import kotlin.math.absoluteValue

private const val SWIPES_ARROW_VISIBLE = 10

@Composable
fun SwipeOptions(
    animal1Painter: Painter,
    animal2Painter: Painter,
    progress: Float,
    swipeCount: Int,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // Limit the card height to a fixed fraction, rather than
        // relying on auto constraints to make it a percentage of the
        // remaining screen height, probably a better way but this works
        // for now
        //
        // TODO: use a different dynamic layout to clean this up a bit
        val cardHeight = maxHeight * 0.4f

        Column(modifier = Modifier.fillMaxSize()) {
            SwipeableCard(
                cardHeight = cardHeight,
                animalPainter = animal1Painter,
                leftAligned = false,
                offset = -progress,
                shouldShowArrow = swipeCount < SWIPES_ARROW_VISIBLE,
            )

            Spacer(modifier = Modifier.height(32.dp))

            SwipeableCard(
                cardHeight = cardHeight,
                animalPainter = animal2Painter,
                leftAligned = true,
                offset = progress,
                shouldShowArrow = swipeCount < SWIPES_ARROW_VISIBLE,
            )
        }
    }
}

@Composable
fun SwipeableCard(
    cardHeight: Dp,
    animalPainter: Painter,
    offset: Float,
    leftAligned: Boolean,
    shouldShowArrow: Boolean,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = if (leftAligned) {
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
        }
    ) {
        val maxWidth = maxWidth

        val leftAlignedAdjustment = if (leftAligned) 1f else -1f

        val position: Dp
        val alpha: Float
        val rotation: Float

        if (offset > 0f) {
            // The animation parameters if this is the one you are selecting. It moves to just off
            // the edge of the screen, it stays visible the entire time, and after the half way
            // point it rotates 90 degrees in total
            position = maxWidth * offset
            alpha = 1f
            rotation = (offset - 0.5f).coerceAtLeast(0f) * 180f
        } else {
            // The animation parameters if this is the one you aren't selecting. It stays still,
            // but fades out, and is invisible by the half way point
            position = 0.dp
            alpha = 1f + offset * 2f
            rotation = 0f
        }

        if (shouldShowArrow) {
            // Arrow showing which way to swipe
            Image(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = stringResource(R.string.swipe_arrow_desc),
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .offset(maxWidth * 0.7f * leftAlignedAdjustment)
                    .rotate(leftAlignedAdjustment.coerceAtMost(0f) * 180f)
                    .alpha(1f - (offset.absoluteValue * 5f)),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            )
        }

        // Actual animal image they are choosing
        Image(
            painter = animalPainter,
            contentDescription = stringResource(id = R.string.swipe_animal_desc),
            modifier = Modifier
                .offset(position * leftAlignedAdjustment, 0.dp)
                .alpha(alpha)
                .rotate(rotation * leftAlignedAdjustment)
                .requiredHeight(cardHeight)
                .fillMaxWidth(0.6f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )
    }
}

@Composable
fun SwipeLoading() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun SwipeScreenTop(animalType: AnimalType) {
    Text(
        stringResource(
            R.string.swipe_which_animal,
            stringResource(animalType.animalName)
        ),
        style = MaterialTheme.typography.h4,
        modifier = Modifier
            .padding(start = 32.dp, top = 32.dp, end = 32.dp, bottom = 8.dp),
        textAlign = TextAlign.Center,
    )

    Text(
        stringResource(id = R.string.swipe_swipe_to_select),
        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Light),
        modifier = Modifier
            .padding(start = 32.dp, top = 0.dp, end = 32.dp, bottom = 16.dp),
        textAlign = TextAlign.Center,
    )
}
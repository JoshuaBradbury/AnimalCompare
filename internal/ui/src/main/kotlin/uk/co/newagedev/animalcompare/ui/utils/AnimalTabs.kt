package uk.co.newagedev.animalcompare.ui.utils

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import uk.co.newagedev.animalcompare.domain.model.AnimalFilter
import uk.co.newagedev.animalcompare.ui.R

sealed class AnimalTab(@StringRes val name: Int) : Parcelable {
    @Parcelize
    object All : AnimalTab(R.string.tab_all)

    @Parcelize
    object Dog : AnimalTab(R.string.tab_dogs)

    @Parcelize
    object Cat : AnimalTab(R.string.tab_cats)

    @Parcelize
    object Fox : AnimalTab(R.string.tab_foxes)

    fun toFilter(): AnimalFilter {
        return when (this) {
            All -> AnimalFilter.ALL
            Dog -> AnimalFilter.DOG
            Cat -> AnimalFilter.CAT
            Fox -> AnimalFilter.FOX
        }
    }

    companion object {
        val values by lazy {
            listOf(
                All,
                Dog,
                Cat,
                Fox,
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AnimalTabs(
    tabs: List<AnimalTab>,
    tabView: @Composable (AnimalTab) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = tabs.size)
    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            tabs.forEachIndexed { index, animalTab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                ) {
                    Text(
                        text = stringResource(animalTab.name),
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .paddingFromBaseline(top = 16.dp),
                    )
                }
            }
        }

        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            key(page) {
                tabView(tabs[page])
            }
        }
    }
}
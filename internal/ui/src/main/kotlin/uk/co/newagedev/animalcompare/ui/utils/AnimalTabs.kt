package uk.co.newagedev.animalcompare.ui.utils

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

    fun toFilter(): AnimalFilter {
        return when (this) {
            All -> AnimalFilter.ALL
            Dog -> AnimalFilter.DOG
            Cat -> AnimalFilter.CAT
        }
    }

    companion object {
        val values by lazy {
            listOf(
                All,
                Dog,
                Cat,
            )
        }
    }
}

@Composable
fun AnimalTabs(
    currentTab: AnimalTab,
    updateCurrentTab: (AnimalTab) -> Unit,
    tabs: List<AnimalTab>,
    tabView: @Composable (AnimalTab) -> Unit,
) {
    val selectedTabIndex = tabs.indexOfFirst { it == currentTab }
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
        ) {
            tabs.forEachIndexed { index, animalTab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        updateCurrentTab(animalTab)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            tabView(currentTab)
        }
    }
}
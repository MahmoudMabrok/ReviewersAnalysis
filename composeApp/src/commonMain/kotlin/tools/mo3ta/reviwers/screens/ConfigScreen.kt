package tools.mo3ta.reviwers.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.russhwolf.settings.Settings
import tools.mo3ta.reviwers.components.LabeledContent


object Keys {
    const val API_KEY =
        "API_KEY";
    const val OWNER_REPO =
        "OWNER_REPO";
    const val IS_ENTERPRISE =
        "IS_ENTERPRISE";
    const val ENTERPRISE =
        "ENTERPRISE";
    const val PAGE_SIZE =
        "PAGE_SIZE";
    const val LAST_PAGE_NUMBER =
        "LAST_PAGE_NUMBER";
}

object ConfigScreen :
    Screen {
    @Composable
    override fun Content() {

        val navigator =
            LocalNavigator.currentOrThrow

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
                .padding(
                    16.dp
                )
                .background(
                    Color(
                        0XFFAAFF
                    )
                ).verticalScroll(
                    rememberScrollState()
                )
        ) {

            val settings =
                Settings()

            var githubKey by rememberSaveable {
                mutableStateOf(
                    settings.getString(
                        Keys.API_KEY,
                        ""
                    )
                )
            }
            var ownerWithRepo by rememberSaveable {
                mutableStateOf(
                    settings.getString(
                        Keys.OWNER_REPO,
                        ""
                    )
                )
            }
            var isEnterprise by rememberSaveable {
                mutableStateOf(
                    settings.getBoolean(
                        Keys.IS_ENTERPRISE,
                        false
                    )
                )
            }
            var enterprise by rememberSaveable {
                mutableStateOf(
                    settings.getString(
                        Keys.ENTERPRISE,
                        ""
                    )
                )
            }
            var pageSize by rememberSaveable {
                mutableStateOf(
                    settings.getInt(
                        Keys.PAGE_SIZE,
                        100
                    ).toString()
                )
            }
            var lastPageNumber by rememberSaveable {
                mutableStateOf(
                    settings.getInt(
                        Keys.LAST_PAGE_NUMBER,
                        1
                    ).toString()
                )
            }

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(
                    32.dp
                ),
            ) {
                Text(
                    "Config data",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    modifier = Modifier.fillMaxWidth()
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .border(
                            shape = RectangleShape,
                            border = BorderStroke(
                                1.dp,
                                Color.Black
                            )
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                )

                OutlinedTextField(
                    value = githubKey,
                    onValueChange = {
                        githubKey =
                            it
                    },
                    singleLine = true,
                    label = {
                        Text(
                            "github api key"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ownerWithRepo,
                    onValueChange = {
                        ownerWithRepo =
                            it
                    },
                    singleLine = true,
                    label = {
                        Text(
                            "user/repo:"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pageSize,
                    onValueChange = {
                        pageSize = it
                    },
                    singleLine = true,
                    label = {
                        Text(
                            "pageSize"
                        )
                    }
                )

                OutlinedTextField(
                    value = lastPageNumber,
                    onValueChange = {
                        lastPageNumber =
                            it
                    },
                    singleLine = true,
                    label = {
                        Text(
                            "lastPageNumber"
                        )
                    },
                )

                Row {
                    Checkbox(
                        checked = isEnterprise,
                        onCheckedChange = { state ->
                            isEnterprise =
                                state
                        })
                    Text(
                        "is Enterprise ?"
                    )
                }


                if (isEnterprise) {
                    OutlinedTextField(
                        value = enterprise,
                        onValueChange = {
                            enterprise =
                                it
                        },
                        singleLine = true,
                        label = {
                            Text(
                                "Enterprise name"
                            )
                        },
                    )

                } else {
                    Spacer(
                        modifier = Modifier.size(
                            32.dp
                        )
                    )
                }

                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Button(
                        onClick = {

                            saveData(
                                settings,
                                githubKey,
                                ownerWithRepo,
                                isEnterprise,
                                enterprise,
                                pageSize.toIntOrNull() ?: 30,
                                lastPageNumber.toIntOrNull() ?: 3
                            )

                            navigator.push(
                                PRReviewAnalysisScreen(
                                    getPullsData(
                                        githubKey,
                                        ownerWithRepo,
                                        isEnterprise,
                                        enterprise,
                                        pageSize.toIntOrNull() ?: 30,
                                        lastPageNumber.toIntOrNull() ?: 3
                                    )
                                )
                            )
                        }) {
                        Text(
                            "PR Review "
                        )
                    }

                    Spacer(Modifier.size(16.dp))

                    Button(
                        onClick = {

                            saveData(
                                settings,
                                githubKey,
                                ownerWithRepo,
                                isEnterprise,
                                enterprise,
                                pageSize.toIntOrNull() ?: 30,
                                lastPageNumber.toIntOrNull() ?: 3
                            )

                            navigator.push(
                                PullAnalysisScreen(
                                    getPullsData(
                                        githubKey,
                                        ownerWithRepo,
                                        isEnterprise,
                                        enterprise,
                                        pageSize.toIntOrNull() ?: 30,
                                        lastPageNumber.toIntOrNull() ?: 3
                                    )
                                )
                            )
                        }) {
                        Text(
                            "PR Events"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.size(
                        16.dp
                    )
                )
            }


        }
    }

    private fun getPullsData(
            githubKey: String,
            ownerWithRepo: String,
            isEnterprise: Boolean,
            enterprise: String,
            pageSize: Int,
            lastPageNumber: Int) =
        PullsData(
            apiKey = githubKey,
            ownerWithRepo = ownerWithRepo,
            isEnterprise = isEnterprise,
            enterprise = enterprise,
            pageSize = pageSize,
            lastPageNumber = lastPageNumber
        )

    private fun saveData(
            settings: Settings,
            githubKey: String,
            ownerWithRepo: String,
            isEnterprise: Boolean,
            enterprise: String,
            pageSize: Int,
            lastPageNumber: Int) {
        settings.putString(
            Keys.API_KEY,
            githubKey
        )
        settings.putString(
            Keys.OWNER_REPO,
            ownerWithRepo
        )
        settings.putBoolean(
            Keys.IS_ENTERPRISE,
            isEnterprise
        )
        settings.putString(
            Keys.ENTERPRISE,
            enterprise
        )
        settings.putInt(
            Keys.PAGE_SIZE,
            pageSize
        )
        settings.putInt(
            Keys.LAST_PAGE_NUMBER,
            lastPageNumber
        )
    }

}


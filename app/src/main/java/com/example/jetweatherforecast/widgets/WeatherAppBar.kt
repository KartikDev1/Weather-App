package com.example.jetweatherforecast.widgets

import android.content.Context
import android.graphics.drawable.Icon
import android.icu.text.CaseMap.Title
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.jetweatherforecast.model.Favorite
import com.example.jetweatherforecast.navigation.WeatherScreens
import com.example.jetweatherforecast.screens.favorites.FavoriteViewModel


@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun WeatherAppBar(
    title: String = "Title",
    icon: ImageVector? = null,
    isMainScreen : Boolean = true,
    elevation: Dp = 0.dp,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    onAddActionClicked : () -> Unit = {},
    onButtonClicked : () -> Unit = {}
    ){

    val showDialog = remember {
        mutableStateOf(false)
    }

    if(showDialog.value){
        ShowSettingDropDownMenu(showDialog = showDialog,navController = navController)
    }

    val showIt = remember {
        mutableStateOf(false)
    }

    val context  = LocalContext.current

    TopAppBar(title = {
        Text(text = title,
            color = MaterialTheme.colorScheme.secondary,
            style = TextStyle(fontWeight = FontWeight.Bold,
                fontSize = 18.sp),
            modifier = Modifier.padding(horizontal = 10.dp))
    },
        actions = {
            if (isMainScreen){
                IconButton(onClick = {
                    onAddActionClicked.invoke()
                }) {
                    Icon(imageVector = Icons.Default.Search,
                        contentDescription = "search icon")

                }
                IconButton(onClick = {
                    showDialog.value = true
                }) {
                    Icon(imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More Icon" )

                }

            }else Box {}
        },
        navigationIcon = {
                         if(icon != null){
                            Icon(imageVector = icon, contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.clickable {
                                    onButtonClicked.invoke()
                                })
                         }

                        if(isMainScreen){
                            val isAlreadyFavList = favoriteViewModel.favList.collectAsState().value.filter { item->
                                (item.city == title.split(",")[0])
                            }

                            if(isAlreadyFavList.isNullOrEmpty()){
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "favorite icon", tint = Color.Red.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .scale(0.9f)
                                        .clickable {
                                            val split = title.split(",")
                                            favoriteViewModel.insertFavorite(
                                                Favorite(
                                                    city = split[0],
                                                    country = split[1]
                                                )
                                            ).run {
                                                showIt.value = true
                                            }
                                        }
                                )
                            }else {
                                showIt.value = false
                                Box{}
                            }

                            ShowToast(context = context,showIt)
                        }
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
        modifier = Modifier
            .padding(3.dp)
            .shadow(
                elevation = 5.dp,
                spotColor = Color.LightGray,
                shape = RoundedCornerShape(1.dp)
            )
        )
}

@Composable
fun ShowToast(context: Context, showIt: MutableState<Boolean>) {
    if(showIt.value){
        Toast.makeText(context,"Added to Favorites",Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ShowSettingDropDownMenu(
    showDialog: MutableState<Boolean>,
    navController: NavController
) {
    // Tie the dropdown's expanded state to showDialog.value
    val expanded = showDialog.value
    val items = listOf("About", "Favorites", "Settings")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
            .padding(top = 45.dp, end = 20.dp) // Replaced absolutePadding with padding
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { showDialog.value = false }, // Close both dialog and dropdown
            modifier = Modifier
                .width(140.dp)
                .background(Color.White)
        ) {
            items.forEach { text ->
                DropdownMenuItem(
                    onClick = {
                        showDialog.value = false // Close the dialog
                        navController.navigate(
                            when (text) {
                                "About" -> WeatherScreens.AboutScreen.name
                                "Favorites" -> WeatherScreens.FavoriteScreen.name
                                else -> WeatherScreens.SettingScreen.name
                            }
                        )
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (text) {
                                    "About" -> Icons.Default.Info
                                    "Favorites" -> Icons.Default.FavoriteBorder
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = text,
                                fontWeight = FontWeight.W300
                            )
                        }
                    }
                )
            }
        }
    }
}

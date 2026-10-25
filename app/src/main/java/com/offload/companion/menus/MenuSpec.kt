package com.offload.companion.menus

sealed interface MenuSpec {
    data object None : MenuSpec
}

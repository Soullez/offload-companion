package com.offload.companion.dialogs

sealed interface DialogSpec {
    data object None : DialogSpec
}

package com.example.myapplication.ui.navigation

// INTERFACE UMUM UNTUK SEMUA DESTINASI
interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

//AUTH
object DestinasiWelcome : DestinasiNavigasi {
    override val route = "welcome"
    override val titleRes = "Welcome"
}

object DestinasiRegister : DestinasiNavigasi {
    override val route = "register"
    override val titleRes = "Daftar"
}

object DestinasiLogin : DestinasiNavigasi {
    override val route = "login"
    override val titleRes = "Masuk"
}

//HOME
object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Home"
}

//ENTRY
object DestinasiEntry : DestinasiNavigasi {
    override val route = "entry_hiburan"
    override val titleRes = "Tambah Hiburan"
}

//DETAIL
object DestinasiDetail : DestinasiNavigasi {
    override val route = "detail_hiburan"
    override val titleRes = "Detail Hiburan"
    const val idArg = "id_hiburan"
    val routeWithArg = "$route/{$idArg}"
}

//UPDATE
object DestinasiUpdate : DestinasiNavigasi {
    override val route = "update_hiburan"
    override val titleRes = "Edit Hiburan"
    const val idArg = "id_update"
    val routeWithArg = "$route/{$idArg}"
}

//PROFILE
object DestinasiEditProfil : DestinasiNavigasi {
    override val route = "edit_profil"
    override val titleRes = "Edit Profil"
}

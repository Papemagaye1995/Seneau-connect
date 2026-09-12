package com.example.ui.strings

import com.example.data.model.AppLanguage

object SeneauStrings {
    fun appTitle(lang: AppLanguage) = "SEN'EAU Connect"
    fun appSubtitle(lang: AppLanguage) = if (lang == AppLanguage.WO) "Lëkkalekaay ndoxu Senegaal ci jàmm" else "Service de raccordement d'eau en ligne au Sénégal"

    fun navHome(lang: AppLanguage) = if (lang == AppLanguage.WO) "Dalal" else "Accueil"
    fun navRequest(lang: AppLanguage) = if (lang == AppLanguage.WO) "Làccu" else "Demande"
    fun navTrack(lang: AppLanguage) = if (lang == AppLanguage.WO) "Toppte" else "Suivi"
    fun navPay(lang: AppLanguage) = if (lang == AppLanguage.WO) "Fey" else "Paiement"
    fun navHistory(lang: AppLanguage) = if (lang == AppLanguage.WO) "Jaar-jaar" else "Historique"
    fun navSecurity(lang: AppLanguage) = if (lang == AppLanguage.WO) "Kaarange" else "Sécurité"

    fun welcomeUser(lang: AppLanguage, name: String) =
        if (lang == AppLanguage.WO) "Dalal ak jàmm, $name" else "Bonjour, $name"

    fun balanceLabel(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Li nga am ci sa kàmp SEN'EAU" else "Solde disponible sur compte usager"

    fun rechargeBtn(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Doolil" else "Recharger"

    fun budgetTrackTitle(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Saytu sa alal ci kër gi" else "Suivi budgétaire eau"

    fun monthlyBudgetCap(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Tolluwaayu koppar bi" else "Plafond mensuel"

    fun newRequestBanner(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Làccu lëkkalekaay ndox bu bees" else "Nouvelle demande de branchement"

    fun newRequestDesc(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Yebal sa dosiye ci internet, am saytu ak lëkkalekaay gaaw" else "Déposez votre dossier 100% en ligne, sans vous déplacer en agence."

    fun trackDossierBanner(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Toppte sa dosiye ci saa si" else "Suivi de dossier en temps réel"

    fun liveSmsTitle(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Bataaxal SMS yi génn" else "Notifications SMS en temps réel"

    fun noActiveRequest(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Amul benn dosiye bu dox leegi." else "Aucune demande de branchement enregistrée."

    fun startRequestBtn(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Door làccu bi" else "Faire une demande"

    fun exportCsvBtn(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Yebal CSV" else "Exporter en CSV"

    fun simulateProgress(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Yóbbal dosiye bi kanam" else "Faire avancer l'étape du dossier"

    fun paymentHeader(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Fey sa kàrtu ndox ak saytu" else "Paiement et transactions"

    fun selectPaymentMethod(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Tannal nooy feye" else "Sélectionnez le mode de paiement"

    fun biometricPromptTitle(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Kaarange ak baram" else "Authentification sécurisée"

    fun biometricPromptSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Defal sa baram walla sa kod PIN ngir fey" else "Confirmez votre empreinte ou saisissez votre code PIN pour valider"

    fun dataProtectionTitle(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Samm say mbir ci kaarange (RGPD)" else "Protection des données & RGPD"

    fun dataProtectionSub(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Chiffrement AES-256 de bout en bout. Say mbir kenn du ko gis." else "Chiffrement AES-256 de bout en bout. Vos informations personnelles sont strictement protégées."

    fun offlineBadge(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Béréb bi (Offline)" else "Mode hors-ligne actif"

    fun onlineBadge(lang: AppLanguage) =
        if (lang == AppLanguage.WO) "Lëkkale na ci Cloud" else "Synchronisé Cloud SEN'EAU"
}

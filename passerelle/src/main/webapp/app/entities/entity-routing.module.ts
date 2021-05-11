import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'lot',
        data: { pageTitle: 'passerelleApp.gestioneauLot.home.title' },
        loadChildren: () => import('./gestioneau/lot/lot.module').then(m => m.GestioneauLotModule),
      },
      {
        path: 'source-approv-ep',
        data: { pageTitle: 'passerelleApp.gestioneauSourceApprovEp.home.title' },
        loadChildren: () => import('./gestioneau/source-approv-ep/source-approv-ep.module').then(m => m.GestioneauSourceApprovEpModule),
      },
      {
        path: 'nature-ouvrage',
        data: { pageTitle: 'passerelleApp.gestioneauNatureOuvrage.home.title' },
        loadChildren: () => import('./gestioneau/nature-ouvrage/nature-ouvrage.module').then(m => m.GestioneauNatureOuvrageModule),
      },
      {
        path: 'localite',
        data: { pageTitle: 'passerelleApp.gestioneauLocalite.home.title' },
        loadChildren: () => import('./gestioneau/localite/localite.module').then(m => m.GestioneauLocaliteModule),
      },
      {
        path: 'annee',
        data: { pageTitle: 'passerelleApp.gestioneauAnnee.home.title' },
        loadChildren: () => import('./gestioneau/annee/annee.module').then(m => m.GestioneauAnneeModule),
      },
      {
        path: 'commune',
        data: { pageTitle: 'passerelleApp.gestioneauCommune.home.title' },
        loadChildren: () => import('./gestioneau/commune/commune.module').then(m => m.GestioneauCommuneModule),
      },
      {
        path: 'type-habitation',
        data: { pageTitle: 'passerelleApp.gestioneauTypeHabitation.home.title' },
        loadChildren: () => import('./gestioneau/type-habitation/type-habitation.module').then(m => m.GestioneauTypeHabitationModule),
      },
      {
        path: 'centre',
        data: { pageTitle: 'passerelleApp.gestioneauCentre.home.title' },
        loadChildren: () => import('./gestioneau/centre/centre.module').then(m => m.GestioneauCentreModule),
      },
      {
        path: 'prefabricant',
        data: { pageTitle: 'passerelleApp.gestioneauPrefabricant.home.title' },
        loadChildren: () => import('./gestioneau/prefabricant/prefabricant.module').then(m => m.GestioneauPrefabricantModule),
      },
      {
        path: 'mode-evacuation-eau-usee',
        data: { pageTitle: 'passerelleApp.gestioneauModeEvacuationEauUsee.home.title' },
        loadChildren: () =>
          import('./gestioneau/mode-evacuation-eau-usee/mode-evacuation-eau-usee.module').then(
            m => m.GestioneauModeEvacuationEauUseeModule
          ),
      },
      {
        path: 'direction-regionale',
        data: { pageTitle: 'passerelleApp.gestioneauDirectionRegionale.home.title' },
        loadChildren: () =>
          import('./gestioneau/direction-regionale/direction-regionale.module').then(m => m.GestioneauDirectionRegionaleModule),
      },
      {
        path: 'macon',
        data: { pageTitle: 'passerelleApp.gestioneauMacon.home.title' },
        loadChildren: () => import('./gestioneau/macon/macon.module').then(m => m.GestioneauMaconModule),
      },
      {
        path: 'prevision',
        data: { pageTitle: 'passerelleApp.gestioneauPrevision.home.title' },
        loadChildren: () => import('./gestioneau/prevision/prevision.module').then(m => m.GestioneauPrevisionModule),
      },
      {
        path: 'type-commune',
        data: { pageTitle: 'passerelleApp.gestioneauTypeCommune.home.title' },
        loadChildren: () => import('./gestioneau/type-commune/type-commune.module').then(m => m.GestioneauTypeCommuneModule),
      },
      {
        path: 'region',
        data: { pageTitle: 'passerelleApp.gestioneauRegion.home.title' },
        loadChildren: () => import('./gestioneau/region/region.module').then(m => m.GestioneauRegionModule),
      },
      {
        path: 'parcelle',
        data: { pageTitle: 'passerelleApp.gestioneauParcelle.home.title' },
        loadChildren: () => import('./gestioneau/parcelle/parcelle.module').then(m => m.GestioneauParcelleModule),
      },
      {
        path: 'secteur',
        data: { pageTitle: 'passerelleApp.gestioneauSecteur.home.title' },
        loadChildren: () => import('./gestioneau/secteur/secteur.module').then(m => m.GestioneauSecteurModule),
      },
      {
        path: 'fiche-suivi-ouvrage',
        data: { pageTitle: 'passerelleApp.gestioneauFicheSuiviOuvrage.home.title' },
        loadChildren: () =>
          import('./gestioneau/fiche-suivi-ouvrage/fiche-suivi-ouvrage.module').then(m => m.GestioneauFicheSuiviOuvrageModule),
      },
      {
        path: 'section',
        data: { pageTitle: 'passerelleApp.gestioneauSection.home.title' },
        loadChildren: () => import('./gestioneau/section/section.module').then(m => m.GestioneauSectionModule),
      },
      {
        path: 'province',
        data: { pageTitle: 'passerelleApp.gestioneauProvince.home.title' },
        loadChildren: () => import('./gestioneau/province/province.module').then(m => m.GestioneauProvinceModule),
      },
      {
        path: 'mode-evac-excreta',
        data: { pageTitle: 'passerelleApp.gestioneauModeEvacExcreta.home.title' },
        loadChildren: () => import('./gestioneau/mode-evac-excreta/mode-evac-excreta.module').then(m => m.GestioneauModeEvacExcretaModule),
      },
      {
        path: 'centre-regroupement',
        data: { pageTitle: 'passerelleApp.gestioneauCentreRegroupement.home.title' },
        loadChildren: () =>
          import('./gestioneau/centre-regroupement/centre-regroupement.module').then(m => m.GestioneauCentreRegroupementModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

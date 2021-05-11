import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFicheSuiviOuvrage } from '../fiche-suivi-ouvrage.model';

@Component({
  selector: 'jhi-fiche-suivi-ouvrage-detail',
  templateUrl: './fiche-suivi-ouvrage-detail.component.html',
})
export class FicheSuiviOuvrageDetailComponent implements OnInit {
  ficheSuiviOuvrage: IFicheSuiviOuvrage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ficheSuiviOuvrage }) => {
      this.ficheSuiviOuvrage = ficheSuiviOuvrage;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

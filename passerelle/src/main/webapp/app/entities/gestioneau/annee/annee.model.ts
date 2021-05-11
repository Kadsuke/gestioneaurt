import { IPrevision } from 'app/entities/gestioneau/prevision/prevision.model';

export interface IAnnee {
  id?: number;
  libelle?: string;
  prevision?: IPrevision | null;
}

export class Annee implements IAnnee {
  constructor(public id?: number, public libelle?: string, public prevision?: IPrevision | null) {}
}

export function getAnneeIdentifier(annee: IAnnee): number | undefined {
  return annee.id;
}

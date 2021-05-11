import { ILocalite } from 'app/entities/gestioneau/localite/localite.model';

export interface ISecteur {
  id?: number;
  libelle?: string;
  localite?: ILocalite | null;
}

export class Secteur implements ISecteur {
  constructor(public id?: number, public libelle?: string, public localite?: ILocalite | null) {}
}

export function getSecteurIdentifier(secteur: ISecteur): number | undefined {
  return secteur.id;
}

import { ICentre } from 'app/entities/gestioneau/centre/centre.model';
import { IAnnee } from 'app/entities/gestioneau/annee/annee.model';

export interface IPrevision {
  id?: number;
  nbLatrine?: number;
  nbPuisard?: number;
  nbPublic?: number;
  nbScolaire?: number;
  centre?: ICentre | null;
  refannee?: IAnnee | null;
}

export class Prevision implements IPrevision {
  constructor(
    public id?: number,
    public nbLatrine?: number,
    public nbPuisard?: number,
    public nbPublic?: number,
    public nbScolaire?: number,
    public centre?: ICentre | null,
    public refannee?: IAnnee | null
  ) {}
}

export function getPrevisionIdentifier(prevision: IPrevision): number | undefined {
  return prevision.id;
}

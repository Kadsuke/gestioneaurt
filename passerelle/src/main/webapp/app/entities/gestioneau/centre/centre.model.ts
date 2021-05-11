import { ICentreRegroupement } from 'app/entities/gestioneau/centre-regroupement/centre-regroupement.model';
import { IPrevision } from 'app/entities/gestioneau/prevision/prevision.model';

export interface ICentre {
  id?: number;
  libelle?: string;
  responsable?: string;
  contact?: string;
  centreregroupement?: ICentreRegroupement | null;
  prevision?: IPrevision | null;
}

export class Centre implements ICentre {
  constructor(
    public id?: number,
    public libelle?: string,
    public responsable?: string,
    public contact?: string,
    public centreregroupement?: ICentreRegroupement | null,
    public prevision?: IPrevision | null
  ) {}
}

export function getCentreIdentifier(centre: ICentre): number | undefined {
  return centre.id;
}

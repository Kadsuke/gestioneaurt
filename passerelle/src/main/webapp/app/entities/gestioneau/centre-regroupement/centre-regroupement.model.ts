import { IDirectionRegionale } from 'app/entities/gestioneau/direction-regionale/direction-regionale.model';

export interface ICentreRegroupement {
  id?: number;
  libelle?: string;
  responsable?: string;
  contact?: string;
  directionregionale?: IDirectionRegionale | null;
}

export class CentreRegroupement implements ICentreRegroupement {
  constructor(
    public id?: number,
    public libelle?: string,
    public responsable?: string,
    public contact?: string,
    public directionregionale?: IDirectionRegionale | null
  ) {}
}

export function getCentreRegroupementIdentifier(centreRegroupement: ICentreRegroupement): number | undefined {
  return centreRegroupement.id;
}

import { ILot } from 'app/entities/gestioneau/lot/lot.model';

export interface IParcelle {
  id?: number;
  libelle?: string;
  lot?: ILot | null;
}

export class Parcelle implements IParcelle {
  constructor(public id?: number, public libelle?: string, public lot?: ILot | null) {}
}

export function getParcelleIdentifier(parcelle: IParcelle): number | undefined {
  return parcelle.id;
}

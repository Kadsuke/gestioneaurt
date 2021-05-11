import { ISection } from 'app/entities/gestioneau/section/section.model';

export interface ILot {
  id?: number;
  libelle?: string;
  section?: ISection | null;
}

export class Lot implements ILot {
  constructor(public id?: number, public libelle?: string, public section?: ISection | null) {}
}

export function getLotIdentifier(lot: ILot): number | undefined {
  return lot.id;
}

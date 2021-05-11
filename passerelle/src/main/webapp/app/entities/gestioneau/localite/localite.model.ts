import { ICommune } from 'app/entities/gestioneau/commune/commune.model';

export interface ILocalite {
  id?: number;
  libelle?: string;
  commune?: ICommune | null;
}

export class Localite implements ILocalite {
  constructor(public id?: number, public libelle?: string, public commune?: ICommune | null) {}
}

export function getLocaliteIdentifier(localite: ILocalite): number | undefined {
  return localite.id;
}
